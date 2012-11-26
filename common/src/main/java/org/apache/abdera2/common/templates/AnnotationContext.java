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
package org.apache.abdera2.common.templates;

import org.apache.abdera2.common.anno.Param;
import static com.google.common.base.Preconditions.*;

public class AnnotationContext extends MapContext {

  private static final long serialVersionUID = 3092158634973274492L;
  
  private void process(org.apache.abdera2.common.anno.Context context) {
    for (Param param : context.value())
      put(param.name().toLowerCase(),param.value());
  }
  
  public AnnotationContext(org.apache.abdera2.common.anno.Context context) {
    process(context);
  }
  
  public AnnotationContext(Object object) {
    checkNotNull(object);
    org.apache.abdera2.common.anno.Context context = getContext(object);
    checkNotNull(context);
    process(context);
  }
  
  public static org.apache.abdera2.common.anno.Context getContext(Object object) {
    checkNotNull(object);
    Class<?> _class = 
      object instanceof Class<?> ? 
          (Class<?>)object : object.getClass();
    if (_class.isAnnotationPresent(org.apache.abdera2.common.anno.Context.class)) {
      return
        _class.getAnnotation(org.apache.abdera2.common.anno.Context.class);
    }
    return null;
  }
  
}
