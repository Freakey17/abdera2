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

import javax.activation.MimeType;

import org.apache.abdera2.common.http.Authentication;
import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.WebLink;
import org.apache.abdera2.common.iri.IRI;
import org.joda.time.DateTime;

import com.google.common.base.Function;

/**
 * A protocol message. This is used as the basis for both request and response objects in order to provide a consistent
 * interface.
 */
public interface Message {

    <T>T getHeader(String name, Function<String,T> transform);
  
    <T>Iterable<T> getHeaders(String name, Function<String,T> transform);
    
    /**
     * Get the value of the specified header
     */
    String getHeader(String name);

    /**
     * Get the decoded value of a RFC 2047 header
     */
    String getDecodedHeader(String name);

    /**
     * Return multiple values for the specified header
     */
    Iterable<Object> getHeaders(String name);

    /**
     * Return multiple decoded values for the specified header
     */
    Iterable<String> getDecodedHeaders(String name);

    /**
     * Return a listing of header names
     */
    Iterable<String> getHeaderNames();

    /**
     * Return the value of the Slug header
     */
    String getSlug();

    /**
     * Return the value of the Content-Type header
     */
    MimeType getContentType();

    /**
     * Return the value of the Content-Location header
     */
    IRI getContentLocation();

    /**
     * Return the value of the Content-Language header
     */
    String getContentLanguage();

    /**
     * Return the value of a Date header
     */
    DateTime getDateHeader(String name);

    /**
     * Return the Cache Control Data
     */
    CacheControl getCacheControl();
    
    /**
     * Return the Authentication Data (WWW-Authentication or Authorization)
     * @return
     */
    Iterable<Authentication> getAuthentication();

    /**
     * Return the Link header
     */
    Iterable<WebLink> getWebLinks();
    
    /**
     * Return the Prefer header
     */
    Iterable<Preference> getPrefer();
    
}
