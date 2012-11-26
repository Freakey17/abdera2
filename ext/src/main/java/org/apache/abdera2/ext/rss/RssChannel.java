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
package org.apache.abdera2.ext.rss;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.model.Category;
import org.apache.abdera2.model.DateTime;
import org.apache.abdera2.model.Div;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ElementWrapper;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.ExtensibleElement;
import org.apache.abdera2.model.ExtensibleElementWrapper;
import org.apache.abdera2.model.Generator;
import org.apache.abdera2.model.IRIElement;
import org.apache.abdera2.model.Link;
import org.apache.abdera2.model.Person;
import org.apache.abdera2.model.Text;
import org.apache.abdera2.model.Text.Type;
import org.apache.abdera2.model.ElementIterator;
import org.apache.abdera2.model.ElementList;
import org.apache.abdera2.parser.axiom.FOMElement;
import org.apache.abdera2.xpath.XPath;
import org.apache.abdera2.xpath.XPathException;

@SuppressWarnings("rawtypes")
public class RssChannel extends ExtensibleElementWrapper {

    public RssChannel(Element internal) {
        super(internal);
    }

    public RssChannel(Factory factory, QName qname) {
        super(factory, qname);
    }

    public void addEntry(Entry entry) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Entry addEntry() {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public List<Entry> getEntries() {
      return getEntries(null);
    }
    
    public List<Entry> getEntries(Selector selector) {
        QName qname = getQName();
        if (qname.getNamespaceURI().equals(RssConstants.RSS1_NS) && qname.getLocalPart().equals("channel")) {
            List<Entry> entries = new ArrayList<Entry>();
            ExtensibleElement items = getExtension(RssConstants.QNAME_RDF_ITEMS);
            if (items != null) {
                ExtensibleElement se = items.getExtension(RssConstants.QNAME_RDF_SEQ);
                if (se != null) {
                    List<Element> seq = se.getExtensions(RssConstants.QNAME_RDF_LI);
                    for (Element el : seq) {
                        String res = el.getAttributeValue("resource");
                        if (res != null) {
                            String path = "//rss:item[@rdf:about='" + res + "']";
                            Element entryel = null;
                            try {
                                entryel = locate(path);
                            } catch (Exception e) {
                            }
                            if (entryel != null) {
                                // TODO:fix this.. entryel should already be an RssItem
                              RssItem ri = entryel instanceof RssItem ? (RssItem)entryel : new RssItem(entryel);
                              if (selector == null || selector.select(ri))
                                entries.add(ri);
                            }
                        }
                    }
                }
            }
            return entries;
        } else {
            return getExtensions(RssConstants.QNAME_ITEM,selector);
        }
    }

    public Entry getEntry(String id) {
        if (id == null)
            return null;
        List<Entry> l = getEntries();
        for (Entry e : l) {
            IRI eid = e.getId();
            if (eid != null && eid.equals(new IRI(id)))
                return e;
        }
        return null;
    }

    public void insertEntry(Entry entry) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Entry insertEntry() {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void sortEntries(Comparator<Entry> comparator) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void sortEntriesByEdited(boolean new_first) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void sortEntriesByUpdated(boolean new_first) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void addAuthor(Person person) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Person addAuthor(String name) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Person addAuthor(String name, String email, String iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void addCategory(Category category) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Category addCategory(String term) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Category addCategory(String scheme, String term, String label) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void addLink(Link link) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link addLink(String href) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link addLink(String href, String rel) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link addLink(String href, String rel, String type, String title, String hreflang, long length) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link getAlternateLink() {
        Link link = getExtension(RssConstants.QNAME_LINK);
        if (link == null)
            link = getExtension(RssConstants.QNAME_RDF_LINK);
        return link;
    }

    public Link getAlternateLink(String type, String hreflang) {
        return getAlternateLink();
    }

    public IRI getAlternateLinkResolvedHref() {
        Link link = getAlternateLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getAlternateLinkResolvedHref(String type, String hreflang) {
        return getAlternateLinkResolvedHref();
    }

    public Person getAuthor() {
        Person person = getExtension(RssConstants.QNAME_MANAGINGEDITOR);
        if (person == null)
            person = getExtension(RssConstants.QNAME_DC_CREATOR);
        return person;
    }

    public List<Person> getAuthors() {
        List<Person> people = getExtensions(RssConstants.QNAME_MANAGINGEDITOR);
        if (people == null || people.size() == 0)
            people = getExtensions(RssConstants.QNAME_DC_CREATOR);
        return people;
    }

    public List<Person> getAuthors(Selector selector) {
      List<Person> people = getExtensions(RssConstants.QNAME_MANAGINGEDITOR,selector);
      if (people == null || people.size() == 0)
          people = getExtensions(RssConstants.QNAME_DC_CREATOR,selector);
      return people;
    }
    
    public List<Person> getContributors() {
        List<Person> people = getExtensions(RssConstants.QNAME_DC_CONTRIBUTOR);
        return people;
    }
    
    public List<Person> getContributors(Selector selector) {
      return getExtensions(RssConstants.QNAME_DC_CONTRIBUTOR, selector);
    }

    public List<Category> getCategories() {
        List<Category> cats = getExtensions(RssConstants.QNAME_CATEGORY);
        if (cats == null || cats.size() == 0)
            cats = getExtensions(RssConstants.QNAME_DC_SUBJECT);
        return cats;
    }
    
    public List<Category> getCategories(Selector selector) {
      List<Category> cats = getExtensions(RssConstants.QNAME_CATEGORY, selector);
      if (cats == null || cats.size() == 0)
        cats = getExtensions(RssConstants.QNAME_DC_SUBJECT);
      return cats;
    }

    public List<Category> getCategories(String scheme) {
        return (scheme != null) ? new ElementList<Category>(new ElementIterator<Category>(getInternal(), RssCategory.class,
                                                                               new QName("domain"), scheme, null))
            : getCategories();
    }

    public Generator getGenerator() {
        return getExtension(RssConstants.QNAME_GENERATOR);
    }

    public IRI getIcon() {
        IRIElement iri = getIconElement();
        return (iri != null) ? iri.getValue() : null;
    }

    public IRIElement getIconElement() {
        return getLogoElement();
    }

    public IRI getId() {
        IRIElement id = getIdElement();
        return (id != null) ? id.getValue() : null;
    }

    public IRIElement getIdElement() {
        return getExtension(RssConstants.QNAME_DC_IDENTIFIER);
    }

    public Link getLink(String rel) {
        if (rel.equals(Link.REL_ALTERNATE) || 
            rel.equals(Link.Helper.getRelEquiv(Link.REL_ALTERNATE))) {
            RssGuid guid = (RssGuid)getIdElement();
            if (guid != null && guid.isPermalink())
                return guid;
            return getAlternateLink();
        }
        List<Link> links = FOMElement.getLinks(getInternal(), rel);
        return (links != null && links.size() > 0) ? links.get(0) : null;
    }

    public IRI getLinkResolvedHref(String rel) {
        Link link = getLink(rel);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public List<Link> getLinks() {
        return getExtensions(Constants.LINK);
    }
    
    public List<Link> getLinks(Selector selector) {
      return getExtensions(Constants.LINK, selector);
    }

    public List<Link> getLinks(String rel) {
        return FOMElement.getLinks(getInternal(), rel);
    }

    public List<Link> getLinks(String... rel) {
        return FOMElement.getLinks(getInternal(), rel);
    }

    public IRI getLogo() {
        IRIElement iri = getLogoElement();
        return (iri != null) ? iri.getValue() : null;
    }

    public IRIElement getLogoElement() {

        IRIElement iri = getExtension(RssConstants.QNAME_IMAGE);
        if (iri == null) {
            Element image = getExtension(RssConstants.QNAME_RDF_IMAGE);
            if (image != null) {
                String id = image.getAttributeValue(RssConstants.QNAME_RDF_RESOURCE);
                if (id != null) {
                    String path = "//rss:image[@rdf:about='" + id + "']";
                    Element res = null;
                    try {
                        res = locate(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (res != null) {
                        // TODO: Need to fix
                        return new RssImage(res);
                    }
                } else {
                    return (IRIElement)image;
                }
            }

        }
        return iri;
    }

    public String getRights() {
        Text text = getRightsElement();
        return (text != null) ? text.getValue() : null;
    }

    public Text getRightsElement() {
        Text text = getExtension(RssConstants.QNAME_COPYRIGHT);
        if (text == null)
            text = getExtension(RssConstants.QNAME_DC_RIGHTS);
        return text;
    }

    public Type getRightsType() {
        Text text = getRightsElement();
        return (text != null) ? text.getTextType() : null;
    }

    public Link getSelfLink() {
        return getLink("self");
    }

    public IRI getSelfLinkResolvedHref() {
        Link link = getSelfLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public String getSubtitle() {
        Text text = getSubtitleElement();
        return (text != null) ? text.getValue() : null;
    }

    public Text getSubtitleElement() {
        Text text = getExtension(RssConstants.QNAME_DESCRIPTION);
        if (text == null)
            text = getExtension(RssConstants.QNAME_RDF_DESCRIPTION);
        if (text == null)
            text = getExtension(RssConstants.QNAME_DC_DESCRIPTION);
        return text;
    }

    public Type getSubtitleType() {
        Text text = getSubtitleElement();
        return (text != null) ? text.getTextType() : null;
    }

    public String getTitle() {
        Text text = getTitleElement();
        return (text != null) ? text.getValue() : null;
    }

    public Text getTitleElement() {
        Text text = getExtension(RssConstants.QNAME_TITLE);
        if (text == null)
            text = getExtension(RssConstants.QNAME_RDF_TITLE);
        if (text == null)
            text = getExtension(RssConstants.QNAME_DC_TITLE);
        return text;
    }

    public Type getTitleType() {
        Text text = getTitleElement();
        return (text != null) ? text.getTextType() : null;
    }

    public org.joda.time.DateTime getUpdated() {
        DateTime dt = getUpdatedElement();
        return (dt != null) ? dt.getValue() : null;
    }

    public DateTime getUpdatedElement() {
        DateTime dt = getExtension(RssConstants.QNAME_LASTBUILDDATE);
        if (dt == null)
            dt = getExtension(RssConstants.QNAME_LASTBUILDDATE2);
        if (dt == null)
            dt = getExtension(RssConstants.QNAME_PUBDATE);
        if (dt == null)
            dt = getExtension(RssConstants.QNAME_PUBDATE2);
        if (dt == null)
            dt = getExtension(RssConstants.QNAME_DC_DATE);
        return dt;
    }

    public String getUpdatedString() {
        DateTime dt = getUpdatedElement();
        return (dt != null) ? dt.getString() : null;
    }

    public org.joda.time.DateTime getPublished() {
        DateTime dt = getPublishedElement();
        return (dt != null) ? dt.getValue() : null;
    }

    public DateTime getPublishedElement() {
        DateTime dt = getExtension(RssConstants.QNAME_PUBDATE);
        if (dt == null)
            dt = getExtension(RssConstants.QNAME_PUBDATE2);
        if (dt == null)
            dt = getExtension(RssConstants.QNAME_DC_DATE);
        return dt;
    }

    public String getPublishedString() {
        DateTime dt = getPublishedElement();
        return (dt != null) ? dt.getString() : null;
    }

    public IRIElement newId() {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void setGenerator(Generator generator) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Generator setGenerator(String iri, String version, String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public IRIElement setIcon(String iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void setIconElement(IRIElement iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public IRIElement setId(String id) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public IRIElement setId(String id, boolean normalize) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void setIdElement(IRIElement id) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public IRIElement setLogo(String iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void setLogoElement(IRIElement iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRights(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRights(String value, Type type) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRights(Div value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRightsAsHtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRightsAsXhtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void setRightsElement(Text text) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitle(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitle(String value, Type type) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitle(Div value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitleAsHtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitleAsXhtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void setSubtitleElement(Text text) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitle(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitle(String value, Type type) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitle(Div value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitleAsHtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitleAsXhtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void setTitleElement(Text text) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public DateTime setUpdated(org.joda.time.DateTime value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public DateTime setUpdated(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public void setUpdatedElement(DateTime dateTime) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public String getLanguage() {
        String lang = getSimpleExtension(RssConstants.QNAME_LANGUAGE);
        if (lang == null)
            lang = getSimpleExtension(RssConstants.QNAME_DC_LANGUAGE);
        return lang;
    }

    public <T extends Element> T setLanguage(String language) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    private Element locate(String path) throws XPathException {
        Abdera abdera = this.getFactory().getAbdera();
        XPath xpath = abdera.getXPath();
        Map<String, String> ns = xpath.getDefaultNamespaces();
        ns.put("rdf", RssConstants.RDF_NS);
        ns.put("rss", RssConstants.RSS1_NS);
        Element el = getDocument().getRoot();
        if (el instanceof ElementWrapper) {
            el = ((ElementWrapper)el).getInternal();
        }
        return (Element)xpath.selectSingleNode(path, el, ns);
    }
}
