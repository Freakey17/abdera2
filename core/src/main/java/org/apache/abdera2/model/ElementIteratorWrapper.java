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
package org.apache.abdera2.model;

import java.util.Iterator;

import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.factory.Factory;

@SuppressWarnings("rawtypes")
public class ElementIteratorWrapper<T extends Element> implements Iterator<T> {

    private final Iterator<?> iterator;
    private final Selector selector;
    private final Factory factory;
    private T current;

    public ElementIteratorWrapper(Factory factory, Iterator<?> iterator) {
      this(factory,iterator,null);
    }
    
    public ElementIteratorWrapper(Factory factory, Iterator<?> iterator, Selector selector) {
        this.iterator = iterator;
        this.selector = selector;
        this.factory = factory;
        current = get_current();
    }

    @SuppressWarnings("unchecked")
    private T get_current() {
      while(iterator.hasNext()) {
        T item = (T) iterator.next();
        if (selector == null || selector.select(item))
          return item;
      }
      return null;
    }
    
    public boolean hasNext() {
        return current != null;
    }

    public T next() {
      T item = current;
      current = get_current();
      return factory.<T>getElementWrapper(item);
    }

    public void remove() {
        iterator.remove();
    }

}
