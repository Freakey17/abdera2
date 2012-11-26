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

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.MediaLink;
import org.apache.abdera2.common.anno.Name;

import com.google.common.base.Supplier;

public class ImageObject 
  extends ASObject {

  public static final String FULLIMAGE = "fullImage";
  
  public ImageObject(Map<String,Object> map) {
    super(map,ImageBuilder.class,ImageObject.class);
  }
  
  public <X extends ImageObject, M extends Builder<X,M>>ImageObject(Map<String,Object> map, Class<M> _class,Class<X>_obj) {
    super(map,_class,_obj);
  }
    
  public MediaLink getFullImage() {
    return getProperty(FULLIMAGE);
  }

  public static ImageBuilder makeImage() {
    return new ImageBuilder("image");
  }
  
  @Name("image")
  public static final class ImageBuilder extends Builder<ImageObject,ImageBuilder> {
    public ImageBuilder() {
      super(ImageObject.class, ImageBuilder.class);
    }
    public ImageBuilder(Map<String, Object> map) {
      super(map, ImageObject.class, ImageBuilder.class);
    }
    public ImageBuilder(String objectType) {
      super(objectType,ImageObject.class, ImageBuilder.class);
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends ImageObject, M extends Builder<X,M>> 
    extends ASObject.Builder<X,M> {
    public Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M fillImage(Supplier<MediaLink> fullImage) {
      return fullImage(fullImage.get());
    }
    public M fullImage(MediaLink fullImage) {
      set(FULLIMAGE, fullImage);
      return (M)this;
    }
  }
}
