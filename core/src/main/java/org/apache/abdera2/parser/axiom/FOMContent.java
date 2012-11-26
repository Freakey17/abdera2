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

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.activation.URLDataSource;
import javax.xml.namespace.QName;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.model.Content;
import org.apache.abdera2.model.Div;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ElementWrapper;
import org.apache.axiom.attachments.utils.DataHandlerUtils;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings("unchecked")
public class FOMContent extends FOMExtensibleElement implements Content {

    private static final long serialVersionUID = -5499917654824498563L;
    protected Type type = Type.TEXT;

    public FOMContent(Content.Type type) {
        super(Constants.CONTENT);
        init(type);
    }

    public FOMContent(String name, OMNamespace namespace, Type type, OMContainer parent, OMFactory factory)
        throws OMException {
        super(name, namespace, parent, factory);
        init(type);
    }

    public FOMContent(QName qname, Type type, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
        init(type);
    }

    public FOMContent(QName qname, Type type, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(qname, parent, factory, builder);
        init(type);
    }

    public FOMContent(Type type, OMContainer parent, OMFactory factory) throws OMException {
        super(CONTENT, parent, factory);
        init(type);
    }

    public FOMContent(Type type, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(CONTENT, parent, factory, builder);
        init(type);
    }

    private void init(Type type) {
        this.type = type;
        if (type == null || type == Type.MEDIA) 
          removeAttribute(TYPE);
        else
          setAttributeValue(TYPE,type.label());
    }

    public final Type getContentType() {
        return type;
    }

    public Content setContentType(Type type) {
        complete();
        init(type);
        return this;
    }

    public <T extends Element> T getValueElement() {
        FOMFactory factory = (FOMFactory)getFactory();
        return (T)factory.getElementWrapper((Element)this.getFirstElement());
    }

    public <T extends Element> Content setValueElement(T value) {
        complete();
        if (value != null) {
            if (this.getFirstElement() != null)
                this.getFirstElement().discard();

            MimeType mtype = this.getMimeType();
            if (mtype == null) {
                String mt = getFactory().getMimeType(value);
                if (mt != null) {
                    setMimeType(mt);
                    mtype = getMimeType();
                }
            }

            if (value instanceof Div && type != Content.Type.XML)
                init(Content.Type.XHTML);
            else {
                if (mtype == null) {
                    init(Content.Type.XML);
                }
            }
            OMElement el = (OMElement)(value instanceof ElementWrapper ? ((ElementWrapper)value).getInternal() : value);
            this.setFirstChild(el);
        } else {
            _removeAllChildren();
        }
        return this;
    }

    public MimeType getMimeType() {
        MimeType type = null;
        String mimeType = getAttributeValue(TYPE);
        if (mimeType != null) {
            try {
                type = new MimeType(mimeType);
            } catch (Exception e) {
            }
        }
        return type;
    }

    public Content setMimeType(String type) {
      try {
        complete();
        return setAttributeValue(TYPE,type==null?null:(new MimeType(type)).toString());
      } catch (javax.activation.MimeTypeParseException e) {
        throw new org.apache.abdera2.common.mediatype.MimeTypeParseException(e);
      }
    }

    public IRI getSrc() {
        return _getUriValue(getAttributeValue(SRC));
    }

    public IRI getResolvedSrc() {
        return _resolve(getResolvedBaseUri(), getSrc());
    }

    public Content setSrc(String src) {
      complete();
      return setAttributeValue(SRC, src==null?null:(new IRI(src)).toString());
    }

    public DataHandler getDataHandler() {
        if (!Type.MEDIA.equals(type))
            throw new UnsupportedOperationException(Localizer.get("DATA.HANDLER.NOT.SUPPORTED"));
        MimeType type = getMimeType();
        java.net.URL src = null;
        try {
            src = getSrc().toURL();
        } catch (Exception e) {
        }
        DataHandler dh = null;
        if (src == null) {
            dh =
                (DataHandler)DataHandlerUtils
                    .getDataHandlerFromText(getText(), (type != null) ? type.toString() : null);
        } else {
            dh = new DataHandler(new URLDataSource(src));
        }
        return dh;
    }

    public Content setDataHandler(DataHandler dataHandler) {
        complete();
        if (Type.MEDIA != type)
            throw new IllegalArgumentException();
        if (dataHandler.getContentType() != null) {
            try {
                setMimeType(dataHandler.getContentType());
            } catch (Exception e) {
            }
        }
        _removeAllChildren();
        addChild(factory.createOMText(dataHandler, true));
        return this;
    }

    public String getValue() {
        String val = null;
        switch(type) {
        case MEDIA:
        case TEXT: 
        case HTML: val = getText(); break;
        case XHTML:
          FOMDiv div = (FOMDiv)this.getFirstChildWithName(Constants.DIV);
          if (div != null)
              val = div.getInternalValue();
          break;
        case XML:
          OMElement el = this.getFirstElement();
          if (el != null)
              val = el.toString();
          break;
        }
        return val;
    }

    public <T extends Element> T setText(Content.Type type, String value) {
        complete();
        init(type);
        if (value != null) {
            OMNode child = this.getFirstOMChild();
            while (child != null) {
                if (child.getType() == OMNode.TEXT_NODE) {
                    child.detach();
                }
                child = child.getNextOMSibling();
            }
            getOMFactory().createOMText(this, value);
        } else
            _removeAllChildren();
        return (T)this;
    }

    public void setText(String value) {
        setText(Content.Type.TEXT, value);
    }

    public Content setValue(String value) {
        complete();
        if (value != null)
            removeAttribute(SRC);
        if (value != null) {
          switch(type) {
          case TEXT:
          case HTML:
            _removeAllChildren();
            setText(type, value);
            break;
          case XHTML: {
            IRI baseUri = null;
            Element element = null;
            value = String.format("<div xmlns=\"%s\">%s</div>",XHTML_NS,value);
            try {
                baseUri = getResolvedBaseUri();
                element = _parse(value, baseUri);
            } catch (Exception e) {
            }
            if (element != null && element instanceof Div)
                setValueElement((Div)element);
            break;
          }
          case XML: {
            IRI baseUri = null;
            Element element = null;
            try {
                baseUri = getResolvedBaseUri();
                element = _parse(value, baseUri);
            } catch (Exception e) {
            }
            if (element != null)
                setValueElement(element);
            try {
                if (getMimeType() == null)
                    setMimeType("application/xml");
            } catch (Exception e) {
            }
            break;
          }
          case MEDIA:
            _removeAllChildren();
            setText(type, value);
            try {
                if (getMimeType() == null)
                    setMimeType("text/plain");
            } catch (Exception e) {
            }
            break;
          }
        } else {
            _removeAllChildren();
        }
        return this;
    }

    public String getWrappedValue() {
        if (Type.XHTML == type) {
            return this.getFirstChildWithName(Constants.DIV).toString();
        } else {
            return getText();
        }
    }

    public Content setWrappedValue(String wrappedValue) {
        complete();
        if (Type.XHTML == type) {
            IRI baseUri = null;
            Element element = null;
            try {
                baseUri = getResolvedBaseUri();
                element = _parse(wrappedValue, baseUri);
            } catch (Exception e) {
            }
            if (element != null && element instanceof Div)
                setValueElement((Div)element);
        } else {
            ((Element)this).setText(wrappedValue);
        }
        return this;
    }

    @Override
    public IRI getBaseUri() {
        if (Type.XHTML == type) {
            Element el = getValueElement();
            if (el != null) {
                if (el.getAttributeValue(BASE) != null) {
                    if (getAttributeValue(BASE) != null)
                        return super.getBaseUri().resolve(el.getAttributeValue(BASE));
                    else
                        return _getUriValue(el.getAttributeValue(BASE));
                }
            }
        }
        return super.getBaseUri();
    }

    @Override
    public IRI getResolvedBaseUri() {
        if (Type.XHTML == type) {
            Element el = getValueElement();
            if (el != null) {
                if (el.getAttributeValue(BASE) != null) {
                    return super.getResolvedBaseUri().resolve(el.getAttributeValue(BASE));
                }
            }
        }
        return super.getResolvedBaseUri();
    }

    @Override
    public String getLanguage() {
        String lang = null;
        if (Type.XHTML == type) {
            Element el = getValueElement();
            lang = el.getAttributeValue(LANG);
        }
        return lang != null ? lang : super.getLanguage();
    }

    @Override
    public Object clone() {
        FOMContent content = (FOMContent)super.clone();
        content.type = this.type;
        return content;
    }

}
