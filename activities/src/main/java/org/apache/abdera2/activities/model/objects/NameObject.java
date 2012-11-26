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

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.iri.IRI;

public class NameObject 
  extends ASObject {

  public NameObject(Map<String,Object> map) {
    super(map,NameBuilder.class,NameObject.class);
  }
  
  public <X extends NameObject, M extends Builder<X,M>>NameObject(Map<String,Object> map, Class<M> _class, Class<X>_obj) {
    super(map,_class,_obj);
  }

  public String getFamilyName() {
    return getProperty("familyName");
  }
  
  public String getFormatted() {
    return getProperty("formatted");
  }
  
  public String getGivenName() {
    return getProperty("givenName");
  }
  
  public String getHonorificPrefix() {
    return getProperty("honorificPrefix");
  }
  
  public String getHonorificSuffix() {
    return getProperty("honorificSuffix");
  }
  
  public String getMiddleName() {
    return getProperty("middleName");
  }
  
  public String getPronunciation() {
    return getProperty("pronunciation");
  }
  
  public IRI getPronunciationUrl() {
    return getProperty("pronunciationUrl");
  }
  
  public static NameBuilder makeName() {
    return new NameBuilder("name");
  }
  
  @org.apache.abdera2.common.anno.Name("name")
  public static final class NameBuilder extends Builder<NameObject,NameBuilder> {

    public NameBuilder() {
      super(NameObject.class,NameBuilder.class);
    }

    public NameBuilder(Map<String, Object> map) {
      super(map, NameObject.class,NameBuilder.class);
    }

    public NameBuilder(String objectType) {
      super(objectType, NameObject.class,NameBuilder.class);
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends NameObject, M extends Builder<X,M>> 
    extends ASObject.Builder<X,M> {
    public Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M familyName(String fn) {
      set("familyName",fn);
      return (M)this;
    }
    public M formatted(String fn) {
      set("formatted",fn);
      return (M)this;
    }
    public M givenName(String fn) {
      set("givenName",fn);
      return (M)this;
    }
    public M honorificPrefix(String fn) {
      set("honorificPrefix",fn);
      return (M)this;
    }
    public M honorificSuffix(String fn) {
      set("honorificSuffix",fn);
      return (M)this;
    }
    public M middleName(String fn) {
      set("middleName",fn);
      return (M)this;
    }
    public M pronunciation(String fn) {
      set("pronunciation",fn);
      return (M)this;
    }
    public M pronunciationUrl(String fn) {
      return pronunciationUrl(new IRI(fn));
    }
    public M pronunciationUrl(IRI fn) {
      set("pronunciationUrl",fn);
      return (M)this;
    }
  }
}
