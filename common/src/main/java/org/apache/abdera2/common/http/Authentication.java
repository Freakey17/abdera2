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
import static com.google.common.base.Preconditions.*;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.text.CharUtils;
import org.apache.abdera2.common.text.CharUtils.Profile;
import org.apache.abdera2.common.text.Codec;

import static org.apache.abdera2.common.text.CharUtils.appendcomma;
import static org.apache.abdera2.common.text.CharUtils.unquote;
import static org.apache.abdera2.common.text.CharUtils.quotedIfNotToken;
import static org.apache.abdera2.common.text.CharUtils.quoted;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of the HTTP Challenge/Credentials Construct. This is helpful when 
 * using custom authentication mechanisms with an HTTP Request. It provides
 * a simple means of working with the WWW-Authenticate and Proxy-Authenticate
 * headers.
 **/
public class Authentication implements Iterable<String>, Serializable {

  private static final long serialVersionUID = 7237004814934751226L;
  public final static String BASIC = "basic";
  public final static String DIGEST = "digest";
  
  private final static String TOKEN = "[\\!\\#\\$\\%\\&\\'\\*\\+\\-\\.\\^\\_\\`\\|\\~a-zA-Z0-9]+";
  private final static String B64 = "([a-zA-Z0-9\\-\\.\\_\\~\\+\\/]+\\=*)";
  private final static String PARAM = TOKEN+"\\s*=\\s*(?:(?:\"(?:(?:\\Q\\\"\\E)|[^\"])*\")|(?:"+TOKEN+"))";
  private final static String PARAMS = "\\s*,?\\s*(" + PARAM + "(?:\\s*,\\s*(?:"+PARAM+")?)*)";
  private final static String B64orPARAM = "(?:" + PARAMS + "|" + B64 + ")";
  private final static String PATTERN = "("+TOKEN+")(?:\\s*" + B64orPARAM + ")?";
  private final static Pattern pattern = 
    Pattern.compile(PATTERN);
  private final static Pattern param = 
    Pattern.compile("("+PARAM+")");
  
  private final static Set<String> ALWAYS = 
    Sets.newHashSet(
      "domain",
      "nonce",
      "opaque",
      "qop",
      "realm");
  
  public static synchronized void alwaysQuote(String... names) {
    checkArgument(names.length > 0);
    for (String name : checkNotNull(names))
      ALWAYS.add(name);
  }
  
  public static final Function<String, Iterable<Authentication>> parser = 
    new Function<String,Iterable<Authentication>>() {
      public Iterable<Authentication> apply(String input) {
        return input != null ? 
          parse(input) : 
          Collections.<Authentication>emptySet();
      }
  };
  
  public static Iterable<Authentication> parse(String challenge) {
    checkNotNull(challenge);
    List<Authentication> challenges = new ArrayList<Authentication>();
    Matcher matcher = pattern.matcher(challenge);
    while (matcher.find()) {
      String scheme = matcher.group(1);
      String params = matcher.group(2);
      params = params != null ? params.replaceAll(",\\s*,", ",").replaceAll(",\\s*,", ",") : null;
      String b64token = matcher.group(3); 
      Authentication.Builder auth = 
        make()
          .scheme(scheme)
          .b64token(b64token);
      if (params != null) {
        Matcher mparams = param.matcher(params);
        while(mparams.find()) {
          String p = mparams.group(1);
          String[] ps = p.split("\\s*=\\s*", 2);
          String name = ps[0];
          if (name.charAt(name.length()-1)=='*')
            name = name.substring(0,name.length()-1);
          auth.param(name, Codec.decode(unquote(CharUtils.unescape(ps[1]))));
        }
      }
      challenges.add(auth.get());
    }
    return challenges;
  }

  public static class Builder 
    implements Supplier<Authentication> {
    String scheme;
    String b64token;
    final ImmutableMap.Builder<String,String> params = 
      ImmutableMap.builder();
    final ImmutableSet.Builder<String> quoted = 
      ImmutableSet.builder();
    
    public Authentication get() {
      checkNotNull(scheme);
      return new Authentication(this);
    }
   
    public Builder params(Map<String,String> map) {
      params.putAll(map);
      return this;
    }
    
    public Builder params(
      Map<String,String> map, 
      Predicate<Map.Entry<String,String>> predicate) {
      params.putAll(Maps.filterEntries(map, predicate));
      return this;
    }
    
    public Builder param(String name, String val) {
      params.put(name, val);
      return this;
    }
    
    public Builder quotedParam(String name, String val) {
      param(name,val);
      quoted.add(name);
      return this;
    }
    
    public Builder quoted(String name) {
      quoted.add(name);
      return this;
    }
    
    public Builder scheme(String scheme) {
      checkNotNull(scheme);
      this.scheme = scheme.toLowerCase();
      return this;
    }
    
    public Builder b64token(String token) {
      this.b64token = token;
      return this;
    }
    
    public Builder basicCredentials(
      String user, 
      String password) {
        return b64token(
          Base64.encodeBase64String(
            CharUtils.utf8bytes(
              String.format(
                "%s:%s",
                user,
                password))));
    }
   
  }
  
  public static Builder make() {
    return new Builder();
  }
  
  private final String scheme;
  private final String b64token;
  private final ImmutableMap<String,String> params;
  private final ImmutableSet<String> quoted;
    
  public Authentication(String scheme) {
    this(scheme,null);
  }
  
  public Authentication(String scheme, String b64token) {
    checkNotNull(scheme);
    this.scheme = scheme.toLowerCase(Locale.US);
    this.b64token = b64token;
    this.params = ImmutableMap.<String,String>of();
    this.quoted = ImmutableSet.<String>of();
  }
  
  Authentication(Builder builder) {
    this.scheme = builder.scheme;
    this.b64token = builder.b64token;
    this.params = builder.params.build();
    this.quoted = builder.quoted.build();
  }
  
  public String getScheme() {
    return scheme;
  }
  
  public String getBase64Token() {
    return b64token;
  }
  
  public String getParam(String name) {
    return params.get(name);
  }
  
  private static boolean is_always_quoted(String name) {
    return name != null ? ALWAYS.contains(name) : false;
  }
  
  public boolean hasParam(String name) {
    return params.containsKey(name);
  }
  
  public Iterator<String> iterator() {
    return params.keySet().iterator();
  }
  
  private boolean isquoted(String param) {
    return quoted.contains(param);
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder(scheme);
    if (b64token != null || params.size() > 0)
      buf.append(' ');
    if (b64token != null)
      buf.append(b64token);
    else {
      boolean first = true;
      for (String param : this) {
        first = appendcomma(first,buf);
        String val = getParam(param);
        buf.append(param);
        boolean always = is_always_quoted(param) || isquoted(param);
        if (Profile.TOKEN.check(val) && !always)
          buf.append('*')
             .append('=')
             .append(Codec.encode(val,Codec.STAR));
        else
          buf.append('=')
             .append(always?quoted(val,true):quotedIfNotToken(val));
      }
    }
    return buf.toString();
  }

  @Override
  public int hashCode() {
    return MoreFunctions.genHashCode(1,b64token,params,scheme.toLowerCase());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Authentication other = (Authentication) obj;
    if (b64token == null) {
      if (other.b64token != null)
        return false;
    } else if (!b64token.equals(other.b64token))
      return false;
    if (params == null) {
      if (other.params != null)
        return false;
    } else if (!params.equals(other.params))
      return false;
    if (scheme == null) {
      if (other.scheme != null)
        return false;
    } else if (!scheme.equalsIgnoreCase(other.scheme))
      return false;
    return true;
  }
  
  public static Authentication basic(String userid, String password) {
    return new Authentication(
      BASIC, 
      StringUtils.newStringUsAscii(
        Base64.encodeBase64(
          bytes(userid,":",password))));
  }
  
  private static byte[] bytes(String val, String... vals) {
    if (val == null) return new byte[0];
    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();
    try {
      out.write(StringUtils.getBytesUtf8(val));
      for (String v : vals)
        out.write(StringUtils.getBytesUtf8(v));
    } catch (Throwable t) {}
    return out.toByteArray();
  }
  
  public static String toString(Authentication auth, Authentication... auths) {
    if (auth == null) return null;
    StringBuilder buf = new StringBuilder();
    buf.append(auth.toString());
    for (Authentication a : auths)
      buf.append(", ").append(a.toString());
    return buf.toString();
  }
  
  public static String toString(Iterable<Authentication> auths) {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for (Authentication auth : auths) {
      first = appendcomma(first,buf);
      buf.append(auth.toString());
    }
    return buf.toString();
  }
}
