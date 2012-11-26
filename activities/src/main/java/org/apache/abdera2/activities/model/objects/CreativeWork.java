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

import com.google.common.base.Supplier;

/**
 * Abstract base class for several extension ASObject types
 */
@SuppressWarnings("unchecked")
public abstract class CreativeWork extends ASObject {


  protected <X extends CreativeWork, M extends Builder<X,M>>CreativeWork(Map<String,Object> map, Class<M> _class, Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public <T extends ASObject>T getAbout() {
    return (T)getProperty("about");
  }
  
  public <T extends ASObject>T getGenre() {
    return (T)getProperty("genre");
  }
  
  public <T extends ASObject>T getPublisher() {
    return (T)getProperty("publisher");
  }
  
  public <T extends ASObject>T getProvider() {
    return (T)getProperty("provider");
  }
  
  public <T extends ASObject>T getContributor() {
    return (T)getProperty("contributor");
  }
  
  public <T extends ASObject>T getEditor() {
    return (T)getProperty("editor");
  }
  
  public static abstract class Builder<X extends CreativeWork, M extends Builder<X,M>> 
    extends ASObject.Builder<X,M> {
    protected Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    protected Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    protected Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M about(Supplier<? extends ASObject> obj) {
      return about(obj.get());
    }
    public M about(ASObject object) {
      set("about",object);
      return (M)this;
    }
    public M contributor(Supplier<? extends ASObject> obj) {
      return contributor(obj.get());
    }
    public M contributor(ASObject object) {
      set("contributor",object);
      return (M)this;
    }
    public M editor(Supplier<? extends ASObject> obj) {
      return editor(obj.get());
    }
    public M editor(ASObject object) {
      set("editor",object);
      return (M)this;
    }
    public M genre(Supplier<? extends ASObject> obj) {
      return genre(obj.get());
    }
    public M genre(ASObject object) {
      set("genre",object);
      return (M)this;
    }
    public M provider(Supplier<? extends ASObject> obj) {
      return provider(obj.get());
    }
    public M provider(ASObject object) {
      set("provider",object);
      return (M)this;
    }
    public M publisher(Supplier<? extends ASObject> obj) {
      return publisher(obj.get());
    }
    public M publisher(ASObject object) {
      set("publisher",object);
      return (M)this;
    }
  }
}
