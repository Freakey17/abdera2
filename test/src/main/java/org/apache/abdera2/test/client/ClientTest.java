package org.apache.abdera2.test.client;
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera2.common.misc.MoreExecutors2;
import org.apache.abdera2.common.protocol.BasicClient;
import org.apache.abdera2.common.protocol.Client;
import org.apache.abdera2.common.protocol.ClientResponse;
import org.apache.abdera2.common.protocol.Session;
import org.apache.abdera2.common.protocol.Session.Listener;
import org.apache.abdera2.test.JettyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.AbstractFuture;
import static com.google.common.base.Preconditions.checkState;

public class ClientTest {

    @BeforeClass
    public static void setUp() throws Exception {
      JettyUtil.addServlet(TestServlet.class.getName(), "/");
      JettyUtil.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
      JettyUtil.stop();
    }

    public static class TestServlet extends HttpServlet {
      private static final long serialVersionUID = -1725406584245400456L;
      protected void doGet(
        HttpServletRequest request, 
        HttpServletResponse response)
          throws ServletException, IOException {
        Cookie cookie = 
          new Cookie(
            "x-test", 
            "x-test");
        response.addCookie(
          cookie);
        
        String val = "Foo";
        Cookie[] cookies = request.getCookies();
        if (cookies.length > 0)
          for (Cookie c : cookies)
            if (c.getName().equalsIgnoreCase("x-test"))
              val = c.getValue();
        response.getWriter().write(val);
      }
    }
    
    String read(Reader reader) throws IOException {
      StringBuilder sb = new StringBuilder();
      char[] buf = new char[100];
      int r = -1;
      while ((r = reader.read(buf)) > -1) 
        sb.append(buf,0,r);
      return sb.toString();
    }
    
    @Test
    public void testClient() throws Exception {
      Client client = new BasicClient();
      
      Session session = client.newSession();
      
      // Test Blocking Request
      ClientResponse resp = session.get("http://localhost:9002/");
      assertEquals(200,resp.getStatus());
      assertEquals("Foo",read(resp.getReader()));
      resp.release();
      
      // Test Non-Blocking Request using a listener.. test cookies while we're at it
      ExecutorService exec =
        MoreExecutors2.getExitingExecutor();
      
      final FutureMap<String,Object> future =
        new FutureMap<String,Object>();
      session.get(
        "http://localhost:9002/", 
        exec,
        new Listener<ClientResponse>() {
          public void onResponse(ClientResponse resp) {
            try {
              future
                .put("status", resp.getStatus())
                .put("data", read(resp.getReader()));
            } catch (Throwable t) {
              future.put("error", t);
            } finally {
              future.complete();
              //resp.release(); session will auto-release the response
            }
          }
        }
      );
      Map<String,Object> map = future.get();
      assertEquals(Integer.valueOf(200),map.get("status"));
      assertEquals("x-test",map.get("data"));
      
      // Test Non-Blocking Request using a Future
      Future<ClientResponse> respFuture = 
        session.get("http://localhost:9002/", exec);
      resp = respFuture.get();
      assertEquals(200,resp.getStatus());
      assertEquals("x-test",read(resp.getReader()));
      resp.release();
      
      client.shutdown();
    }
    
    static class FutureMap<X,Y> extends AbstractFuture<Map<X,Y>> {
      private ImmutableMap.Builder<X,Y> builder = ImmutableMap.builder();
      FutureMap<X,Y> put(X key, Y value) {
        checkState(!isDone());
        builder.put(key,value);
        return this;
      }
      void complete() {
        checkState(!isDone());
        this.set(builder.build());
      }
    }
}
