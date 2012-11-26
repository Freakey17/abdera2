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

import org.apache.abdera2.common.misc.MoreFunctions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

public final class CacheControl implements Serializable {

  static final Set<String> reserved = 
    ImmutableSet.of(
      "private","public","no-cache","no-store","no-transform","only-if-cached",
      "must-revalidate", "proxy-revalidate", "max-age", "max-stale", "min-fresh",
      "stale-if-error", "stale-while-revalidate");
  
  public static Builder make() {
    return new Builder();
  }
  
  public static Builder make(CacheControl template) {
    return new Builder(template);
  }
  
  public static class Builder implements Supplier<CacheControl> {

    protected int flags = 0;
    protected Iterable<String>
      nocache_headers,
      private_headers;
    protected long max_age = -1,
                   max_stale = -1,
                   min_fresh = -1,
                   smax_age = -1,
                   staleiferror = -1,
                   stalewhilerevalidate = -1;
    protected final ImmutableMap.Builder<String,Object> exts =
      ImmutableMap.builder();
    
    public Builder() {
      defaults();
    }
    
    public Builder(CacheControl cc) {
      from(cc);
    }
    
    public Builder from(CacheControl cc) {
      this.flags = cc.flags;
      this.nocache_headers = cc.nocache_headers;
      this.private_headers = cc.private_headers;
      this.max_age = cc.max_age;
      this.max_stale = cc.max_stale;
      this.min_fresh = cc.min_fresh;
      this.smax_age = cc.smax_age;
      this.staleiferror = cc.staleiferror;
      this.stalewhilerevalidate = cc.stalewhilerevalidate;
      this.exts.putAll(cc.exts);
      return this;
    }
    
    public Builder defaults() {
      return 
         noCache(false)
        .noStore(false)
        .noTransform(false)
        .onlyIfCached(false)
        .maxAge(-1)
        .maxStale(-1)
        .minFresh(-1)
        .staleIfError(-1)
        .staleWhileRevalidate(-1)
        .mustRevalidate(false)
        .isPrivate(false)
        .isPublic(false)
        .maxAge(-1);
    }
    
    public CacheControl get() {
      return new CacheControl(this);
    }
    
    private void checkReserved(String name) {
      checkNotNull(name);
      checkArgument(!reserved.contains(name.toLowerCase()), "Reserved directive", name);
    }
    
    public Builder extensions(Map<String,Object> exts) {
      for (Map.Entry<String, Object> entry : exts.entrySet()) {
        String name = entry.getKey().toLowerCase(Locale.US);
        checkReserved(name);
        this.exts.put(
          name, 
          entry.getValue());
      }
      return this;
    }
    
    public Builder extension(String name, Object value) {
      checkReserved(name);
      exts.put(name.toLowerCase(Locale.US),value);
      return this;
    }
    
    public Builder extension(String name) {
      checkReserved(name);
      exts.put(name.toLowerCase(Locale.US),"");
      return this;
    }
    
    public Builder staleIfError(long delta) {
      this.staleiferror = Math.max(-1,delta);
      return this;
    }
    
    public Builder staleWhileRevalidate(long delta) {
      this.stalewhilerevalidate = Math.max(-1,delta);
      return this;
    }
    
    public Builder maxAge(long max_age) {
      this.max_age = Math.max(-1,max_age);
      return this;
    }

    public Builder mustRevalidate() {
      toggle(true,REVALIDATE);
      return this;
    }
    
    public Builder mustRevalidate(boolean val) {
      toggle(val,REVALIDATE);
      return this;
    }

    public Builder proxyRevalidate() {
      toggle(true,PROXYREVALIDATE);
      return this;
    }
    
    public Builder proxyRevalidate(boolean val) {
      toggle(val,PROXYREVALIDATE);
      return this;
    }

    public Builder noCache() {
      toggle(true,NOCACHE);
      return this;
    }
    
    public Builder noCache(boolean val) {
      toggle(val,NOCACHE);
      return this;
    }

    public Builder noStore() {
      toggle(true,NOSTORE);
      return this;
    }
    
    public Builder noStore(boolean val) {
      toggle(val,NOSTORE);
      return this;
    }

    public Builder noTransform() {
      toggle(true,NOTRANSFORM);
      return this;
    }
    
    public Builder noTransform(boolean val) {
      toggle(val,NOTRANSFORM);
      return this;
    }
    
    public Builder isPublic() {
      toggle(true,PUBLIC);
      return this;
    }
    
    public Builder isPublic(boolean val) {
      toggle(val,PUBLIC);
      return this;
    }

    public Builder isPrivate() {
      toggle(true,PRIVATE);
      return this;
    }
    
    public Builder isPrivate(boolean val) {
      toggle(val,PRIVATE);
      return this;
    }

    public Builder privateHeaders(String... headers) {
      if (headers == null) return this;
      this.private_headers = ImmutableSet.copyOf(headers);
      if (headers.length > 0) isPrivate();
      return this;
    }

    public Builder noCacheHeaders(String... headers) {
      if (headers == null) return this;
      this.nocache_headers = ImmutableSet.copyOf(headers);
      if (headers.length > 0) noCache();
      return this;
    }

    public Builder maxStale(long max_stale) {
      this.max_stale = max_stale;
      return this;
    }

    public Builder minFresh(long min_fresh) {
      this.min_fresh = min_fresh;
      return this;
    }
    
    public Builder onlyIfCached() {
      toggle(true,ONLYIFCACHED);
      return this;
    }
    
    public Builder onlyIfCached(boolean val) {
      toggle(val,ONLYIFCACHED);
      return this;
    }
    
    private void toggle(boolean val, int flag) {
      if (val) flags |= flag;
      else flags &= ~flag;
    }
  
  }
  
  public static final Function<String,CacheControl> parser = 
    new Function<String,CacheControl>() {
      public CacheControl apply(String input) {
        return input != null ? parse(input) : null;
      }
  };
  
  public static CacheControl parse(String cc) {
    return CacheControlUtil.parseCacheControl(cc, make()).get();
  }
  
  private static final long serialVersionUID = 3554586802963893228L;
  public final static int NOCACHE = 1,
                          NOSTORE = 2,
                          NOTRANSFORM = 4,
                          PUBLIC = 8,
                          PRIVATE = 16,
                          REVALIDATE = 32,
                          PROXYREVALIDATE = 64,
                          ONLYIFCACHED = 128;
  final int flags;
  final Iterable<String> 
    nocache_headers,
    private_headers;
  final long max_age,
                 max_stale,
                 min_fresh,
                 smax_age,
                 staleiferror,
                 stalewhilerevalidate;
  final ImmutableMap<String,Object> exts;
  
  CacheControl(Builder builder) {
    this.flags = builder.flags;
    this.nocache_headers = 
      builder.nocache_headers != null ?
        builder.nocache_headers : 
        ImmutableSet.<String>of();
    this.private_headers = 
      builder.private_headers != null ?
        builder.private_headers : 
        ImmutableSet.<String>of();
    this.max_age = builder.max_age;
    this.max_stale = builder.max_stale;
    this.min_fresh = builder.min_fresh;
    this.smax_age = builder.smax_age;
    this.staleiferror = builder.staleiferror;
    this.stalewhilerevalidate = builder.stalewhilerevalidate;
    this.exts = builder.exts.build();
  }
  
  public Object getExtension(String name) {
    return exts.get(name);
  }
  
  public Iterable<String> listExtensions() {
    return exts.keySet();
  }
  
  protected boolean check(int flag) {
    return (flags & flag) == flag;
  }
  
  public boolean isNoCache() {
    return check(NOCACHE);
  }
  
  public boolean isNoStore() {
      return check(NOSTORE);
  }
  
  public boolean isNoTransform() {
      return check(NOTRANSFORM);
  }
  
  public long getMaxAge() {
      return max_age;
  }
  
  public Iterable<String> getNoCacheHeaders() {
    return isNoCache() ? 
      nocache_headers : 
      ImmutableSet.<String>of();
  }

  public Iterable<String> getPrivateHeaders() {
    return isPrivate() ? 
      private_headers : 
      ImmutableSet.<String>of();
  }

  public long getSMaxAge() {
      return smax_age;
  }

  public boolean isMustRevalidate() {
    return check(REVALIDATE);
  }

  public boolean isPrivate() {
    return check(PRIVATE);
  }

  public boolean isProxyRevalidate() {
    return check(PROXYREVALIDATE);
  }

  public boolean isPublic() {
    return check(PUBLIC);
  }

  public long getStaleIfError() {
    return staleiferror;
  }
  
  public long getStaleWhileRevalidate() {
    return stalewhilerevalidate;
  }
    
  public boolean isOnlyIfCached() {
    return check(ONLYIFCACHED);
  }
  
  public long getMaxStale() {
    return max_stale;
  }

  public long getMinFresh() {
    return min_fresh;
  }
  
  public String toString() {
    return CacheControlUtil.buildCacheControl(this);
  }
  
  @Override
  public int hashCode() {
    return MoreFunctions.genHashCode(
      1, flags, 
      max_age,
      max_stale,
      min_fresh,
      nocache_headers,
      private_headers,
      smax_age,
      staleiferror,
      stalewhilerevalidate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CacheControl other = (CacheControl) obj;
    if (flags != other.flags)
      return false;
    if (max_age != other.max_age)
      return false;
    if (max_stale != other.max_stale)
      return false;
    if (min_fresh != other.min_fresh)
      return false;
    if (!Iterables.elementsEqual(nocache_headers, other.nocache_headers))
      return false;
    if (!Iterables.elementsEqual(private_headers, other.private_headers))
      return false;
    if (smax_age != other.smax_age)
      return false;
    if (staleiferror != other.staleiferror)
      return false;
    if (stalewhilerevalidate != other.stalewhilerevalidate)
      return false;
    return true;
  }
  
  public static CacheControl NOCACHE() {
    return make().noCache(true).get();
  }
  
  public static CacheControl NONNOCACHE() {
    return make().noCache(false).get();
  }
  
  public static CacheControl NOSTORE() {
    return make().noStore(true).get();
  }
  
  public static CacheControl NONNOSTORE() {
    return make().noStore(false).get();
  }
  
  public static CacheControl MAXAGE(long age) {
    return make().maxAge(age).get();
  }
  
  public static CacheControl PUBLIC() {
    return make().isPublic(true).get();
  }
  
  public static CacheControl PRIVATE() {
    return make().isPrivate(true).get();
  }
  
}
