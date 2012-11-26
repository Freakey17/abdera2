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
package org.apache.abdera2.factory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;

/**
 * A utility implementation of ExtensionFactory used internally by Abdera. It maintains the collection ExtensionFactory
 * instances discovered on the classpath and a cache of Internal-Wrapper mappings.
 */
public class ExtensionFactoryMap 
  implements ExtensionFactory, Iterable<ExtensionFactory> {

    private final Set<ExtensionFactory> factories = 
      new HashSet<ExtensionFactory>();
    
    private final Set<String> namespaces = 
      new HashSet<String>();
    
    public ExtensionFactoryMap(Iterable<ExtensionFactory> factories) {
      for (ExtensionFactory factory : factories)
        addFactory(factory);
    }

    @SuppressWarnings("unchecked")
    public <T extends Element> T getElementWrapper(Element internal) {
        if (internal == null)
            return null;
        T t = null;
        QName qname = internal.getQName();
        String ns = qname.getNamespaceURI();
        for (ExtensionFactory factory : factories) {
            if (ns == null || factory.handlesNamespace(ns)) {
              t = (T)factory.getElementWrapper(internal);
              if (t != null && t != internal)
                  return t;
            }
        }
        return (t != null) ? t : (T)internal;
    }

    public Iterable<String> getNamespaces() {
        return namespaces;
    }

    public boolean handlesNamespace(String namespace) {
        return namespaces.contains(namespace);
    }

    public ExtensionFactoryMap addFactory(ExtensionFactory factory) {
        factories.add(factory);
        for (String ns : factory.getNamespaces())
          namespaces.add(ns);
        return this;
    }

    public <T extends Base> String getMimeType(T base) {
        Element element = base instanceof Element ? (Element)base : ((Document<?>)base).getRoot();
        String namespace = element.getQName().getNamespaceURI();
        for (ExtensionFactory factory : factories)
            if (factory.handlesNamespace(namespace))
                return factory.getMimeType(base);
        return null;
    }

    public Iterator<ExtensionFactory> iterator() {
      return factories.iterator();
    }
}
