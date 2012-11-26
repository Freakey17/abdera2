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
import static com.google.common.base.Preconditions.*;
import java.util.Iterator;

import org.apache.abdera2.common.misc.MoreFunctions;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings("unchecked")
public class DefaultingContext 
  extends DelegatingContext {

  private static final long serialVersionUID = 3395776996994628136L;
  private final Context defaults;
  
  public DefaultingContext(Context main, Context defaults) {
    super(main);
    checkNotNull(defaults);
    this.defaults = defaults;
  }

  public <T> T resolve(String var) {
    T t = (T)super.resolve(var);
    return t != null ? t : (T)defaults.resolve(var);
  }

  public Iterator<String> iterator() {
    ImmutableSet.Builder<String> set = ImmutableSet.builder();
    for (String name : subcontext)
      set.add(name);
    for (String name : defaults)
      set.add(name);
    return set.build().iterator();
  }
  
  public boolean contains(String var) {
    boolean a = subcontext.contains(var);
    return a ? a : defaults.contains(var);
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("Subcontext: " + subcontext);
    buf.append("\n");
    buf.append("Default Context: " + defaults);
    return buf.toString();
  }

  @Override
  public int hashCode() {
    return MoreFunctions.genHashCode(1, defaults);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DefaultingContext other = (DefaultingContext) obj;
    if (defaults == null) {
      if (other.defaults != null)
        return false;
    } else if (!defaults.equals(other.defaults))
      return false;
    return true;
  }
  
}
