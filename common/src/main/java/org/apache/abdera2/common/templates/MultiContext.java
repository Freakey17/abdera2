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
package org.apache.abdera2.common.templates;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.*;

public final class MultiContext 
  extends CachingContext {

  public static Builder make() {
    return new Builder();
  }
  
  public static final class Builder implements Supplier<Context> {

    final ImmutableSet.Builder<Context> contexts = 
      ImmutableSet.builder();
    boolean isiri;
    
    public Builder iri() {
      this.isiri = false;
      return this;
    }
    
    public Builder with(Context context) {
      checkNotNull(context);
      this.contexts.add(context);
      return this;
    }
    
    @SuppressWarnings("unchecked")
    public Builder with(Object object) {
      checkNotNull(object);
      this.contexts.add(
        object instanceof Context ? 
          (Context)object :
          object instanceof Map ?
            new MapContext((Map<String,Object>)object) :
            object instanceof Supplier ?
              new ObjectContext(((Supplier<?>)object).get()) :
              new ObjectContext(object)
      );
      return this;
    }
    
    public Context get() {
      return new MultiContext(this);
    }
    
  }
  
  private static final long serialVersionUID = 1691294411780004133L;
  private final Set<Context> contexts;
  
  MultiContext(Builder builder) {
    super(builder.isiri);
    this.contexts = builder.contexts.build();
  } 
  public boolean contains(String var) {
    for (Context context : contexts)
      if (context.contains(var))
        return true;
    return false;
  }
  public Iterator<String> iterator() {
    ImmutableSet.Builder<String> names = ImmutableSet.builder();
    for (Context context : contexts) 
      for (String name : context)
        names.add(name);
    return names.build().iterator();
  }
  protected <T> T resolveActual(String var) {
    for (Context context : contexts)
      if (context.contains(var))
        return context.<T>resolve(var);
    return null;
  }
}
