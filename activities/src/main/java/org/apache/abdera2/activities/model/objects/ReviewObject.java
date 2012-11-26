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
import org.apache.abdera2.common.anno.Name;

public class ReviewObject 
  extends ASObject {

  
  public static <T extends ReviewObject>Builder makeReview() {
    return new Builder("review");
  }
  @Name("review")
  public static final class Builder 
    extends ASObject.Builder<ReviewObject,Builder> {
    public Builder(String objectType) {
      super(objectType,ReviewObject.class,Builder.class);
    }
    public Builder() {
      super(ReviewObject.class,Builder.class);
    }
    public Builder(Map<String,Object> map) {
      super(map,ReviewObject.class,Builder.class);
    }
    public Builder of(ASObject obj) {
      set("of",obj);
      return this;
    }
    public Builder template() {
      return new Builder(map.build());
    }
  }
  
  public ReviewObject(Map<String,Object> map) {
    super(map,Builder.class,ReviewObject.class);
  }
  
  public <X extends ReviewObject, M extends ASObject.Builder<X,M>>ReviewObject(Map<String,Object> map,Class<M> _class,Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public <X extends ASObject>X getOf() {
    return this.<X>getProperty("of");
  }
}
