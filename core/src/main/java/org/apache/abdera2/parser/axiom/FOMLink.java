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

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.Link;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMLink extends FOMExtensibleElement implements Link {

    private static final long serialVersionUID = 2239772197929910635L;

    public FOMLink() {
        super(Constants.LINK);
    }

    public FOMLink(String href) {
        this();
        setHref(href);
    }

    public FOMLink(String href, String rel) {
        this();
        setHref(href);
        setRel(rel);
    }

    public FOMLink(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    public FOMLink(OMContainer parent, OMFactory factory) throws OMException {
        super(LINK, parent, factory);
    }

    public FOMLink(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    public FOMLink(OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(LINK, parent, factory, builder);
    }

    public FOMLink(QName qname, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder)
        throws OMException {
        super(qname, parent, factory, builder);
    }

    public IRI getHref() {
        return _getUriValue(getAttributeValue(HREF));
    }

    public IRI getResolvedHref() {
        return _resolve(getResolvedBaseUri(), getHref());
    }

    public Link setHref(String href) {
      complete();
      return setAttributeValue(HREF, href==null?null:(new IRI(href)).toString());
    }
    
    public Link setHref(IRI href) {
      complete();
      return setAttributeValue(HREF, href==null?null:href.toString());
    }

    public String getRel() {
        return getAttributeValue(REL);
    }
    
    public String getCanonicalRel() {
      String rel = getRel();
      if (rel != null && rel.startsWith(IANA_BASE))
        rel = Link.Helper.getRelEquiv(rel);
      return rel != null ? rel : Link.REL_ALTERNATE;
    }

    public Link setRel(String rel) {
      complete();
        if (rel != null && rel.startsWith(IANA_BASE))
          rel = Link.Helper.getRelEquiv(rel);
        return setAttributeValue(REL, rel);
    }

    public MimeType getMimeType() {
        try {
            String type = getAttributeValue(TYPE);
            return (type != null) ? new MimeType(type) : null;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera2.common.mediatype.MimeTypeParseException(e);
        }
    }

    public void setMimeType(MimeType type) {
      complete();
        setAttributeValue(TYPE, (type != null) ? type.toString() : null);
    }

    public Link setMimeType(String type) {
        try {
          complete();
          return setAttributeValue(TYPE,type==null?null:(new MimeType(type)).toString());
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera2.common.mediatype.MimeTypeParseException(e);
        }
    }

    public String getHrefLang() {
        return getAttributeValue(HREFLANG);
    }

    public Link setHrefLang(String lang) {
      complete();
      return setAttributeValue(HREFLANG,lang);
    }

    public String getTitle() {
        return getAttributeValue(ATITLE);
    }

    public Link setTitle(String title) {
      complete();
      return setAttributeValue(ATITLE,title);
    }

    public long getLength() {
        String l = getAttributeValue(LENGTH);
        return (l != null) ? Long.valueOf(l) : -1;
    }

    public Link setLength(long length) {
      complete();
      return setAttributeValue(LENGTH, length<0?null:Long.toString(length));
    }

    public String getValue() {
        return getText();
    }

    public void setValue(String value) {
        complete();
        if (value != null)
            ((Element)this).setText(value);
        else
            _removeAllChildren();
    }

}
