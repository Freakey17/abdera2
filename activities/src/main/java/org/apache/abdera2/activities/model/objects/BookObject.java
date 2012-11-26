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

import com.google.common.base.Supplier;

/**
 * A simple "objectType":"book" object that serves primarily as an 
 * example of creating new ASObject types.
 */
@SuppressWarnings("unchecked")
public class BookObject extends CreativeWork {


  public BookObject(Map<String,Object> map) {
    super(map,BookBuilder.class,BookObject.class);
  }
  
  public <X extends BookObject, M extends Builder<X,M>>BookObject(Map<String,Object> map, Class<M> _class,Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public <T extends ASObject>T getFormat() {
    return (T)getProperty("format");
  }
  
  public String getEdition() {
    return getProperty("edition");
  }
  
  public String getIsbn10() {
    return getProperty("isbn10");
  }
  
  public String getIsbn13() {
    return getProperty("isbn13");
  }
  
  public int getPageCount() {
    return getPropertyInt("pageCount");
  }
  
  public <T extends ASObject>T getIllustrator() {
    return (T)getProperty("illustrator");
  }
  
  public static BookBuilder makeBook() {
    return new BookBuilder("book");
  }
  
  @Name("book")
  public static final class BookBuilder extends Builder<BookObject,BookBuilder> {
    public BookBuilder() {
      super(BookObject.class,BookBuilder.class);
    }
    public BookBuilder(Map<String, Object> map) {
      super(map, BookObject.class,BookBuilder.class);
    }
    public BookBuilder(String objectType) {
      super(objectType, BookObject.class,BookBuilder.class);
    }
  }
  
  public abstract static class Builder<X extends BookObject,M extends Builder<X,M>> 
    extends CreativeWork.Builder<X,M> {
    public Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    protected Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M edition(String val) {
      set("edition",val);
      return (M)this;
    }
    public M format(Supplier<? extends ASObject> obj) {
      return format(obj.get());
    }
    public M format(ASObject obj) {
      set("format",obj);
      return (M)this;
    }
    public M illustrator(Supplier<? extends ASObject> obj) {
      return illustrator(obj.get());
    }
    public M illustrator(ASObject obj) {
      set("illustrator",obj);
      return (M)this;
    }
    public M isbn10(String val) {
      set("isbn10",val);
      return (M)this;
    }
    public M isbn13(String val) {
      set("isbn13",val);
      return (M)this;
    }
    public M pageCount(int count) {
      set("pageCount",count);
      return (M)this;
    }

  }
}
