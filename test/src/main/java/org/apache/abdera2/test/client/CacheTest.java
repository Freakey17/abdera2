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
package org.apache.abdera2.test.client;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.protocol.BasicCachingClient;
import org.apache.abdera2.common.protocol.Client;
import org.apache.abdera2.common.protocol.ClientResponse;
import org.apache.abdera2.common.protocol.RequestOptions;
import org.apache.abdera2.common.protocol.Session;
import org.apache.abdera2.test.JettyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These cache tests were originally based on Mark Nottingham's javascript cache tests, available at:
 * http://www.mnot.net/javascript/xmlhttprequest/cache.html They have since been modified to use an embedded Jetty
 * server instead of going off over the internet to hit Mark's server, since there are too many things that can get in
 * the way of those sort things (proxies, intermediate caches, etc) if you try to talk to a remote server.
 */
@SuppressWarnings("serial")
public class CacheTest {

    private static String CHECK_CACHE_INVALIDATE;
    private static String CHECK_NO_CACHE;
    // private static String CHECK_AUTH;
    private static String CHECK_MUST_REVALIDATE;

    public CacheTest() {
        String base = getBase();
        CHECK_CACHE_INVALIDATE = base + "/check_cache_invalidate";
        CHECK_NO_CACHE = base + "/no_cache";
        // CHECK_AUTH = base + "/auth";
        CHECK_MUST_REVALIDATE = base + "/must_revalidate";
    }

    protected static void getServletHandler(String... servletMappings) {
        for (int n = 0; n < servletMappings.length; n = n + 2) {
            String name = servletMappings[n];
            String root = servletMappings[n + 1];
            JettyUtil.addServlet(name, root);
        }
    }

    protected String getBase() {
        return "http://localhost:" + JettyUtil.getPort();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        getServletHandler();
        JettyUtil.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        JettyUtil.stop();
    }

    protected static void getServletHandler() {
        getServletHandler("org.apache.abdera2.test.client.CacheTest$CheckCacheInvalidateServlet",
                          "/check_cache_invalidate",
                          "org.apache.abdera2.test.client.CacheTest$NoCacheServlet",
                          "/no_cache",
                          "org.apache.abdera2.test.client.CacheTest$AuthServlet",
                          "/auth",
                          "org.apache.abdera2.test.client.CacheTest$CheckMustRevalidateServlet",
                          "/must_revalidate");
    }

    public static class CheckMustRevalidateServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            String reqnum = request.getHeader("X-Reqnum");
            int req = Integer.parseInt(reqnum);
            if (req == 1) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/plain");
                response.setHeader("Cache-Control", "must-revalidate");
                response.setDateHeader("Date", System.currentTimeMillis());
                response.getWriter().println(reqnum);
                response.getWriter().close();
            } else if (req == 2) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setContentType("text/plain");
                response.setDateHeader("Date", System.currentTimeMillis());
                return;
            } else if (req == 3) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setDateHeader("Date", System.currentTimeMillis());
                return;
            }
        }
    }

    public static class CheckCacheInvalidateServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            String reqnum = request.getHeader("X-Reqnum");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setDateHeader("Date", System.currentTimeMillis());
            response.setContentType("text/plain");
            response.setHeader("Cache-Control", "max-age=60");
            response.getWriter().println(reqnum);
            response.getWriter().close();
        }

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        }

        protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        }

        protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        }
    }

    public static class NoCacheServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            String reqnum = request.getHeader("X-Reqnum");
            int reqtest = Integer.parseInt(request.getHeader("X-Reqtest"));

            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK);
            switch (reqtest) {
                case NOCACHE:
                    response.setHeader("Cache-Control", "no-cache");
                    break;
                case NOSTORE:
                    response.setHeader("Cache-Control", "no-store");
                    break;
                case MAXAGE0:
                    response.setHeader("Cache-Control", "max-age=0");
                    break;
            }
            response.setDateHeader("Date", System.currentTimeMillis());

            response.getWriter().println(reqnum);
            response.getWriter().close();
        }
    }

    public static class AuthServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            String reqnum = request.getHeader("X-Reqnum");
            int num = Integer.parseInt(reqnum);
            switch (num) {
                case 1:
                    response.setStatus(HttpServletResponse.SC_OK);
                    break;
                case 2:
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_OK);
                    break;
            }
            response.setDateHeader("Date", System.currentTimeMillis());
            response.setContentType("text/plain");
            response.getWriter().println(reqnum);
            response.getWriter().close();
        }
    }

    private static final int NOCACHE = 0;
    private static final int NOSTORE = 1;
    private static final int MAXAGE0 = 2;
    private static final int POST = 3;
    private static final int DELETE = 4;
    private static final int PUT = 5;

    @Test
    public void testRequestNoStore() throws Exception {
        _requestCacheInvalidation(NOSTORE);
    }

    @Test
    public void testRequestNoCache() throws Exception {
        _requestCacheInvalidation(NOCACHE);
    }

    @Test
    public void testRequestMaxAge0() throws Exception {
        _requestCacheInvalidation(MAXAGE0);
    }

    @Test
    public void testResponseNoStore() throws Exception {
        _responseNoCache(NOSTORE);
    }

    @Test
    public void testResponseNoCache() throws Exception {
        _responseNoCache(NOCACHE);
    }

    @Test
    public void testResponseMaxAge0() throws Exception {
        _responseNoCache(MAXAGE0);
    }

    @Test
    public void testPostInvalidates() throws Exception {
        _methodInvalidates(POST);
    }

    @Test
    public void testPutInvalidates() throws Exception {
        _methodInvalidates(PUT);
    }

    @Test
    public void testDeleteInvalidates() throws Exception {
        _methodInvalidates(DELETE);
    }

    @Test
    public void testAuthForcesRevalidation() throws Exception {

        // TODO: Actually need to rethink this. Responses to authenticated requests
        // should never be cached unless the resource is explicitly marked as
        // being cacheable (e.g. using Cache-Control: public). So this test
        // was testing incorrect behavior.

        // AbderaClient client = new CommonsClient();
        // client.usePreemptiveAuthentication(true);
        // client.addCredentials(CHECK_AUTH, null, null, new UsernamePasswordCredentials("james","snell"));
        // RequestOptions options = client.getDefaultRequestOptions();
        // options.setHeader("Connection", "close");
        // options.setRevalidateWithAuth(true);
        // options.setHeader("x-reqnum", "1");
        // Response response = client.get(CHECK_AUTH, options);
        //  
        // // first request works as expected. fills the cache
        // String resp1 = getResponse(response);
        // assertEquals(resp1, "1");
        //
        // // second request uses authentication, should force revalidation of the cache
        // options.setHeader("x-reqnum", "2");
        // response = client.get(CHECK_AUTH, options);
        //  
        // resp1 = getResponse(response);
        // assertEquals(response.getStatus(), HttpServletResponse.SC_UNAUTHORIZED);
        // assertEquals(resp1, "2");
        //
        // // third request does not use authentication, but since the previous request
        // // resulted in an "unauthorized" response, the cache needs to be refilled
        // options.setHeader("x-reqnum", "3");
        // client.usePreemptiveAuthentication(false);
        // response = client.get(CHECK_AUTH, options);
        //  
        // resp1 = getResponse(response);
        // assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        // assertEquals(resp1, "3");
        //
        // // fourth request does not use authentication, will pull from the cache
        // options = client.getDefaultRequestOptions();
        // options.setHeader("x-reqnum", "4");
        // client.usePreemptiveAuthentication(false);
        // response = client.get(CHECK_AUTH, options);
        //  
        // resp1 = getResponse(response);
        // assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        // assertEquals(resp1, "3");
        //    
        // // fifth request uses authentication, will force revalidation
        // options.setAuthorization("Basic amFtZXM6c25lbGw=");
        // options.setHeader("x-reqnum", "5");
        // response = client.get(CHECK_AUTH, options);
        //    
        // resp1 = getResponse(response);
        // assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        // assertEquals(resp1, "5");
    }

    @Test
    public void testResponseMustRevalidate() throws Exception {
        Client abderaClient = new BasicCachingClient();
        Session session = abderaClient.newSession();
        ClientResponse response = 
          session.get(
            CHECK_MUST_REVALIDATE, 
            session.getDefaultRequestOptions()
              .header("Connection", "close")
              .header("x-reqnum", "1")
              .get());

        String resp1 = getResponse(response);
        assertEquals("1", resp1);

        response = session.get(
          CHECK_MUST_REVALIDATE, 
          session.getDefaultRequestOptions()
            .header("Connection", "close")
            .header("x-reqnum", "2")
            .get());

        assertEquals(304, response.getStatus());

        response = session.get(
          CHECK_MUST_REVALIDATE, 
          session.getDefaultRequestOptions()
            .header("Connection", "close")
            .header("x-reqnum", "3")
            .get());
        assertEquals(404, response.getStatus());
        response.release();

        abderaClient.shutdown();
    }

    private RequestOptions.Builder getRequestOptions(Session session, int num) {
        return session.getDefaultRequestOptions()
          .header("Connection", "close")
          .header("x-reqnum", String.valueOf(num))
          .doNotUseExpectContinue();
    }

    private void _methodInvalidates(int type) throws Exception {

        Client abderaClient = new BasicCachingClient();
        Session session = abderaClient.newSession();
        ClientResponse response = session.get(
          CHECK_CACHE_INVALIDATE, 
          getRequestOptions(session, 1).get());

        String resp1 = getResponse(response);

        response.release();
        assertEquals("1", resp1);

        switch (type) {
            case POST:
                response = session.post(
                  CHECK_CACHE_INVALIDATE, 
                  new ByteArrayInputStream("".getBytes()), 
                  getRequestOptions(session, 2).get());
                break;
            case PUT:
                response = session.put(
                  CHECK_CACHE_INVALIDATE, 
                  new ByteArrayInputStream("".getBytes()), 
                  getRequestOptions(session, 2).get());
                break;
            case DELETE:
                response = session.delete(
                  CHECK_CACHE_INVALIDATE, 
                  getRequestOptions(session, 2).get());
                break;
        }
        response.release();
        
        response = session.get(
          CHECK_CACHE_INVALIDATE, 
          getRequestOptions(session, 3).get());

        resp1 = getResponse(response);
        response.release();
        assertEquals("3", resp1);
        
        abderaClient.shutdown();
    }

    private void _requestCacheInvalidation(int type) throws Exception {

        BasicCachingClient abderaClient = new BasicCachingClient();
        Session session = abderaClient.newSession();
        ClientResponse response = session.get(
          CHECK_CACHE_INVALIDATE, 
          getRequestOptions(session, 1).get());
        String resp1 = getResponse(response);
        assertEquals("1", resp1);

        // Should use the cache
        RequestOptions.Builder builder = getRequestOptions(session, 3);
        switch (type) {
          case NOCACHE:
            builder.cacheControl(CacheControl.NONNOCACHE());
            break;
          case NOSTORE:
            builder.cacheControl(CacheControl.NONNOSTORE());
            break;
          case MAXAGE0:
            builder.cacheControl(CacheControl.MAXAGE(60));
            try {
              // sleep for a few seconds to let the cache age;
              Thread.sleep(5*1000);
            } catch (Throwable t) {}
              break;
        }
        response = session.get(CHECK_CACHE_INVALIDATE, builder.get());
        String resp3 = getResponse(response);
        assertEquals("1", resp3);
        
        // Should not use the cache
        builder = getRequestOptions(session, 2);
        switch (type) {
          case NOCACHE:
            builder.cacheControl(CacheControl.NOCACHE());
            break;
          case NOSTORE:
            builder.cacheControl(CacheControl.NOSTORE());
            break;
          case MAXAGE0:
            builder.cacheControl(CacheControl.MAXAGE(0));
            try {
              // sleep for a few seconds to let the cache age;
              Thread.sleep(5*1000);
            } catch (Throwable t) {}
            break;
        }
        response = session.get(CHECK_CACHE_INVALIDATE, builder.get());

        String resp2 = getResponse(response);
        assertEquals("2", resp2);
       
        abderaClient.shutdown();
    }

    private void _responseNoCache(int type) throws Exception {

        Client abderaClient = new BasicCachingClient();
        Session session = abderaClient.newSession();
        ClientResponse response = 
          session.get(
            CHECK_NO_CACHE, 
            getRequestOptions(session, 1)
              .header("x-reqtest", String.valueOf(type))
              .get());

        String resp1 = getResponse(response);
        assertEquals("1", resp1);

        // Should not use the cache
        response = session.get(
          CHECK_NO_CACHE, 
          getRequestOptions(session, 2)
            .header("x-reqtest", String.valueOf(type))
            .get());

        String resp2 = getResponse(response);
        assertEquals("2", resp2);

        // Should use the cache
        response = session.get(
          CHECK_NO_CACHE, 
          getRequestOptions(session, 3)
            .header("x-reqtest", String.valueOf(type))
            .get());

        String resp3 = getResponse(response);
        assertEquals("3", resp3);
        
        abderaClient.shutdown();
    }

    private static String getResponse(ClientResponse response) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int m = -1;
        InputStream in = response.getInputStream();
        if (in == null) return null;
        while ((m = in.read()) != -1) {
            out.write(m);
        }
        in.close();
        String resp = new String(out.toByteArray());
        return resp.trim();
    }

}
