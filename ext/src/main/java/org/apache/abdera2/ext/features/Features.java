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

import java.util.List;

import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.common.anno.QName;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ExtensibleElementWrapper;
import static org.apache.abdera2.ext.features.FeaturesHelper.*;

@QName(value="features", 
    ns=FNS,
    pfx=FPX)
public class Features extends ExtensibleElementWrapper {

    public Features(Element internal) {
        super(internal);
    }

    public Features(Factory factory, javax.xml.namespace.QName qname) {
        super(factory, qname);
    }

    public IRI getResolvedHref() {
        IRI base = getResolvedBaseUri();
        IRI href = getHref();
        return base != null ? base.resolve(href) : href;
    }

    public IRI getHref() {
        String href = getAttributeValue("href");
        return href != null ? new IRI(href) : null;
    }

    public void setHref(String href) {
        setAttributeValue("href", (new IRI(href)).toString());
    }

    public String getName() {
        return getAttributeValue("name");
    }

    public void setName(String name) {
        setAttributeValue("name", name);
    }

    public void addFeature(Feature feature) {
        addExtension(feature);
    }

    public void addFeature(Feature... features) {
        for (Feature feature : features)
            addFeature(feature);
    }

    public Feature addFeature(String feature) {
        Feature f = addExtension(FeaturesHelper.FEATURE);
        f.setRef(feature);
        return f;
    }

    public Feature addFeature(String feature, String href, String label) {
        Feature f = addExtension(FeaturesHelper.FEATURE);
        f.setRef(feature);
        f.setHref(href);
        f.setLabel(label);
        return f;
    }

    public Iterable<Feature> addFeatures(String... features) {
        for (String feature : features)
            addFeature(feature);
        return getFeatures();
    }

    public List<Feature> getFeatures() {
        return getExtensions(FeaturesHelper.FEATURE);
    }
}
