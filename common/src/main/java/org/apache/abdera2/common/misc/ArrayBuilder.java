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
package org.apache.abdera2.common.misc;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

@SuppressWarnings("unchecked")
public final class ArrayBuilder<T> {
  
  public static <T>ArrayBuilder<T> list(Class<? super T> _class) {
    return new ArrayBuilder<T>(_class,ImmutableList.<T>builder());
  }
  
  public static <T>ArrayBuilder<T> set(Class<? super T> _class) {
    return new ArrayBuilder<T>(_class,ImmutableSet.<T>builder());
  }
  
  public static <T>ArrayBuilder<T> sortedSet(Class<? super T> _class, Comparator<T> comp) {
    return new ArrayBuilder<T>(_class,ImmutableSortedSet.orderedBy(comp));
  }
  
  public static <T extends Comparable<T>>ArrayBuilder<T> sortedSet(Class<? super T> _class) {
    return new ArrayBuilder<T>(_class,ImmutableSortedSet.<T>naturalOrder());
  }
  
  private final ImmutableCollection.Builder<T> builder;
  private final Class<? super T> _class;

  ArrayBuilder(Class<? super T> _class, ImmutableCollection.Builder<T> builder) {
    this.builder = builder;
    this._class = _class;
  }
  public T[] build() {
    ImmutableCollection<T> list = builder.build();
    return list.toArray((T[])Array.newInstance(_class, list.size()));
  }
  public ArrayBuilder<T> add(T item) {
    builder.add(item);
    return this;
  }
  public ArrayBuilder<T> add(T... items) {
    builder.add(items);
    return this;
  }
  public ArrayBuilder<T> addAll(Iterable<T> items) {
    builder.addAll(items);
    return this;
  }
  public ArrayBuilder<T> addAll(Iterator<T> items) {
    builder.addAll(items);
    return this;
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_class == null) ? 0 : _class.hashCode());
    result = prime * result + ((builder == null) ? 0 : builder.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ArrayBuilder<?> other = (ArrayBuilder<?>) obj;
    if (_class == null) {
      if (other._class != null)
        return false;
    } else if (!_class.equals(other._class))
      return false;
    if (builder == null) {
      if (other.builder != null)
        return false;
    } else if (!builder.equals(other.builder))
      return false;
    return true;
    }
    
  }