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
package org.apache.abdera2.activities.model;

import java.util.Locale;

import javax.activation.MimeType;

import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.joda.time.DateTime;

import com.google.common.base.Supplier;

public final class ASDocument<T extends ASBase> {

  private final T root;
  private final MimeType contentType;
  private final DateTime lastModified;
  private final EntityTag entityTag;
  private final Lang language;
  private final String slug;
  private final IRI base;
  
  @SuppressWarnings("synthetic-access")
  ASDocument(Builder<T> builder) {
    this.root = builder.root;
    this.contentType = builder.contentType;
    this.lastModified = builder.lastModified;
    this.entityTag = builder.entityTag;
    this.language = builder.language;
    this.slug = builder.slug;
    this.base = builder.base;
  }
  
  public T getRoot() {
    return root;
  }
  
  public MimeType getContentType() {
    return contentType;
  }
  
  public DateTime getLastModified() {
    return lastModified;
  }
  
  public EntityTag getEntityTag() {
    return entityTag;
  }
  
  public Lang getLanguage() {
    return language;
  }
  
  public String getSlug() {
    return slug;
  }
  
  public IRI getBaseUri() {
    return base;
  }
  
  public static <T extends ASBase>Builder<T> make() {
    return new Builder<T>();
  }
  
  public static <T extends ASBase>Builder<T> make(T root) {
    return new Builder<T>(root);
  }
  
  public static class Builder<T extends ASBase> 
    implements Supplier<ASDocument<T>> {

    private T root;
    private MimeType contentType;
    private DateTime lastModified;
    private EntityTag entityTag;
    private Lang language;
    private String slug;
    private IRI base;
    
    public Builder() {}
    
    public Builder(T root) {
      this.root = root;
    }
    
    public Builder<T> root(T root) {
      this.root = root;
      return this;
    }
    
    public Builder<T> contentType(String mt) {
      this.contentType = MimeTypeHelper.unmodifiableMimeType(mt);
      return this;
    }
    
    public Builder<T> contentType(MimeType mt) {
      this.contentType = MimeTypeHelper.unmodifiableMimeType(mt);
      return this;
    }
    
    public Builder<T> lastModified(DateTime dt) {
      this.lastModified = dt;
      return this;
    }
    
    public Builder<T> entityTag(String etag) {
      this.entityTag = EntityTag.parse(etag);
      return this;
    }
    
    public Builder<T> entityTag(EntityTag etag) {
      this.entityTag = etag;
      return this;
    }
    
    public Builder<T> language(String lang) {
      this.language = Lang.parse(lang);
      return this;
    }
    
    public Builder<T> language(Lang lang) {
      this.language = lang;
      return this;
    }
    
    public Builder<T> language(Locale locale) {
      this.language = new Lang(locale);
      return this;
    }
    
    public Builder<T> slug(String slug) {
      this.slug = slug;
      return this;
    }
    
    public Builder<T> base(String iri) {
      this.base =new IRI(iri);
      return this;
    }
    
    public Builder<T> base(IRI iri) {
      this.base = iri;
      return this;
    }
    
    public ASDocument<T> get() {
      return new ASDocument<T>(this);
    }
  }
}
