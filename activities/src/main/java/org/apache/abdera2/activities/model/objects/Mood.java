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
package org.apache.abdera2.activities.model.objects;

import java.util.Map;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.MediaLink;
import org.apache.abdera2.common.iri.IRI;

import com.google.common.base.Supplier;

public final class Mood extends ASBase {

  public static final String IMAGE = "image";
  public static final String DISPLAYNAME = "displayName";
  
  public Mood(Map<String,Object> map) {
    super(map,Builder.class,Mood.class);
  }
  
  public String getDisplayName() {
    return getProperty(DISPLAYNAME);
  }
  
  public MediaLink getImage() {
    return getProperty(IMAGE);
  }
  
  public String toString() {
    return getDisplayName();
  }
  
  public static Builder makeMood() {
    return new Builder();
  }
  
  public static Mood makeMood(String name, MediaLink image) {
    return makeMood().displayName(name).image(image).get();
  }
  
  public static Mood makeMood(String name, String image) {
    return makeMood().displayName(name).image(MediaLink.makeMediaLink(image)).get();
  }
  
  public static Mood makeMood(String name, IRI image) {
    return makeMood().displayName(name).image(MediaLink.makeMediaLink(image)).get();
  }

  public static final class Builder extends ASBase.Builder<Mood,Builder> {
    public Builder() {
      super(Mood.class,Builder.class);
    }
    protected Builder(Map<String,Object> map) {
      super(map,Mood.class,Builder.class);
    }
    public Builder displayName(String dn) {
      set(DISPLAYNAME,dn);
      return this;
    }
    public Builder image(Supplier<MediaLink> link) {
      return image(link.get());
    }
    public Builder image(MediaLink link) {
      set(IMAGE, link);
      return this;
    }
  }
}
