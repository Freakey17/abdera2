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

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.abdera2.common.selector.AbstractSelector;
import org.apache.abdera2.common.selector.PropertySelector;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.common.text.CodepointMatcher;

import com.google.common.base.Predicates;

import static org.apache.abdera2.common.misc.Comparisons.*;
import static com.google.common.base.Preconditions.*;

public final class MorePredicates {

  private MorePredicates() {}
  
  public static Selector<String> equalsIgnoreCase(final String val) {
    return new AbstractSelector<String>() {
      public boolean select(Object input) {
        if (bothAreNull(val, input)) return true;
        if (onlyOneIsNull(val, input)) return false;
        return input.toString().equalsIgnoreCase(val);
      }
    };
  }
  
  public static Selector<String> notNullOrEmpty() {
    return new AbstractSelector<String>() {
      public boolean select(Object input) {
        checkArgument(input instanceof String);
        String val = input.toString();
        return val != null && val.length() > 0;
      }
    };
  }
  
  /**
   * Returns a Predicate that checks if the named property of instances of
   * the specified class are null. The named property MUST NOT be private 
   * and MUST NOT require any input parameters. The method name is case
   * sensitive. 
   */
  public static <T>Selector<T> isNull(Class<T> _class, String method) {
    return PropertySelector.<T>create(_class, method, Predicates.isNull());
  }
  
  /**
   * Returns a Predicate that checks if the named property of instances of
   * the specified class are not null. The named property MUST NOT be private 
   * and MUST NOT require any input parameters. The method name is case
   * sensitive. 
   */
  public static <T>Selector<T> isNotNull(Class<T> _class, String method) {
    return PropertySelector.<T>create(_class, method, Predicates.not(Predicates.isNull()));
  }
  
  /**
   * Returns a Predicate that checks if the value of a named property of 
   * instances of the specified class is an instance of the given test class 
   */
  public static <T>Selector<T> instanceOf(Class<T> _class, String method, Class<?> _test) {
    return PropertySelector.<T>create(_class, method, Predicates.instanceOf(_test));
  }
  
  public static <T>Selector<T> assignableFrom(Class<T> _class, String method, Class<?> _test) {
    return PropertySelector.<T>create(_class, method, Predicates.assignableFrom(_test));
  }
  
  public static <T>Selector<T> containsPattern(Class<T> _class, String method, Pattern pattern) {
    return PropertySelector.<T>create(_class, method, Predicates.contains(pattern));
  }
  
  public static <T>Selector<T> containsPattern(Class<T> _class, String method, String pattern) {
    return containsPattern(_class,method,Pattern.compile(pattern));
  }
  
  public static <T>Selector<T> matches(Class<T> _class, String method, CodepointMatcher matcher) {
    return PropertySelector.<T>create(_class, method, matcher);
  }
  
  public static <T>Selector<T> equalTo(Class<T> _class, String method, Object obj) {
    return PropertySelector.<T>create(_class, method, Predicates.equalTo(obj));
  }
  
  public static <T>Selector<T> in(Class<T> _class, String method, Collection<T> items) {
    return PropertySelector.<T>create(_class, method, Predicates.in(items));
  }
  
  public static <T>Selector<T> in(Class<T> _class, String method, T... items) {
    return in(_class,method,Arrays.asList(items));
  }
  
  public final static Selector<Long> longNotNegativeOrNull = 
    new AbstractSelector<Long>() {
      public boolean select(Object item) {
        if (item == null) return false;
        checkArgument(item instanceof Long);
        Long i = (Long)item;
        return i >= 0;
      }
  };
  
  public final static Selector<Integer> intNotNegativeOrNull = 
    new AbstractSelector<Integer>() {
      public boolean select(Object item) {
        if (item == null) return false;
        checkArgument(item instanceof Integer);
        Integer i = (Integer)item;
        return i >= 0;
      }
  };
  
  public final static Selector<Short> shortNotNegativeOrNull = 
    new AbstractSelector<Short>() {
      public boolean select(Object item) {
        if (item == null) return false;
        checkArgument(item instanceof Short);
        Short i = (Short)item;
        return i >= 0;
      }
  };
  
}
