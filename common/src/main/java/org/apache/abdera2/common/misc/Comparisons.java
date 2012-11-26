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

import org.apache.abdera2.common.text.CharUtils;

import com.google.common.base.Equivalence;
import com.google.common.base.Predicate;
import static com.google.common.base.Preconditions.*;

public class Comparisons {

  private Comparisons() {}
  
  public static <R>Comparison<R> forEquivalence(final Equivalence<R> r) {
    return new Comparison<R>() {
      public boolean apply(R r1, R r2) {
        return r.equivalent(r1, r2);
      }
    };
  }
  
  public static <T>Comparison<T> and(final Comparison<T>...comparisons) {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        for (Comparison<T> compare : comparisons)
          if (!compare.apply(r1, r2))
            return false;
        return true;
      }      
    };
  }
  
  public static <T>Comparison<T> or(final Comparison<T>...comparisons) {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        for (Comparison<T> compare : comparisons)
          if (compare.apply(r1, r2))
            return true;
        return false;
      }      
    };
  }
  
  public static <T>boolean bothAreNull(T t1, T t2) {
    return bothAreNull().apply(t1,t2);
  }
  
  public static <T>boolean onlyFirstIsNull(T t1, T t2) {
    return onlyFirstIsNull().apply(t1,t2);
  }
  
  public static <T>boolean onlySecondIsNull(T t1, T t2) {
    return onlySecondIsNull().apply(t1,t2);
  }
  
  public static boolean bothAreTrue(boolean t1, boolean t2) {
    return t1 && t2;
  }
  
  public static boolean onlyOneIsTrue(boolean t1, boolean t2) {
    return t1 != t2;
  }
  
  public static boolean bothAreEmpty(String t1, String t2) {
    return !CharUtils.not_empty(t1) && !CharUtils.not_empty(t2);
  }
  
  public static <T>boolean onlyOneIsNull(T t1, T t2) {
    return onlyOneIsNull().apply(t1,t2);
  }
  
  public static <T>boolean neitherIsNull(T t1, T t2) {
    return neitherIsNull().apply(t1,t2);
  }
  
  public static <T>boolean bothApply(T t1, T t2, Predicate<T> predicate) {
    return bothApply(predicate).apply(t1,t2);
  }
  
  public static <T>boolean neitherApply(T t1, T t2, Predicate<T> predicate) {
    return neitherApply(predicate).apply(t1,t2);
  }
  
  public static <T>boolean eitherApply(T t1, T t2, Predicate<T> predicate) {
    return eitherApply(predicate).apply(t1, t2);
  }
  
  public static <T>boolean onlyFirstApplies(T t1, T t2, Predicate<T> predicate) {
    return onlyFirstApplies(predicate).apply(t1, t2);
  }

  public static <T>boolean onlySecondApplies(T t1, T t2, Predicate<T> predicate) {
    return onlySecondApplies(predicate).apply(t1, t2);
  }
  
  public static <T>boolean testBoth(T t1, T t2, Predicate<T> p1, Predicate<T> p2) {
    return testBoth(p1,p2).apply(t1,t2);
  }
  
  public static final Comparison<Object> bothAreNull = 
    bothAreNull();
  
  public static final Comparison<Object> onlyFirstIsNull = 
    onlyFirstIsNull();
  
  public static final Comparison<Object> onlySecondIsNull = 
    onlySecondIsNull();
  
  public static final Comparison<Object> onlyOneIsNull = 
    onlyOneIsNull();
  
  public static final Comparison<Object> neitherIsNull = 
    neitherIsNull();
  
  @SuppressWarnings("unchecked")
  public static <X extends Predicate<? extends T[]>,T>X asPredicate(
    final Comparison<T> comparison) {
    return (X)new Predicate<T[]>() {
      public boolean apply(T[] input) {
        checkNotNull(input);
        checkArgument(
          input.length == 2, 
          "The input array must have exactly two items");
        return comparison.apply(input[0],input[1]);
      }
    };
  }
  
  public static <X extends Predicate<? extends T[]>,T>X asPredicate(
    final Equivalence<T> equivalence) {
      return Comparisons.<X,T>asPredicate(forEquivalence(equivalence));
  }
  
  /**
   * Returns true if predicate1.apply(r1) && predicate2.apply(r2)
   */
  public static <T>Comparison<T> testBoth(final Predicate<T> predicate1, final Predicate<T> predicate2) {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return predicate1.apply(r1) && predicate2.apply(r2);
      }
    };
  }
  
  /**
   * Returns a Comparison that checks both inputs against a Predicate
   */
  public static <T>Comparison<T> bothApply(final Predicate<T> predicate) {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return predicate.apply(r1) && predicate.apply(r2);
      }
    };
  }
  
  /**
   * Returns a Comparison that checks both inputs against a Predicate
   */
  public static <T>Comparison<T> onlyFirstApplies(final Predicate<T> predicate) {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return predicate.apply(r1) && !predicate.apply(r2);
      }
    };
  }
  
  /**
   * Returns a Comparison that checks both inputs against a Predicate
   */
  public static <T>Comparison<T> eitherApply(final Predicate<T> predicate) {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return predicate.apply(r1) || predicate.apply(r2);
      }
    };
  }
  
  /**
   * Returns a Comparison that checks both inputs against a Predicate
   */
  public static <T>Comparison<T> onlySecondApplies(final Predicate<T> predicate) {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return !predicate.apply(r1) && predicate.apply(r2);
      }
    };
  }
  
  /**
   * Returns a Comparison that checks both inputs against a Predicate
   */
  public static <T>Comparison<T> neitherApply(final Predicate<T> predicate) {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return !predicate.apply(r1) && !predicate.apply(r2);
      }
    };
  }
  
  /**
   * Returns true if both input objects are null
   */
  public static <T>Comparison<T> bothAreNull() {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return r1 == null && r2 == null;
      }      
    };
  }

  /**
   * Returns true if only the first of the two input objects is null;
   */
  public static <T>Comparison<T> onlyFirstIsNull() {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return r1 == null && r2 != null;
      }
    };
  }
  
  /**
   * Returns true if only the second of the two input objects is null;
   */
  public static <T>Comparison<T> onlySecondIsNull() {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return r1 != null && r2 == null;
      }
    };
  }
  
  /**
   * Returns true if only one of the two input objects is null;
   */
  public static <T>Comparison<T> onlyOneIsNull() {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return (r1 == null && r2 != null) ||
               (r1 != null && r2 == null);
      }
    };
  }
  
  /**
   * Returns true if neither input objects are null
   */
  public static <T>Comparison<T> neitherIsNull() {
    return new Comparison<T>() {
      public boolean apply(T r1, T r2) {
        return r1 != null && r2 != null;
      }      
    };
  }
  
  public static <T>Comparison<T> equals() {
    return Comparisons.<T>neitherIsNull()
      .and(new Comparison<T>() {
        public boolean apply(T r1, T r2) {
          return r1.equals(r2);
        }
      });
  }
  
  public static <T>Comparison<T> identity() {
    return Comparisons.<T>neitherIsNull()
      .and(new Comparison<T>() {
        public boolean apply(T r1, T r2) {
          return r1 == r2;
        }
      });
  }
  
  public static Comparison<String> equalsIgnoreCase() {
    return Comparisons.<String>neitherIsNull()
      .and(new Comparison<String>() {
        public boolean apply(String r1, String r2) {
          return r1.equalsIgnoreCase(r2);
        }
      });
  }
  
}
