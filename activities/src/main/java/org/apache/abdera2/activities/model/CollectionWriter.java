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

/**
 * Interface used to stream a Collection of activity objects.
 */
public interface CollectionWriter {

  /**
   * Writes all of the properties other than the "items" property. 
   * This MUST be called before calling writeObject or writeObjects
   * and cannot be called after calling either of those.
   */
  <X extends CollectionWriter>X writeHeader(ASBase base);
  
  <X extends CollectionWriter>X writeHeader(ASBase.Builder<?,?> base);
  
  /**
   * Writes an object to the items array of the Collection
   */
  <X extends CollectionWriter>X writeObject(ASObject object);
  
  <X extends CollectionWriter>X writeObject(ASObject.Builder<?,?> object);
  
  /**
   * Writes one or more objects to the items array of the Collection
   */
  <X extends CollectionWriter>X writeObjects(ASObject... objects);
  
  /**
   * Writes one or more objects to the items array of the Collection
   */
  <X extends CollectionWriter>X writeObjects(Iterable<ASObject> objects);
  
  /**
   * Completes the Collection
   */
  void complete();
  
}
