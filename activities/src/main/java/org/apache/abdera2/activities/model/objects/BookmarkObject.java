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
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.iri.IRI;

public class BookmarkObject 
  extends ASObject {

  public static final String TARGETURL = "targetUrl";
  
  public BookmarkObject(Map<String,Object> map) {
    super(map,BookmarkBuilder.class,BookmarkObject.class);
  }
  
  public <X extends BookmarkObject, M extends Builder<X,M>>BookmarkObject(Map<String,Object> map, Class<M> _class, Class<X>_obj) {
    super(map,_class,_obj);
  }

  public String getTargetUrl() {
    return getProperty(TARGETURL);
  }
  
  public static BookmarkBuilder makeBookmark() {
    return new BookmarkBuilder("bookmark");
  }

  public static BookmarkBuilder makeBookmark(String targetUrl) {
    return makeBookmark().targetUrl(targetUrl);
  }
  
  public static BookmarkBuilder makeBookmark(IRI targetUrl) {
    return makeBookmark().targetUrl(targetUrl);
  }
  
  @Name("bookmark")
  public static final class BookmarkBuilder 
    extends Builder<BookmarkObject,BookmarkBuilder> {
    public BookmarkBuilder() {
      super(BookmarkObject.class,BookmarkBuilder.class);
    }
    public BookmarkBuilder(Map<String, Object> map) {
      super(map, BookmarkObject.class,BookmarkBuilder.class);
    }
    public BookmarkBuilder(String objectType) {
      super(objectType, BookmarkObject.class,BookmarkBuilder.class);
    }
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends BookmarkObject, M extends Builder<X,M>> 
    extends ASObject.Builder<X,M> {
    public Builder(Class<X> _class,Class<M>_builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X> _class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X> _class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M targetUrl(String uri) {
      return targetUrl(new IRI(uri));
    }
    public M targetUrl(IRI uri) {
      set(TARGETURL,uri);
      try {
        if (isExperimentalEnabled())
          link("bookmark",uri);
      } catch (IllegalStateException e) {}
      return (M)this;
    }    
  }
}
