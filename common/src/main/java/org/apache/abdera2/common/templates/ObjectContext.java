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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import static com.google.common.base.Preconditions.*;
import static org.apache.abdera2.common.misc.MorePreconditions.*;

import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.templates.CachingContext;
import org.apache.abdera2.common.templates.ObjectContext;
import org.apache.abdera2.common.anno.Name;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("unchecked")
public final class ObjectContext extends CachingContext {

    private static final long serialVersionUID = -1387599933658718221L;
    private final Object target;
    private final ImmutableMap<String, AccessibleObject> accessors;

    public ObjectContext(Object object) {
      this(object, false);
    }

    public ObjectContext(Object object, boolean isiri) {
      super(isiri);
      checkNotNull(object);
      this.target = object;
      this.accessors = initMethods();
    }
    
    private ImmutableMap<String,AccessibleObject> initMethods() {
      ImmutableMap.Builder<String, AccessibleObject> accessors = 
        ImmutableMap.builder();
      Class<?> _class = target.getClass();
      checkArguments(!_class.isAnnotation(),
                     !_class.isArray(),
                     !_class.isEnum(),
                     !_class.isPrimitive());
      if (!_class.isInterface()) {
        Field[] fields = _class.getFields();
        for (Field field : fields)
          if (!Modifier.isPrivate(field.getModifiers()))
            accessors.put(getName(field), field);
      }
      Method[] methods = _class.getMethods();
      for (Method method : methods) {
        String name = method.getName();
        if (!Modifier.isPrivate(method.getModifiers()) && method.getParameterTypes().length == 0
            && !method.getReturnType().equals(Void.class)
            && !isReserved(name))
          accessors.put(getName(method), method);
      }
      return accessors.build();
    }

    private String getName(AccessibleObject object) {
      String name = null;
      Name varName = object.getAnnotation(Name.class);
      if (varName != null)
          return varName.value();
      if (object instanceof Field)
          name = ((Field)object).getName().toLowerCase();
      else if (object instanceof Method) {
        name = ((Method)object).getName().toLowerCase();
        if (name.startsWith("get"))
          name = name.substring(3);
        else if (name.startsWith("is"))
          name = name.substring(2);
      }
      return name;
    }

    private boolean isReserved(String name) {
        return (name.equals("toString") || name.equals("hashCode")
            || name.equals("notify")
            || name.equals("notifyAll")
            || name.equals("getClass") || name.equals("wait"));
    }

    @Override
    protected <T> T resolveActual(String var) {
        try {
            var = var.toLowerCase();
            AccessibleObject accessor = accessors.get(var);
            if (accessor == null)
                return null;
            if (accessor instanceof Method) {
                Method method = (Method)accessor;
                return (T)method.invoke(target);
            } else if (accessor instanceof Field) {
                Field field = (Field)accessor;
                return (T)field.get(target);
            } else
                return null;
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Accessor: " + var, e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Accessor: " + var, e);
        }
    }

    public Iterator<String> iterator() {
        return accessors.keySet().iterator();
    }

    @Override
    public int hashCode() {
      return MoreFunctions.genHashCode(1, target);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ObjectContext other = (ObjectContext)obj;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        return true;
    }

    public boolean contains(String var) {
      return accessors.containsKey(var);
    }
}
