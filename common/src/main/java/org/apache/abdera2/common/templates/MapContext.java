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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.templates.Context;
import org.apache.abdera2.common.templates.MapContext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import static com.google.common.base.Preconditions.*;

/**
 * Context implementation based on a HashMap
 */
@SuppressWarnings("unchecked")
public class MapContext 
  extends HashMap<String, Object> 
  implements Context {

  private static final long serialVersionUID = 2206000974505975049L;

  private final boolean isiri;

  public static MapContext with(Map<? extends String,? extends Object> map) {
    MapContext ctx = new MapContext();
    ctx.putAll(map);
    return ctx;
  }
  
  public static MapContext with(Map<? extends String,? extends Object> map, boolean isiri) {
    MapContext ctx = new MapContext(isiri);
    ctx.putAll(map);
    return ctx;
  }
  
  public MapContext() {
    this.isiri = false;
  }

  public MapContext(boolean isiri) {
    this.isiri = isiri;
  }
  
  public MapContext(Map<String, Object> map) {
      super(map);
      this.isiri = false;
  }

  public MapContext(Map<String, Object> map, boolean isiri) {
      super(map);
      this.isiri = isiri;
  }
  
  public MapContext(Context context) {
    checkNotNull(context);
    this.isiri = context.isIri();
    for (String name : context)
      put(name, context.resolve(name));
  }

  public <T> T resolve(String var) {
      return (T)get(var);
  }

  public boolean isIri() {
      return isiri;
  }

  public Iterator<String> iterator() {
      return keySet().iterator();
  }

  @Override
  public int hashCode() {
    return MoreFunctions.genHashCode(super.hashCode(), isiri);
  }

  @Override
  public boolean equals(Object obj) {
      if (this == obj)
          return true;
      if (!super.equals(obj))
          return false;
      if (getClass() != obj.getClass())
          return false;
      final MapContext other = (MapContext)obj;
      if (isiri != other.isiri)
          return false;
      return true;
  }

  public boolean contains(String var) {
    return containsKey(var);
  }
  
  public static MapContext fromMultimap(
    Multimap<String,Object> multimap) {
      MapContext mc = new MapContext();
      mc.putAll(multimap.asMap());
      return mc;
  }
  
  /**
   * Returns an Immutable version of this MapContext
   */
  public MapContext immutable() {
    return new ImmutableMapContext(this);
  }
  
  private static final class ImmutableMapContext extends MapContext {
    private static final long serialVersionUID = 7979187415358996598L;
    ImmutableMapContext(MapContext context) {
      super((Map<String,Object>)context);
    }
    public void clear() {
      throw new UnsupportedOperationException();
    }
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
      return ImmutableSet.copyOf(super.entrySet());
    }
    public Set<String> keySet() {
      return ImmutableSet.copyOf(super.keySet());
    }
    public Object put(String arg0, Object arg1) {
      throw new UnsupportedOperationException();
    }
    public void putAll(Map<? extends String, ? extends Object> arg0) {
      throw new UnsupportedOperationException();
    }
    public Object remove(Object arg0) {
      throw new UnsupportedOperationException();
    }
    public Collection<Object> values() {
      return ImmutableList.copyOf(super.values());
    }
    
  }
}
