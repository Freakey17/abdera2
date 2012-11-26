/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera2.common.http;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.security.Key;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.security.HashHelper;
import org.apache.abdera2.common.templates.MapContext;
import org.apache.abdera2.common.templates.Template;
import org.apache.abdera2.common.text.CharUtils;
import org.apache.abdera2.common.text.UrlEncoding;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * This is largely experimental... it implements a number of 
 * OAuth related Authentication header builders for OAuth 1.0,
 * Bearer and MAC style WWW-Authenticate/Authorization headers
 */
public final class OAuthUtil {

  private static final int NONCE_SIZE = ((23 * 5) / 8) + 1;
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  
  static final Predicate<Map.Entry<String,String>> is_oauth_param = 
    new Predicate<Map.Entry<String,String>>() {
      public boolean apply(Entry<String, String> input) {
        return input.getKey()
          .toLowerCase(Locale.US)
          .startsWith("oauth_") || 
          input.getKey().equalsIgnoreCase("realm");
      }
  };
  
  static long getTimestamp() {
    return DateTimes.now().getMillis() / 1000;
  }
  
  static String getNonce() {
    byte[] nonce = new byte[NONCE_SIZE];
    SECURE_RANDOM.nextBytes(nonce);
    return Hex.encodeHexString(nonce);
  }
  
  public static OAuth1HeaderBuilder makeOAuth1Header() {
    return new OAuth1HeaderBuilder();
  }
  
  public static enum OAuth1SignatureMethod {
    PLAINTEXT,
    RSA_SHA1,
    HMAC_SHA1;
    
    public String label() {
      return name().replace('_','-');
    }
  }
  
  public static class OAuth1HeaderBuilder 
    implements Supplier<Authentication> {

    private OAuth1SignatureMethod method;
    private String httpMethod;
    private IRI requestUri;
    private PrivateKey sigKey;
    private String consumer_secret;
    private String token_secret;
    private boolean include_additional = false;
    private ImmutableMap.Builder<String,String> builder = 
      ImmutableMap.<String,String>builder()
        .put("oauth_version", "1.0");
    
    public OAuth1HeaderBuilder includeAdditional() {
      this.include_additional = true;
      return this;
    }
    
    public OAuth1HeaderBuilder doNotIncludeAdditional() {
      this.include_additional = false;
      return this;
    }
    
//    public OAuth1HeaderBuilder consumerDetails(
//      ClientSecrets secrets, 
//      ClientSecrets.Type type) {
//      checkNotNull(secrets);
//      if (secrets.has(type)) {
//        consumerKey(secrets.getClientId(type));
//        consumerSecret(secrets.getClientSecret(type));
//      }
//      return this;
//    }
    
    public OAuth1HeaderBuilder consumerSecret(String secret) {
      this.consumer_secret = secret;
      return this;
    }
    
    public OAuth1HeaderBuilder tokenSecret(String secret) {
      this.token_secret = secret;
      return this;
    }
    
    private static final ImmutableSet<String> reserved = 
      ImmutableSet.of(
        "realm",
        "oauth_consumer_key",
        "oauth_signature_method",
        "oauth_signature",
        "oauth_timestamp",
        "oauth_nonce",
        "oauth_version",
        "oauth_token",
        "oauth_token_secret",
        "oauth_callback",
        "oauth_consumer_secret");
    
    public OAuth1HeaderBuilder additional(String key, String val) {
      key = checkNotNull(key).toLowerCase(Locale.US);
      checkArgument(!reserved.contains(key));
      builder.put(key,val);
      return this;
    }
    
    public OAuth1HeaderBuilder requestUri(IRI uri) {
      this.requestUri = uri.base();
      return this;
    }
    
    public OAuth1HeaderBuilder requestUri(String uri) {
      return requestUri(new IRI(uri));
    }
    
    public OAuth1HeaderBuilder httpMethod(String method) {
      this.httpMethod = method;
      return this;
    }
    
    public OAuth1HeaderBuilder realm(String val) {
      builder.put("realm", val);
      return this;
    }
    
    public OAuth1HeaderBuilder consumerKey(String key) {
      builder.put("oauth_consumer_key", key);
      return this;
    }
    
    public OAuth1HeaderBuilder token(String token) {
      builder.put("oauth_token", token);
      return this;
    }
    
    public OAuth1HeaderBuilder callback(String uri) {
      return callback(new IRI(uri));
    }
    
    public OAuth1HeaderBuilder callback(IRI iri) {
      builder.put("oauth_callback", iri.toASCIIString());
      return this;
    }
    
    public OAuth1HeaderBuilder plainText() {
      this.method = OAuth1SignatureMethod.PLAINTEXT;
      return signatureMethod(this.method.label());
    }
    
    public OAuth1HeaderBuilder hmacSha1() {
      this.method = OAuth1SignatureMethod.HMAC_SHA1;
      return signatureMethod(this.method.label());
    }
    
    public OAuth1HeaderBuilder rsaSha1(PrivateKey key) {
      this.sigKey = key;
      this.method = OAuth1SignatureMethod.RSA_SHA1;
      return signatureMethod(this.method.label());
    }
    
    private OAuth1HeaderBuilder signatureMethod(String method) {
      builder.put("oauth_signature_method", method);
      return this;
    }
    
    public OAuth1HeaderBuilder timestampNow() {
      return timestamp(getTimestamp());
    }
    
    public OAuth1HeaderBuilder timestamp(long timestamp) {
      builder.put("oauth_timestamp", Long.toString(timestamp));
      return this;
    }
    
    public OAuth1HeaderBuilder nonce() {
      return nonce(getNonce());
    }
    
    public OAuth1HeaderBuilder nonce(String nonce) {
      builder.put("oauth_nonce", nonce);
      return this;
    }
    
    public Authentication getPlaintext() {
      return plainText().get();
    }
    
    public Authentication getHmacSha1() {
      return hmacSha1().get();
    }
    
    public Authentication getRsaSha1(PrivateKey key) {
      return rsaSha1(key).get();
    }

    private String getSignature(ImmutableMap<String,String> map) {
      String signature = null;
      if (method != null) {
        switch(method) {
        case PLAINTEXT:
          signature = generatePlainTextSignature(map);
          break;
        case HMAC_SHA1:
          byte[] base = generateSignatureBaseString(httpMethod,requestUri,map);  
          signature = hmac(hmacKey(consumer_secret,token_secret), base);
          break;
        case RSA_SHA1:
          base = generateSignatureBaseString(httpMethod,requestUri,map);
          signature = HashHelper.sig(sigKey, "SHA1withRSA", base);
          break;
        }
      }
      return signature;
    }
    
    public Authentication get() {
      ImmutableMap<String,String> map = 
        this.builder.build();
      Authentication.Builder builder = 
        Authentication.make()
          .scheme("OAuth");
      for (Map.Entry<String, String> entry : map.entrySet()) {
        if (include_additional || is_oauth_param.apply(entry))
          builder.param(entry.getKey(), escaped(entry.getValue()));
      }
      String signature = getSignature(map);
      if (signature != null)
        builder.param("oauth_signature", signature);
      return builder.get();
    }
    
    public String getAsQuery() {
      return get_query(false);
    }
    
    public String getAsQueryFragment() {
      return get_query(true);
    }
    
    private String get_query(boolean fragment) {
      ImmutableMap<String,String> map = 
        this.builder.build();
      Template template = buildTemplate(fragment,map);
      String signature = getSignature(map);
      MapContext ctx = new MapContext();
      ctx.putAll(map);
      if (signature != null) 
        ctx.put("oauth_signature", signature);
      return template.expand(ctx);
    }
    
    private static final Joiner comma_joiner = Joiner.on(',');
    
    private Template buildTemplate(boolean fragment, ImmutableMap<String,String> map) {
      StringBuilder buf = new StringBuilder("{");
      buf.append(fragment?'&':'?');
      buf.append(comma_joiner.join(map.keySet()));
      if (map.size() > 0) buf.append(',');
      buf.append("oauth_signature");
      return new Template(buf.append("}").toString());
    }
   
    private String hmac(Key key, byte[] mat) {
      try {
        Mac mac = Mac.getInstance("HmacSha1");
        mac.init(key);
        mac.update(mat,0,mat.length);
        byte[] sig = mac.doFinal();
        return escaped(Base64.encodeBase64String(sig));
      } catch (Throwable t) {
        throw ExceptionHelper.propogate(t);
      }
    }
    
    private Key hmacKey(String consumer_secret, String token_secret) {
      try {
        return new SecretKeySpec(new StringBuilder()
          .append(consumer_secret!=null?escaped(consumer_secret):"")
          .append('&')
          .append(token_secret!=null?escaped(token_secret):"")
          .toString().getBytes("UTF-8"),"HmacSha1");
      } catch (Throwable t) {
        throw ExceptionHelper.propogate(t);
      }
    }
    
    private String escaped(String val) {
      return UrlEncoding.encode(val, CharUtils.Profile.UNRESERVED);
    }
    
    private byte[] generateSignatureBaseString(
        String method, 
        IRI request_uri,
        ImmutableMap<String,String> map) {
      try {
        return new StringBuilder()
          .append(checkNotNull(method).toUpperCase(Locale.US))
          .append('&')
          .append(escaped(checkNotNull(request_uri).toASCIIString()))
          .append('&')
          .append(generateSignatureBaseString2(map))
          .toString().getBytes("UTF-8");
      } catch (Throwable t) {
        throw ExceptionHelper.propogate(t);
      }
    }
    
    private String generateSignatureBaseString2(
        ImmutableMap<String,String> unsorted) {
      TreeMap<String,String> map = Maps.newTreeMap();
      map.putAll(unsorted);
      StringBuilder buf = new StringBuilder();
      boolean first = true;
      for (Map.Entry<String,String> entry : map.entrySet()) {
        if (!first) buf.append('&');
        else first = false;
        buf.append(entry.getKey());
        buf.append('=');
        buf.append(entry.getValue());
      }
      return escaped(buf.toString());
    }
    
    private String generatePlainTextSignature(ImmutableMap<String,String> map) {
      StringBuilder buf = new StringBuilder();
      buf.append(consumer_secret != null ? consumer_secret : "");
      buf.append('&');
      buf.append(map.containsKey("oauth_token") ? map.get("oauth_token") : "");
      return escaped(buf.toString());
    }
  }
  
  static final Joiner space_joiner = 
    Joiner.on(' ');
  
  public static enum BearerErrorCode {
    INVALID_REQUEST,
    INVALID_TOKEN,
    INSUFFICIENT_SCOPE;
    private final String label;
    BearerErrorCode() {
      this.label = name().toLowerCase(Locale.US);
    }
    public String label() {
      return label;
    }
  }
  
  public static BearerBuilder makeBearerHeader() {
    return new BearerBuilder();
  }
  
  public static final class BearerBuilder 
    implements Supplier<Authentication> {

    private final Authentication.Builder builder =
      Authentication.make().scheme("bearer")
        .quoted("error")
        .quoted("error-desc");
    
    private static final ImmutableSet<String> reserved = 
      ImmutableSet.of(
        "realm",
        "scope",
        "error",
        "error-desc",
        "error-uri");
    
    public BearerBuilder param(String name, String val) {
      name = checkNotNull(name).toLowerCase(Locale.US);
      checkArgument(!reserved.contains(name));
      builder.param(name, val);
      return this;
    }
    
    public BearerBuilder params(Map<String,String> map) {
      for (Map.Entry<String, String> entry : map.entrySet())
        param(entry.getKey(),entry.getValue());
      return this;
    }
    
    public BearerBuilder token(String token) {
      builder.b64token(token);
      return this;
    }
    
    public BearerBuilder realm(String realm) {
      builder.param("realm", realm);
      return this;
    }
    
    public BearerBuilder scope(String... scopes) {
      builder.param("scope", space_joiner.join(scopes));
      return this;
    }
    
    public BearerBuilder error(BearerErrorCode code) {
      builder.param("error", code.label());
      return this;
    }
    
    public BearerBuilder errorDescription(String description) {
      builder.param("error_description", description);
      return this;
    }
    
    public BearerBuilder errorUri(IRI uri) {
      builder.param("error-uri", uri.toASCIIString());
      return this;
    }

    public BearerBuilder errorUri(String uri) {
      return errorUri(new IRI(uri));
    }
    
    public Authentication get() {
      return builder.get();
    }
  }
  
  public static MacBuilder makeMacHeader() {
    return new MacBuilder();
  }
  
  public static enum MacAlgorithm {
    HMAC_SHA1("hmac-sha-1","HmacSha1"),
    HMAC_SHA256("hmac-sha-256","HmacSha256");
    private final String label;
    private final String alg;
    MacAlgorithm(String label,String alg) {
      this.label = label;
      this.alg = alg;
    }
    public String alg() {
      return alg;
    }
    public String label() {
      return label;
    }
    public static MacAlgorithm get(String label) {
      for (MacAlgorithm alg : MacAlgorithm.values())
        if (alg.label().equalsIgnoreCase(label))
          return alg;
      return null;
    }
  }
  
  public static final class MacBuilder 
    implements Supplier<Authentication> {

    private String key_id;
    private String key;
    private MacAlgorithm algorithm = MacAlgorithm.HMAC_SHA1;
    private DateTime issueTime = DateTimes.now();
    private long age = -1;
    private String httpMethod = "GET";
    private IRI request_uri;
    private String host;
    private int port;
    private String bodyhash;
    private String ext;
    private String nonce;
    
    public MacBuilder age(long age) {
      this.age = Math.max(age,-1);
      return this;
    }
    
    public MacBuilder nonce() {
      this.nonce = getNonce();
      return this;
    }
    
    public MacBuilder nonce(String nonce) {
      this.nonce = nonce;
      return this;
    }
    
    public MacBuilder bodyHash(String hash) {
      this.bodyhash = hash;
      return this;
    }
    
    public MacBuilder ext(String ext) {
      this.ext = ext;
      return this;
    }
    
    public MacBuilder id(String id) {
      this.key_id = id;
      return this;
    }
    
    public MacBuilder key(String key) {
      this.key = key;
      return this;
    }
    
    public MacBuilder algorithm(MacAlgorithm alg) {
      this.algorithm = alg;
      return this;
    }
    
    public MacBuilder issueTime(DateTime issue) {
      this.issueTime = issue;
      return this;
    }
    
    public MacBuilder httpMethod(String method) {
      this.httpMethod = method.toUpperCase(Locale.US);
      return this;
    }
    
    public MacBuilder requestUri(IRI iri) {
      this.request_uri = iri.relative();
      host(iri);
      port(iri.getScheme());
      return this;
    }
    
    public MacBuilder requestUri(String iri) {
      return requestUri(new IRI(iri));
    }
    
    public MacBuilder host(String host) {
      if (Strings.isNullOrEmpty(host)) return this;
      IRI h = new IRI(String.format("http://%s",host));
      this.host = h.getASCIIHost().toLowerCase(Locale.US);
      return this;
    }
    
    public MacBuilder host(IRI iri) {
      return host(checkNotNull(iri).getASCIIHost());
    }
    
    private MacBuilder port(String scheme) {
      if (scheme == null) port(0);
      if ("http".equalsIgnoreCase(scheme)) port(80);
      else if ("https".equalsIgnoreCase(scheme)) port(443);
      else port(0);
      return this;
    }
    
    public MacBuilder port(int port) {
      this.port = Math.max(0, port);
      return this;
    }
    
    public Authentication getHmacSha1(String key) {
      return key(key).algorithm(MacAlgorithm.HMAC_SHA1).get();
    }
    
    public Authentication getHmacSha256(String key) {
      return key(key).algorithm(MacAlgorithm.HMAC_SHA256).get();
    }
    
    public Authentication get() {
      try {
        String nonce = buildNonce();
        byte[] mat = getNormalizedRequestString(nonce);
        Key hmac = getkey(algorithm,key);
        String mac = HashHelper.hmac(hmac, algorithm.alg(), mat);
        Authentication.Builder builder = 
          Authentication.make()
            .quoted("nonce")
            .quoted("mac")
            .quoted("body-hash")
            .quoted("ext")
            .quoted("id")
            .scheme("mac")
            .param("id", key_id)
            .param("nonce", nonce)
            .param("mac", mac);
        if (!Strings.isNullOrEmpty(bodyhash))
          builder.param("body-hash", bodyhash);
        if (!Strings.isNullOrEmpty(ext))
          builder.param("ext", ext);
        return builder.get();
      } catch (NullPointerException npe) {
        return Authentication.make().scheme("mac").get();
      }
    }
    
    private Key getkey(MacAlgorithm alg, String key) {
      try {
        return new SecretKeySpec(key.getBytes("UTF-8"),alg.alg());
      } catch (Throwable t) {
        throw ExceptionHelper.propogate(t);
      }
    }
    
    private byte[] getNormalizedRequestString(String nonce) {
      try {
        StringBuilder buf = new StringBuilder();
        buf.append(nonce).append("\n");
        buf.append(httpMethod != null ? httpMethod : "GET").append("\n");
        buf.append(request_uri != null ? request_uri.toASCIIString() : "").append("\n");
        buf.append(host != null ? host : "").append("\n");
        buf.append(port > -1 ? port : 80).append("\n");
        buf.append(bodyhash != null ? bodyhash : "").append("\n");
        buf.append(ext != null ? ext : "").append("\n");
        return buf.toString().getBytes("UTF-8");
      } catch (Throwable t) {
        throw ExceptionHelper.propogate(t);
      }
    }
    
    private String buildNonce() {
      if (age < 0) {
        DateTime now = DateTimes.now();
        long now_millis = now.getMillis();
        long then_millis = issueTime.getMillis();
        age = (then_millis - now_millis) / 1000;
      }
      String n = nonce != null ? nonce : getNonce();
      return String.format("%d:%s", age, n);
    }
  }
}
