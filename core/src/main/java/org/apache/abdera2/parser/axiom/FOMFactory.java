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

import java.util.HashSet;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Categories;
import org.apache.abdera2.model.Category;
import org.apache.abdera2.model.Collection;
import org.apache.abdera2.model.Content;
import org.apache.abdera2.model.Control;
import org.apache.abdera2.model.DateTime;
import org.apache.abdera2.model.Div;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.ExtensibleElement;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.model.Generator;
import org.apache.abdera2.model.IRIElement;
import org.apache.abdera2.model.Link;
import org.apache.abdera2.model.Person;
import org.apache.abdera2.model.Service;
import org.apache.abdera2.model.Source;
import org.apache.abdera2.model.Text;
import org.apache.abdera2.model.Workspace;
import org.apache.abdera2.model.Content.Type;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.common.anno.Version;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.factory.ExtensionFactory;
import org.apache.abdera2.factory.ExtensionFactoryMap;
import org.apache.abdera2.factory.Factory;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@SuppressWarnings( {"unchecked", "deprecation"})
public class FOMFactory extends OMLinkedListImplFactory 
    implements Factory, Constants, ExtensionFactory {

    private final ExtensionFactoryMap factoriesMap;
    private final Abdera abdera;

    public static void registerAsDefault() {
        System.setProperty(
          OMAbstractFactory.META_FACTORY_NAME_PROPERTY, 
          FOMFactory.class.getName());
    }

    
    /** 
     * This is a bit of a trick to speed up runtime performance during parse.
     * Basically, in the old model, whenever we'd encounter an element during
     * parsing, we would run through and do an if (qname.equals(whatever)) 
     * check against each of the core known qnames. Each would be checked in
     * sequence until a match was found, then the appropriate object would be 
     * created. That approach meant that for every element parsed, there were
     * we were potentially performing equals checks for every known qname 
     * every time, which is just silly and slows performance during the parse.
     * With this approach, we take a little bit of a hit during class init 
     * and index the Constructors for each of the main types and map those to
     * their qnames. Now, as we parse each element, we only need to look up 
     * the qname in the hashmap and grab the appropriate constructor and invoke
     * it. There are some variations with FOMContent and FOMText elements
     * since their constructors are not identical to the others, but this 
     * approach gives us constant time performance during parse (at least for 
     * the object creation portion of it) and eliminates the need for checking
     * every qname on every iteration... albeit, at a cost of a bit more 
     * complexity
     */
    
    private static java.util.Map<QName,java.lang.reflect.Constructor<?>> conmap =
      ImmutableMap.<QName,java.lang.reflect.Constructor<?>>builder()
      .put(FEED, confrom(FOMFeed.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(SERVICE, confrom(FOMService.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class))
      .put(PRE_RFC_SERVICE, confrom(FOMService.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(ENTRY, confrom(FOMEntry.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(AUTHOR, confrom(FOMPerson.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(CATEGORY, confrom(FOMCategory.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(CONTENT, confrom(FOMContent.class, String.class, OMNamespace.class, Content.Type.class, OMContainer.class, OMFactory.class)) 
      .put(CONTRIBUTOR, confrom(FOMPerson.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(GENERATOR, confrom(FOMGenerator.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(ICON, confrom(FOMIRI.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(ID, confrom(FOMIRI.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(LOGO, confrom(FOMIRI.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(LINK, confrom(FOMLink.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(PUBLISHED, confrom(FOMDateTime.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(SOURCE, confrom(FOMSource.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(RIGHTS, confrom(FOMText.class, Text.Type.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(SUBTITLE, confrom(FOMText.class, Text.Type.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(SUMMARY, confrom(FOMText.class, Text.Type.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(TITLE, confrom(FOMText.class, Text.Type.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(UPDATED, confrom(FOMDateTime.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(WORKSPACE, confrom(FOMWorkspace.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(PRE_RFC_WORKSPACE, confrom(FOMWorkspace.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(COLLECTION, confrom(FOMCollection.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(PRE_RFC_COLLECTION, confrom(FOMCollection.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(NAME, confrom(FOMElement.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(EMAIL, confrom(FOMElement.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(URI, confrom(FOMIRI.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(CONTROL, confrom(FOMControl.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class))
      .put(PRE_RFC_CONTROL, confrom(FOMControl.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class)) 
      .put(DIV, confrom(FOMDiv.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class))
      .put(CATEGORIES, confrom(FOMCategories.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class))
      .put(PRE_RFC_CATEGORIES, confrom(FOMCategories.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class))
      .put(EDITED, confrom(FOMDateTime.class, String.class, OMNamespace.class, OMContainer.class, OMFactory.class))
      .build();
    
    private static java.util.Map<QName,java.lang.reflect.Constructor<?>> conmap2 =
      ImmutableMap.<QName,java.lang.reflect.Constructor<?>>builder()
      .put(FEED, confrom(FOMFeed.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(SERVICE, confrom(FOMService.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class))
      .put(PRE_RFC_SERVICE, confrom(FOMService.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(ENTRY, confrom(FOMEntry.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(AUTHOR, confrom(FOMPerson.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(CATEGORY, confrom(FOMCategory.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(CONTENT, confrom(FOMContent.class, QName.class, Content.Type.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(CONTRIBUTOR, confrom(FOMPerson.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(GENERATOR, confrom(FOMGenerator.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(ICON, confrom(FOMIRI.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(ID, confrom(FOMIRI.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(LOGO, confrom(FOMIRI.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(LINK, confrom(FOMLink.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(PUBLISHED, confrom(FOMDateTime.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(SOURCE, confrom(FOMSource.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(RIGHTS, confrom(FOMText.class, Text.Type.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(SUBTITLE, confrom(FOMText.class, Text.Type.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(SUMMARY, confrom(FOMText.class, Text.Type.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(TITLE, confrom(FOMText.class, Text.Type.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(UPDATED, confrom(FOMDateTime.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(WORKSPACE, confrom(FOMWorkspace.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(PRE_RFC_WORKSPACE, confrom(FOMWorkspace.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(COLLECTION, confrom(FOMCollection.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(PRE_RFC_COLLECTION, confrom(FOMCollection.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(URI, confrom(FOMIRI.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(CONTROL, confrom(FOMControl.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class))
      .put(PRE_RFC_CONTROL, confrom(FOMControl.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(DIV, confrom(FOMDiv.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(CATEGORIES, confrom(FOMCategories.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(PRE_RFC_CATEGORIES, confrom(FOMCategories.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(EDITED, confrom(FOMDateTime.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(NAME, confrom(FOMElement.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .put(EMAIL, confrom(FOMElement.class, QName.class, OMContainer.class, OMFactory.class, OMXMLParserWrapper.class)) 
      .build();

    private static java.util.Set<QName> textset = ImmutableSet.of(RIGHTS,SUBTITLE,SUMMARY,TITLE);
    private static java.lang.reflect.Constructor<?> confrom(Class<?> _class, Class<?>... _types) {
      try {
        return _class.getConstructor(_types);
      } catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }
    private static <T>T create(QName qname, Object... params) {
      try {
        return (T)(conmap.containsKey(qname) ? 
          conmap.get(qname).newInstance(params) : null);
      } catch (Throwable t) {
        return null;
      }
    }
    private static <T>T create2(QName qname, Object... params) {
      try {
        return (T)(conmap2.containsKey(qname) ? 
          conmap2.get(qname).newInstance(params) : null);
      } catch (Throwable t) {
        t.printStackTrace();
        return null;
      }
    }
    private static boolean is_content(QName qname) {
      return qname.equals(CONTENT);
    }
    private static boolean is_text(QName qname) {
      return textset.contains(qname);
    }
    
    /***********************************************************************/
    
    public FOMFactory() {
        this(Abdera.getInstance());
    }

    public FOMFactory(Abdera abdera) {
        Iterable<ExtensionFactory> f = 
          abdera.getConfiguration().getExtensionFactories();
        factoriesMap = 
          new ExtensionFactoryMap(
              f != null ? f : new HashSet<ExtensionFactory>());
        this.abdera = abdera;
    }

    public Parser newParser() {
        return new FOMParser(abdera);
    }

    public <T extends Element> Document<T> newDocument() {
        return new FOMDocument<T>(this);
    }

    public <T extends Element> Document<T> newDocument(OMXMLParserWrapper parserWrapper) {
        return new FOMDocument<T>(parserWrapper, this);
    }

    public <T extends Element> Document<T> newDocument(T root, OMXMLParserWrapper parserWrapper) {
        FOMDocument<T> doc = (FOMDocument<T>)newDocument(parserWrapper);
        doc.setRoot(root);
        return doc;
    }

    public Service newService(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMService(qname, parent, this, parserWrapper);
    }

    public Service newService(Base parent) {
        return new FOMService((OMContainer)parent, this);
    }

    public Workspace newWorkspace(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMWorkspace(qname, parent, this, parserWrapper);
    }

    public Workspace newWorkspace() {
        return newWorkspace(null);
    }

    public Workspace newWorkspace(Element parent) {
        return new FOMWorkspace((OMContainer)parent, this);
    }

    public Collection newCollection(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMCollection(qname, parent, this, parserWrapper);
    }

    public Collection newCollection() {
        return newCollection(null);
    }

    public Collection newCollection(Element parent) {
        return new FOMCollection((OMContainer)parent, this);
    }

    public Collection newMultipartCollection(Element parent) {
        return new FOMMultipartCollection((OMContainer)parent, this);
    }

    public Feed newFeed() {
        Document<Feed> doc = newDocument();
        return newFeed(doc);
    }

    public Entry newEntry() {
        Document<Entry> doc = newDocument();
        return newEntry(doc);
    }

    public Service newService() {
        Document<Service> doc = newDocument();
        return newService(doc);
    }

    public Feed newFeed(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMFeed(qname, parent, this, parserWrapper);
    }

    public Feed newFeed(Base parent) {
        return new FOMFeed((OMContainer)parent, this);
    }

    public Entry newEntry(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMEntry(qname, parent, this, parserWrapper);
    }

    public Entry newEntry(Base parent) {
        return new FOMEntry((OMContainer)parent, this);
    }

    public Category newCategory(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMCategory(qname, parent, this, parserWrapper);
    }

    public Category newCategory() {
        return newCategory(null);
    }

    public Category newCategory(Element parent) {
        return new FOMCategory((OMContainer)parent, this);
    }

    public Content newContent(QName qname, Type type, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        if (type == null)
            type = Content.Type.TEXT;
        return new FOMContent(qname, type, parent, this, parserWrapper);
    }

    public Content newContent() {
        return newContent(Content.Type.TEXT);
    }

    public Content newContent(Type type) {
        if (type == null)
            type = Content.Type.TEXT;
        return newContent(type, null);
    }

    public Content newContent(Type type, Element parent) {
        if (type == null)
            type = Content.Type.TEXT;
        Content content = new FOMContent(type, (OMContainer)parent, this);
        if (type.equals(Content.Type.XML))
            content.setMimeType(XML_MEDIA_TYPE);
        return content;
    }

    public Content newContent(MimeType mediaType) {
        return newContent(mediaType, null);
    }

    public Content newContent(MimeType mediaType, Element parent) {
        Content.Type type = (MimeTypeHelper.isXml(mediaType.toString())) ? Content.Type.XML : Content.Type.MEDIA;
        Content content = newContent(type, parent);
        content.setMimeType(mediaType.toString());
        return content;
    }

    public DateTime newDateTimeElement(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMDateTime(qname, parent, this, parserWrapper);
    }

    public DateTime newDateTime(QName qname, Element parent) {
        return new FOMDateTime(qname, (OMContainer)parent, this);
    }

    public Generator newGenerator(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMGenerator(qname, parent, this, parserWrapper);
    }

    public Generator newDefaultGenerator() {
        return newDefaultGenerator(null);
    }

    public Generator newDefaultGenerator(Element parent) {
        Generator generator = newGenerator(parent);
        Version version = AnnoUtil.getVersion(abdera);
        generator.setVersion(version.value());
        generator.setText(version.name());
        generator.setUri(version.uri());
        return generator;
    }

    public Generator newGenerator() {
        return newGenerator(null);
    }

    public Generator newGenerator(Element parent) {
        return new FOMGenerator((OMContainer)parent, this);
    }

    public IRIElement newID(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMIRI(qname, parent, this, parserWrapper);
    }

    public IRIElement newID() {
        return newID(null);
    }

    public IRIElement newID(Element parent) {
        return new FOMIRI(Constants.ID, (OMContainer)parent, this);
    }

    public IRIElement newURIElement(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMIRI(qname, parent, this, parserWrapper);
    }

    public IRIElement newIRIElement(QName qname, Element parent) {
        return new FOMIRI(qname, (OMContainer)parent, this);
    }

    public Link newLink(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMLink(qname, parent, this, parserWrapper);
    }

    public Link newLink() {
        return newLink(null);
    }

    public Link newLink(Element parent) {
        return new FOMLink((OMContainer)parent, this);
    }

    public Person newPerson(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMPerson(qname, parent, this, parserWrapper);
    }

    public Person newPerson(QName qname, Element parent) {
        return new FOMPerson(qname, (OMContainer)parent, this);
    }

    public Source newSource(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMSource(qname, parent, this, parserWrapper);
    }

    public Source newSource() {
        return newSource(null);
    }

    public Source newSource(Element parent) {
        return new FOMSource((OMContainer)parent, this);
    }

    public Text newText(QName qname, Text.Type type, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        if (type == null)
            type = Text.Type.TEXT;
        return new FOMText(type, qname, parent, this, parserWrapper);
    }

    public Text newText(QName qname, Text.Type type) {
        return newText(qname, type, null);
    }

    public Text newText(QName qname, Text.Type type, Element parent) {
        if (type == null)
            type = Text.Type.TEXT;
        return new FOMText(type, qname, (OMContainer)parent, this);
    }

    public <T extends Element> T newElement(QName qname) {
        return (T)newElement(qname, null);
    }

    public <T extends Element> T newElement(QName qname, Base parent) {
        return (T)newExtensionElement(qname, parent);
    }

    public <T extends Element> T newExtensionElement(QName qname) {
        return (T)newExtensionElement(qname, (OMContainer)null);
    }

    public <T extends Element> T newExtensionElement(QName qname, Base parent) {
        return (T)newExtensionElement(qname, (OMContainer)parent);
    }

    private <T extends Element> T newExtensionElement(QName qname, OMContainer parent) {
        String ns = qname.getNamespaceURI();
        Element el = newExtensionElement(qname, parent, null);
        return (T)((ATOM_NS.equals(ns) || APP_NS.equals(ns)) ? el : factoriesMap.getElementWrapper(el));
    }

    private <T extends Element> T newExtensionElement(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        Element element =
            (parserWrapper == null) ? 
                (Element)createElement(qname, parent, this, null) : 
            (Element)createElement(qname, parent, (FOMBuilder)parserWrapper);

        return (T)element;
    }

    public Control newControl() {
        return newControl(null);
    }

    public Control newControl(Element parent) {
        return new FOMControl((OMContainer)parent, this);
    }

    public Control newControl(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMControl(qname, parent, this, parserWrapper);
    }

    public DateTime newPublished() {
        return newPublished(null);
    }

    public DateTime newPublished(Element parent) {
        return newDateTime(Constants.PUBLISHED, parent);
    }

    public DateTime newUpdated() {
        return newUpdated(null);
    }

    public DateTime newUpdated(Element parent) {
        return newDateTime(Constants.UPDATED, parent);
    }

    public DateTime newEdited() {
        return newEdited(null);
    }

    public DateTime newEdited(Element parent) {
        return newDateTime(Constants.EDITED, parent);
    }

    public IRIElement newIcon() {
        return newIcon(null);
    }

    public IRIElement newIcon(Element parent) {
        return newIRIElement(Constants.ICON, parent);
    }

    public IRIElement newLogo() {
        return newLogo(null);
    }

    public IRIElement newLogo(Element parent) {
        return newIRIElement(Constants.LOGO, parent);
    }

    public IRIElement newUri() {
        return newUri(null);
    }

    public IRIElement newUri(Element parent) {
        return newIRIElement(Constants.URI, parent);
    }

    public Person newAuthor() {
        return newAuthor(null);
    }

    public Person newAuthor(Element parent) {
        return newPerson(Constants.AUTHOR, parent);
    }

    public Person newContributor() {
        return newContributor(null);
    }

    public Person newContributor(Element parent) {
        return newPerson(Constants.CONTRIBUTOR, parent);
    }

    public Text newTitle() {
        return newTitle(Text.Type.TEXT);
    }

    public Text newTitle(Element parent) {
        return newTitle(Text.Type.TEXT, parent);
    }

    public Text newTitle(Text.Type type) {
        return newTitle(type, null);
    }

    public Text newTitle(Text.Type type, Element parent) {
        return newText(Constants.TITLE, type, parent);
    }

    public Text newSubtitle() {
        return newSubtitle(Text.Type.TEXT);
    }

    public Text newSubtitle(Element parent) {
        return newSubtitle(Text.Type.TEXT, parent);
    }

    public Text newSubtitle(Text.Type type) {
        return newSubtitle(type, null);
    }

    public Text newSubtitle(Text.Type type, Element parent) {
        return newText(Constants.SUBTITLE, type, parent);
    }

    public Text newSummary() {
        return newSummary(Text.Type.TEXT);
    }

    public Text newSummary(Element parent) {
        return newSummary(Text.Type.TEXT, parent);
    }

    public Text newSummary(Text.Type type) {
        return newSummary(type, null);
    }

    public Text newSummary(Text.Type type, Element parent) {
        return newText(Constants.SUMMARY, type, parent);
    }

    public Text newRights() {
        return newRights(Text.Type.TEXT);
    }

    public Text newRights(Element parent) {
        return newRights(Text.Type.TEXT, parent);
    }

    public Text newRights(Text.Type type) {
        return newRights(type, null);
    }

    public Text newRights(Text.Type type, Element parent) {
        return newText(Constants.RIGHTS, type, parent);
    }

    public Element newName() {
        return newName(null);
    }

    public Element newName(Element parent) {
        return newElement(Constants.NAME, parent);
    }

    public Element newEmail() {
        return newEmail(null);
    }

    public Element newEmail(Element parent) {
        return newElement(Constants.EMAIL, parent);
    }

    public Div newDiv() {
        return newDiv(null);
    }

    public Div newDiv(Base parent) {
        return new FOMDiv(DIV, (OMContainer)parent, this);
    }

    public Div newDiv(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMDiv(qname, parent, this, parserWrapper);
    }

    public Element newElement(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMExtensibleElement(qname, parent, this, parserWrapper);
    }

    protected OMElement createElement(QName qname, OMContainer parent, OMFactory factory, Object objecttype) {
        OMElement element = null;
        OMNamespace namespace = this.createOMNamespace(qname.getNamespaceURI(), qname.getPrefix());
        
        if (conmap.containsKey(qname)) {
          if (is_text(qname)) {
            Text.Type type = (Text.Type)objecttype;
            element = create(qname,type,qname.getLocalPart(),namespace,parent,factory);
          } else if (is_content(qname)) {
            Content.Type type = (Content.Type)objecttype;
            element = create(qname,qname.getLocalPart(),namespace,type,parent,factory);
          } else {
            element = create(qname,qname.getLocalPart(),namespace,parent,factory);
          }
        } else if (parent instanceof ExtensibleElement || parent instanceof Document) {
            element = (OMElement)new FOMExtensibleElement(qname, parent, this);
        } else {
            element = (OMElement)new FOMExtensibleElement(qname, null, this);
        }
        return element;
    }

    protected OMElement createElement(QName qname, OMContainer parent, FOMBuilder builder) {
        OMElement element = null;
        if (conmap2.containsKey(qname)) {
          if (is_text(qname)) {
            Text.Type type = builder.getTextType();
            element = create2(qname,type,qname,(OMContainer)parent,(OMFactory)this,(OMXMLParserWrapper)builder);
          } else if (is_content(qname)) {
            Content.Type type = builder.getContentType();;
            element = create2(qname,qname,type,(OMContainer)parent,(OMFactory)this,(OMXMLParserWrapper)builder);
          } else {
            element = create2(qname,qname,(OMContainer)parent,(OMFactory)this,(OMXMLParserWrapper)builder);
          }
        } else if (parent instanceof ExtensibleElement || parent instanceof Document) {
            element = (OMElement)new FOMExtensibleElement(qname, parent, this, builder);
        }
        return element;
    }

    public Factory registerExtension(ExtensionFactory factory) {
        factoriesMap.addFactory(factory);
        return this;
    }

    public Categories newCategories() {
        Document<Categories> doc = newDocument();
        return newCategories(doc);
    }

    public Categories newCategories(Base parent) {
        return new FOMCategories((OMContainer)parent, this);
    }

    public Categories newCategories(QName qname, OMContainer parent, OMXMLParserWrapper parserWrapper) {
        return new FOMCategories(qname, parent, this, parserWrapper);
    }

    public String newUuidUri() {
        return String.format("urn:uuid:%s",FOMElement.generateUuid());
    }
    
    public <T extends Element> T getElementWrapper(Element internal) {
        if (internal == null)
            return null;
        String ns = internal.getQName().getNamespaceURI();
        return (T)((ATOM_NS.equals(ns) || APP_NS.equals(ns) || internal.getQName().equals(DIV)) ? internal
            : factoriesMap.getElementWrapper(internal));
    }

    public Iterable<String> getNamespaces() {
        return factoriesMap.getNamespaces();
    }

    public boolean handlesNamespace(String namespace) {
        return factoriesMap.handlesNamespace(namespace);
    }

    public Abdera getAbdera() {
        return abdera;
    }

    public <T extends Base> String getMimeType(T base) {
        String type = factoriesMap.getMimeType(base);
        return type;
    }

    public Iterable<ExtensionFactory> listExtensionFactories() {
        return factoriesMap;
    }

    @Override
    public OMText createOMText(Object arg0, boolean arg1) {
        return new FOMTextValue(arg0, arg1, this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, char[] arg1, int arg2) {
        return new FOMTextValue(arg0, arg1, arg2, this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, QName arg1, int arg2) {
        return new FOMTextValue(arg0, arg1, arg2, this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, QName arg1) {
        return new FOMTextValue(arg0, arg1, this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, String arg1, int arg2) {
        return new FOMTextValue(arg0, arg1, arg2, this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, String arg1, String arg2, boolean arg3) {
        return new FOMTextValue(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, String arg1) {
        return new FOMTextValue(arg0, arg1, this);
    }

    @Override
    public OMText createOMText(String arg0, int arg1) {
        return new FOMTextValue(arg0, arg1, this);
    }

    @Override
    public OMText createOMText(String arg0, OMContainer arg1, OMXMLParserWrapper arg2) {
        return new FOMTextValue(arg0, arg1, arg2, this);
    }

    @Override
    public OMText createOMText(String arg0, String arg1, boolean arg2) {
        return new FOMTextValue(arg0, arg1, arg2, this);
    }

    @Override
    public OMText createOMText(String arg0) {
        return new FOMTextValue(arg0, this);
    }

    @Override
    public OMComment createOMComment(OMContainer arg0, String arg1) {
        return new FOMComment(arg0, arg1, this);
    }

    @Override
    public OMProcessingInstruction createOMProcessingInstruction(OMContainer arg0, String arg1, String arg2) {
        return new FOMProcessingInstruction(arg0, arg1, arg2, this);
    }

    public DateTime newDateTime(Class<?> _class, Element parent) {
      QName qname = AnnoUtil.getQName(_class);
      if (qname == null)
        throw new IllegalArgumentException();
      return newDateTime(qname,parent);
    }

    public IRIElement newIRIElement(Class<?> _class, Element parent) {
      QName qname = AnnoUtil.getQName(_class);
      if (qname == null)
        throw new IllegalArgumentException();
      return newIRIElement(qname,parent);
    }

    public Person newPerson(Class<?> _class, Element parent) {
      QName qname = AnnoUtil.getQName(_class);
      if (qname == null)
        throw new IllegalArgumentException();
      return newPerson(qname,parent);
    }

    public Text newText(Class<?> _class, org.apache.abdera2.model.Text.Type type) {
      QName qname = AnnoUtil.getQName(_class);
      if (qname == null)
        throw new IllegalArgumentException();
      return newText(qname,type);
    }

    public Text newText(Class<?> _class,
        org.apache.abdera2.model.Text.Type type, Element parent) {
      QName qname = AnnoUtil.getQName(_class);
      if (qname == null)
        throw new IllegalArgumentException();
      return newText(qname,type,parent);
    }

    public <T extends Element> T newElement(Class<?> _class) {
      QName qname = AnnoUtil.getQName(_class);
      if (qname == null)
        throw new IllegalArgumentException();
      return (T)newElement(qname);
    }

    public <T extends Element> T newElement(Class<?> _class, Base parent) {
      QName qname = AnnoUtil.getQName(_class);
      if (qname == null)
        throw new IllegalArgumentException();
      return (T)newElement(qname,parent);
    }

    public <T extends Element> T newExtensionElement(Class<?> _class) {
      QName qname = AnnoUtil.getQName(_class);
      if (qname == null)
        throw new IllegalArgumentException();
      return (T)newExtensionElement(qname);
    }

    public <T extends Element> T newExtensionElement(Class<?> _class,
        Base parent) {
      QName qname = AnnoUtil.getQName(_class);
      if (qname == null)
        throw new IllegalArgumentException();
      return (T)newExtensionElement(qname,parent);
    }

}
