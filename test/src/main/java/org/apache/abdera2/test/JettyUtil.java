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
package org.apache.abdera2.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyUtil {

    private static final String PORT_PROP = "abdera.test.client.port";

    private static int PORT = 9002;
    private static Server server = null;
    private static ServletContextHandler handler = null;

    public static int getPort() {
        if (System.getProperty(PORT_PROP) != null) {
            PORT = Integer.parseInt(System.getProperty(PORT_PROP));
        }
        return PORT;
    }

    public static void initServer() throws Exception {
        server = new Server(getPort());      
        handler = 
          new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        server.setHandler(handler);
    }

    public static ServletContextHandler getSch() {
      return (ServletContextHandler) server.getHandler();
    }
    
    public static void addServlet(String _class, String path) {
        try {
            if (server == null)
                initServer();
        } catch (Exception e) {
        }
        handler.addServlet(_class, path);
    }
    
    public static void addServlet(ServletHolder holder, String path) {
      try {
        if (server == null)
          initServer();
      } catch (Exception e) {
        
      }
      handler.addServlet(holder, path);
    }
    
    public static void setContextPath(String path) {
      handler.setContextPath(path);
    }

    public static void start() throws Exception {
        if (server == null)
            initServer();
        if (server.isRunning())
            return;
        server.start();
    }

    public static void stop() throws Exception {
        if (server == null)
            return;
        server.stop();
        server = null;
    }

    public static boolean isRunning() {
        return (server != null);
    }

}
