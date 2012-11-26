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

import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.factory.Factory;

import javax.xml.namespace.QName;

/**
 * Most of the original code for this class came from the OMChildrenQNameIterator from Axiom
 */
@SuppressWarnings("rawtypes")
public class ExtensionIterator implements Iterator<Element> {

    /**
     * Field givenQName
     */
    private final String namespace;
    private final String extns;
    private final Factory factory;
    private final Selector selector;

    protected final Iterator<?> children;
    protected Element currentChild = null;
    

    /**
     * Constructor OMChildrenQNameIterator.
     * 
     * @param currentChild
     * @param givenQName
     */
    public ExtensionIterator(Element parent) {
        this(parent,null,null);
    }
    
    public ExtensionIterator(Element parent, Selector selector) {
      this(parent,null,selector);
    }

    public ExtensionIterator(Element parent, String extns) {
      this(parent,extns,null);
    }
    
    public ExtensionIterator(Element parent, String extns, Selector selector) {
        this.children = parent.iterator();
        this.selector = selector;
        this.namespace = parent.getQName().getNamespaceURI();
        this.factory = parent.getFactory();
        this.currentChild = getNext();
        this.extns = extns;
    }

    private Element getNext() {
      while (children.hasNext()) {
        Object child = children.next();
        if ((child instanceof Element) && (isQNamesMatch(((Element)child).getQName(),
            this.namespace)) && (selector == null || selector.select((Element)child))) {
          return factory.getElementWrapper((Element)child);
        }
      }
      return null;
    }
    
    public boolean hasNext() {
      return currentChild != null;
    }

    public Element next() {
      if (currentChild == null) return null;
      Element child = currentChild;
      currentChild = getNext();
      return factory.getElementWrapper(child);
    }

    private boolean isQNamesMatch(QName elementQName, String namespace) {
        String elns = elementQName == null ? "" : elementQName.getNamespaceURI();
        boolean namespaceURIMatch = (namespace == null) || ("".equals(namespace)) || elns.equals(namespace);
        if (!namespaceURIMatch && extns != null && !elns.equals(extns))
            return false;
        else
            return !namespaceURIMatch;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

}
