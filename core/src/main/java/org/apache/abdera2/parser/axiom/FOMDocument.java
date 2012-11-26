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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.stream.XMLStreamException;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.misc.ArrayBuilder;
import org.apache.abdera2.common.xml.XMLVersion;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ElementWrapper;
import org.apache.abdera2.writer.Writer;
import org.apache.abdera2.writer.WriterOptions;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;
import org.apache.axiom.om.util.StAXUtils;
import org.joda.time.DateTime;

@SuppressWarnings("unchecked")
public class FOMDocument<T extends Element> extends OMDocumentImpl implements Document<T> {

    private static final long serialVersionUID = -3255339511063344662L;
    protected IRI base = null;
    protected MimeType contentType = null;
    protected DateTime lastModified = null;
    protected EntityTag etag = null;
    protected String language = null;
    protected String slug = null;
    protected boolean preserve = true;

    public FOMDocument() {
        super(new FOMFactory());
    }

    protected FOMDocument(OMElement documentElement, OMXMLParserWrapper parserWrapper, OMFactory factory) {
        super(documentElement, parserWrapper, factory);
    }

    protected FOMDocument(OMElement documentElement, OMXMLParserWrapper parserWrapper) {
        super(documentElement, parserWrapper, new FOMFactory());
    }

    protected FOMDocument(OMFactory factory) {
        super(factory);
    }

    protected FOMDocument(OMXMLParserWrapper parserWrapper, OMFactory factory) {
        super(parserWrapper, factory);
    }

    protected FOMDocument(OMXMLParserWrapper parserWrapper) {
        super(parserWrapper, new FOMFactory());
    }

    public T getRoot() {
        FOMFactory factory = (FOMFactory)getFactory();
        return (T)factory.getElementWrapper((T)this.getOMDocumentElement());
    }

    public Document<T> setRoot(T root) {
        if (root instanceof OMElement) {
            this.setOMDocumentElement((OMElement)root);
        } else if (root instanceof ElementWrapper) {
            this.setOMDocumentElement((OMElement)((ElementWrapper)root).getInternal());
        }
        return this;
    }

    public IRI getBaseUri() {
        return base;
    }

    public Document<T> setBaseUri(String base) {
        this.base = new IRI(base);
        return this;
    }

    public void writeTo(OutputStream out, WriterOptions options) throws IOException {
        Writer writer = this.getFactory().getAbdera().getWriter();
        writer.writeTo(this, out, options);
    }

    public void writeTo(java.io.Writer out, WriterOptions options) throws IOException {
        Writer writer = this.getFactory().getAbdera().getWriter();
        writer.writeTo(this, out, options);
    }

    public void writeTo(Writer writer, OutputStream out) throws IOException {
        writer.writeTo(this, out);
    }

    public void writeTo(Writer writer, java.io.Writer out) throws IOException {
        writer.writeTo(this, out);
    }

    public void writeTo(Writer writer, OutputStream out, WriterOptions options) throws IOException {
        writer.writeTo(this, out, options);
    }

    public void writeTo(Writer writer, java.io.Writer out, WriterOptions options) throws IOException {
        writer.writeTo(this, out, options);
    }

    public void writeTo(OutputStream out) throws IOException {
        String charset = getCharset();
        if (charset == null)
            charset = "UTF-8";
        Writer writer = getFactory().getAbdera().getWriter();
        writeTo(writer, new OutputStreamWriter(out, charset));
    }

    public void writeTo(java.io.Writer writer) throws IOException {
        Writer out = getFactory().getAbdera().getWriter();
        if (!(out instanceof FOMWriter)) {
            out.writeTo(this, writer);
        } else {
            try {
                OMOutputFormat outputFormat = new OMOutputFormat();
                if (this.getCharsetEncoding() != null)
                    outputFormat.setCharSetEncoding(this.getCharsetEncoding());
                MTOMXMLStreamWriter omwriter = new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer));
                omwriter.setOutputFormat(outputFormat);
                this.internalSerialize(omwriter);
                omwriter.flush();
            } catch (XMLStreamException e) {
                throw new FOMException(e);
            }
        }
    }

    public MimeType getContentType() {
        return contentType;
    }

    public Document<T> setContentType(String contentType) {
        try {
            this.contentType = new MimeType(contentType);
            if (this.contentType.getParameter("charset") != null)
                setCharset(this.contentType.getParameter("charset"));
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera2.common.mediatype.MimeTypeParseException(e);
        }
        return this;
    }

    public DateTime getLastModified() {
        return this.lastModified;
    }

    public Document<T> setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public Object clone() {
        Document<T> doc = ((FOMFactory)factory).newDocument();
        OMDocument omdoc = (OMDocument)doc;
        for (Iterator<OMNode> i = getChildren(); i.hasNext();) {
            OMNode node = i.next();
            switch (node.getType()) {
                case OMNode.COMMENT_NODE:
                    OMComment comment = (OMComment)node;
                    factory.createOMComment(omdoc, comment.getValue());
                    break;
                case OMNode.DTD_NODE:
                    OMDocType doctype = (OMDocType)node;
                    factory.createOMDocType(omdoc, doctype.getValue());
                    break;
                case OMNode.ELEMENT_NODE:
                    Element el = (Element)node;
                    omdoc.addChild((OMNode)el.clone());
                    break;
                case OMNode.PI_NODE:
                    OMProcessingInstruction pi = (OMProcessingInstruction)node;
                    factory.createOMProcessingInstruction(omdoc, pi.getTarget(), pi.getValue());
                    break;
            }
        }
        return doc;
    }

    public String getCharset() {
        return this.getCharsetEncoding();
    }

    public Document<T> setCharset(String charset) {
        this.setCharsetEncoding(charset);
        return this;
    }

    public Factory getFactory() {
        return (Factory)this.factory;
    }

    public String[] getProcessingInstruction(String target) {
        ArrayBuilder<String> values = ArrayBuilder.list(String.class);
        for (Iterator<OMNode> i = getChildren(); i.hasNext();) {
          OMNode node = i.next();
          if (node.getType() == OMNode.PI_NODE) {
            OMProcessingInstruction pi = (OMProcessingInstruction)node;
            if (pi.getTarget().equalsIgnoreCase(target))
                values.add(pi.getValue());
          }
        }
        return values.build();
    }

    public Document<T> addProcessingInstruction(String target, String value) {
        OMProcessingInstruction pi = this.factory.createOMProcessingInstruction(null, target, value);
        if (this.getOMDocumentElement() != null) {
            this.getOMDocumentElement().insertSiblingBefore(pi);
        } else {
            this.addChild(pi);
        }
        return this;
    }

    public Document<T> addStylesheet(String href, String media) {
        if (media == null) {
            addProcessingInstruction("xml-stylesheet", "href=\"" + href + "\"");
        } else {
            addProcessingInstruction("xml-stylesheet", "href=\"" + href + "\" media=\"" + media + "\"");
        }
        return this;
    }

    public <X extends Base> X addComment(String value) {
        OMComment comment = this.factory.createOMComment(null, value);
        if (this.getOMDocumentElement() != null) {
            this.getOMDocumentElement().insertSiblingBefore(comment);
        } else {
            this.addChild(comment);
        }
        return (X)this;
    }

    public EntityTag getEntityTag() {
        return etag;
    }

    public Document<T> setEntityTag(EntityTag tag) {
        this.etag = tag;
        return this;
    }

    public Document<T> setEntityTag(String tag) {
        this.etag = new EntityTag(tag);
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public Lang getLanguageTag() {
        String lang = getLanguage();
        return (lang != null) ? new Lang(lang) : null;
    }

    public Document<T> setLanguage(String lang) {
        this.language = lang;
        return this;
    }

    public String getSlug() {
        return slug;
    }

    public Document<T> setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public boolean getMustPreserveWhitespace() {
        return preserve;
    }

    public Document<T> setMustPreserveWhitespace(boolean preserve) {
        this.preserve = preserve;
        return this;
    }

    public XMLVersion getXmlVersion() {
        return XMLVersion.get(super.getXMLVersion());
    }

    public WriterOptions getDefaultWriterOptions() {
        return new FOMWriter().getDefaultWriterOptions();
    }
    
    public WriterOptions.Builder makeDefaultWriterOptions() {
      return new FOMWriter().makeDefaultWriterOptions();
    }

    /**
     * Ensure that the underlying streams are fully parsed. We might eventually need to find a more efficient way of
     * doing this, but for now, calling toString() will ensure that this particular object is fully parsed and ready to
     * be modified.
     */
    public <X extends Base> X complete() {
        if (!isComplete() && getRoot() != null)
            getRoot().complete();
        return (X)this;
    }

    public void writeTo(String writer, OutputStream out) throws IOException {
        writeTo(getFactory().getAbdera().getWriterFactory().getWriter(writer), out);
    }

    public void writeTo(String writer, java.io.Writer out) throws IOException {
        writeTo(getFactory().getAbdera().getWriterFactory().getWriter(writer), out);
    }

    public void writeTo(String writer, OutputStream out, WriterOptions options) throws IOException {
        writeTo(getFactory().getAbdera().getWriterFactory().getWriter(writer), out, options);
    }

    public void writeTo(String writer, java.io.Writer out, WriterOptions options) throws IOException {
        writeTo(getFactory().getAbdera().getWriterFactory().getWriter(writer), out, options);
    }

    public String toFormattedString() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeTo("prettyxml", out);
            return new String(out.toByteArray(), "UTF-8");
        } catch (Exception e) {
            return toString();
        }
    }
}
