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

public class ProductObject 
  extends ASObject {
  public static final String FULLIMAGE = "fullImage";
  
  public ProductObject(Map<String,Object> map) {
    super(map,ProductBuilder.class,ProductObject.class);
  }
  
  public <X extends ProductObject, M extends Builder<X,M>>ProductObject(Map<String,Object> map, Class<M> _class, Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public MediaLink getFullImage() {
    return getProperty(FULLIMAGE);
  }

  public static ProductBuilder makeProduct() {
    return new ProductBuilder("product");
  }
  
  @Name("product")
  public static final class ProductBuilder extends Builder<ProductObject,ProductBuilder> {

    public ProductBuilder() {
      super(ProductObject.class,ProductBuilder.class);
    }

    public ProductBuilder(Map<String, Object> map) {
      super(map, ProductObject.class,ProductBuilder.class);
    }

    public ProductBuilder(String objectType) {
      super(objectType, ProductObject.class,ProductBuilder.class);
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends ProductObject, M extends Builder<X,M>>
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
    public M fullImage(Supplier<MediaLink> fullImage) {
      return fullImage(fullImage.get());
    }
    public M fullImage(MediaLink fullImage) {
      set(FULLIMAGE,fullImage);
      return (M)this;
    }
  }
}
