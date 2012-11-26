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
package org.apache.abdera2.common.misc;

import org.apache.abdera2.common.mediatype.MimeTypeParseException;

public class ExceptionHelper {

  public static <T extends Throwable>void checked(
      boolean expression, 
      Class<T> _class) throws T {
    checked(expression,_class,null);
  }
  
  public static <T extends Throwable>void checked(
    boolean expression, 
    Class<T> _class, 
    String message, 
    Object... args) throws T {
      if (!expression) {
        T t = null;
        try {
          if (message != null) {
            StringBuilder buf = 
              new StringBuilder(message);
            if (args.length > 0)
              buf.append(" ");
            for (Object arg : args)
              buf.append('[')
                 .append(arg)
                 .append(']');
            t = MoreFunctions
              .<T>createInstance(
                _class,
                buf.toString());
          } else
            t = MoreFunctions
              .<T>createInstance(_class)
                .apply(null);
        } catch (Throwable e) {
          throw propogate(e);
        }
        throw t;
      }
  }
  
  public static RuntimeException propogate(Throwable t) {
    if (t instanceof RuntimeException)
      throw (RuntimeException)t;
    else if (t instanceof Error)
      throw (Error)t;
    else if (t instanceof javax.activation.MimeTypeParseException) 
      throw MimeTypeParseException.wrap(
        (javax.activation.MimeTypeParseException)t);
    else return new RuntimeException(t);
  }
  
//  public static void log(Log log, Throwable t) {
//    if (t instanceof ResponseContextException) {
//      ResponseContextException rce = 
//        (ResponseContextException)t;
//        if (rce.getResponseType() == ResponseType.CLIENT_ERROR)
//            log.info(t);
//        else
//            log.error(t);
//    } else log.error(t);
//  }
  
//  public static void responseLog(Log log, ResponseContextException t) {
//    if (log.isDebugEnabled())
//      log.debug("A ResponseException was thrown.", t);
//    else if (t.getResponseContext().getType() == ResponseType.SERVER_ERROR)
//      log.warn("A ResponseException was thrown.", t);
//  }
}
