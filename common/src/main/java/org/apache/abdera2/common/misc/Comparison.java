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

import org.apache.abdera2.common.selector.AbstractSelector;
import org.apache.abdera2.common.selector.Selector;

import com.google.common.base.Predicate;

/**
 * A Comparison is similiar to Comparable in that it compares
 * the equivalence of two Objects based on some specific condition,
 * however, unlike Comparable which returns either a -1, 0 or 1 
 * for use when Sorting objects, Comparison returns a simple boolean
 * response similar to a Predicate.
 * 
 * A Comparison is similar to the Guava Libraries Equivalence but 
 * differs semantically.. the purpose of an Equivalence is to test
 * whether two items are semantically equivalent to one another, either
 * in terms of equality, identity, or identical meaning. A 
 * Comparison, on the other hand, checks only to see if the two objects
 * adhere to some arbitrary comparison logic regardless of whether the
 * two objects are semantically equivalent.
 */
public abstract class Comparison<R> {

  public abstract boolean apply(R r1, R r2);
  
  public final Comparison<R> negate() {
    final Comparison<R> _this = this;
    return new Comparison<R>() {
      public boolean apply(R r1, R r2) {
        return !_this.apply(r1, r2);
      }
    };
  }
  
  public final Comparison<R> and(final Comparison<R> other) {
    final Comparison<R> _this = this;
    return new Comparison<R>() {
      public boolean apply(R r1, R r2) {
        return _this.apply(r1, r2) && other.apply(r1, r2);
      }      
    };
  }
  
  public final Comparison<R> or(final Comparison<R> other) {
    final Comparison<R> _this = this;
    return new Comparison<R>() {
      public boolean apply(R r1, R r2) {
        return _this.apply(r1, r2) || other.apply(r1, r2);
      }
    };
  }
  
  public final Comparison<R> not(final Comparison<R> other) {
    final Comparison<R> _this = this;
    return new Comparison<R>() {
      public boolean apply(R r1, R r2) {
        return _this.apply(r1, r2) && !other.apply(r1, r2);
      }
    };
  } 

  public final Predicate<R> predicateFor(final R first) {
    final Comparison<R> comp = this;
    return new Predicate<R>() {
      public boolean apply(R input) {
        return comp.apply(first, input);
      }
    };
  }
  
  public final Selector<R> selectorFor(final R first) {
    final Comparison<R> comp = this;
    return new AbstractSelector<R>() {
      @SuppressWarnings("unchecked")
      public boolean select(Object item) {
        return comp.apply(first,(R)item);
      }
    };
  }
}
