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
package org.apache.abdera2.parser.axiom;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.model.Collection;
import org.apache.abdera2.model.Element;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings("deprecation")
public class FOMMultipartCollection extends FOMCollection {

    public FOMMultipartCollection() {
        super();
    }

    public FOMMultipartCollection(String title, String href, Map<String, String> accepts) {
        this();
        setTitle(title);
        setHref(href);
        setAccept(accepts);
    }

    public FOMMultipartCollection(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    public FOMMultipartCollection(QName qname, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(qname, parent, factory, builder);
    }

    public FOMMultipartCollection(OMContainer parent, OMFactory factory) {
        super(COLLECTION, parent, factory);
    }

    public FOMMultipartCollection(OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(COLLECTION, parent, factory, builder);
    }

    public boolean acceptsMultipart(String mediaType) {
        Map<String, String> accept = getAcceptMultiparted();
        if (accept.size() == 0)
            accept = Collections.singletonMap("application/atom+xml;type=entry", null);
        for (Map.Entry<String, String> entry : accept.entrySet()) {
            if (MimeTypeHelper.isMatch(entry.getKey(), mediaType) && entry.getValue() != null
                && entry.getValue().equals(LN_ALTERNATE_MULTIPART_RELATED))
                return true;
        }
        return false;
    }

    public boolean acceptsMultipart(MimeType mediaType) {
        return accepts(mediaType.toString());
    }

    public Map<String, String> getAcceptMultiparted() {
        Map<String, String> accept = new HashMap<String, String>();
        Iterator<?> i = getChildrenWithName(ACCEPT);
        if (i == null || !i.hasNext())
            i = getChildrenWithName(PRE_RFC_ACCEPT);
        while (i.hasNext()) {
            Element e = (Element)i.next();
            String t = e.getText();
            if (t != null) {
                if (e.getAttributeValue(ALTERNATE) != null && e.getAttributeValue(ALTERNATE).trim().length() > 0) {
                    accept.put(t.trim(), e.getAttributeValue(ALTERNATE));
                } else {
                    accept.put(t.trim(), null);
                }
            }
        }
        return accept;
    }

    public Collection setAccept(String mediaRange, String alternate) {
        return setAccept(Collections.singletonMap(mediaRange, alternate));
    }

    public Collection setAccept(Map<String, String> mediaRanges) {
        complete();
        if (mediaRanges != null && mediaRanges.size() > 0) {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
            if (mediaRanges.size() == 1 && mediaRanges.keySet().iterator().next().equals("")) {
                addExtension(ACCEPT);
            } else {
                for (Map.Entry<String, String> entry : mediaRanges.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("entry")) {
                        addSimpleExtension(ACCEPT, "application/atom+xml;type=entry");
                    } else {
                        try {
                            Element accept = addSimpleExtension(ACCEPT, new MimeType(entry.getKey()).toString());
                            if (entry.getValue() != null) {
                                accept.setAttributeValue(ALTERNATE, entry.getValue());
                            }
                        } catch (javax.activation.MimeTypeParseException e) {
                            throw new org.apache.abdera2.common.mediatype.MimeTypeParseException(e);
                        }
                    }
                }
            }
        } else {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
        }
        return this;
    }

    public Collection addAccepts(String mediaRange, String alternate) {
        return addAccepts(Collections.singletonMap(mediaRange, alternate));
    }

    public Collection addAccepts(Map<String, String> mediaRanges) {
        complete();
        if (mediaRanges != null) {
            for (Map.Entry<String, String> entry : mediaRanges.entrySet()) {
                if (!accepts(entry.getKey())) {
                    Element accept = 
                      addSimpleExtension(
                        ACCEPT,
                        MimeTypeHelper.create(
                          entry.getKey()).toString());
                    if (entry.getValue() != null)
                      accept.setAttributeValue(
                        ALTERNATE, 
                        entry.getValue());
                }
            }
        }
        return this;
    }
}
