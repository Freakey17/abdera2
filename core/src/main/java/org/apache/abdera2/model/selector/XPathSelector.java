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
package org.apache.abdera2.model.selector;

import java.util.Map;

import org.apache.abdera2.common.selector.AbstractSelector;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.xpath.XPath;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * Selects a collection based on a boolean XPath expression
 * @see org.apache.abdera2.common.selector.Selector
 */
public class XPathSelector 
extends AbstractSelector<Object>
implements Selector<Object> {

    public static Builder make() {
      return new Builder();
    }
    
    public static Builder make(XPath xpath) {
      return new Builder().using(xpath);
    }
  
    public static final class Builder 
      implements Supplier<Selector<Object>> {
      private XPath xpath;
      private String path;
      private ImmutableMap.Builder<String, String> namespaces =
        ImmutableMap.<String,String>builder();
      public Builder using(XPath xpath) {
        this.xpath = xpath;
        return this;
      }
      public Builder path(String path) {
        this.path = path;
        return this;
      }
      public Builder with(String prefix, String namespace) {
        this.namespaces.put(prefix,namespace);
        return this;
      }
      public Builder with(Map<String,String> namespaces) {
        this.namespaces.putAll(namespaces);
        return this;
      }
      public Selector<Object> get() {
        return new XPathSelector(this);
      }
    }
  
    private static final long serialVersionUID = 7751803876821166591L;

    private final XPath xpath;
    private final Map<String, String> namespaces;
    private final String path;
    
    XPathSelector(Builder builder) {
      this.path = builder.path;
      this.xpath = builder.xpath;
      this.namespaces = builder.namespaces.build();
    }

    public boolean select(Object element) {
      if (!(element instanceof Element)) return false;
        if (xpath.booleanValueOf(path, (Element)element, namespaces))
            return true;
        return false;
    }

    public void addNamespace(String prefix, String uri) {
        namespaces.put(prefix, uri);
    }
}
