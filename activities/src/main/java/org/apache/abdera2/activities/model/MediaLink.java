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
package org.apache.abdera2.activities.model;

import java.util.Map;

import org.apache.abdera2.common.iri.IRI;

/**
 * Represents the Activity Streams Media Link construct.
 */
public final class MediaLink extends ASBase {

  public static final String DURATION = "duration";
  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  public static final String URL = "url";
  
  public static Builder makeMediaLink() {
    return new Builder();
  }
  
  public static MediaLink makeMediaLink(String iri) {
    return makeMediaLink().url(iri).get();
  }
  
  public static MediaLink makeMediaLink(IRI iri) {
    return makeMediaLink().url(iri).get();
  }
  
  public static MediaLink makeMediaLink(
    String iri,
    int height,
    int width,
    int duration) {
    return makeMediaLink()
      .url(iri)
      .height(height)
      .width(width)
      .duration(duration)
      .get();
  }
  
  public static MediaLink makeMediaLink(
    IRI iri,
    int height,
    int width,
    int duration) {
    return makeMediaLink()
      .url(iri)
      .height(height)
      .width(width)
      .duration(duration)
      .get();
  }
  
  public final static class Builder 
    extends ASBase.Builder<MediaLink,Builder> {

    public Builder() {
      super(MediaLink.class,Builder.class);
    }
    
    public Builder(Map<String,Object> map) {
      super(map,MediaLink.class,Builder.class);
    }
    
    public Builder duration(int duration) {
      set(DURATION,Math.max(0,duration));
      return this;
    }
    
    public Builder height(int height) {
      set(HEIGHT,Math.max(0,height));
      return this;
    }
    
    public Builder width(int width) {
      set(WIDTH,Math.max(0,width));
      return this;
    }
    
    public Builder url(IRI iri) {
      set(URL,iri);
      try {
        if (isExperimentalEnabled())
          link("alternate",iri);
      } catch (IllegalStateException e) {}
      return this;
    }
    
    public Builder url(String uri) {
      return url(new IRI(uri));
    }
  }
  
  public MediaLink(Map<String,Object> map) {
    super(map,Builder.class,MediaLink.class);
  }
  
  public int getDuration() {
    return (Integer)getProperty(DURATION);
  }
  
  public int getHeight() {
    return (Integer)getProperty(HEIGHT);
  }
  
  public int getWidth() {
    return (Integer)getProperty(WIDTH);
  }
  
  public IRI getUrl() {
    return getProperty(URL);
  }
  
}
