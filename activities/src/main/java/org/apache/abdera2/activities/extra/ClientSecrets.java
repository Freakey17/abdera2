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
package org.apache.abdera2.activities.extra;

import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import java.util.Map;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.IO;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Basic implementation of... http://code.google.com/p/google-api-python-client/wiki/ClientSecrets
 */
public final class ClientSecrets extends ASBase {

  public static ClientSecrets read(InputStream in) {
    return read(IO.get(),in,"UTF-8");
  }
  
  public static ClientSecrets read(InputStream in, String charset) {
    return read(IO.get(),in,charset);
  }
  
  public static ClientSecrets read(IO io, InputStream in) {
    return read(io,in,"UTF-8");
  }
  
  public static ClientSecrets read(IO io, InputStream in, String charset) {
    return io.readAs(in, charset, ClientSecrets.class);
  }
  
  public static ClientSecrets read(Reader in) {
    return read(IO.get(),in);
  }

  public static ClientSecrets read(IO io, Reader in) {
    return io.readAs(in, ClientSecrets.class);
  }
  
  public static enum Type {
    INSTALLED, WEB;
    private final String ln;
    Type() {
      this.ln = name().toLowerCase(Locale.US);
    }
    public String label() {
      return ln;
    }
  }
  
  public boolean has(Type type) {
    return has(type.label());
  }
  
  public ASBase getType(Type type) {
    return has(type.label()) ? this.<ASBase>getProperty(type.label()) : null;
  }
  
  public ASBase getType(String type) {
    if (has(type)) {
      Object obj = getProperty(type);
      return obj instanceof ASBase ? (ASBase)obj : null;
    } else return null;
  }
  
  public String getClientId(Type type) {
    return this.<String>get(checkNotNull(type).label(), "client_id");
  }
  
  public String getClientSecret(Type type) {
    return this.<String>get(checkNotNull(type).label(), "client_secret");
  }
  
  public Iterable<String> getRedirectUris(Type type) {
    return this.<Iterable<String>>get(checkNotNull(type).label(), "redirect_uris");
  }
  
  public String getAuthUri(Type type) {
    return this.<String>get(checkNotNull(type).label(), "auth_uri");
  }
  
  public String getTokenUri(Type type) {
    return this.<String>get(checkNotNull(type).label(), "token_uri");
  }
  
  public <X>X getProperty(Type type, String field) {
    return this.<X>get(checkNotNull(type).label(), field);
  }
  
  public String getClientId(String type) {
    return this.<String>get(checkNotNull(type), "client_id");
  }
  
  public String getClientSecret(String type) {
    return this.<String>get(checkNotNull(type), "client_secret");
  }
  
  public Iterable<String> getRedirectUris(String type) {
    return this.<Iterable<String>>get(checkNotNull(type), "redirect_uris");
  }
  
  public String getAuthUri(String type) {
    return this.<String>get(checkNotNull(type), "auth_uri");
  }
  
  public String getTokenUri(String type) {
    return this.<String>get(checkNotNull(type), "token_uri");
  }
  
  public <X>X getProperty(String type, String field) {
    return this.<X>get(checkNotNull(type), field);
  }
  
  private <X>X get(String type, String field) {
    ASBase base = getProperty(type);
    return base != null ? base.<X>getProperty(field) : null;
  }
  
  public ClientSecrets(Map<String,Object> map) {
    super(map,Builder.class,ClientSecrets.class);
  }

  public static Builder makeClientSecrets() {
    return new Builder();
  }

  public static final class Builder extends ASBase.Builder<ClientSecrets,Builder> {
    
    private final Map<String,ASBase.ASBuilder> builders = 
      Maps.newHashMap();
    
    public Builder() {
      super(ClientSecrets.class,Builder.class);
    }
    protected Builder(Map<String,Object> map) {
      super(map,ClientSecrets.class,Builder.class);
    }

    public Builder clientId(Type type, String id) {
      field(checkNotNull(type).label(), "client_id", id);
      return this;
    }
    
    public Builder clientSecret(Type type, String secret) {
      field(checkNotNull(type).label(), "client_secret", secret);
      return this;
    }
    
    public Builder redirectUris(Type type, String... urls) {
      field(checkNotNull(type).label(), "redirect_uris", ImmutableSet.copyOf(checkNotNull(urls)));
      return this;
    }
    
    public Builder authUri(Type type, String url) {
      field(checkNotNull(type).label(), "auth_uri", url);
      return this;
    }
    
    public Builder tokenUri(Type type, String url) {
      field(checkNotNull(type).label(), "token_uri", url);
      return this;
    }
    
    public <X>Builder set(Type type, String field, Supplier<X> val) {
      field(checkNotNull(type).label(), field, val.get());
      return this;
    }
    
    public Builder set(Type type, String field, Object val) {
      field(checkNotNull(type).label(), field, val);
      return this;
    }
    
    public Builder clientId(String type, String id) {
      field(checkNotNull(type), "client_id", id);
      return this;
    }
    
    public Builder clientSecret(String type, String secret) {
      field(checkNotNull(type), "client_secret", secret);
      return this;
    }
    
    public Builder redirectUris(String type, String... urls) {
      field(checkNotNull(type), "redirect_uris", ImmutableSet.copyOf(checkNotNull(urls)));
      return this;
    }
    
    public Builder authUri(String type, String url) {
      field(checkNotNull(type), "auth_uri", url);
      return this;
    }
    
    public Builder tokenUri(String type, String url) {
      field(checkNotNull(type), "token_uri", url);
      return this;
    }
    
    public <X>Builder set(String type, String field, Supplier<X> val) {
      field(checkNotNull(type), field, val.get());
      return this;
    }
    
    public Builder set(String type, String field, Object val) {
      field(checkNotNull(type), field, val);
      return this;
    }
    
    private void field(String key, String field, String id) {
      getBuilder(key).set(field, id);
    }

    private void field(String key, String field, Object id) {
      getBuilder(key).set(field, id);
    }
    
    private ASBase.ASBuilder getBuilder(String key) {
      ASBase.ASBuilder builder = builders.get(key);
      if (builder == null) {
        builder = ASBase.make();
        builders.put(key,builder);
      }
      return builder;
    }
    
    protected void preGet() {
      for (Map.Entry<String, ASBase.ASBuilder> entry : builders.entrySet())
        set(entry.getKey().toLowerCase(), entry.getValue());
    }
    
  }
  
}
