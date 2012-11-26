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

import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.misc.ArrayBuilder;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.model.Categories;
import org.apache.abdera2.model.Category;
import org.apache.abdera2.model.Collection;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.Text;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import static org.apache.abdera2.common.mediatype.MimeTypeHelper.*;

import com.google.common.collect.Iterables;

@SuppressWarnings({"deprecation","rawtypes"})
public class FOMCollection extends FOMExtensibleElement implements Collection {

    private static final String[] ENTRY = {"application/atom+xml;type=\"entry\""};

    private static final long serialVersionUID = -5291734055253987136L;

    public FOMCollection() {
        super(Constants.COLLECTION);
    }

    public FOMCollection(String title, String href, String[] accepts) {
        this();
        setTitle(title);
        setHref(href);
        setAccept(accepts);
    }

    public FOMCollection(String name, OMNamespace namespace, OMContainer parent, OMFactory factory)
        throws OMException {
        super(name, namespace, parent, factory);
    }

    public FOMCollection(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    public FOMCollection(QName qname, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(qname, parent, factory, builder);
    }

    public FOMCollection(OMContainer parent, OMFactory factory) {
        super(COLLECTION, parent, factory);
    }

    public FOMCollection(OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(COLLECTION, parent, factory, builder);
    }

    public String getTitle() {
        Text title = this.getFirstChild(TITLE);
        return (title != null) ? title.getValue() : null;
    }

    private Text setTitle(String title, Text.Type type) {
        complete();
        FOMFactory fomfactory = (FOMFactory)factory;
        Text text = fomfactory.newText(PREFIXED_TITLE, type);
        text.setValue(title);
        this._setChild(PREFIXED_TITLE, (OMElement)text);
        return text;
    }

    public Text setTitle(String title) {
        return setTitle(title, Text.Type.TEXT);
    }

    public Text setTitleAsHtml(String title) {
        return setTitle(title, Text.Type.HTML);
    }

    public Text setTitleAsXHtml(String title) {
        return setTitle(title, Text.Type.XHTML);
    }

    public Text getTitleElement() {
        return getFirstChild(TITLE);
    }

    public IRI getHref() {
        return _getUriValue(getAttributeValue(HREF));
    }

    public IRI getResolvedHref() {
        return _resolve(getResolvedBaseUri(), getHref());
    }

    public Collection setHref(String href) {
      complete();
        return setAttributeValue(HREF, href==null?null:(new IRI(href).toString()));
    }

    public String[] getAccept() {
      ArrayBuilder<String> accept = ArrayBuilder.list(String.class);
      Iterator<?> i = getChildrenWithName(ACCEPT);
      if (i == null || !i.hasNext())
          i = getChildrenWithName(PRE_RFC_ACCEPT);
      while (i.hasNext()) {
          Element e = (Element)i.next();
          String t = e.getText();
          if (t != null)
            accept.add(t.trim());
      }
      return condense(accept.build());
    }

    public Collection setAccept(String mediaRange) {
        return setAccept(new String[] {mediaRange});
    }

    public Collection setAccept(Iterable<String> mediaRanges) {
      return setAccept(Iterables.toArray(mediaRanges, String.class));
    }
    
    public Collection setAccept(String... mediaRanges) {
        complete();
        if (mediaRanges != null && mediaRanges.length > 0) {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
            if (mediaRanges.length == 1 && mediaRanges[0].equals("")) {
              addExtension(ACCEPT);
            } else {
              mediaRanges = condense(mediaRanges);
              for (String type : mediaRanges) {
                if (type.equalsIgnoreCase("entry")) {
                  addSimpleExtension(ACCEPT, "application/atom+xml;type=entry");
                } else {
                  addSimpleExtension(ACCEPT, unmodifiableMimeType(type).toString());
                }
              }
            }
        } else {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
        }
        return this;
    }

    public Collection addAccepts(String mediaRange) {
        return addAccepts(new String[] {mediaRange});
    }

    public Collection addAccepts(String... mediaRanges) {
        complete();
        if (mediaRanges != null) 
          for (String type : mediaRanges)
            if (!accepts(type))
              addSimpleExtension(
                ACCEPT, 
                unmodifiableMimeType(type).toString());
        return this;
    }

    public Collection addAcceptsEntry() {
        return addAccepts("application/atom+xml;type=entry");
    }

    public Collection setAcceptsEntry() {
        return setAccept("application/atom+xml;type=entry");
    }

    public Collection setAcceptsNothing() {
        return setAccept("");
    }

    public boolean acceptsEntry() {
        return accepts("application/atom+xml;type=entry");
    }

    public boolean acceptsNothing() {
        return accepts("");
    }

    public boolean accepts(String mediaType) {
        String[] accept = getAccept();
        if (accept.length == 0)
          accept = ENTRY;
        for (String a : accept)
          if (isMatch(a, mediaType))
            return true;
        return false;
    }

    public boolean accepts(MimeType mediaType) {
        return accepts(mediaType.toString());
    }

    public Categories addCategories() {
        complete();
        return ((FOMFactory)factory).newCategories(this);
    }

    public Collection addCategories(Categories categories) {
        complete();
        addChild((OMElement)categories);
        return this;
    }

    public Categories addCategories(String href) {
      complete();
      Categories cats = ((FOMFactory)factory).newCategories();
      cats.setHref(href);
      addCategories(cats);
      return cats;
    }

    public Categories addCategories(List<Category> categories, boolean fixed, String scheme) {
      complete();
      Categories cats = ((FOMFactory)factory).newCategories();
      cats.setFixed(fixed);
      if (scheme != null)
          cats.setScheme(scheme);
      if (categories != null)
        for (Category category : categories)
          cats.addCategory(category);
      addCategories(cats);
      return cats;
    }

    public List<Categories> getCategories() {
        List<Categories> list = _getChildrenAsSet(CATEGORIES);
        if (list == null || list.size() == 0)
            list = _getChildrenAsSet(PRE_RFC_CATEGORIES);
        return list;
    }

    public List<Categories> getCategories(Selector selector) {
      List<Categories> list = _getChildrenAsSet(CATEGORIES,selector);
      if (list == null || list.size() == 0)
          list = _getChildrenAsSet(PRE_RFC_CATEGORIES,selector);
      return list;
    }
    
}
