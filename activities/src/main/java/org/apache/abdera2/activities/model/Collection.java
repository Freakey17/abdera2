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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.selector.Selector;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * An Activity Streams Collection... used as the root object of
 * JSON Activity Streams documents and as the value for a variety 
 * of properties (such as "replies"). Encapsulates an array of 
 * items with additional metadata. As an alternative to specifying
 * the array of items inline, the "url" property can be used to 
 * reference an external Collection document. 
 */
public class Collection<T extends ASObject> 
  extends ASObject {

  public static final String TOTAL_ITEMS = "totalItems";
  public static final String URL = "url";
  public static final String ITEMS = "items";
  public static final String OBJECT_TYPES = "objectTypes";

  /**
   * Begin making a new collection object using the fluent factory api
   */
  public static <T extends ASObject>CollectionBuilder<T> makeCollection() {
    return new CollectionBuilder<T>("collection");
  }
  
  public static <T extends ASObject>Collection<T> makeCollection(Iterable<T> items) {
    return Collection.<T>makeCollection().items(items).get();
  }
  
  public static <T extends ASObject>Collection<T> makeCollection(T... items) {
    return Collection.<T>makeCollection().items(items).get();
  }
  
  public static <T extends ASObject>Collection<T> makeCollection(Supplier<? extends T>... items) {
    return Collection.<T>makeCollection().items(items).get();
  }
  
  @SuppressWarnings("unchecked")
  static <T extends ASObject>Class<Collection<T>> _class(Class<?> _class) {
    return (Class<Collection<T>>) _class;
  }
  
  @SuppressWarnings("unchecked")
  static <T extends ASObject>Class<CollectionBuilder<T>> _builder(Class<?> _class) {
    return (Class<CollectionBuilder<T>>) _class;
  }
  
  @Name("collection")
  public static class CollectionBuilder<T extends ASObject>
    extends Builder<T,Collection<T>,CollectionBuilder<T>> {

    public CollectionBuilder() {
      super(
        Collection.<T>_class(Collection.class),
        Collection.<T>_builder(CollectionBuilder.class));
    }

    public CollectionBuilder(Map<String, Object> map) {
      super(map, Collection.<T>_class(Collection.class),
          Collection.<T>_builder(CollectionBuilder.class));
    }

    public CollectionBuilder(String objectType) {
      super(objectType, Collection.<T>_class(Collection.class),
          Collection.<T>_builder(CollectionBuilder.class));
    }
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<T extends ASObject, X extends ASObject, M extends Builder<T,X,M>>
    extends ASObject.Builder<X,M> {
    private final ImmutableSet.Builder<T> items = 
      ImmutableSet.builder();
    private final ImmutableSet.Builder<String> types = 
      ImmutableSet.builder();
    boolean a,b,c;
    public Builder(Class<X> _class, Class<M> _builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X> _class, Class<M> _builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X> _class, Class<M> _builder) {
      super(map,_class,_builder);
    }
    public M item(Supplier<? extends T> item) {
      return item(item.get());
    }
    public M item(T item) {
      if (item == null) return (M)this;
      a=true;
      items.add(item);
      return (M)this;
    } 
    public M items(Supplier<? extends T>... items) {
      if (items == null) return (M)this;
      for (Supplier<? extends T> item : items)
        item(item.get());
      return (M)this;
    }
    public M items(T... items) {
      if (items == null) return (M)this;
      for (T item : items)
        item(item);
      return (M)this;
    }
    public M items(Iterable<? extends T> items) {
      for (T item : items)
        item(item);
      return (M)this;
    }
    public M objectTypes(String... types) {
      if (types.length == 0) return (M)this;
      b=true;
      for (String type : types)
        this.types.add(type);
      return (M)this;
    } 
    public M totalItems(int count) {
      c = true;
      set(TOTAL_ITEMS, Math.max(0,count));
      return (M)this;
    }
    public void preGet() {
      super.preGet();
      Set<T> i = items.build();
      if(b) set(OBJECT_TYPES, types.build());
      if(a) {
        if (!c) set(TOTAL_ITEMS, i.size());
        map.put(ITEMS, i);
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  public Collection(Map<String,Object> map) {
    super(map,CollectionBuilder.class,Collection.class);
  }
  
  public <X extends Collection<T>, M extends Builder<T,X,M>>Collection(Map<String,Object> map, Class<M> _class, Class<X> _obj) {
    super(map,_class,_obj);
  }
  
  /**
   * Return the value of the "totalItems" property... this does not 
   * necessarily reflect the actual number of items in the "items" 
   * iterator.
   */
  public int getTotalItems() {
    return (Integer)getProperty(TOTAL_ITEMS);
  }
  
  /**
   * Get the url of this collection
   */
  public IRI getUrl() {
    return getProperty(URL);
  }
  
  /**
   * Get the list of objectTypes expected to be found in this collection
   */
  public Iterable<String> getObjectTypes() {
    return checkEmpty(this.<Iterable<String>>getProperty(OBJECT_TYPES));
  }
  
  /**
   * get the items collection using the specified selector as a filter
   */
  public Iterable<T> getItems(Selector<T> selector) {
    List<T> list = new ArrayList<T>();
    for (T item : getItems()) 
      if (selector.apply(item))
        list.add(item);
    return list;
  }
  
  /**
   * get the items contained in this collection
   */
  @SuppressWarnings("unchecked")
  public Iterable<T> getItems() {
    return checkEmpty((Iterable<T>)getProperty(ITEMS));
  }
 
}
