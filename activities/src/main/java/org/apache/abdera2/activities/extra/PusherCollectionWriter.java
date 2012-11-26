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

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASBase.Builder;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.CollectionWriter;
import org.apache.abdera2.common.pusher.Pusher;

/**
 * Simple CollectionWriter implementation that wraps a Pusher object. 
 * Calls to writeObject/writeObjects are sent through to the pusher.push 
 * and pusher.pushAll methods. The writeHeader and complete methods are 
 * ignored.
 */
@SuppressWarnings("unchecked")
public final class PusherCollectionWriter 
  implements CollectionWriter {

  private final Pusher<ASObject> pusher;
  
  public PusherCollectionWriter(Pusher<ASObject> pusher) {
    this.pusher = pusher;
  }

  /**
   * Ignored in this implementation
   */
  public <X extends CollectionWriter>X writeHeader(ASBase base) {
    // We ignore this in the pusher...
    return (X)this;
  }

  public <X extends CollectionWriter>X writeObject(ASObject object) {
    pusher.push(object);
    return (X)this;
  }

  public <X extends CollectionWriter>X writeObjects(ASObject... objects) {
    for (ASObject object : objects)
      pusher.push(object);
    return (X)this;
  }
  
  public <X extends CollectionWriter>X writeObjects(Iterable<ASObject> objects) {
    pusher.pushAll(objects);
    return (X)this;
  }

  /**
   * Ignored by this implementation
   */
  public void complete() {
    // ignored
  }

  public <X extends CollectionWriter> X writeHeader(Builder<?, ?> base) {
    return (X) writeHeader(base.get());
  }

  public <X extends CollectionWriter> X writeObject(
      ASObject.Builder<?, ?> object) {
    return (X) writeObject(object.get());
  }
  
}
