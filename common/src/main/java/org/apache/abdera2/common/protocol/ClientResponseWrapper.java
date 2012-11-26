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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.activation.MimeType;

import org.apache.abdera2.common.http.Authentication;
import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.Method;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.ResponseType;
import org.apache.abdera2.common.http.WebLink;
import org.apache.abdera2.common.iri.IRI;
import org.joda.time.DateTime;

import com.google.common.base.Function;

public class ClientResponseWrapper 
  implements ClientResponse {

  private final ClientResponse internal;
  
  public ClientResponseWrapper(ClientResponse internal) {
    this.internal = internal;
  }
  
  public EntityTag getEntityTag() {
    return internal.getEntityTag();
  }

  public ResponseType getType() {
    return internal.getType();
  }

  public int getStatus() {
    return internal.getStatus();
  }

  public String getStatusText() {
    return internal.getStatusText();
  }

  public DateTime getLastModified() {
    return internal.getLastModified();
  }

  public long getContentLength() {
    return internal.getContentLength();
  }

  public String getAllow() {
    return internal.getAllow();
  }

  public IRI getLocation() {
    return internal.getLocation();
  }

  public long getAge() {
    return internal.getAge();
  }

  public DateTime getExpires() {
    return internal.getExpires();
  }

  public String getHeader(String name) {
    return internal.getHeader(name);
  }

  public String getDecodedHeader(String name) {
    return internal.getDecodedHeader(name);
  }

  public Iterable<Object> getHeaders(String name) {
    return internal.getHeaders(name);
  }

  public Iterable<String> getDecodedHeaders(String name) {
    return internal.getDecodedHeaders(name);
  }

  public Iterable<String> getHeaderNames() {
    return internal.getHeaderNames();
  }

  public String getSlug() {
    return internal.getSlug();
  }

  public MimeType getContentType() {
    return internal.getContentType();
  }

  public IRI getContentLocation() {
    return internal.getContentLocation();
  }

  public String getContentLanguage() {
    return internal.getContentLanguage();
  }

  public DateTime getDateHeader(String name) {
    return internal.getDateHeader(name);
  }

  public CacheControl getCacheControl() {
    return internal.getCacheControl();
  }

  public Iterable<Authentication> getAuthentication() {
    return internal.getAuthentication();
  }

  public Iterable<WebLink> getWebLinks() {
    return internal.getWebLinks();
  }

  public Iterable<Preference> getPrefer() {
    return internal.getPrefer();
  }

  public Method getMethod() {
    return internal.getMethod();
  }

  public String getUri() {
    return internal.getUri();
  }

  public void release() {
    internal.release();
  }

  public InputStream getInputStream() throws IOException {
    return internal.getInputStream();
  }

  public Reader getReader() throws IOException {
    return internal.getReader();
  }

  public Reader getReader(String charset) throws IOException {
    return internal.getReader(charset);
  }

  public DateTime getServerDate() {
    return internal.getServerDate();
  }

  public String getCharacterEncoding() {
    return internal.getCharacterEncoding();
  }

  public void writeTo(OutputStream out) throws IOException {
    internal.writeTo(out);
  }

  public Session getSession() {
    return internal.getSession();
  }

  public <T> T getHeader(String name, Function<String, T> transform) {
    return internal.getHeader(name, transform);
  }

  public <T> Iterable<T> getHeaders(String name, Function<String, T> transform) {
    return internal.getHeaders(name, transform);
  }

}
