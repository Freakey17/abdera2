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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;

import javax.activation.MimeType;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.apache.abdera2.common.geo.IsoPosition;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.templates.Template;
import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.AbstractCollectionWriter;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.CollectionWriter;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.activities.model.MediaLink;
import org.apache.abdera2.activities.model.TypeAdapter;
import org.apache.abdera2.activities.model.Verb;
import org.apache.abdera2.activities.model.objects.Address;
import org.apache.abdera2.activities.model.objects.Mood;
import org.apache.abdera2.activities.model.objects.PlaceObject;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

@SuppressWarnings("unchecked")
public class GsonIO extends IO {
  
  public static class Builder extends IO.Builder {
       
    private final BaseAdapter asbs = new BaseAdapter();
    
    public IO get() {
      return new GsonIO(
        this,
        asbs,
        gson(
          prettyprint,
          asbs,
          adapters.build()));
    }
   
    public Builder property(String name, Class<?> _class) {
      asbs.addPropertyMap(name, _class);
      return this;
    }

    @SuppressWarnings({ "rawtypes" })
    public <X extends ASObject.Builder> Builder object(
        Class<? extends X>... _class) {
      asbs.addObjectMap(_class);
      return this;
    }    
  }
  
  static Gson gson(Boolean pretty, BaseAdapter asbs, Iterable<TypeAdapter<?>> adapters) {
    GsonBuilder gb = new GsonBuilder()   
      .registerTypeHierarchyAdapter(Verb.class, new VerbAdapter())
      .registerTypeHierarchyAdapter(Lang.class, new LangAdapter())
      .registerTypeHierarchyAdapter(ASBase.class,  asbs)
      .registerTypeHierarchyAdapter(Multimap.class, new MultimapAdapter())
      .registerTypeHierarchyAdapter(MimeType.class, new MimeTypeAdapter())
      .registerTypeAdapter(ASBase.class, asbs)
      .registerTypeAdapter(Date.class, new DateAdapter())
      .registerTypeAdapter(DateTime.class, new DateTimeAdapter())
      .registerTypeAdapter(Duration.class, new DurationAdapter())
      .registerTypeAdapter(Interval.class, new IntervalAdapter())
      .registerTypeAdapter(Activity.class,  asbs)
      .registerTypeAdapter(PlaceObject.class, asbs)
      .registerTypeAdapter(Mood.class, asbs)
      .registerTypeAdapter(Address.class, asbs)
      .registerTypeAdapter(IRI.class, new IriAdapter())
      .registerTypeAdapter(IsoPosition.class, new PositionAdapter())
      .registerTypeAdapter(EntityTag.class, new EntityTagAdapter())
      .registerTypeAdapter(Template.class, new TemplateAdapter())
      .registerTypeAdapter(MimeType.class, new MimeTypeAdapter());
    for(TypeAdapter<?> adapter : adapters)
      if (adapter instanceof GsonTypeAdapter)
        gb.registerTypeAdapter(
          adapter.getAdaptedClass(), adapter);
    gb.enableComplexMapKeySerialization();
    if (pretty)
      gb.setPrettyPrinting();
    return gb.create();
  }
  
  private final Gson gson;
  
  GsonIO(Builder builder, BaseAdapter asbs, Gson gson) {
    super(builder);
    this.gson = gson;
  }
  
  public String write(ASBase base) {
    return gson.toJson(base);
  }
  
  public void write(ASBase base, Appendable writer) {
    gson.toJson(base, writer);
  }

  public <T extends ASBase>T read(Reader reader) {
    return gson.<T>fromJson(reader, ASBase.class);
  }
  
  public <T extends ASBase>T read(String json) {
    return gson.<T>fromJson(json, ASBase.class);
  }
  
  public Activity readActivity(Reader reader) {
    return gson.fromJson(reader, Activity.class);
  }
  
  public Activity readActivity(String json) {
    return gson.fromJson(json, Activity.class);
  }
  
  public <T extends ASObject>Collection<T> readCollection(Reader reader) {
    return gson.fromJson(reader, Collection.class);
  }
  
  public <T extends ASObject>Collection<T> readCollection(String json) {
    return gson.fromJson(json, Collection.class);
  }
  
  public <T extends ASObject>T readObject(Reader reader) {
    return gson.<T>fromJson(reader, ASObject.class);
  }
  
  public <T extends ASObject>T readObject(String json) {
    return gson.<T>fromJson(json, ASObject.class);
  }
  
  public MediaLink readMediaLink(Reader reader) {
    return gson.fromJson(reader, MediaLink.class);
  }
  
  public MediaLink readMediaLink(String json) {
    return gson.fromJson(json, MediaLink.class);
  }
  
  public void writeCollection(
    OutputStream out, 
    String charset,
    ASBase header,
    Iterable<ASObject> objects) {
    try {
      OutputStreamWriter outw = 
        new OutputStreamWriter(out,charset);
      writeCollection(outw,header,objects);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public void writeCollection(
    Writer out,
    ASBase header,
    Iterable<ASObject> objects) {
    try {
      JsonWriter writer = 
        new JsonWriter(out)
          .beginObject();
      if (header != null) {
        for (String name : header) {
          Object val = header.getProperty(name);
          if (val != null) {
            writer.name(name);
            gson.toJson(val,val.getClass(),writer);
          }
          else writer.nullValue();
        }
      }
      writer.name("items")
            .beginArray();
      for (ASObject obj : objects)
        gson.toJson(obj,ASBase.class,writer);
      writer.endArray()
            .endObject()
            .flush();
      if (autoclose)
        writer.close();
    } catch (IOException t) {
      throw new RuntimeException(t);
    }
  }
  
  public CollectionWriter getCollectionWriter(Writer out) {
    return new GsonCollectionWriter(gson,out, autoclose);
  }
  
  private static class GsonCollectionWriter
    extends AbstractCollectionWriter {
    private final JsonWriter writer;
    private final Gson gson;
    private final boolean autoclose;
    
    GsonCollectionWriter(Gson gson, Writer out, boolean autoclose) {
      this.gson = gson;
      this.writer = new JsonWriter(out);
      this.autoclose = autoclose;
      try {
        writer.beginObject();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    public void complete() {
      try {
        if (_items) writer.endArray();
        writer.endObject()
              .flush();
        if (autoclose) 
          writer.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    @Override
    protected void write(String name, Object val) {
      try {
        writer.name(name);
        if (val != null)
          gson.toJson(val,val.getClass(),writer);
        else writer.nullValue();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    @Override
    protected void startItems() {
      try {
        writer.name("items")
              .beginArray();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    @Override
    protected void writeItem(ASObject object) {
      gson.toJson(object,ASBase.class,writer);
    }
    
    protected void flush() {
      try {
        writer.flush();
      } catch (IOException t) {
        throw new RuntimeException(t);
      }
    }
  }
}
