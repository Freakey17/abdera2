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

/**
 * Utility interface that is used as a Filter in several places 
 * throughout the Abdera API.
 */
public interface Selector<X>
  extends Predicate<X>, Constraint<X> {

    /** Returns true the item is to be selected **/
    boolean select(Object item);

    /** Returns true if all the items in the set match **/
    boolean all(Iterable<X> items);
    
    /** Returns true if any the items in the set match **/
    boolean any(Iterable<X> items);
    
    /** Returns true if none the items in the set match **/
    boolean none(Iterable<X> items);
    
    /**
     * Returns an immutable iterable containing only the items 
     * from the given iterable for which the selector applies
     */
    Iterable<X> filter(Iterable<X> items);
    
    /**
     * Returns an immutable iterable containing only the items
     * from the given iterable for which the selector does not apply
     */
    Iterable<X> filterOut(Iterable<X> items);
    
    /**
     * Returns the first item in the set that matches the selector
     */
    X choose(Iterable<X> items);
    
    /**
     * Returns the first item in the set that matches the selector
     */
    X chooseNot(Iterable<X> items);
    
    /** Returns the object if the Selector applies, or return null if it doesn't **/
    X test(X item);
    
    /** Returns the result of transform.apply(item) if the Selector applies to the input **/
    <Y>Y test(X item, Function<X,Y> transform);
    
    /** Returns the object if the Selector applies, or return the other X if it doesn't **/
    X test(X item, X otherwise);
    
    Function<X,Boolean> asFunction();
    Predicate<X> asPredicate();
    Constraint<X> asConstraint();
    
    
    /**
     * Returns a Selector that matches this selector up to a specific number of times
     */
    Selector<X> limit(int limit);
    
    /**
     * Returns a Selector<Y> A using this Selector B and Funtion<Y,X> C such that
     * X = C(Y),
     * B(X),
     * A(Y) = B(C(Y))
     */
    <Y>Selector<Y> compose(Function<Y,X> transform);
    /**
     * Returns a Selector that selects the opposite of this selector
     */
    Selector<X> negate();
    /**
     * Returns a Selector that is the union of this and the specified selector
     */
    Selector<X> and(Selector<X> selector);
    /**
     * Returns a Selector that matches either this or the specified selector
     */
    Selector<X> or(Selector<X> selector);
    /**
     * Returns a Selector that matches this, but not the specified selector
     */
    Selector<X> andNot(Selector<X> selector);
    /**
     * Returns a Selector that matches this or the inverse of the specified selector
     */
    Selector<X> orNot(Selector<X> selector);
    
    public static class ConstraintSelector<X>
      extends AbstractSelector<X> {
      private final Constraint<X> internal;
      ConstraintSelector(Constraint<X> internal) {
        this.internal = internal;
      }
      @SuppressWarnings("unchecked")
      public boolean select(Object item) {
        return internal.checkElement((X)item) == item;
      }
      public X checkElement(X element) {
        return internal.checkElement(element);
      }
    }
    
    public static class PredicateSelector<X> 
      extends AbstractSelector<X> {
      private final Predicate<X> internal;
      PredicateSelector(Predicate<X> internal) {
        this.internal = internal;
      }
      @SuppressWarnings("unchecked")
      public boolean select(Object item) {
        return this.internal.apply((X)item);
      }
    }
}
