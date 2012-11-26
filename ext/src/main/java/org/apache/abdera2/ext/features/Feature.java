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
package org.apache.abdera2.ext.features;

import java.util.HashSet;
import java.util.Set;

import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.common.anno.QName;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ExtensibleElementWrapper;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import static org.apache.abdera2.ext.features.FeaturesHelper.*;
import static com.google.common.base.Preconditions.*;

@QName(value="feature", 
    ns=FNS,
    pfx=FPX)
public class Feature extends ExtensibleElementWrapper {

    public Feature(Element internal) {
        super(internal);
    }
    
    public Feature(Factory factory, javax.xml.namespace.QName qname) {
        super(factory, qname);
    }

    public IRI getRef() {
        String ref = getAttributeValue("ref");
        return (ref != null) ? new IRI(ref) : null;
    }

    public IRI getHref() {
        String href = getAttributeValue("href");
        return (href != null) ? new IRI(href) : null;
    }

    public String getLabel() {
        return getAttributeValue("label");
    }

    public void setRef(String ref) {
      setAttributeValue("ref", (new IRI(checkNotNull(ref))).toString());
    }

    public void setHref(String href) {
      if (href != null)
        setAttributeValue("href", (new IRI(href)).toString());
      else
        removeAttribute("href");
    }

    public void setLabel(String label) {
        if (label != null)
            setAttributeValue("label", label);
        else
            removeAttribute("label");
    }

    public void addType(String mediaRange) {
        addType(new String[] {mediaRange});
    }

    public void addType(String... mediaRanges) {
        mediaRanges = MimeTypeHelper.condense(mediaRanges);
        for (String mediaRange : mediaRanges)
          addSimpleExtension(
            FeaturesHelper.TYPE, 
            MimeTypeHelper.unmodifiableMimeType(mediaRange).toString());
    }

    public Iterable<String> getTypes() {
        Set<String> list = new HashSet<String>();
        for (Element type : getExtensions(FeaturesHelper.TYPE)) {
          String value = type.getText();
          if (value != null)
            list.add(MimeTypeHelper.unmodifiableMimeType(value.trim()).toString());
        }
        return list;
    }

}
