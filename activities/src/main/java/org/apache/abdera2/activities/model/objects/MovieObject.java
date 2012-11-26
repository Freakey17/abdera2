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

import org.apache.abdera2.activities.io.gson.Properties;
import org.apache.abdera2.activities.io.gson.Property;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.MediaLink;
import org.apache.abdera2.common.anno.Name;
import org.joda.time.Duration;

import com.google.common.base.Supplier;

/**
 * A simple "objectType":"movie" object that serves primarily as an 
 * example of creating new ASObject types.
 */
@SuppressWarnings("unchecked")
public class MovieObject extends CreativeWork {


  public MovieObject(Map<String,Object> map) {
    super(map,MovieBuilder.class,MovieObject.class);
  }
  
  public <X extends MovieObject, M extends Builder<X,M>>MovieObject(Map<String,Object> map, Class<M> _class, Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public <T extends ASObject>T getActors() {
    return (T)getProperty("actors");
  }
  
  public <T extends ASObject>T getDirector() {
    return (T)getProperty("director");
  }
  
  public Duration getDuration() {
    return getProperty("duration");
  }
  
  public <T extends ASObject>T getMusicBy() {
    return (T)getProperty("musicBy");
  }
  
  public <T extends ASObject>T getProducer() {
    return (T)getProperty("producer");
  }
  
  public <T extends ASObject>T getProductionCompany() {
    return (T)getProperty("productionCompany");
  }
  
  public MediaLink getPreview() {
    return getProperty("preview");
  }
  
  public static MovieBuilder makeMovie() {
    return new MovieBuilder("movie");
  }
  
  @Name("movie")
  @Properties({
    @Property(name="preview",to=MediaLink.class),
    @Property(name="duration",to=Duration.class)
  })
  public static final class MovieBuilder extends Builder<MovieObject,MovieBuilder> {

    public MovieBuilder() {
      super(MovieObject.class, MovieBuilder.class);
    }

    public MovieBuilder(Map<String, Object> map) {
      super(map, MovieObject.class, MovieBuilder.class);
    }

    public MovieBuilder(String objectType) {
      super(objectType, MovieObject.class, MovieBuilder.class);
    }
    
  }
  
  public static class Builder<X extends MovieObject, M extends Builder<X,M>> 
    extends CreativeWork.Builder<X,M> {
    public Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M actors(Supplier<? extends ASObject> obj) {
      return actors(obj.get());
    }
    public M actors(ASObject obj) {
      set("actors",obj);
      return (M)this;
    }
    public M director(Supplier<? extends ASObject> obj) {
      return director(obj.get());
    }
    public M director(ASObject obj) {
      set("director",obj);
      return (M)this;
    }
    public M duration(Duration obj) {
      set("duration",obj);
      return (M)this;
    }
    public M musicBy(Supplier<? extends ASObject> obj) {
      return musicBy(obj.get());
    }
    public M musicBy(ASObject obj) {
      set("musicBy",obj);
      return (M)this;
    }
    public M preview(Supplier<MediaLink> obj) {
      return preview(obj.get());
    }
    public M preview(MediaLink obj) {
      set("preview",obj);
      return (M)this;
    }
    public M producer(Supplier<? extends ASObject> obj) {
      return producer(obj.get());
    }
    public M producer(ASObject obj) {
      set("producer",obj);
      return (M)this;
    }
    public M productionCompany(Supplier<? extends ASObject> obj) {
      return productionCompany(obj.get());
    }
    public M productionCompany(ASObject obj) {
      set("productionCompany",obj);
      return (M)this;
    }
  }
}
