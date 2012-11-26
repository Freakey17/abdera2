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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Iterators;

import static java.lang.String.format;
import static org.apache.abdera2.common.text.CharUtils.*;

/**
 * Provides parsing and properly handling of the HTTP Cache-Control header.
 */
public final class CacheControlUtil {

    private CacheControlUtil() {}
  
    static long value(String val) {
        return (val != null) ? Long.parseLong(val) : -1;
    }
    
    /**
     * Construct the Cache-Control header from info in the request object
     */
    public static String buildCacheControl(CacheControl cc) {
        StringBuilder buf = new StringBuilder();
        appendwithsepif(cc.isPrivate(), buf, "private");
        appendif(cc.isPrivate(), buf, cc.getPrivateHeaders());
        appendwithsepif(cc.isPublic(), buf, "public");
        appendwithsepif(cc.isNoCache(), buf, "no-cache");
        appendif(cc.isNoCache(), buf, cc.getNoCacheHeaders());  
        appendwithsepif(cc.isNoStore(), buf, "no-store");
        appendwithsepif(cc.isNoTransform(), buf, "no-transform");
        appendwithsepif(cc.isOnlyIfCached(), buf, "only-if-cached");
        appendwithsepif(cc.isMustRevalidate(), buf, "must-revalidate");
        appendwithsepif(cc.isProxyRevalidate(), buf, "proxy-revalidate");
        appendwithsepif(cc.getMaxAge() != -1, buf, "max-age=%d", cc.getMaxAge());
        appendwithsepif (cc.getMaxStale() != -1, buf, "max-stale=%d", cc.getMaxStale());
        appendwithsepif (cc.getMinFresh() != -1, buf, "min-fresh=%d", cc.getMinFresh());
        appendwithsepif (cc.getStaleIfError() != -1, buf, "stale-if-error=%d", cc.getStaleIfError());
        appendwithsepif (cc.getStaleWhileRevalidate() != -1, buf, "stale-while-revalidate=%d", cc.getStaleWhileRevalidate());
        for (String ext : cc.listExtensions()) {
          append(buf, ext);
          Object val = cc.getExtension(ext); 
          if (val != null) {
            if (Number.class.isAssignableFrom(val.getClass()))
              buf.append('=').append(val);            
            else {
              String v = val.toString();
              if (v.length() > 0)
                buf.append(
                  format("=%s",quotedIfNotToken(v)));
            }
          }
        }
        return buf.toString();
    }

    /**
     * Parse the Cache-Control header
     */
    public static CacheControl.Builder parseCacheControl(String cc, CacheControl.Builder builder) {
        if (cc == null) return builder;
        builder.defaults();
        CacheControlParser parser = new CacheControlParser(cc);
        for (Directive directive : parser)
          directive.set(builder, parser);
        builder.extensions(parser.getExtensions());
        return builder;
    }

    /**
     * Cache Control Directives
     */
    public enum Directive {
        MAXAGE,
        MAXSTALE, 
        MINFRESH, 
        NOCACHE, 
        NOSTORE, 
        NOTRANSFORM, 
        ONLYIFCACHED, 
        MUSTREVALIDATE, 
        PROXYREVALIDATE,
        PRIVATE,
        PUBLIC, 
        STALEWHILEREVALIDATE,
        STALEIFERROR,
        UNKNOWN;

        public static Directive select(String d) {
            try {
                d = d.toUpperCase().replaceAll("-", "");
                return Directive.valueOf(d);
            } catch (Exception e) {
            }
            return UNKNOWN;
        }
        
        public void set(CacheControl.Builder builder, CacheControlParser parser) {
          switch (this) {
          case NOCACHE:
              builder.noCache();
              builder.noCacheHeaders(parser.getValues(this));
              break;
          case NOSTORE:
              builder.noStore();
              break;
          case NOTRANSFORM:
              builder.noTransform();
              break;
          case ONLYIFCACHED:
              builder.onlyIfCached();
              break;
          case MAXAGE:
              builder.maxAge(value(parser.getValue(this)));
              break;
          case MAXSTALE:
              builder.maxStale(value(parser.getValue(this)));
              break;
          case MINFRESH:
              builder.minFresh(value(parser.getValue(this)));
              break;
          case STALEIFERROR:
              builder.staleIfError(value(parser.getValue(this)));
              break;
          case MUSTREVALIDATE:
              builder.mustRevalidate();
              break;
          case PROXYREVALIDATE:
              builder.proxyRevalidate();
              break;
          case PUBLIC:
              builder.isPublic();
              break;
          case PRIVATE:
              builder.isPrivate();
              builder.privateHeaders(parser.getValues(this));
            break;
          case STALEWHILEREVALIDATE:
            builder.staleWhileRevalidate(value(parser.getValue(this)));
            break;
          }
        }
    }

    /**
     * Parser for the Cache-Control header
     */
    public static class CacheControlParser 
      implements Iterable<Directive> {

        private static final String REGEX =
            "\\s*([\\w\\-]+)\\s*(=)?\\s*(\\d+|\\\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)+\\\"|[^,]+)?\\s*";

        private static final Pattern pattern = Pattern.compile(REGEX);

        private final Map<Directive, String> values = 
          new LinkedHashMap<Directive, String>();
        private final Map<String,Object> exts = 
          new LinkedHashMap<String,Object>();

        public CacheControlParser(String value) {
            Matcher matcher = pattern.matcher(value);
            while (matcher.find()) {
                String d = matcher.group(1);
                Directive directive = Directive.select(d);
                if (directive != Directive.UNKNOWN) {
                    values.put(directive, matcher.group(3));
                } else {
                  String val = matcher.group(3);
                  try {
                    Long l = Long.parseLong(val);
                    exts.put(d, l);
                  } catch (Throwable t) {
                    exts.put(d, unquote(val!=null?val:""));
                  }
                }
            }
        }

        public Map<String,Object> getExtensions() {
          return Collections.unmodifiableMap(exts);
        }
        
        public Map<Directive, String> getValues() {
          return Collections.unmodifiableMap(values);
        }

        public String getValue(Directive directive) {
          return values.get(directive);
        }

        public Iterator<Directive> iterator() {
          return Iterators.unmodifiableIterator(values.keySet().iterator());
        }

        public String[] getValues(Directive directive) {
          String value = getValue(directive);
          return value == null ?
            null :
            splitAndTrim(value);
        }

    }
}
