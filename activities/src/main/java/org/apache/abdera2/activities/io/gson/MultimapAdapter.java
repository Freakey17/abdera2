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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.common.misc.ExceptionHelper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

@SuppressWarnings({"rawtypes","unchecked"})
@AdaptedType(Multimap.class)
class MultimapAdapter
  implements GsonTypeAdapter<Multimap> {
  
  public Class<Multimap> getAdaptedClass() {
    return Multimap.class;
  }

  public JsonElement serialize(
    Multimap src, 
    Type typeOfSrc,
    JsonSerializationContext context) {
      return context.serialize(src.asMap(), Map.class);
  }

  protected static ImmutableList<Object> arraydes(
    JsonArray array,
    JsonDeserializationContext context) {
    ImmutableList.Builder<Object> builder = 
      ImmutableList.builder();
    for (JsonElement child : array)
      if (child.isJsonArray())
        builder.add(arraydes(child.getAsJsonArray(),context));
      else if (child.isJsonObject())
        builder.add(context.deserialize(child, ASBase.class));
      else if (child.isJsonPrimitive())
        builder.add(primdes(child.getAsJsonPrimitive()));
    return builder.build();
  }
  
  protected static Object primdes(JsonPrimitive prim) {
    if (prim.isBoolean())
      return prim.getAsBoolean();
    else if (prim.isNumber())
      return prim.getAsNumber();
    else return prim.getAsString();
  }
  
  public Multimap deserialize(
    JsonElement json, 
    Type typeOfT,
    JsonDeserializationContext context) 
      throws JsonParseException {
    Multimap mm = create(typeOfT);
    JsonObject obj = json.getAsJsonObject();
    for (Map.Entry<String,JsonElement> entry : obj.entrySet()) {
      String key = entry.getKey();
      JsonElement val = entry.getValue();
      if (val.isJsonArray())
        for (JsonElement el : val.getAsJsonArray())
          if (el.isJsonArray())
            mm.put(key, arraydes(el.getAsJsonArray(),context));
          else if (el.isJsonObject())
            mm.put(key, context.deserialize(el, ASBase.class));
          else if (el.isJsonNull())
            mm.put(key, null);
          else if (el.isJsonPrimitive())
            mm.put(key,primdes(el.getAsJsonPrimitive()));
      else if (val.isJsonObject()) 
        mm.put(key, context.deserialize(val, ASBase.class));
      else if (val.isJsonPrimitive())
        mm.put(key, primdes(val.getAsJsonPrimitive()));
    }
    return mm;
  }  
  
  private static Multimap create(Type typeOfT) {
    try {
      Class<? extends Multimap> _class = (Class<? extends Multimap>)typeOfT;
      if (_class == Multimap.class) _class = LinkedHashMultimap.class;
      Method method = _class.getMethod("create");
      return (Multimap) method.invoke(_class);
    } catch (Throwable t) {
      throw ExceptionHelper.propogate(t);
    }
  }
}
