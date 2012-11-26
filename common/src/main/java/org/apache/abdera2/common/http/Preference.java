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

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.text.CharUtils;

import static org.apache.abdera2.common.text.CharUtils.appendcomma;
import static org.apache.abdera2.common.text.CharUtils.quotedIfNotToken;
import org.apache.abdera2.common.text.Codec;
import org.apache.abdera2.common.text.CharUtils.Profile;
import static com.google.common.base.Preconditions.*;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Implementation of the Prefer HTTP Header, e.g.
 * 
 * Prefer: return-no-content, my-preference=abc;xyz=123
 */
public class Preference implements Serializable {
  
  public static final String RETURN_MINIMAL = "return-minimal";
  public static final String RETURN_ASYNCH = "return-asynch";
  public static final String RETURN_REPRESENTATION = "return-representation";
  public static final String WAIT = "wait";
  public static final String STRICT = "strict";
  public static final String LENIENT = "lenient";
  
  public static final Preference PREF_STRICT = new Preference(STRICT);
  
  public static final Preference PREF_LENIENT = new Preference(LENIENT);
  
  /** 
   * The "return-no-content" token indicates that the client prefers that
   * the server not include an entity in the response to a successful
   * request.  Typically, such responses would use the 204 No Content
   * status code as defined in Section 10.2.5 of [RFC2616], but other
   * status codes can be used as appropriate.
   */
  public static final Preference PREF_RETURN_MINIMAL = 
    new Preference(RETURN_MINIMAL);
  
  /**
   * The "return-accepted" token indicates that the client prefers that
   * the server respond with a 202 Accepted response indicating that the
   * request has been accepted for processing.
   */
  public static final Preference PREF_RETURN_ASYNCH =
    new Preference(RETURN_ASYNCH);
  
  /**
   * The "return-content" token indicates that the client prefers that the
   * server include an entity representing the current state of the
   * resource in the response to a successful request.
   */
  public static final Preference PREF_RETURN_REPRESENTATION =
    new Preference(RETURN_REPRESENTATION);
  
  public static Preference WAIT(long seconds) {
    return 
      make()
        .token(WAIT)
        .value(seconds)
     .get();
  }
  
  public static Builder make() {
    return new Builder();
  }
  
  public static Builder make(String token) {
    return make().token(token.toLowerCase(Locale.US));
  }
  
  public static Builder make(String token, String value) {
    return make(token).value(value);
  }

  public static Builder make(String token, int value) {
    return make(token).value(value);
  }
  
  public static Builder make(String token, long value) {
    return make(token).value(value);
  }
  
  public static Builder make(String token, short value) {
    return make(token).value(value);
  }
  
  public static Builder make(String token, boolean value) {
    return make(token).value(value);
  }
  
  public static class Builder implements Supplier<Preference> {

    String token;
    String value;
    final ImmutableMap.Builder<String,String> params = 
      ImmutableMap.builder();
    
    public Preference get() {
      return new Preference(this);
    }
    
    public Builder token(String token) {
      this.token = token;
      return this;
    }
    
    public Builder value(String value) {
      this.value = value;
      return this;
    }
    
    public Builder value(long value) {
      this.value = Long.toString(value);
      return this;
    }
    
    public Builder value(int value) {
      this.value = Integer.toString(value);
      return this;
    }
    
    public Builder value(short value) {
      this.value = Short.toString(value);
      return this;
    }
    
    public Builder value(boolean value) {
      this.value = Boolean.toString(value);
      return this;
    }
    
    public Builder param(String key) {
      return param(key,"");
    }
    
    public Builder param(String key, String val) {
      checkNotNull(key);
      key = key.toLowerCase(Locale.US);
      checkArgument(!reserved(key));
      this.params.put(key,val);
      return this;
    }
    
    public Builder params(Map<String,String> params) {
      checkNotNull(params);
      for (Map.Entry<String,String> entry : params.entrySet()) {
        String name = entry.getKey().toLowerCase(Locale.US);
        checkArgument(!reserved(name));
        this.params.put(name,entry.getValue());
      }
      return this;
    }
    
  }
  
  private static final long serialVersionUID = -6238673046322517740L;
  private final String token;
  private final String value;
  private final ImmutableMap<String,String> params;
  
  Preference(Builder builder) {
    this.token = builder.token;
    this.value = builder.value;
    this.params = builder.params.build();
  }
  
  public Preference(String token) {
    this(token,null);
  }
  
  public Preference(String token, String value) {
    Profile.TOKEN.verify(token);
    this.token = token.toLowerCase(Locale.US);
    this.value = value;
    this.params = ImmutableMap.<String,String>of();
  }
  
  public String getToken() {
    return token;
  }
  
  public String getValue() {
    return value;
  }
  
  public long getLongValue() {
    return value != null ? Long.parseLong(value) : -1;
  }
  
  public int getIntValue() {
    return value != null ? Integer.parseInt(value) : -1;
  }
  
  public short getShortValue() {
    return value != null ? Short.parseShort(value) : -1;
  }
  
  public boolean getBooleanValue() {
    return value != null ? Boolean.parseBoolean(value) : false;
  }
  
  static final Set<String> reserved = 
    ImmutableSet.<String>of(); // no reserved yet
  
  static boolean reserved(String name) {
    return reserved.contains(name);
  }
  
  public boolean matches(String token) {
    if (token == null) return false;
    return this.token.equalsIgnoreCase(token.toLowerCase());
  }
  
  @Override
  public int hashCode() {
    return MoreFunctions.genHashCode(
      1, params, token, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Preference other = (Preference) obj;
    if (params == null) {
      if (other.params != null)
        return false;
    } else if (!params.equals(other.params))
      return false;
    if (token == null) {
      if (other.token != null)
        return false;
    } else if (!token.equals(other.token))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  public boolean hasParam(String name) {
    checkNotNull(name);
    name = name.toLowerCase(Locale.US);
    return params.containsKey(name);
  }
  
  public int getIntParam(String name) {
    String val = getParam(name);
    return val != null ? Integer.parseInt(val) : -1;
  }
  
  public long getLongParam(String name) {
    String val = getParam(name);
    return val != null ? Long.parseLong(val) : -1;
  }
  
  public short getShortParam(String name) {
    String val = getParam(name);
    return val != null ? Short.parseShort(val) : -1;
  }
  
  public boolean getBooleanParam(String name) {
    String val = getParam(name);
    return val != null ? Boolean.parseBoolean(val) : false;
  }
  
  public String getParam(String name) {
    checkNotNull(name);
    name = name.toLowerCase(Locale.US);
    return params.get(name);
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(token);
    
    if (value != null && value.length() > 0) {
      String encval = Codec.encode(value, Codec.STAR);
      if (value.equals(encval)) {
        buf.append('=')
           .append(quotedIfNotToken(value));
      } else {
        buf.append('*')
           .append('=')
           .append(encval);
      }
    }

   for (Map.Entry<String, String> entry : params.entrySet()) {
     String val = entry.getValue();
     String encval = val != null ? Codec.encode(val,Codec.STAR) : null;
     buf.append(';')
        .append(entry.getKey());
     if (val != null && val.length() > 0) {
       if (!val.equals(encval)) {
         buf.append('*')
            .append('=')
            .append(encval);
       } else {
         buf.append('=')
            .append(quotedIfNotToken(val));
       }
     }
   }
    
    return buf.toString();
  }
  
  private final static String TOKEN = "[\\!\\#\\$\\%\\&\\'\\*\\+\\-\\.\\^\\_\\`\\|\\~a-zA-Z0-9]+";
  private final static String PREF = TOKEN+"(?:\\s*=\\s*(?:(?:\"(?:(?:\\Q\\\"\\E)|[^\"])*\")|(?:"+TOKEN+"))?)?";
  private final static String PARAMS = "(?:\\s*;\\s*" + PREF + ")*";
  private final static String PATTERN = "("+PREF+")(" + PARAMS + ")";

  private final static Pattern pattern = 
    Pattern.compile(PATTERN);
  private final static Pattern param = 
    Pattern.compile("("+PREF+")");
  
  public static Iterable<Preference> parse(String text) {
      ImmutableList.Builder<Preference> prefs = ImmutableList.builder();
      Matcher matcher = pattern.matcher(text);
      while (matcher.find()) {
        String pref = matcher.group(1);
        String params = matcher.group(2);
        String token = null, tokenval = null;
        if (pref != null) {
          String[] ps = pref.split("\\s*\\*?=\\s*", 2);
          token = ps[0].trim();
          if (ps.length == 2)
            tokenval = Codec.decode(CharUtils.unescape(CharUtils.unquote(ps[1])));
        }
        
        Preference.Builder maker = 
          Preference.make().token(token).value(tokenval);   
        if (params != null) {
          Matcher mparams = param.matcher(params);
          while(mparams.find()) {
            String p = mparams.group(1);
            String[] ps = p.split("\\s*\\*?=\\s*", 2);
            if (ps.length == 2)
              maker.param(ps[0], Codec.decode(CharUtils.unescape(CharUtils.unquote(ps[1]))));
            else maker.param(ps[0]);
          }
        }
        prefs.add(maker.get());
      }
      return prefs.build();
  }
  
  public static String toString(
    Preference preference, 
    Preference... preferences) {
    if (preference == null)
      return null;
    StringBuilder buf = new StringBuilder();
    buf.append(preference.toString());
    for (Preference pref : preferences) {
      buf.append(',').append(pref.toString());
    }
    return buf.toString();
  }
  
  public static String toString(Iterable<Preference> preferences) {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for (Preference pref : preferences) {
      first = appendcomma(first,buf);
      buf.append(pref.toString());
    }
    return buf.toString();
  }
  
  /**
   * Utility method that checks to see if the given token is included
   * in the collection of preferences.
   */
  public static boolean contains(
    Iterable<Preference> preferences, 
    String token) {
    for (Preference pref : preferences)
      if (pref.matches(token))
        return true;
    return false;
  }
  
  public static Iterable<Preference> concat(Preference pref, Preference... prefs) {
    return ImmutableSet
      .<Preference>builder()
      .add(pref)
      .add(prefs)
      .build();
  }
  
  /**
   * Utility method that checks to see if the given token is included
   * in the collection of preference... this ignores the parameters
   * and looks only at the preference token
   */
  public static boolean contains(
    Iterable<Preference> preferences, 
    Preference preference) {
      return get(preferences,preference.getToken()) != null;
  }
  
  public static Preference get(Iterable<Preference> preferences, String token) {
    for (Preference pref : preferences)
      if (pref.matches(token))
        return pref;
    return null;
  }
}
