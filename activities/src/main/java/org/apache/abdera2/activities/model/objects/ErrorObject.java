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

import org.apache.abdera2.activities.io.gson.Properties;
import org.apache.abdera2.activities.io.gson.Property;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;

public final class ErrorObject extends ASObject {
  public static final String TYPE = "error";
  public ErrorObject(Map<String,Object> map) {
    super(map,Builder.class,ErrorObject.class);
  }  

  public int getCode() {
    return getPropertyInt("code");
  }
  
  public static Builder makeError() {
    return new Builder("error");
  }
  
  public static ErrorObject makeError(int code, String message) {
    return makeError()
      .code(code)
      .displayName(message)
      .get();
  }
  
  @Name("error")
  @Properties(@Property(name="code",to=Integer.class))
  public final static class Builder 
    extends ASObject.Builder<ErrorObject,Builder> {
    public Builder() {
      super(ErrorObject.class,Builder.class);
    }
    public Builder(String objectType) {
      super(objectType,ErrorObject.class,Builder.class);
    }
    public Builder(Map<String,Object> map) {
      super(map,ErrorObject.class,Builder.class);
    }
    public Builder template() {
      return new Builder(map.build());
    }
    public Builder code(int code) {
      set("code",code);
      return this;
    }
    public ErrorObject get() {
      return new ErrorObject(map.build());
    }
  }
}
