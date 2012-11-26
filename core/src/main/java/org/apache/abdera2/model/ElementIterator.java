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
package org.apache.abdera2.model;

import java.util.Iterator;

import javax.xml.namespace.QName;


public class ElementIterator<T extends Element> implements Iterator<T> {

    /**
     * Field givenQName
     */
    protected QName attribute = null;
    protected String value = null;
    protected String defaultValue = null;
    
    protected final Class<?> _class;
    protected final Iterator<?> children;
    protected Element currentChild = null;

    /**
     * Constructor OMChildrenQNameIterator.
     * 
     * @param currentChild
     * @param givenQName
     */
    public ElementIterator(Element parent, Class<?> _class) {
        this.children = parent.iterator();
        this._class = _class;
    }

    public ElementIterator(Element parent, Class<?> _class, QName attribute, String value, String defaultValue) {
        this(parent, _class);
        this.attribute = attribute;
        this.value = value;
        this.defaultValue = defaultValue;
        this.currentChild = getNext();
    }

    private Element getNext() {
      while (children.hasNext()) {
        Object child = children.next();
        if (child instanceof Element && ((_class != null && _class.isAssignableFrom(child.getClass())) || _class == null) && isMatch((Element)child)) {
          return (Element)child;
        }
      }
      return null;
    }
    
    public boolean hasNext() {
      return this.currentChild != null;
    }

    @SuppressWarnings("unchecked")
    public T next() {
      if (currentChild == null) return null;
      Element child = currentChild;
      currentChild = getNext();
      return (T)child;
    }

    protected boolean isMatch(Element el) {
        if (attribute != null) {
            String val = el.getAttributeValue(attribute);
            return ((val == null && value == null) || (val == null && value != null && value.equals(defaultValue)) || (val != null && val
                .equals(value)));
        }
        return true;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
}
