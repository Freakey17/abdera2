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

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.http.Authentication;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.ResponseType;
import org.joda.time.DateTime;

public abstract class AbstractResponse extends AbstractMessage implements Response {
  
    public long getAge() {
      return getHeader("Age", MoreFunctions.parseLong);
    }

    public String getAllow() {
        return getHeader("Allow");
    }

    public long getContentLength() {
      return getHeader("Content-Length", MoreFunctions.parseLong);
    }

    public EntityTag getEntityTag() {
      return getHeader("ETag", EntityTag.parser);
    }

    public DateTime getExpires() {
        return getDateHeader("Expires");
    }

    public DateTime getLastModified() {
        return getDateHeader("Last-Modified");
    }

    public IRI getLocation() {
      return getHeader("Location",IRI.parser);
    }

    public ResponseType getType() {
      return ResponseType.select(getStatus());
    }

    public Iterable<Authentication> getAuthentication() {
      return getHeader("WWW-Authenticate", Authentication.parser);
  }
}
