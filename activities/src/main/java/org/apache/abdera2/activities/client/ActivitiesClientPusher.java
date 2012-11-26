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

import java.util.concurrent.ExecutorService;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.protocol.ClientResponse;
import org.apache.abdera2.common.protocol.RequestOptions;
import org.apache.abdera2.common.protocol.Session.Listener;
import org.apache.abdera2.common.pusher.Pusher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

import static org.apache.abdera2.common.misc.MoreExecutors2.getExitingExecutor;

/**
 * Utility that wraps an ActivitiesClient and uses the pusher 
 * interface to asynchronously send those off to somewhere else
 * using POST requests. The default behavior is to attempt to 
 * push the item once and forget about it, the status of the 
 * request will not be checked. Subclasses can customize that 
 * behavior as necessary
 */
public class ActivitiesClientPusher<T extends ASObject> 
  implements Pusher<T>, Listener<ClientResponse> {
  private final static Log log = 
    LogFactory.getLog(
      ActivitiesClientPusher.class);
  
  public static <T extends ASObject>Pusher<T> create(String iri) {
    return new ActivitiesClientPusher<T>(iri);
  }
  
  public static <T extends ASObject>Pusher<T> create(
    String iri,
    RequestOptions options) {
    return new ActivitiesClientPusher<T>(iri,options);
  }
  
  public static <T extends ASObject>Pusher<T> create(
    String iri,
    ActivitiesSession session) {
    return new ActivitiesClientPusher<T>(iri,session);
  }
  
  public static <T extends ASObject>Pusher<T> create(
    String iri,
    ActivitiesSession session,
    RequestOptions options) {
    return new ActivitiesClientPusher<T>(iri,session,options);
  }
  
  protected final ActivitiesSession session;
  protected final String iri;
  protected final ExecutorService exec;
  protected final RequestOptions options;

  public ActivitiesClientPusher(String iri) {
    this(iri, initSession());
  }
  
  public ActivitiesClientPusher(String iri, RequestOptions options) {
    this(iri, initSession(), options);
  }
  
  private static ActivitiesSession initSession() {
    return new ActivitiesClient().newSession();
  }
  
  public ActivitiesClientPusher(
    String iri, 
    ActivitiesSession session) {
      this(iri,session,session.getDefaultRequestOptions().get());
  }
  
  public ActivitiesClientPusher(
    String iri, 
    ActivitiesSession session,
    RequestOptions options) {
    this.session = session;
    this.iri = iri;
    this.options = options;
    this.exec = initExecutor();
  }
  
  protected ExecutorService initExecutor() {
    return getExitingExecutor();
  }
  
  public void push(final T t) {
    try {
      session.post(iri,t,options,exec,this);
    } catch (Throwable x) {
      handle(x);
    }
  }
  
  protected void handle(Throwable t) {
    // by default, do nothing
    log.error(t);
  }

  public void pushAll(Iterable<T> t) {
    for (T i : t) push(i);
  }

  public void onResponse(ClientResponse resp) {
    resp.release();
  }

  public void push(Supplier<? extends T> t) {
    if (t == null) return;
    T i = t.get();
    if (i != null) push(i);
  }

  public void pushAll(T... t) {
    if (t == null) return;
    pushAll(ImmutableList.copyOf(t));
  }

  public void pushAll(Supplier<? extends T>... t) {
    ImmutableList.Builder<T> list = ImmutableList.builder();
    for (Supplier<? extends T> s : t) {
      T i = s.get();
      if (i != null)
        list.add(i);
    }
    pushAll(list.build());
  }

}
