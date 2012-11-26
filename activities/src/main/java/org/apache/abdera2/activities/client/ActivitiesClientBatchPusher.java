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
package org.apache.abdera2.activities.client;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.common.protocol.RequestOptions;
import org.apache.abdera2.common.pusher.Pusher;

/**
 * Identical to ActivitiesClientPusher with the exception that the 
 * pushAll method will create a Collection object containing all of 
 * the items and will post the single Collection object to the server
 * rather than sending one post per item
 */
public class ActivitiesClientBatchPusher<T extends ASObject> 
  extends ActivitiesClientPusher<T> {

  public static <T extends ASObject>Pusher<T> create(String iri) {
    return new ActivitiesClientBatchPusher<T>(iri);
  }
  
  public static <T extends ASObject>Pusher<T> create(
    String iri,
    RequestOptions options) {
    return new ActivitiesClientBatchPusher<T>(iri,options);
  }
  
  public static <T extends ASObject>Pusher<T> create(
    String iri,
    ActivitiesSession session) {
    return new ActivitiesClientBatchPusher<T>(iri,session);
  }
  
  public static <T extends ASObject>Pusher<T> create(
    String iri,
    ActivitiesSession session,
    RequestOptions options) {
    return new ActivitiesClientBatchPusher<T>(iri,session,options);
  }
  
  public ActivitiesClientBatchPusher(
    String iri, 
    ActivitiesSession session,
    RequestOptions options) {
    super(iri, session, options);
  }

  public ActivitiesClientBatchPusher(
    String iri, 
    ActivitiesSession session) {
    super(iri, session);
  }

  public ActivitiesClientBatchPusher(
    String iri, 
    RequestOptions options) {
    super(iri, options);
  }

  public ActivitiesClientBatchPusher(
    String iri) {
    super(iri);
  }

  @Override
  public void pushAll(final Iterable<T> t) {
    try {
      session.post(
        iri, 
        Collection.<T>makeCollection(t), 
        options,
        exec,
        this);
    } catch (Throwable x) {
      handle(x);
    }
  }

}
