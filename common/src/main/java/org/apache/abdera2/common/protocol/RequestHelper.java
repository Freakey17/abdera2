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

import java.net.URI;

import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.Method;

import static org.apache.abdera2.common.http.Method.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import com.google.common.collect.ImmutableSet;

public final class RequestHelper {

    private RequestHelper() {}
     
    // the set of methods we never use post-override for
    private static final ImmutableSet<Method> nopostoveride = 
      ImmutableSet.of(
        Method.GET,
        Method.POST,
        Method.OPTIONS,
        Method.HEAD,
        Method.TRACE);
    
    public static HttpUriRequest createRequest(
      String method, 
      String uri, 
      HttpEntity entity, 
      RequestOptions options) {
        if (method == null)
            return null;
        if (options == null)
          options = createAtomDefaultRequestOptions().get();
        Method m = Method.get(method);
        Method actual = null;
        HttpUriRequest httpMethod = null;
        if (options.isUsePostOverride() && 
            !nopostoveride.contains(m)) {
          actual = m;
          m = Method.POST;
        }
        if (m == GET)
          httpMethod = new HttpGet(uri);
        else if (m == POST) {
          httpMethod = new HttpPost(uri);
          if (entity != null) 
            ((HttpPost)httpMethod).setEntity(entity);
        } else if (m == PUT) {
          httpMethod = new HttpPut(uri);
          if (entity != null)
            ((HttpPut)httpMethod).setEntity(entity);
        } else if (m == DELETE)
          httpMethod = new HttpDelete(uri);
        else if (m == HEAD)
          httpMethod = new HttpHead(uri);
        else if (m == OPTIONS)
          httpMethod = new HttpOptions(uri);
        else if (m == TRACE)
          httpMethod = new HttpTrace(uri);
//        else if (m == PATCH)
//          httpMethod = new ExtensionRequest(m.name(),uri,entity);
        else
          httpMethod = new ExtensionRequest(m.name(),uri,entity);
        if (actual != null) {
            httpMethod.addHeader("X-HTTP-Method-Override", actual.name());
        }
        initHeaders(options, httpMethod);
        HttpParams params = httpMethod.getParams();
        if (!options.isUseExpectContinue()) {
          params.setBooleanParameter(
            CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        } else {
          if (options.getWaitForContinue() > -1)
            params.setIntParameter(
              CoreProtocolPNames.WAIT_FOR_CONTINUE, 
              options.getWaitForContinue());
        }
        if (!(httpMethod instanceof HttpEntityEnclosingRequest))
            params.setBooleanParameter(
              ClientPNames.HANDLE_REDIRECTS, 
              options.isFollowRedirects()); 
        return httpMethod;
    }

    private static void initHeaders(RequestOptions options, HttpRequest request) {
        Iterable<String> headers = options.getHeaderNames();
        for (String header : headers) {
            Iterable<Object> values = options.getHeaders(header);
            for (Object value : values)
                request.addHeader(header, value.toString());
        }
        CacheControl cc = options.getCacheControl();
        if (cc != null) { 
            String scc = cc.toString();
            if (scc.length() > 0)
              request.setHeader("Cache-Control", scc);
        }
// TODO: Authentication setup per request???
//        if (options.getAuthorization() != null)
//            method.setDoAuthentication(false);
    }

    public static class ExtensionRequest 
      extends HttpEntityEnclosingRequestBase {
      private final String method;
      public ExtensionRequest(String method, String uri, HttpEntity entity) {
        try {
          super.setURI(new URI(uri));
        } catch (Throwable t) {
          throw new IllegalArgumentException(t);
        }
        this.method = method;
        if (entity != null)
          setEntity(entity);
      }
      public String getMethod() {
        return method;
      }
      
    }
    
    public static RequestOptions.Builder createAtomDefaultRequestOptions() {
      return RequestOptions.make()
        .acceptEncoding("gzip", "deflate")
        .accept(
          "application/atom+xml;type=entry",
          "application/atom+xml;type=feed",
          "application/atom+xml",
          "application/atomsvc+xml",
          "application/atomcat+xml",
          "application/xml;q=0.5",
          "text/xml;q=0.5",
          "*/*;q=0.01")
        .acceptCharset("utf-8", "*;q=0.5");
    }
    
    public static RequestOptions.Builder createActivitiesDefaultRequestOptions() {
      return RequestOptions.make()
        .acceptEncoding("gzip", "deflate")
        .accept(
          "application/json",
          "*/*;q=0.01")
        .acceptCharset(
          "utf-8", 
          "*;q=0.5");
    }
}
