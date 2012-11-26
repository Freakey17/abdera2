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

import javax.activation.MimeType;

import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.WebLink;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.common.text.Codec;
import org.apache.abdera2.common.text.UrlEncoding;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.*;
/**
 * Root impl for Message interface impls. This is provided solely as a way of keeping the interface and impl's
 * consistent across the Request and Response objects.
 */
public abstract class AbstractMessage 
  implements Message {

    public <T>T getHeader(String name, Function<String,T> transform) {
      checkNotNull(transform);
      return transform.apply(getHeader(name));
    }
    
    public <T>Iterable<T> getHeaders(String name, Function<String,T> transform) {
      Iterable<Object> objs = this.getHeaders(name);
      ImmutableList.Builder<T> list = ImmutableList.builder();
      for (Object obj : objs)
        list.add(transform.apply(obj.toString()));
      return list.build();
    }
  
    public CacheControl getCacheControl() {
      return getHeader("Cache-Control", CacheControl.parser);
    }

    public String getContentLanguage() {
      return getHeader("Content-Language");
    }

    public IRI getContentLocation() {
      return getHeader("Content-Location", IRI.parser);
    }

    public MimeType getContentType() {
      return getHeader("Content-Type", MimeTypeHelper.parser);
    }

    private static final Function<String,String> DEC = 
      Functions.<String,String,String>compose(
        UrlEncoding.decoder(),
        Codec.decode());
    
    public String getDecodedHeader(String header) {
      return getHeader(
        header, 
        DEC);
    }

    public Iterable<String> getDecodedHeaders(String header) {
      return getHeaders(header, DEC);
    }

    public String getSlug() {
      return getDecodedHeader("Slug");
    }

    public Iterable<WebLink> getWebLinks() {
      ImmutableList.Builder<WebLink> links = ImmutableList.builder();
      Iterable<Object> headers = this.getHeaders("Link");      
      for (Object obj : headers)
        links.addAll(
          WebLink.parse(
            obj.toString()));
      return links.build();
    }
    
    public Iterable<Preference> getPrefer() {
      ImmutableList.Builder<Preference> links = ImmutableList.builder();
      Iterable<Object> headers = this.getHeaders("Prefer");
      for (Object obj : headers)
        links.addAll(
          Preference.parse(
             obj.toString()));
      return links.build();
    }

}
