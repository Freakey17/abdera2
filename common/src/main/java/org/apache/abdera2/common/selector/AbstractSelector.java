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
package org.apache.abdera2.common.selector;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Constraint;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@SuppressWarnings("unchecked")
public abstract class AbstractSelector<X> 
  implements Selector<X> {

  public boolean apply(X item) {
    return select(item);
  }
  
  public boolean all(Iterable<X> items) {
    if (items == null) return false;
    return Iterables.all(items, this);
  }
  
  public boolean any(Iterable<X> items) {
    if (items == null) return false;
    return Iterables.any(items, this);
  }
  
  public boolean none(Iterable<X> items) {
    if (items == null) return false;
    return Iterables.all(items, this.negate());
  }
  
  public Iterable<X> filter(Iterable<X> items) {
    if (items == null) return ImmutableSet.<X>of();
    return Iterables.filter(items, this);
  }
  
  public Iterable<X> filterOut(Iterable<X> items) {
    if (items == null) return ImmutableSet.of();
    return Iterables.<X>filter(items, this.negate());
  }
  
  public X choose(Iterable<X> items) {
    if (items == null) return null;
    return Iterables.find(items, this);
  }
  
  public X chooseNot(Iterable<X> items) {
    if (items == null) return null;
    return Iterables.find(items, this.negate());
  }
  
  public X test(X item) {
    return apply(item) ? item : null;
  }
  
  public X test(X item, X otherwise) {
    return apply(item) ? item : otherwise;
  }
  
  public <Y>Y test(X item, Function<X,Y> transform) {
    return apply(item) ? transform.apply(item) : null;
  }
 
 
  public X checkElement(X element) {
    if (apply(element))
      return element;
    throw new IllegalArgumentException();
  }

  public Function<X,Boolean> asFunction() {
    return Selectors.asFunction(this);
  }
  
  public Predicate<X> asPredicate() {
    return this;
  }
  public Constraint<X> asConstraint() {
    return this;
  }
  
  public Selector<X> limit(int limit) {
    return (Selector<X>)and(Selectors.<X>limit(limit));
  }
  
  public <Y>Selector<Y> compose(Function<Y,X> transform) {
    return Selectors.compose(this, transform);
  }
  
  public Selector<X> negate() {
    return Selectors.negate(this);
  }
  
  public Selector<X> and(Selector<X> selector) {
    return Selectors.<X>and(this,selector);
  }
  public Selector<X> or(Selector<X> selector) {
    return Selectors.<X>or(this,selector);
  }
  public Selector<X> andNot(Selector<X> selector) {
    return Selectors.<X>and(this, selector.negate());
  }
  public Selector<X> orNot(Selector<X> selector) {
    return Selectors.<X>or(this, selector.negate());
  }
}
