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
package org.apache.abdera2.ext.sharing;

import java.util.Date;
import java.util.List;

import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.common.anno.QName;
import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ExtensibleElementWrapper;

@QName(value="sharing",ns=SharingHelper.SSENS,pfx=SharingHelper.SSEPFX)
public class Sharing extends ExtensibleElementWrapper {

    public Sharing(Element internal) {
        super(internal);
    }

    public Sharing(Factory factory, javax.xml.namespace.QName qname) {
        super(factory, qname);
    }

    public Date getSince() {
        String since = getAttributeValue("since");
        return since != null ? DateTimes.parse(since) : null;
    }

    public void setSince(Date since) {
        if (since != null) {
            setAttributeValue("since", DateTimes.format(since));
        } else {
            removeAttribute("since");
        }
    }

    public Date getUntil() {
        String until = getAttributeValue("until");
        return until != null ? DateTimes.parse(until) : null;
    }

    public void setUntil(Date until) {
        if (until != null) {
            setAttributeValue("until", DateTimes.format(until));
        } else {
            removeAttribute("until");
        }
    }

    public Date getExpires() {
        String expires = getAttributeValue("expires");
        return expires != null ? DateTimes.parse(expires) : null;
    }

    public void setExpires(Date expires) {
        if (expires != null) {
            setAttributeValue("expires", DateTimes.format(expires));
        } else {
            removeAttribute("expires");
        }
    }

    public List<Related> getRelated() {
        return getExtensions(SharingHelper.SSE_RELATED);
    }

    public void addRelated(Related related) {
        addExtension(related);
    }

    public Related addRelated() {
        return getFactory().newElement(SharingHelper.SSE_RELATED, this);
    }

    public Related addRelated(String link, String title, Related.Type type) {
        Related related = addRelated();
        related.setLink(link);
        related.setTitle(title);
        related.setType(type);
        return related;
    }
}
