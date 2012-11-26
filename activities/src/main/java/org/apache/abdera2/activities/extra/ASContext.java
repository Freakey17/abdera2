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
package org.apache.abdera2.activities.extra;

import java.util.Iterator;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.common.templates.AbstractContext;

/**
 * URI Templates Context implementation based on an Activity Streams
 * object. Makes it easier to construct new URLs based on the properties
 * of an Activity Streams object. For example:
 * 
 * Template template = new Template("{?nextPageToken}"};
 * String the_new_iri = 
 *   template.expand(
 *     ASContext.create(
 *       ASObject
 *         .makeObject()
 *         .set("nextPageToken","..."));
 * 
 * Instances are immutable and threadsafe
 */
public final class ASContext 
  extends AbstractContext {
  private static final long serialVersionUID = 4445623432125049535L;
  private final transient ASBase base;
  
  public static ASContext create(ASBase base) {
    return new ASContext(base);
  }
  
  public ASContext(ASBase base) {
    super(false);
    this.base = base;
  }
  
  public ASContext(ASBase base, boolean isiri) {
    super(isiri);
    this.base = base;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <T> T resolve(String var) {
    Object obj = base.getProperty(var);
    if (obj instanceof Iterable)
      return (T)new IterableWrapper((Iterable)obj);
    return obj instanceof ASBase ? 
      (T)new ASContext((ASBase)obj) :
      (T)base.getProperty(var);
  }

  public boolean contains(String var) {
    return base.has(var);
  }

  public Iterator<String> iterator() {
    return base.iterator();
  }
  
  @SuppressWarnings("rawtypes")
  static class IterableWrapper 
    implements Iterable {

    private final Iterable i;
    
    IterableWrapper(Iterable i) {
      this.i = i;
    }
    
    public Iterator iterator() {
      return new IteratorWrapper(i.iterator());
    }
    
  }
  
  /**
   * This and IterableWrapper ensure that all ASBase values
   * within an ASBase are wrapped as ASContext instances 
   * for purposes of URI Template expansion
   */
  @SuppressWarnings("rawtypes")
  static class IteratorWrapper 
    implements Iterator {

    private final Iterator i;
    
    IteratorWrapper(Iterator i) {
      this.i = i;
    }
    
    public boolean hasNext() {
      return i.hasNext();
    }

    public Object next() {
      Object obj = i.next();
      if (obj instanceof ASBase) 
        return ASContext.create((ASBase)obj);
      if (obj instanceof Iterable)
        return new IterableWrapper((Iterable)obj);
      return obj;
    }

    public void remove() {
      i.remove();
    }
    
  }
}