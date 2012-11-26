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
package org.apache.abdera2.activities.io.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import static com.google.common.base.Preconditions.*;

public abstract class SimpleAdapter<T>
  implements GsonTypeAdapter<T> {

  private final Class<T> _class;
  
  public SimpleAdapter() {
    this._class = _getAdaptedClass(this.getClass());
  }
  
  @SuppressWarnings("unchecked")
  private static <T>Class<T> _getAdaptedClass(Class<?> _class) {
    checkArgument(_class.isAnnotationPresent(AdaptedType.class));
    AdaptedType at = _class.getAnnotation(AdaptedType.class);
    return (Class<T>) at.value();
  }
  
  public SimpleAdapter(Class<T> _class) {
    this._class = _class;
  }
  
  public Class<T> getAdaptedClass() {
    return _class;
  }

  public JsonElement serialize(
    T t, 
    Type type, 
    JsonSerializationContext context) {
      return context.serialize(serialize(t));
  }

  protected String serialize(T t) {
    return t != null ? t.toString() : null;
  }
  
  protected abstract T deserialize(String v);
  
  public T deserialize(
    JsonElement json, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    return deserialize(json.getAsJsonPrimitive().getAsString());
  }
}