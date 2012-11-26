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
package org.apache.abdera2.activities.extra;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.AbstractCollectionWriter;
import org.apache.abdera2.activities.model.Collection;

/**
 * Simple implementation of the CollectionWriter interface that builds
 * an in-memory Collection using the CollectionWriter's streaming interface.
 */
@SuppressWarnings({"unchecked"})
public class SimpleCollectionWriter<T extends ASObject> 
  extends AbstractCollectionWriter {

  private final Collection.CollectionBuilder<T> builder = 
    Collection.makeCollection();
  
  @Override
  protected void write(String name, Object val) {
    builder.set(name, val);
  }

  protected void startItems() {}

  protected void writeItem(ASObject object) {
    builder.item((T)object);
  }

  public void complete() {}

  public Collection<T> getCollection() {
    return builder.get();
  }
}
