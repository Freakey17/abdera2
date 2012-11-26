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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.activation.MimeType;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASDocument;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.protocol.Client;
import org.apache.abdera2.common.protocol.ClientResponse;
import org.apache.abdera2.common.protocol.ProtocolException;
import org.apache.abdera2.common.protocol.RequestHelper;
import org.apache.abdera2.common.protocol.RequestOptions;
import org.apache.abdera2.common.protocol.Session;
import org.joda.time.DateTime;

/**
 * Extension of the base Abdera Client Session that provides utility 
 * methods for working with Activity Streams objects.
 */
@SuppressWarnings("unchecked")
public class ActivitiesSession 
  extends Session {

  private final IO io;
  
  protected ActivitiesSession(Client client, IO io) {
    super(client);
    this.io = io;
  }
  
  protected ActivitiesSession(Client client) {
    super(client);
    this.io = IO.get();
  }

  public IO getIO() {
    return io;
  }
  
  protected ActivitiesClient getActivitiesClient() {
    return (ActivitiesClient) client;
  }

  public <M extends ASObject,T extends Collection<M>>ASDocument<T> getCollection(String uri) {
    return this.<M,T>getCollection(uri, this.getDefaultRequestOptions().get());
  }
  
  public <T extends ClientResponse>T post(String uri, ASBase base) {
    return this.<T>post(uri,base, this.getDefaultRequestOptions().get());
  }
  
  public <T extends ClientResponse>Callable<T> postTask(
    final String uri, 
    final ASBase base) {
    return new Callable<T>() {
      public T call() throws Exception {
        return (T) post(uri,base);
      }
    };
  }
  
  public <T extends ClientResponse>Callable<T> postTask(
    final String uri, 
    final ASBase base,
    final RequestOptions options) {
    return new Callable<T>() {
      public T call() throws Exception {
        return (T) post(uri,base,options);
      }
    };
  }
  
  public <T extends ClientResponse>T post(String uri, ASBase base, RequestOptions options) {
    ActivityEntity entity = new ActivityEntity(base);
    return this.<T>post(uri, entity, options);
  }
  
  public <T extends ClientResponse>Callable<T> putTask(
    final String uri, 
    final ASBase base) {
    return new Callable<T>() {
      public T call() throws Exception {
        return (T) put(uri,base);
      }
    };
  }
  
  public <T extends ClientResponse>Callable<T> putTask(
    final String uri, 
    final ASBase base,
    final RequestOptions options) {
    return new Callable<T>() {
      public T call() throws Exception {
        return (T) put(uri,base,options);
      }
    };
  }

  public <T extends ClientResponse>T put(String uri, ASBase base) {
    return this.<T>put(uri,base, this.getDefaultRequestOptions().get());
  }
  
  public <T extends ClientResponse>T put(String uri, ASBase base, RequestOptions options) {
    ActivityEntity entity = new ActivityEntity(base);
    return this.<T>put(uri, entity, options);
  }
  
  public <M extends ASObject,T extends Collection<M>>Callable<ASDocument<T>> getCollectionTask(final String uri) {
    final ActivitiesSession session = this;
    return new Callable<ASDocument<T>>() {
      public ASDocument<T> call() throws Exception {
        return session.<M,T>getCollection(uri);
      }
    };
  }
  
  public <M extends ASObject, T extends Collection<M>>Callable<ASDocument<T>> getCollectionTask(
    final String uri, 
    final RequestOptions options) {
    final ActivitiesSession session = this;
    return new Callable<ASDocument<T>>() {
      public ASDocument<T> call() throws Exception {
        return session.<M,T>getCollection(uri,options);
      }
    };
  }
  
  public <M extends ASObject, T extends Collection<M>>ASDocument<T> getCollection(String uri, RequestOptions options) {
    return ActivitiesSession.<M,T>getCollectionFromResp(io,get(uri, options));
  }
  
  public static <M extends ASObject, T extends Collection<M>>ASDocument<T> getCollectionFromResp(IO io, ClientResponse cr) {
    try {
      if (cr != null) {
        switch(cr.getType()) {
        case SUCCESSFUL:
          try {
            T t = (T)io.readCollection(cr.getReader());
            return getDoc(cr,t);
          } catch (Throwable t) {
            throw new ProtocolException(601, t.getMessage());
          }
        default:
          throw new ProtocolException(cr.getStatus(), cr.getStatusText());
        }
      } else {
        throw new ProtocolException(600, "Null Response");
      }
    } finally {
      if (cr != null) cr.release();
    }
  }
  
  public <T extends Activity>Callable<ASDocument<T>> getActivityTask(final String uri) {
    return new Callable<ASDocument<T>>() {
      public ASDocument<T> call() throws Exception {
        return getActivity(uri);
      }
    };
  }
  
  public <T extends Activity>Callable<ASDocument<T>> getActivityTask(
    final String uri, 
    final RequestOptions options) {
    return new Callable<ASDocument<T>>() {
      public ASDocument<T> call() throws Exception {
        return getActivity(uri,options);
      }
    };
  }

  public <T extends Activity>ASDocument<T> getActivity(String uri) {
    return this.<T>getActivity(uri,this.getDefaultRequestOptions().get());
  }
  
  public <T extends Activity>ASDocument<T> getActivity(String uri, RequestOptions options) {
    return getActivityFromResponse(io,get(uri, options));
  }
  
  public static <T extends Activity>ASDocument<T> getActivityFromResponse(IO io,ClientResponse cr) {
    try {
      if (cr != null) {
        switch(cr.getType()) {
        case SUCCESSFUL:
          try {
            T t = (T)io.readActivity(cr.getReader());
            return getDoc(cr,t);
          } catch (Throwable t) {
            throw new ProtocolException(601, t.getMessage());
          }
        default:
          throw new ProtocolException(cr.getStatus(), cr.getStatusText());
        }
      } else {
        throw new ProtocolException(600, "Null Response");
      }
    } finally {
      if (cr != null) cr.release();
    }
  }
  
  public <T extends ASObject>Callable<ASDocument<T>> getObjectTask(final String uri) {
    return new Callable<ASDocument<T>>() {
      public ASDocument<T> call() throws Exception {
        return getObject(uri);
      }
    };
  }
  
  public <T extends ASObject>Callable<ASDocument<T>> getObjectTask(
    final String uri, 
    final RequestOptions options) {
    return new Callable<ASDocument<T>>() {
      public ASDocument<T> call() throws Exception {
        return getObject(uri,options);
      }
    };
  }
  
  public <T extends ASObject>ASDocument<T> getObject(String uri) {
    return this.<T>getObject(uri, this.getDefaultRequestOptions().get());
  }

  public <T extends ASObject>ASDocument<T> getObject(String uri, RequestOptions options) {
    return getObjectFromResponse(io,get(uri, options));
  }
  
  public static <T extends ASObject>ASDocument<T> getObjectFromResponse(IO io,ClientResponse cr) {
    try {
      if (cr != null) {
        switch(cr.getType()) {
        case SUCCESSFUL:
          try {
            T t = (T)io.readObject(cr.getReader());
            return getDoc(cr,t);
          } catch (Throwable t) {
            throw new ProtocolException(601, t.getMessage());
          }
        default:
          throw new ProtocolException(cr.getStatus(), cr.getStatusText());
        }
      } else {
        throw new ProtocolException(600, "Null Response");
      }
    } finally {
      if (cr != null) cr.release();
    }
  }
  
  private static <T extends ASBase>ASDocument<T> getDoc(ClientResponse resp, T base) {
    ASDocument.Builder<T> builder = 
      ASDocument.make(base);
    EntityTag etag = resp.getEntityTag();
    if (etag != null)
        builder.entityTag(etag);
    DateTime lm = resp.getLastModified();
    if (lm != null)
        builder.lastModified(lm);
    MimeType mt = resp.getContentType();
    if (mt != null)
        builder.contentType(mt.toString());
    String language = resp.getContentLanguage();
    if (language != null)
        builder.language(language);
    String slug = resp.getSlug();
    if (slug != null)
        builder.slug(slug);
    return builder.get();
  }
  
  public RequestOptions.Builder getDefaultRequestOptions() {
    return RequestHelper.createActivitiesDefaultRequestOptions();
  }
  
  public <T extends ClientResponse>void post(
    String uri, 
    ASBase base, 
    ExecutorService exec, 
    Listener<T> listener) {
      process(exec,this.<T>postTask(uri,base),listener);
  }
  
  public <T extends ClientResponse>void post(
    String uri, 
    ASBase base, 
    RequestOptions options,
    ExecutorService exec, 
    Listener<T> listener) {
      process(exec,this.<T>postTask(uri,base,options),listener);
  }
  
  public <T extends ClientResponse>Future<T> post(
    String uri, 
    ASBase base, 
    ExecutorService exec) {
      return process(exec,this.<T>postTask(uri,base));
  }
  
  public <T extends ClientResponse>Future<T> post(
    String uri, 
    ASBase base, 
    RequestOptions options,
    ExecutorService exec) {
      return process(exec,this.<T>postTask(uri,base,options));
  }
  
  public <T extends ClientResponse>void put(
    String uri, 
    ASBase base, 
    ExecutorService exec, 
    Listener<T> listener) {
      process(exec,this.<T>putTask(uri,base),listener);
  }
  
  public <T extends ClientResponse>void put(
    String uri, 
    ASBase base, 
    RequestOptions options,
    ExecutorService exec, 
    Listener<T> listener) {
      process(exec,this.<T>putTask(uri,base,options),listener);
  }
  
  public <T extends ClientResponse>Future<T> put(
    String uri, 
    ASBase base, 
    ExecutorService exec) {
      return process(exec,this.<T>putTask(uri,base));
  }
  
  public <T extends ClientResponse>Future<T> put(
    String uri, 
    ASBase base, 
    RequestOptions options,
    ExecutorService exec) {
      return process(exec,this.<T>putTask(uri,base,options));
  }

  static abstract class ASListener<T extends ClientResponse, X extends ASBase> 
    implements Listener<T> {
      protected final IO io;
      protected ASListener(IO io) {
        this.io = io;
      }
      protected abstract void onResponse(ASDocument<X> doc);
  }
  
  public static abstract class CollectionListener<T extends ClientResponse,X extends ASObject> 
    extends ASListener<T,Collection<X>> {
    protected CollectionListener(IO io) {
     super(io);
    }
    public void onResponse(T resp) {
      onResponse(ActivitiesSession.<X,Collection<X>>getCollectionFromResp(io,resp));
    }
  }
  
  public static abstract class SimpleActivityCollectionListener 
    extends CollectionListener<ClientResponse,Activity>{
    protected SimpleActivityCollectionListener(IO io) {
      super(io);
     }
  }
  
  public static abstract class SimpleObjectCollectionListener 
    extends CollectionListener<ClientResponse,ASObject>{
    protected SimpleObjectCollectionListener(IO io) {
      super(io);
     }
  }
  
  public static abstract class SimpleActivityListener 
    extends ActivityListener<ClientResponse,Activity>{
    protected SimpleActivityListener(IO io) {
      super(io);
     }
  }
  
  public static abstract class SimpleObjectListener 
    extends ObjectListener<ClientResponse,ASObject>{
    protected SimpleObjectListener(IO io) {
      super(io);
     }
  }
  
  public static abstract class ActivityListener<T extends ClientResponse,X extends Activity> 
    extends ASListener<T,X> {
    protected ActivityListener(IO io) {
      super(io);
     }
    public void onResponse(T resp) {
      onResponse(ActivitiesSession.<X>getActivityFromResponse(io,resp));
    }
  }
  
  public static abstract class ObjectListener<T extends ClientResponse,X extends ASObject> 
    extends ASListener<T,X> {
    protected ObjectListener(IO io) {
      super(io);
     }
    public void onResponse(T resp) {
      onResponse(ActivitiesSession.<X>getObjectFromResponse(io,resp));
    }
  }
}
