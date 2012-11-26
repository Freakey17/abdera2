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

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Constraint;
import com.google.common.collect.ImmutableSet;

public final class Selectors {
  
  private Selectors() {}
  
  public static <X>Selector<X> notNull() {
    return forPredicate(Predicates.<X>notNull());
  }
  
  public static <X>Selector<X> isNull() {
    return forPredicate(Predicates.<X>isNull());
  }
  
  public static <X>Selector<X> alwaysTrue() {
    return forPredicate(Predicates.<X>alwaysTrue());
  }
  
  public static <X>Selector<X> alwaysFalse() {
    return forPredicate(Predicates.<X>alwaysFalse());
  }
  
  public static <X>Selector<X> oneOf(final X... items) {
    return forPredicate(Predicates.in(ImmutableSet.<X>copyOf(items)));
  }
  
  public static Selector<Object> instanceOf(final Class<?> _class) {
    return forPredicate(Predicates.instanceOf(_class));
  }
  
  /**
   * Uses ==
   */
  public static <X>Selector<X> identity(final X instance) {
    return new AbstractSelector<X>() {
      public boolean select(Object item) {
        return item == instance;
      }
    };
  }
  
  /**
   * Uses equals()
   */
  public static <X>Selector<X> of(final X instance) {
    return new AbstractSelector<X>() {
      public boolean select(Object item) {
        return item.equals(instance);
      }
    };
  }
  
  /**
   * Creates a selector that will select at most the given number 
   * of items. Once the threshold has been met, the selector will
   * return false;
   */
  public static <X>Selector<X> limit(int limit) {
    return new Selectors.CountingSelector<X>(limit);
  }
  
  private static class CountingSelector<X> 
    extends AbstractSelector<X>
    implements StatefulSelector<X> {
      private final AtomicInteger counter = 
        new AtomicInteger();
      private final int limit;
      private boolean done = false;
      public CountingSelector(int limit) {
        this.limit = limit;
      }
      public boolean select(Object object) {
        if (done) return false;
        if (counter.incrementAndGet() <= limit)
          return true;
        else done = true;
        return false;
      }
  }
  
  /**
   * Creates a new Selector A using Selector B and Function C such that
   * X = C(Y),
   * B(X),
   * A(Y) = B(C(Y)). 
   * That is, for instance, supposing we have a 
   * Selector<Long> B and Function<String,Long> C, this creates a 
   * Selector<String> that will first pass the input string to the 
   * Function, which returns a Long, and in turn passes that to selector
   * B.
   */
  public static <X,Y>Selector<Y> compose(Selector<X> b, Function<Y,X> c) {
    return new TransformSelector<Y,X>(b,c);
  }
  
  /**
   * Returns the Selector<X> as a Function<X,Boolean>
   */
  public static <X>Function<X,Boolean> asFunction(final Selector<X> selector) {
    return new Function<X,Boolean>() {
      public Boolean apply(X input) {
        return selector.apply(input);
      }
    };
  }
  
  /**
   * Returns a Selector<X> that selects the inverse of the provided
   * Selector<X>
   */
  public static <X>Selector<X> negate(final Selector<X> selector) {
    return new AbstractSelector<X>() {
      public boolean select(Object item) {
        return !selector.select(item);
      }
    };
  }
  
  /**
   * Returns a Selector<X> for the given Equivalence
   */
  public static <X>Selector<X> equivalentTo(
    final Equivalence<X> equivalence,
    final X item) {
      return forPredicate(equivalence.equivalentTo(item));
  }
  
  /**
   * Returns a Selector<X> that wraps the specified Predicate<X>
   */
  public static <X>Selector<X> forPredicate(
    Predicate<X> predicate) {
      return new Selector.PredicateSelector<X>(predicate);
  }
  
  /**
   * Returns a Selector<X> that wraps a Constraint<X>
   */
  public static <X>Selector<X> forConstraint(
    Constraint<X> constraint) {
      return new Selector.ConstraintSelector<X>(constraint);
  }
  
  public static <X>Selector<X> not(Selector<X>...selectors) {
    return Selectors.negate(and(selectors));
  }

  public static <X>Selector<X> or(Selector<X>...selectors) {
    return new MultiSelector<X>(selectors) {
      public boolean select(Object item) {
        for (Selector<X> selector : selectors)
          if (selector.select(item))
            return true;
        return false;
      }
    };
  }
  
  public static <X>Selector<X> and(Selector<X>...selectors) {
    return new MultiSelector<X>(selectors) {
      public boolean select(Object item) {
        for (Selector<X> selector : selectors)
          if (!selector.select(item))
            return false;
        return true;
      }
    };
  }
  
  private static abstract class MultiSelector<X> 
    extends AbstractSelector<X>
    implements Selector<X> {

    private static final long serialVersionUID = 5257601171344714824L;
    protected final Selector<X>[] selectors;
  
    public MultiSelector(Selector<X>... selectors) {
      this.selectors = selectors;
    }
  }
  
  
  private static class TransformSelector<X,Y>
    extends AbstractSelector<X> {  
    private final Selector<Y> inner;
    private final Function<X,Y> function;
    public TransformSelector(
      Selector<Y> selector, 
      Function<X,Y> transform) {
        this.inner = selector;
        this.function = transform;
    }
    @SuppressWarnings("unchecked")
    public boolean select(Object item) {
      return inner.select((Y)function.apply((X)item));
    }
  }
}