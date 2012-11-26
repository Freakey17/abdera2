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
package org.apache.abdera2.parser.filter;

import java.util.Set;
import javax.xml.namespace.QName;
import com.google.common.collect.ImmutableSet;


/**
 * <p>
 * A simple compound parse filter that allows us to apply multiple parse filters to a single parse operation.
 * </p>
 * 
 * <pre>
 * CompoundParseFilter filter =
 *     new CompoundParseFilter(CompoundParseFilter.Condition.ACCEPTABLE_TO_ALL, new SafeContentWhiteListParseFilter(),
 *                             new MyWhiteListParseFilter(), new MySomeOtherKindOfParseFilter());
 * options.setParseFilter(filter);
 * </pre>
 */
public class CompoundParseFilter 
  extends AbstractParseFilter 
  implements ParseFilter {

    private static final long serialVersionUID = -7871289035422204698L;

    public enum Condition {
        ACCEPTABLE_TO_ALL, 
        ACCEPTABLE_TO_ANY, 
        UNACCEPTABLE_TO_ALL, 
        UNACCEPTABLE_TO_ANY;

        byte evaluate(boolean b) {
            if (b) {
                switch (this) {
                    case ACCEPTABLE_TO_ANY:
                        return 1;
                    case UNACCEPTABLE_TO_ALL:
                        return -1;
                }
            } else {
                switch (this) {
                    case ACCEPTABLE_TO_ALL:
                        return -1;
                    case UNACCEPTABLE_TO_ANY:
                        return 1;
                }
            }
            return 0;
        }
    };
    
    public static ParseFilter acceptableToAll(ParseFilter... filters) {
      return makeAcceptableToAll().filters(filters).get();
    }
    
    public static ParseFilter acceptableToAny(ParseFilter... filters) {
      return makeAcceptableToAny().filters(filters).get();
    }
    
    public static ParseFilter unacceptableToAll(ParseFilter... filters) {
      return makeUnacceptableToAll().filters(filters).get();
    }
    
    public static ParseFilter unacceptableToAny(ParseFilter... filters) {
      return makeUnacceptableToAny().filters(filters).get();
    }
    
    public static Builder makeAcceptableToAll() {
      return make().condition(Condition.ACCEPTABLE_TO_ALL);
    }
    
    public static Builder makeAcceptableToAny() {
      return make().condition(Condition.ACCEPTABLE_TO_ANY);
    }
    
    public static Builder makeUnacceptableToAll() {
      return make().condition(Condition.UNACCEPTABLE_TO_ALL);
    }
    
    public static Builder makeUnacceptableToAny() {
      return make().condition(Condition.UNACCEPTABLE_TO_ANY);
    }
    
    public static Builder make() {
      return new Builder();
    }
    
    public static class Builder 
      extends AbstractParseFilter.Builder<CompoundParseFilter> {

      Condition condition;
      final ImmutableSet.Builder<ParseFilter> filters = 
        ImmutableSet.builder();
      
      public CompoundParseFilter get() {
        return new CompoundParseFilter(this);
      }
      
      public Builder condition(Condition condition) {
        this.condition = condition;
        return this;
      }
      
      public Builder filter(ParseFilter filter) {
        this.filters.add(filter);
        return this;
      }
      
      public Builder filters(ParseFilter... filters) {
        for (ParseFilter filter : filters)
          filter(filter);
        return this;
      }
    }

    protected final Condition condition;
    protected final Set<ParseFilter> filters;

    protected CompoundParseFilter(Builder builder) {
      super(builder);
      this.condition = builder.condition;
      this.filters = builder.filters.build();
    }

    private Iterable<ParseFilter> getFilters() {
        return filters;
    }

    public boolean acceptable(QName qname) {
      for (ParseFilter filter : getFilters()) {
        switch (condition.evaluate(filter.acceptable(qname))) {
          case 1:
            return true;
          case -1:
            return checkThrow(false,qname,null);
        }
      }
      return true;
    }

    public boolean acceptable(QName qname, QName attribute) {
      for (ParseFilter filter : getFilters()) {
        switch (condition.evaluate(filter.acceptable(qname, attribute))) {
          case 1:
            return true;
          case -1:
            return checkThrow(false,qname,attribute);
        }
      }
      return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
