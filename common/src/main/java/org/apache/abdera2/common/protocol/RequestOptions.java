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
package org.apache.abdera2.common.protocol;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.activation.MimeType;

import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.selector.AbstractSelector;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.common.text.Codec;
import org.apache.abdera2.common.text.UrlEncoding;
import org.apache.abdera2.common.text.CharUtils.Profile;
import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.common.http.Authentication;
import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.WebLink;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.joda.time.DateTime;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * The RequestOptions class allows a variety of options affecting the execution of the request to be modified.
 */
public class RequestOptions extends AbstractRequest implements Request {

  public static Builder make() {
    return new Builder();
  }
  
  public static Builder make(DateTime ifModifiedSince) {
    return make().ifModifiedSince(ifModifiedSince);
  }

  public static Builder make(String ifNoneMatch) {
    return make().ifNoneMatch(ifNoneMatch);
  }

  public static Builder make(String etag, String... ifNoneMatch) {
    return make().ifNoneMatch(etag, ifNoneMatch);
  }

  public static Builder make(DateTime ifModifiedSince, String ifNoneMatch) {
    return make()
      .ifModifiedSince(ifModifiedSince)
      .ifNoneMatch(ifNoneMatch);
  }

  public static Builder make(DateTime ifModifiedSince, String etag, String... ifNoneMatch) {
    return make()
      .ifModifiedSince(ifModifiedSince)
      .ifNoneMatch(etag, ifNoneMatch);
  }

  public static Builder make(boolean no_cache) {
    return make().cacheControl(CacheControl.NOCACHE());
  }
  
  public Builder template() {
    return new Builder(this);
  }
  
  public Builder template(Selector<Map.Entry<String, Set<String>>> filter) {
    return new Builder(this,filter);
  }
  
  public static Selector<Map.Entry<String, Set<String>>> withAllHeaders() {
    return new AbstractSelector<Map.Entry<String, Set<String>>>() {
      public boolean select(Object item) {
        return true;
      }
    };
  }
  
  public static Selector<Map.Entry<String, Set<String>>> withNoHeaders() {
    return new AbstractSelector<Map.Entry<String, Set<String>>>() {
      public boolean select(Object item) {
        return false;
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  public static Selector<Map.Entry<String, Set<String>>> withHeaders(String... names) {
    final ImmutableSet<String> set = ImmutableSet.copyOf(names);
    return new AbstractSelector<Map.Entry<String, Set<String>>>() {
      public boolean select(Object item) {
        Map.Entry<String, Set<String>> entry = 
          (Entry<String, Set<String>>) item;
        return set.contains(entry.getKey());
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  public static Selector<Map.Entry<String, Set<String>>> withoutHeaders(String... names) {
    final ImmutableSet<String> set = ImmutableSet.copyOf(names);
    return new AbstractSelector<Map.Entry<String, Set<String>>>() {
      public boolean select(Object item) {
        Map.Entry<String, Set<String>> entry = 
          (Entry<String, Set<String>>) item;
        return !set.contains(entry.getKey());
      }
    };
  }
  
  
  public static class Builder implements Supplier<RequestOptions> {

    boolean revalidateAuth = false;
    boolean useChunked = false;
    boolean usePostOverride = false;
    boolean requestException4xx = false;
    boolean requestException5xx = false;
    boolean useExpectContinue = true;
    boolean useConditional = true;
    boolean followRedirects = true;
    CacheControl cacheControl = null;
    int waitForContinue = -1;
    
    final Map<String, ImmutableSet.Builder<String>> headers = 
      Maps.newHashMap();
    
    public Builder() {}
    
    Builder(RequestOptions template) {
      this(template,null);
    }
    
    Builder(RequestOptions template,Selector<Map.Entry<String, Set<String>>> filter) {
      this.revalidateAuth = template.revalidateAuth;
      this.followRedirects = template.followRedirects;
      this.cacheControl = template.cacheControl;
      this.useChunked = template.useChunked;
      this.usePostOverride = template.usePostOverride;
      this.requestException4xx = template.requestException4xx;
      this.requestException5xx = template.requestException5xx;
      this.useExpectContinue = template.useExpectContinue;
      this.useConditional = template.useConditional;
      this.waitForContinue = template.waitForContinue;
      for (Map.Entry<String, Set<String>> header : template.headers.entrySet()) {
        if (filter == null || filter.apply(header)) {
          ImmutableSet.Builder<String> builder = ImmutableSet.builder();
          builder.addAll(header.getValue());
          this.headers.put(header.getKey(),builder);
        }
      }
    }
    
    public Builder revalidateAuth() {
      this.revalidateAuth = true;
      return this;
    }
    
    public Builder useChunked() {
      this.useChunked = true;
      return this;
    }
    
    public Builder usePostOverride() {
      this.usePostOverride = true;
      return this;
    }
    
    public Builder requestException4xx() {
      this.requestException4xx = true;
      return this;
    }
    
    public Builder requestException5xx() {
      this.requestException5xx = true;
      return this;
    }
    
    public Builder doNotUseExpectContinue() {
      this.useExpectContinue = false;
      return this;
    }
    
    public Builder waitForContinue(int millis) {
      this.waitForContinue = millis;
      return this;
    }
    
    public Builder doNotFollowRedirects() {
      this.followRedirects = false;
      return this;
    }
    
    public Builder contentType(String value) {
      return header("Content-Type", value);
    }

    public Builder contentType(MimeType value) {
      return header("Content-Type", value.toString());
    }
    
    public Builder contentLocation(String iri) {
      return header("Content-Location", iri);
    }

    public Builder setAuthorization(String auth) {
      return header("Authorization", auth);
    }

    public Builder setAuthorization(Authentication auth) {
      return header("Authorization",auth.toString());
    }
    
    public Builder encodedHeader(String header, String charset, String value) {
      return header(header, Codec.encode(value, charset));
    }

    public Builder encodedHeader(String header, String charset, String... values) {
      if (values != null && values.length > 0) {
        ImmutableSet.Builder<String> vals = headers.get(header);
        if (vals == null) {
          vals = ImmutableSet.builder();
          headers.put(header, vals);
        }
        for (String value : values) 
          vals.add(Codec.encode(value,charset));
      }
      return this;
    }

    public Builder header(String header, String value) {
      if (value != null)
        header(header, MoreFunctions.array(value));
      return this;
    }

    public Builder header(String header, String... values) {
      if (values != null && values.length > 0) {
        ImmutableSet.Builder<String> vals = headers.get(header);
        if (vals == null) {
          vals = ImmutableSet.builder();
          headers.put(header, vals);
        }
        vals.add(combine(values));
      }
      return this;
    }

    public Builder dateHeader(
      String header, 
      DateTime value) {
      if (value != null)
        header(header, DateUtils.formatDate(value.toDate()));
      return this;
    }

    private String combine(String... values) {
      StringBuilder v = new StringBuilder();
      for (String val : values) {
          if (v.length() > 0)
              v.append(", ");
          v.append(val);
      }
      return v.toString();
    }

    public Builder ifMatch(EntityTag entity_tag) {
        return header("If-Match", entity_tag.toString());
    }

    /**
     * Sets the value of the HTTP If-Match header
     */
    public Builder ifMatch(EntityTag tag, EntityTag... entity_tags) {
        return header("If-Match", EntityTag.toString(tag,entity_tags));
    }

    /**
     * Sets the value of the HTTP If-Match header
     */
    public Builder ifMatch(String etag, String... entity_tags) {
        return header("If-Match", EntityTag.toString(etag, entity_tags));
    }

    /**
     * Sets the value of the HTTP If-None-Match header
     */
    public Builder ifNoneMatch(String entity_tag) {
        return ifNoneMatch(new EntityTag(entity_tag));
    }

    /**
     * Sets the value of the HTTP If-None-Match header
     */
    public Builder ifNoneMatch(EntityTag entity_tag) {
        return header("If-None-Match", entity_tag.toString());
    }

    /**
     * Sets the value of the HTTP If-None-Match header
     */
    public Builder ifNoneMatch(EntityTag etag, EntityTag... entity_tags) {
        return header("If-None-Match", EntityTag.toString(etag, entity_tags));
    }

    /**
     * Sets the value of the HTTP If-None-Match header
     */
    public Builder ifNoneMatch(String etag, String... entity_tags) {
        return header("If-None-Match", EntityTag.toString(etag, entity_tags));
    }

    /**
     * Sets the value of the HTTP If-Modified-Since header
     */
    public Builder ifModifiedSince(DateTime date) {
        return dateHeader("If-Modified-Since", date);
    }
    
    public Builder ifModifiedSinceNow() {
      return ifModifiedSince(DateTimes.now());
    }

    /**
     * Sets the value of the HTTP If-Unmodified-Since header
     */
    public Builder ifUnmodifiedSince(DateTime date) {
        return dateHeader("If-Unmodified-Since", date);
    }

    /**
     * Sets the value of the HTTP Accept header
     */
    public Builder accept(String accept) {
      return accept(new String[] {accept});
    }

    /**
     * Sets the value of the HTTP Accept header
     */
    public Builder accept(String... accept) {
      return header("Accept", combine(accept));
    }

    public Builder acceptLanguage(Locale locale) {
      return acceptLanguage(Lang.fromLocale(locale));
    }

    public Builder acceptLanguage(Locale... locales) {
      String[] langs = new String[locales.length];
      for (int n = 0; n < locales.length; n++)
          langs[n] = Lang.fromLocale(locales[n]);
      acceptLanguage(langs);
      return this;
    }

    /**
     * Sets the value of the HTTP Accept-Language header
     */
    public Builder acceptLanguage(String accept) {
        return acceptLanguage(new String[] {accept});
    }

    /**
     * Sets the value of the HTTP Accept-Language header
     */
    public Builder acceptLanguage(String... accept) {
      return header("Accept-Language", combine(accept));
    }

    /**
     * Sets the value of the HTTP Accept-Charset header
     */
    public Builder acceptCharset(String accept) {
      return acceptCharset(new String[] {accept});
    }

    /**
     * Sets the value of the HTTP Accept-Charset header
     */
    public Builder acceptCharset(String... accept) {
        return header("Accept-Charset", combine(accept));
    }

    /**
     * Sets the value of the HTTP Accept-Encoding header
     */
    public Builder acceptEncoding(String accept) {
        return acceptEncoding(new String[] {accept});
    }

    /**
     * Sets the value of the HTTP Accept-Encoding header
     */
    public Builder acceptEncoding(String... accept) {
        return header("Accept-Encoding", combine(accept));
    }

    /**
     * Sets the value of the Atom Publishing Protocol Slug header
     */
    public Builder slug(String slug) {
        if (slug.indexOf((char)10) > -1 || slug.indexOf((char)13) > -1)
            throw new IllegalArgumentException(Localizer.get("SLUG.BAD.CHARACTERS"));
        return header("Slug", UrlEncoding.encode(slug, Profile.PATHNODELIMS));
    }

    public Builder cacheControl(String cc) {
      this.cacheControl = CacheControl.parse(cc);
      return this;
    }

    public Builder cacheControl(CacheControl cc) {
      this.cacheControl = cc;
      return this;
    }
    
    public Builder ifMatch(String entity_tag) {
        return ifMatch(new EntityTag(entity_tag));
    }    
    
    public Builder webLinks(WebLink weblink, WebLink... links) {
      header("Link", WebLink.toString(weblink,links));
      return this;
    }
    
    public Builder prefer(Preference pref, Preference... prefs) {
      header("Prefer", Preference.toString(pref, prefs));
      return this;
    }
    
    public RequestOptions get() {
      ImmutableMap.Builder<String,Set<String>> actuals =
        ImmutableMap.builder();
      for (Map.Entry<String, ImmutableSet.Builder<String>> header : headers.entrySet())
        actuals.put(header.getKey(), header.getValue().build());
      return new RequestOptions(this,actuals.build());
    }
    
  }
  
    final boolean revalidateAuth;
    final boolean useChunked;
    final boolean usePostOverride;
    final boolean requestException4xx;
    final boolean requestException5xx;
    final boolean useExpectContinue;
    final boolean useConditional;
    final boolean followRedirects;
    final CacheControl cacheControl;
    final ImmutableMap<String, Set<String>> headers;
    final int waitForContinue;

    RequestOptions(Builder builder,ImmutableMap<String,Set<String>> headers) {
      this.revalidateAuth = builder.revalidateAuth;
      this.useChunked = builder.useChunked;
      this.usePostOverride = builder.usePostOverride;
      this.requestException4xx = builder.requestException4xx;
      this.requestException5xx = builder.requestException5xx;
      this.useConditional = builder.useConditional;
      this.useExpectContinue = builder.useExpectContinue;
      this.followRedirects = builder.followRedirects;
      this.cacheControl = builder.cacheControl;
      this.headers = headers;
      this.waitForContinue = builder.waitForContinue;
    }

    private Map<String, Set<String>> getHeaders() {
        return headers;
    }

    /**
     * Returns the text value of the specified header
     */
    public String getHeader(String header) {
        Set<String> list = getHeaders().get(header);
        return list.size() > 0 ? list.iterator().next() : null;
    }

    /**
     * Return a listing of text values for the specified header
     */
    public Iterable<Object> getHeaders(String header) {
      return ImmutableSet.<Object>copyOf(getHeaders().get(header));
    }

    /**
     * Returns the date value of the specified header
     */
    public DateTime getDateHeader(String header) {
      String val = getHeader(header);
      try {
          return (val != null) ? new DateTime(DateUtils.parseDate(val)) : null;
      } catch (DateParseException e) {
        throw ExceptionHelper.propogate(e);
      }
    }

    /**
     * Returns a listing of header names
     */
    public Iterable<String> getHeaderNames() {
      return getHeaders().keySet();
    }

    /**
     * Return the value of the Cache-Control header
     */
    public CacheControl getCacheControl() {
        return cacheControl;
    }

    /**
     * Configure the AbderaClient Side cache to revalidate when using Authorization
     */
    public boolean getRevalidateWithAuth() {
        return revalidateAuth;
    }

    /**
     * Should the request use chunked encoding?
     */
    public boolean isUseChunked() {
        return useChunked;
    }

    /**
     * Return whether the request should use the X-HTTP-Method-Override option
     */
    public boolean isUsePostOverride() {
        return this.usePostOverride;
    }

    /**
     * Return true if a RequestException should be thrown on 4xx responses
     */
    public boolean is4xxRequestException() {
        return this.requestException4xx;
    }

    /**
     * Return true if a RequestException should be thrown on 5xx responses
     */
    public boolean is5xxRequestException() {
        return this.requestException5xx;
    }

    /**
     * Return true if Expect-Continue should be used
     */
    public boolean isUseExpectContinue() {
        return this.useExpectContinue;
    }

    /**
     * True if HTTP Conditional Requests should be used automatically. This only has an effect when putting a Document
     * that has an ETag or Last-Modified date present
     */
    public boolean isConditionalPut() {
        return this.useConditional;
    }

    /**
     * True if the client should follow redirects automatically
     */
    public boolean isFollowRedirects() {
        return followRedirects;
    }
    
    public boolean has(String header) {
      return headers.containsKey(header);
    }
    
    public int getWaitForContinue() {
      return waitForContinue;
    }
}
