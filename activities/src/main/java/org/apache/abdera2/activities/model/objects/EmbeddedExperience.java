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
package org.apache.abdera2.activities.model.objects;

import java.util.Map;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.common.iri.IRI;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * Represents an Embedded Experience data structure. Embedded Experiences
 * were introduced to Activity Streams through the OpenSocial 2.0 specification.
 * While EE structures can be used outside the scope of OpenSocial, they are
 * primarily intended to be used as a means of associating an OpenSocial
 * Gadget specification with an Activity Stream object that can be rendered 
 * in-line when the activity data is displayed within an OpenSocial container.
 */
public final class EmbeddedExperience 
  extends ASBase {
  
  public EmbeddedExperience(Map<String,Object> map) {
    super(map,Builder.class,EmbeddedExperience.class);
  }
  
  public IRI getUrl() {
    return getProperty("url");
  }
  
  public IRI getGadget() {
    return getProperty("gadget");
  }
  
  public ASBase getContext() {
    return getProperty("context");
  }
  
  public IRI getPreviewImage() {
    return getProperty("previewImage");
  }
  
  public static Builder makeEmbeddedExperience() {
    return new Builder();
  }
  
  public static EmbeddedExperience makeGadgetEmbeddedExperience(
    String url, 
    ASBase context, 
    String preview) {
    return makeEmbeddedExperience()
      .gadget(url)
      .context(context)
      .previewImage(preview)
      .get();
  }
  
  public static EmbeddedExperience makeGadgetEmbeddedExperience(
    IRI url, 
    ASBase context, 
    IRI preview) {
    return makeEmbeddedExperience()
      .gadget(url)
      .context(context)
      .previewImage(preview)
      .get();
  }
  
  public static EmbeddedExperience makeGadgetEmbeddedExperience(
    String url, 
    Map<String,Object> context, 
    String preview) {
    return makeEmbeddedExperience()
      .gadget(url)
      .context(context)
      .previewImage(preview)
      .get();
  }
  
  public static EmbeddedExperience makeGadgetEmbeddedExperience(
    IRI url, 
    Map<String,Object> context, 
    IRI preview) {
    return makeEmbeddedExperience()
      .gadget(url)
      .context(context)
      .previewImage(preview)
      .get();
  }
  
  
  public static EmbeddedExperience makeUrlEmbeddedExperience(
    String url, 
    ASBase context, 
    String preview) {
    return makeEmbeddedExperience()
      .url(url)
      .context(context)
      .previewImage(preview)
      .get();
  }
  
  public static EmbeddedExperience makeUrlEmbeddedExperience(
    IRI url, 
    ASBase context, 
    IRI preview) {
    return makeEmbeddedExperience()
      .url(url)
      .context(context)
      .previewImage(preview)
      .get();
  }
  
  public static EmbeddedExperience makeUrlEmbeddedExperience(
    String url, 
    Map<String,Object> context, 
    String preview) {
    return makeEmbeddedExperience()
      .url(url)
      .context(context)
      .previewImage(preview)
      .get();
  }
  
  public static EmbeddedExperience makeUrlEmbeddedExperience(
    IRI url, 
    Map<String,Object> context, 
    IRI preview) {
    return makeEmbeddedExperience()
      .url(url)
      .context(context)
      .previewImage(preview)
      .get();
  }
  
  public final static class Builder 
    extends ASBase.Builder<EmbeddedExperience,Builder> {

    public Builder() {
      super(EmbeddedExperience.class,Builder.class);
    }
    
    protected Builder(Map<String,Object> map) {
      super(map,EmbeddedExperience.class,Builder.class);
    }

    public Builder context(Supplier<? extends ASBase> object) {
      return context(object.get());
    }
    
    public Builder context(ASBase object) {
      set("context", object);
      return this;
    }
    
    public Builder context(Map<String,Object> map) {
      set("context", ImmutableMap.copyOf(map));
      return this;
    }
    
    public Builder gadget(IRI iri) {
      set("gadget",iri);
      return this;
    }
    
    public Builder gadget(String iri) {
      return gadget(new IRI(iri));
    }
    
    public Builder previewImage(IRI iri) {
      set("previewImage",iri);
      return this;
    }
    
    public Builder previewImage(String iri) {
      return previewImage(new IRI(iri));
    }
    
    public Builder url(IRI iri) {
      set("url",iri);
      return this;
    }
    
    public Builder url(String uri) {
      return url(new IRI(uri));
    }
    
  }
}
