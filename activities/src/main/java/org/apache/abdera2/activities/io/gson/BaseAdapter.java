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
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.activation.MimeType;

import org.apache.abdera2.common.Discover;
import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.common.geo.IsoPosition;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.activities.model.*;
import org.apache.abdera2.activities.model.objects.AccountObject;
import org.apache.abdera2.activities.model.objects.Address;
import org.apache.abdera2.activities.model.objects.AlertObject;
import org.apache.abdera2.activities.model.objects.ArticleObject;
import org.apache.abdera2.activities.model.objects.AudioObject;
import org.apache.abdera2.activities.model.objects.BadgeObject;
import org.apache.abdera2.activities.model.objects.BinaryObject;
import org.apache.abdera2.activities.model.objects.BookObject;
import org.apache.abdera2.activities.model.objects.BookmarkObject;
import org.apache.abdera2.activities.model.objects.CommentObject;
import org.apache.abdera2.activities.model.objects.ErrorObject;
import org.apache.abdera2.activities.model.objects.EventObject;
import org.apache.abdera2.activities.model.objects.FileObject;
import org.apache.abdera2.activities.model.objects.GroupObject;
import org.apache.abdera2.activities.model.objects.ImageObject;
import org.apache.abdera2.activities.model.objects.Mood;
import org.apache.abdera2.activities.model.objects.MovieObject;
import org.apache.abdera2.activities.model.objects.NameObject;
import org.apache.abdera2.activities.model.objects.NoteObject;
import org.apache.abdera2.activities.model.objects.OfferObject;
import org.apache.abdera2.activities.model.objects.OrganizationObject;
import org.apache.abdera2.activities.model.objects.PersonObject;
import org.apache.abdera2.activities.model.objects.PlaceObject;
import org.apache.abdera2.activities.model.objects.ProductObject;
import org.apache.abdera2.activities.model.objects.QuestionObject;
import org.apache.abdera2.activities.model.objects.ReviewObject;
import org.apache.abdera2.activities.model.objects.ServiceObject;
import org.apache.abdera2.activities.model.objects.TaskObject;
import org.apache.abdera2.activities.model.objects.TvEpisodeObject;
import org.apache.abdera2.activities.model.objects.TvSeasonObject;
import org.apache.abdera2.activities.model.objects.TvSeriesObject;
import org.apache.abdera2.activities.model.objects.VersionObject;
import org.apache.abdera2.activities.model.objects.VideoObject;
import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

/**
 * (De)serialization of ASBase object
 */
@SuppressWarnings("rawtypes")
final class BaseAdapter 
  implements GsonTypeAdapter<ASBase> {

  private final Map<String,Class<?>> map = 
    new ConcurrentHashMap<String,Class<?>>();
  
  private final Map<String,Class<? extends ASObject.Builder>> objsmap =
    new ConcurrentHashMap<String,Class<? extends ASObject.Builder>>();
  
  public BaseAdapter() {
    initPropMap();
  }
  
  @SuppressWarnings("unchecked")
  private void initPropMap() {
    map.put("verb",Verb.class);
    map.put("url",IRI.class);
    map.put("fileUrl", IRI.class);
    map.put("gadget", IRI.class);
    map.put("updated", DateTime.class);
    map.put("published", DateTime.class);
    map.put("lang", Lang.class);
    map.put("@language", Lang.class);
    map.put("@base", IRI.class);
    map.put("$ref", IRI.class); // for JSON Reference...
    map.put("icon", MediaLink.class);
    map.put("image", MediaLink.class);
    map.put("totalItems", Integer.class);
    map.put("duration", Integer.class);
    map.put("height", Integer.class);
    map.put("location", PlaceObject.class);
    map.put("reactions", TaskObject.class);
    map.put("mood", Mood.class);
    map.put("address", Address.class);
    map.put("stream", MediaLink.class);
    map.put("fullImage", MediaLink.class);
    map.put("endTime", DateTime.class);
    map.put("startTime", DateTime.class);
    map.put("mimeType", MimeType.class);
    map.put("rating", Double.class);
    map.put("position", IsoPosition.class);
    map.put("etag", EntityTag.class);
    
    // From the replies spec
    map.put("attending", Collection.class);
    map.put("followers", Collection.class);
    map.put("following", Collection.class);
    map.put("friends", Collection.class);
    map.put("friend-requests", Collection.class);
    map.put("likes", Collection.class);
    map.put("notAttending", Collection.class);
    map.put("maybeAttending", Collection.class);
    map.put("members", Collection.class);
    map.put("replies", Collection.class);
    map.put("reviews", Collection.class);
    map.put("saves", Collection.class);
    map.put("shares", Collection.class);
    
    processType(
      objsmap,map,
      Address.AddressBuilder.class,
      Activity.ActivityBuilder.class,
      AlertObject.AlertBuilder.class,
      ArticleObject.Builder.class,
      AudioObject.AudioBuilder.class,
      BadgeObject.Builder.class,
      BookmarkObject.BookmarkBuilder.class,
      Collection.CollectionBuilder.class,
      CommentObject.Builder.class,
      EventObject.EventBuilder.class,
      FileObject.FileBuilder.class,
      GroupObject.Builder.class,
      ImageObject.ImageBuilder.class,
      NoteObject.Builder.class,
      PersonObject.PersonBuilder.class,
      PlaceObject.PlaceBuilder.class,
      ProductObject.ProductBuilder.class,
      QuestionObject.QuestionBuilder.class,
      ReviewObject.Builder.class,
      ServiceObject.Builder.class,
      VideoObject.VideoBuilder.class,
      ErrorObject.Builder.class,
      NameObject.NameBuilder.class,
      AccountObject.AccountBuilder.class,
      OrganizationObject.OrganizationBuilder.class,
      BookObject.BookBuilder.class,
      MovieObject.MovieBuilder.class,
      OfferObject.OfferBuilder.class,
      TvEpisodeObject.TvEpisodeBuilder.class,
      TvSeasonObject.TvSeasonBuilder.class,
      TvSeriesObject.TvSeriesBuilder.class,
      VersionObject.VersionBuilder.class,
      BinaryObject.BinaryBuilder.class,
      TaskObject.TaskBuilder.class
    );
  }
  
  private static <X extends ASObject.Builder>void processType(
    Map<String,Class<? extends ASObject.Builder>> map, 
    Map<String,Class<?>> propsmap,
    Class<? extends X>... _classes) {
    for (Class<? extends X> _class : _classes) {
      String name = AnnoUtil.getName(_class);
      map.put(name, _class);
      if (_class.isAnnotationPresent(Properties.class)) {
        Properties props = _class.getAnnotation(Properties.class);
        for (Property prop : props.value()) {
          String _propname = prop.name();
          Class<?> _propclass = prop.to();
          propsmap.put(_propname, _propclass);
        }
      }
    }
  }
  
  public <X extends ASObject.Builder> void addObjectMap(Class<? extends X>... _class) {
    processType(objsmap,map,_class);
  }
  
  public void addPropertyMap(String name, Class<?> _class) {
    map.put(name,_class);
  }
  
  public JsonElement serialize(
    ASBase asbase, 
    Type type,
    JsonSerializationContext context) {

    JsonObject el = new JsonObject();
    
    for (String key : asbase) {
      Object val = asbase.getProperty(key);
      if (val != null) {
        JsonElement value = null;
        if (val instanceof Verb)
          value = context.serialize(val, Verb.class);
        else 
          value = context.serialize(val, val.getClass());
        el.add(key, value);
      }
    }
    
    return el;
  }

  public ASBase deserialize(
    JsonElement el, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    JsonObject obj = (JsonObject)el;
    ASBase.Builder<?,?> builder;
    if (type == Collection.class)
      builder = Collection.makeCollection();
    else if (type == Activity.class)
      builder = Activity.makeActivity();
    else if (type == MediaLink.class)
      builder = MediaLink.makeMediaLink();
    else if (type == PlaceObject.class)
      builder = PlaceObject.makePlace();
    else if (type == Mood.class)
      builder = Mood.makeMood();
    else if (type == Address.class)
      builder = Address.makeAddress();
    else {
      JsonPrimitive ot = obj.getAsJsonPrimitive("objectType");
      if (ot != null) {
        String ots = ot.getAsString();
        Class<? extends ASObject.Builder> _class = objsmap.get(ots);
        if (_class != null) {
          builder = Discover.locate(_class, _class.getName());
          try {
            builder = _class.getConstructor(String.class).newInstance(ots);
          } catch (Throwable t) {}
          
        } else builder = ASObject.makeObject(ots);
      } else {
        if (obj.has("verb") && (obj.has("actor") || obj.has("object") || obj.has("target"))) {
          builder = Activity.makeActivity();
        } else if (obj.has("items")) {
          builder = Collection.makeCollection();
        } else {
          builder = ASObject.makeObject(); // anonymous
        }
      }
    }
    for (Entry<String,JsonElement> entry : obj.entrySet()) {
      String name = entry.getKey();
      if (name.equalsIgnoreCase("objectType")) continue;
      Class<?> _class = map.get(name);
      JsonElement val = entry.getValue();
      if (val.isJsonPrimitive()) {
        if (_class != null) {
          builder.set(name, context.deserialize(val,_class));
        } else {
          JsonPrimitive prim = val.getAsJsonPrimitive();
          if (prim.isBoolean()) 
            builder.set(name, prim.getAsBoolean());
          else if (prim.isNumber())
            builder.set(name, prim.getAsNumber());
          else {
            builder.set(name, prim.getAsString());
          }
        }
      } else if (val.isJsonArray()) {
        ImmutableList.Builder<Object> list = ImmutableList.builder();
        JsonArray arr = val.getAsJsonArray();
        processArray(arr, _class, context, list);
        builder.set(name, list.build());
      } else if (val.isJsonObject()) {
        if (map.containsKey(name)) {
          builder.set(name, context.deserialize(val, map.get(name)));
        } else
          builder.set(name, context.deserialize(val, ASObject.class));
      }
    }
    return builder.get();
  }

  private void processArray(
    JsonArray arr, 
    Class<?> _class, 
    JsonDeserializationContext context, 
    ImmutableList.Builder<Object> list) {
    for (JsonElement mem : arr) {
      if (mem.isJsonPrimitive()) {
        if (_class != null) {
          list.add(context.deserialize(mem, _class));
        } else {
          JsonPrimitive prim2 = (JsonPrimitive) mem;
          if (prim2.isBoolean())
            list.add(prim2.getAsBoolean());
          else if (prim2.isNumber())
            list.add(prim2.getAsNumber());
          else
            list.add(prim2.getAsString());
        }
      } else if (mem.isJsonObject()) {
        list.add(context.deserialize(mem, _class!=null?_class:ASObject.class));
      } else if (mem.isJsonArray()) {
        JsonArray array = mem.getAsJsonArray();
        ImmutableList.Builder<Object> objs = ImmutableList.builder();
        processArray(array,_class,context,objs);
        list.add(objs.build());
      }
    }
  }
  
  public Class<ASBase> getAdaptedClass() {
    return ASBase.class;
  }

}
