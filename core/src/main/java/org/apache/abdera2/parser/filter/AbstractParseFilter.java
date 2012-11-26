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

import javax.xml.namespace.QName;

import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.misc.MoreFunctions;

import com.google.common.base.Supplier;

@SuppressWarnings("unchecked")
public abstract class AbstractParseFilter 
  implements ParseFilter {

  public static abstract class Builder<E extends ParseFilter> implements Supplier<E> {
    
    protected byte flags = 0;
    Class<? extends UnacceptableException> _throw;

    public <X extends Builder<E>>X throwOnUnacceptable() {
      return (X)throwOnUnacceptable(UnacceptableException.class);
    }
    
    public <X extends Builder<E>>X throwOnUnacceptable(Class<? extends UnacceptableException> error) {
      this._throw = error;
      return (X)this;
    }
    
    private void toggle(boolean s, byte flag) {
      if (s)
          flags |= flag;
      else
          flags &= ~flag;
    }
    
    protected Builder() {
      withDefaults();
    }
    
    public <X extends Builder<E>>X withDefaults() {
      return (X)ignoreComments()
            .ignoreWhitespace()
            .ignoreProcessingInstructions();
    }
    
    public <X extends Builder<E>>X withoutDefaults() {
      toggle(false, COMMENTS);
      toggle(false, WHITESPACE);
      toggle(false, PI);
      return (X)this;
    }
    
    public <X extends Builder<E>>X ignoreComments() {
      toggle(true, COMMENTS);
      return (X)this;
    }

    public <X extends Builder<E>>X ignoreWhitespace() {
      toggle(true, (byte)WHITESPACE);
      return (X)this;
    }

    public <X extends Builder<E>>X ignoreProcessingInstructions() {
      toggle(true, (byte)PI);
      return (X)this;
    }
    
  }
  
    private static final long serialVersionUID = -1866308276050148524L;

    private static final byte COMMENTS = 1;
    private static final byte WHITESPACE = 2;
    private static final byte PI = 4;

    protected final byte flags;
    protected final Class<? extends UnacceptableException> _throw;

    protected AbstractParseFilter(Builder<?> builder) {
      this.flags = builder.flags;
      this._throw = builder._throw;
    }
    
    protected boolean checkThrow(
      boolean answer, 
      QName element, 
      QName attribute) {
      if (!answer && _throw != null) {
        try {
          throw MoreFunctions
            .createInstance(_throw)
            .apply(MoreFunctions
               .array(element,attribute));
        } catch (Throwable e) {
          throw ExceptionHelper.propogate(e);
        }
      }
      return answer;
    }
    
    private boolean check(byte flag) {
        return (flags & flag) == flag;
    }

    public boolean getIgnoreComments() {
        return check(COMMENTS);
    }

    public boolean getIgnoreProcessingInstructions() {
        return check(PI);
    }

    public boolean getIgnoreWhitespace() {
        return check(WHITESPACE);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
