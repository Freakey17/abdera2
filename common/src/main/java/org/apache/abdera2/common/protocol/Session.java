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
package org.apache.abdera2.common.protocol;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.abdera2.common.http.Method;
import org.apache.abdera2.common.http.ResponseType;
import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * A client session. Session's MUST NOT be used by more
 * than one Thread of execution as a time as multiple threads would stomp 
 * all over the shared session context. It is critical to completely
 * consume each ClientResponse before executing an additional request on 
 * the same session.
 */
@SuppressWarnings("unchecked")
public class Session {

    protected final Client client;
    protected final HttpContext localContext;

    protected Session(Client client) {
        this.client = client;
        this.localContext = 
          new BasicHttpContext();
    }
    
    private HttpClient getClient() {
      return client.getClient();
    }
    
    protected <T extends ClientResponse>T wrap(ClientResponse resp) {
      return (T)resp;
    }
    
    /**
     * Sends an HTTP GET request to the specified URI.
     * 
     * @param uri The request URI
     * @param options The request options
     */
    public <T extends ClientResponse>T get(String uri, RequestOptions options) {
        return (T)wrap(execute("GET", uri, (HttpEntity)null, options));
    }
    
    public <T extends ClientResponse>Callable<T> getTask(
      final String uri) {
      return new Callable<T>() {
        public T call() throws Exception {
          return (T) get(uri);
        }
      };
    }
    
    public <T extends ClientResponse>Callable<T> getTask(
      final String uri, 
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) get(uri,options);
          }
        };
    }

    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T post(
      String uri, 
      HttpEntity entity, 
      RequestOptions options) {
      return (T)wrap(execute("POST", uri, entity, options));
    }
    
    public <T extends ClientResponse>Callable<T> postTask(
      final String uri, 
      final HttpEntity entity) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) post(uri,entity);
          }
        };
    }
    
    public <T extends ClientResponse>Callable<T> postTask(
      final String uri, 
      final HttpEntity entity, 
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) post(uri,entity,options);
          }
        };
    }

    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T post(String uri, InputStream in, RequestOptions options) {
        return (T)wrap(execute("POST", uri, new InputStreamEntity(in,-1), options));
    }
    
    public <T extends ClientResponse>Callable<T> postTask(
      final String uri, 
      final InputStream in) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) post(uri,in);
          }
        };
    }
    
    public <T extends ClientResponse>Callable<T> postTask(
      final String uri, 
      final InputStream in, 
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) post(uri,in,options);
          }
        };
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T put(String uri, HttpEntity entity, RequestOptions options) {
        return (T)wrap(execute("PUT", uri, entity, options));
    }

    public <T extends ClientResponse>Callable<T> putTask(
      final String uri, 
      final HttpEntity entity) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) put(uri,entity);
          }
        };
    }
    
    public <T extends ClientResponse>Callable<T> putTask(
      final String uri, 
      final HttpEntity entity, 
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) put(uri,entity,options);
          }
        };
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T put(String uri, InputStream in, RequestOptions options) {
        return (T)wrap(execute("PUT", uri, new InputStreamEntity(in,-1), options));
    }
    
    public <T extends ClientResponse>Callable<T> putTask(
        final String uri, 
        final InputStream in) {
          return new Callable<T>() {
            public T call() throws Exception {
              return (T) put(uri,in);
            }
          };
      }
    
    public <T extends ClientResponse>Callable<T> putTask(
      final String uri, 
      final InputStream in, 
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) put(uri,in,options);
          }
        };
    }

    /**
     * Sends an HTTP DELETE request to the specified URI.
     * 
     * @param uri The request URI
     * @param options The request options
     */
    public <T extends ClientResponse>T delete(String uri, RequestOptions options) {
        return (T)wrap(execute("DELETE", uri, (HttpEntity)null, options));
    }
    
    public <T extends ClientResponse>Callable<T> deleteTask(
      final String uri) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) delete(uri);
          }
        };
    }
    
    public <T extends ClientResponse>Callable<T> deleteTask(
      final String uri,
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) delete(uri,options);
          }
        };
    }

    /**
     * Sends an HTTP HEAD request to the specified URI using the default options
     * 
     * @param uri The request URI
     */
    public <T extends ClientResponse>T head(String uri) {
        return (T)wrap(head(uri, getDefaultRequestOptions().get()));
    }
    
    public <T extends ClientResponse>Callable<T> headTask(
        final String uri) {
          return new Callable<T>() {
            public T call() throws Exception {
              return (T) head(uri);
            }
          };
      }
    
    public <T extends ClientResponse>Callable<T> headTask(
      final String uri,
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) head(uri,options);
          }
        };
    }
    
    /**
     * Sends an HTTP HEAD request to the specified URI
     * 
     * @param uri The request URI
     */
    public <T extends ClientResponse>T head(String uri, RequestOptions options) {
       return (T)wrap(execute("HEAD", uri, (HttpEntity)null, options));
    }

    /**
     * Sends an HTTP GET request to the specified URI using the default options
     * 
     * @param uri The request URI
     */
    public <T extends ClientResponse>T get(String uri) {
        return (T)wrap(get(uri, getDefaultRequestOptions().get()));
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     */
    public <T extends ClientResponse>T post(String uri, HttpEntity entity) {
        return (T)wrap(post(uri, entity, getDefaultRequestOptions().get()));
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     */
    public <T extends ClientResponse>T post(String uri, InputStream in) {
        return (T)wrap(post(uri, in, getDefaultRequestOptions().get()));
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     */
    public <T extends ClientResponse>T put(String uri, HttpEntity entity) {
        return (T)wrap(put(uri, entity, getDefaultRequestOptions().get()));
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     */
    public <T extends ClientResponse>T put(String uri, InputStream in) {
        return (T)wrap(put(uri, in, getDefaultRequestOptions().get()));
    }

    /**
     * Sends an HTTP DELETE request to the specified URI using the default options
     * 
     * @param uri The request URI
     */
    public <T extends ClientResponse>T delete(String uri) {
        return (T)wrap(delete(uri, getDefaultRequestOptions().get()));
    }
    
    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     * @param options The Request Options
     */
    public <T extends ClientResponse>T execute(
        String method, 
        String uri, 
        InputStream in, 
        RequestOptions options) {
        if (options == null)
          options = getDefaultRequestOptions().get();
        InputStreamEntity re = 
          new InputStreamEntity(in, -1);
        re.setContentType(
          options.getContentType().toString());
        return (T)wrap(execute(
          method, uri, re, options));
    }
    
    public <T extends ClientResponse>Callable<T> executeTask(
      final String method, 
      final String uri, 
      final InputStream in, 
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) execute(method,uri,in,options);
          }
        };
    }
    
    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     * @param options The Request Options
     */
    public <T extends ClientResponse>T execute(
        Method method, 
        String uri, 
        InputStream in, 
        RequestOptions options) {
        return (T)wrap(execute(method.name(),uri,in,options));
    }
    
    public <T extends ClientResponse>Callable<T> executeTask(
      final Method method, 
      final String uri, 
      final InputStream in, 
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) execute(method,uri,in,options);
          }
        };
    }

    public <T extends ClientResponse>T execute(
        Method method, 
        String uri, 
        HttpEntity entity, 
        RequestOptions options) {
      return (T)wrap(execute(method.name(),uri,entity,options));
    }
    
    public <T extends ClientResponse>Callable<T> executeTask(
      final Method method, 
      final String uri, 
      final HttpEntity entity, 
      final RequestOptions options) {
        return new Callable<T>() {
          public T call() throws Exception {
            return (T) execute(method,uri,entity,options);
          }
        };
    }
    
    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload for the request
     * @param options The Request Options
     */
    public <T extends ClientResponse>T execute(
        String method, 
        String uri, 
        HttpEntity entity, 
        RequestOptions options) {
        options =
          options != null ? 
            options : 
            getDefaultRequestOptions()
              .get();
        try {
          HttpUriRequest request = 
            RequestHelper.createRequest(
                method, uri, entity, options);
          HttpResponse response = 
            getClient().execute(request, localContext);
          ClientResponse resp = 
            wrap(new ClientResponseImpl(
              this, response, method, localContext));
          return (T)checkRequestException(resp, options);
        } catch (RuntimeException r) {
            throw r;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public <T extends ClientResponse>Callable<T> executeTask(
      final String method, 
      final String uri, 
      final HttpEntity entity, 
      final RequestOptions options) {
       return new Callable<T>() {
          public T call() throws Exception {
            return (T) execute(method,uri,entity,options);
          }
        };
    }
    
    protected <T extends ClientResponse>T checkRequestException(ClientResponse response, RequestOptions options) {
      if (response == null)
          return (T)response;
      ResponseType type = response.getType();
      if ((type.equals(ResponseType.CLIENT_ERROR) && options.is4xxRequestException()) || (type
          .equals(ResponseType.SERVER_ERROR) && options.is5xxRequestException())) {
        throw new ProtocolException(response.getStatus(),response.getStatusText());
      }
      return (T)response;
  }
    
    /**
     * Get a copy of the default request options
     */
    public RequestOptions.Builder getDefaultRequestOptions() {
        return RequestHelper.createAtomDefaultRequestOptions();
    }

    public void usePreemptiveAuthentication(String target, String realm) throws URISyntaxException {
        AuthCache cache = (AuthCache) localContext.getAttribute(ClientContext.AUTH_CACHE);
        if (cache == null) {
          String host = AuthScope.ANY_HOST;
          int port = AuthScope.ANY_PORT;
          if (target != null) {
            URI uri = new URI(target);
            host = uri.getHost();
            port = uri.getPort();
          }
          BasicScheme basicAuth = new BasicScheme();
          HttpHost targetHost = 
            new HttpHost(host,port,basicAuth.getSchemeName());
          cache = new BasicAuthCache();
          cache.put(targetHost, basicAuth);
          localContext.setAttribute(ClientContext.AUTH_CACHE, cache);
        }
    }
    
    public boolean doFormLogin(String uri, String userid, String password) {
      return doFormLogin(uri, "j_username", userid, "j_password", password);
    }
    
    public boolean doFormLogin(String uri, String userfield, String userid, String passfield, String password) {
      return doFormLogin(uri, userfield, userid, passfield, password, (ImmutableMap<String,String>)null);
    }
 
    public boolean doFormLogin(
        String uri, 
        String userfield, 
        String userid, 
        String passfield, 
        String password,
        ImmutableMap.Builder<String,String> additional) {
      return doFormLogin(uri,userfield,userid,passfield,password,additional.build());
    }
    
    public boolean doFormLogin(
      String uri, 
      String userfield, 
      String userid, 
      String passfield, 
      String password,
      ImmutableMap<String,String> additional) {
      try {
        HttpPost httpost = new HttpPost(uri);
        ImmutableList.Builder<BasicNameValuePair> pairs = 
          ImmutableList.<BasicNameValuePair>builder()
           .add(new BasicNameValuePair(userfield,userid))
           .add(new BasicNameValuePair(passfield,password));
        for (Map.Entry<String, String> entry : additional.entrySet())
          pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        httpost.setEntity(
          new UrlEncodedFormEntity(
            pairs.build(),
            HTTP.UTF_8));
        HttpResponse response = getClient().execute(httpost,localContext);
        HttpEntity entity = response.getEntity();
        EntityUtils.consume(entity);
        ResponseType type = ResponseType.select(response.getStatusLine().getStatusCode());
        return type == ResponseType.SUCCESSFUL;
      } catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }
    
  public static interface Listener<X extends ClientResponse> {
    void onResponse(X resp);
  }
  
  /**
   * Processes requests asynchronously.. will return a Future
   * whose value will be set once the call completes
   */
  public <X extends ClientResponse>Future<X> process(
    ExecutorService executor, 
    Callable<X> resp) {
      ListeningExecutorService exec = 
        MoreExecutors.listeningDecorator(executor);
      return exec.submit(resp);
  }
  
  /**
   * Processes requests asynchronously.. the listener will
   * be invoked once the call completes
   */
  public <X extends ClientResponse>void process(
    ExecutorService executor, 
    Callable<X> resp, 
    final Listener<X> listener) {
      ListeningExecutorService exec = MoreExecutors.listeningDecorator(executor);
      final ListenableFuture<X> lf = exec.submit(resp);
      lf.addListener(
        new Runnable() {
          public void run() {
            X resp = null;
            try {
              resp = lf.get();
              listener.onResponse(resp);
            } catch (Throwable t) {
              throw ExceptionHelper.propogate(t);
            } finally { // auto release since by this point we know we're done with it
              if (resp != null) resp.release();
            }
          }
        }, 
        executor);
  }
  
  public <T extends ClientResponse>void get(
    String uri, 
    ExecutorService exec, 
    Listener<T> listener) {
    process(exec,this.<T>getTask(uri),listener);
  }
  
  public <T extends ClientResponse>void get(
    String uri, 
    RequestOptions options,
    ExecutorService exec, 
    Listener<T> listener) {
    process(exec,this.<T>getTask(uri,options),listener);
  }
  
  public <T extends ClientResponse>Future<T> get(
    String uri, 
    ExecutorService exec) {
      return process(exec,this.<T>getTask(uri));
  }
  
  public <T extends ClientResponse>Future<T> get(
    String uri, 
    RequestOptions options,
    ExecutorService exec) {
      return process(exec,this.<T>getTask(uri,options));
  }
  
  public <T extends ClientResponse>void post(
    String uri, 
    InputStream in,
    ExecutorService exec, 
    Listener<T> listener) {
    process(exec,this.<T>postTask(uri,in),listener);
  }
  
  public <T extends ClientResponse>void post(
    String uri, 
    InputStream in,
    RequestOptions options,
    ExecutorService exec, 
    Listener<T> listener) {
    process(exec,this.<T>postTask(uri,in,options),listener);
  }
  
  public <T extends ClientResponse>Future<T> post(
    String uri, 
    InputStream in,
    ExecutorService exec) {
      return process(exec,this.<T>postTask(uri,in));
  }
  
  public <T extends ClientResponse>Future<T> post(
    String uri, 
    InputStream in,
    RequestOptions options,
    ExecutorService exec) {
      return process(exec,this.<T>postTask(uri,in,options));
  }
  
  public <T extends ClientResponse>void post(
    String uri, 
    HttpEntity in,
    ExecutorService exec, 
    Listener<T> listener) {
    process(exec,this.<T>postTask(uri,in),listener);
  }
  
  public <T extends ClientResponse>void post(
    String uri, 
    HttpEntity in,
    RequestOptions options,
    ExecutorService exec, 
    Listener<T> listener) {
    process(exec,this.<T>postTask(uri,in,options),listener);
  }
  
  public <T extends ClientResponse>Future<T> post(
    String uri, 
    HttpEntity in,
    ExecutorService exec) {
      return process(exec,this.<T>postTask(uri,in));
  }
  
  public <T extends ClientResponse>Future<T> post(
    String uri, 
    HttpEntity in,
    RequestOptions options,
    ExecutorService exec) {
      return process(exec,this.<T>postTask(uri,in,options));
  }


  public <T extends ClientResponse>void put(
      String uri, 
      InputStream in,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>putTask(uri,in),listener);
    }
    
    public <T extends ClientResponse>void put(
      String uri, 
      InputStream in,
      RequestOptions options,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>putTask(uri,in,options),listener);
    }
    
    public <T extends ClientResponse>Future<T> put(
      String uri, 
      InputStream in,
      ExecutorService exec) {
        return process(exec,this.<T>putTask(uri,in));
    }
    
    public <T extends ClientResponse>Future<T> put(
      String uri, 
      InputStream in,
      RequestOptions options,
      ExecutorService exec) {
        return process(exec,this.<T>putTask(uri,in,options));
    }
    
    public <T extends ClientResponse>void put(
      String uri, 
      HttpEntity in,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>putTask(uri,in),listener);
    }
    
    public <T extends ClientResponse>void put(
      String uri, 
      HttpEntity in,
      RequestOptions options,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>putTask(uri,in,options),listener);
    }
    
    public <T extends ClientResponse>Future<T> put(
      String uri, 
      HttpEntity in,
      ExecutorService exec) {
        return process(exec,this.<T>putTask(uri,in));
    }
    
    public <T extends ClientResponse>Future<T> put(
      String uri, 
      HttpEntity in,
      RequestOptions options,
      ExecutorService exec) {
        return process(exec,this.<T>putTask(uri,in,options));
    }
    
    public <T extends ClientResponse>void delete(
      String uri, 
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>deleteTask(uri),listener);
    }
    
    public <T extends ClientResponse>void delete(
      String uri, 
      RequestOptions options,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>deleteTask(uri,options),listener);
    }
    
    public <T extends ClientResponse>Future<T> delete(
      String uri, 
      ExecutorService exec) {
        return process(exec,this.<T>deleteTask(uri));
    }
    
    public <T extends ClientResponse>Future<T> delete(
      String uri, 
      RequestOptions options,
      ExecutorService exec) {
        return process(exec,this.<T>deleteTask(uri,options));
    }
    
    public <T extends ClientResponse>void head(
      String uri, 
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>headTask(uri),listener);
    }
    
    public <T extends ClientResponse>void head(
      String uri, 
      RequestOptions options,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>headTask(uri,options),listener);
    }
    
    public <T extends ClientResponse>Future<T> head(
      String uri, 
      ExecutorService exec) {
        return process(exec,this.<T>headTask(uri));
    }
    
    public <T extends ClientResponse>Future<T> head(
      String uri, 
      RequestOptions options,
      ExecutorService exec) {
        return process(exec,this.<T>headTask(uri,options));
    }
    
    
    public <T extends ClientResponse>void execute(
      String method, 
      String uri, 
      InputStream in, 
      RequestOptions options,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>executeTask(method,uri,in,options),listener);
    }
    
    public <T extends ClientResponse>void execute(
      String method, 
      String uri, 
      HttpEntity in, 
      RequestOptions options,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>executeTask(method,uri,in,options),listener);
    }
    
    public <T extends ClientResponse>Future<T> execute(
      String method, 
      String uri, 
      InputStream in, 
      RequestOptions options,
      ExecutorService exec) {
        return process(exec,this.<T>executeTask(method,uri,in,options));
    }
    
    public <T extends ClientResponse>Future<T> execute(
      String method, 
      String uri, 
      HttpEntity in, 
      RequestOptions options,
      ExecutorService exec) {
        return process(exec,this.<T>executeTask(method,uri,in,options));
    }
    
    public <T extends ClientResponse>void execute(
      Method method, 
      String uri, 
      InputStream in, 
      RequestOptions options,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>executeTask(method,uri,in,options),listener);
    }
    
    public <T extends ClientResponse>void execute(
      Method method, 
      String uri, 
      HttpEntity in, 
      RequestOptions options,
      ExecutorService exec, 
      Listener<T> listener) {
      process(exec,this.<T>executeTask(method,uri,in,options),listener);
    }
    
    public <T extends ClientResponse>Future<T> execute(
      Method method, 
      String uri, 
      InputStream in, 
      RequestOptions options,
      ExecutorService exec) {
        return process(exec,this.<T>executeTask(method,uri,in,options));
    }
    
    public <T extends ClientResponse>Future<T> execute(
      Method method, 
      String uri, 
      HttpEntity in, 
      RequestOptions options,
      ExecutorService exec) {
        return process(exec,this.<T>executeTask(method,uri,in,options));
    }
}
