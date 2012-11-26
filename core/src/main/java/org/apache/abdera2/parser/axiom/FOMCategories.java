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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.model.Categories;
import org.apache.abdera2.model.Category;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings("rawtypes")
public class FOMCategories extends FOMExtensibleElement implements Categories {

    private static final long serialVersionUID = 5480273546375102411L;

    public FOMCategories() {
        super(CATEGORIES, new FOMDocument<Categories>(), new FOMFactory());
        init();
    }

    public FOMCategories(String name, OMNamespace namespace, OMContainer parent, OMFactory factory)
        throws OMException {
        super(name, namespace, parent, factory);
        init();
    }

    public FOMCategories(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
        init();
    }

    public FOMCategories(QName qname, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(qname, parent, factory, builder);
    }

    public FOMCategories(OMContainer parent, OMFactory factory) throws OMException {
        super(CATEGORIES, parent, factory);
        init();
    }

    public FOMCategories(OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(CATEGORIES, parent, factory, builder);
    }

    private void init() {
        this.declareNamespace(ATOM_NS, "atom");
    }

    public Categories addCategory(Category category) {
        complete();
        addChild((OMElement)category);
        return this;
    }

    public Category addCategory(String term) {
        complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Category category = factory.newCategory(this);
        category.setTerm(term);
        return category;
    }

    public Category addCategory(String scheme, String term, String label) {
        complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Category category = factory.newCategory(this);
        category.setTerm(term);
        category.setScheme(scheme);
        category.setLabel(label);
        return category;

    }

    public List<Category> getCategories() {
        return _getChildrenAsSet(CATEGORY);
    }

    public List<Category> getCategories(String scheme) {
        return FOMElement.getCategories(this, scheme);
    }

    private List<Category> copyCategoriesWithScheme(List<Category> cats) {
        List<Category> newcats = new ArrayList<Category>();
        IRI scheme = getScheme();
        for (Category cat : cats) {
            Category newcat = (Category)cat.clone();
            if (newcat.getScheme() == null && scheme != null)
                newcat.setScheme(scheme.toString());
            newcats.add(newcat);
        }
        return newcats;
    }

    public List<Category> getCategoriesWithScheme() {
        return copyCategoriesWithScheme(getCategories());
    }

    public List<Category> getCategoriesWithScheme(String scheme) {
        return copyCategoriesWithScheme(getCategories(scheme));
    }

    public List<Category> getCategories(Selector selector) {
      return _getChildrenAsSet(CATEGORY, selector);
    }
    
    public IRI getScheme() {
        String value = getAttributeValue(SCHEME);
        return (value != null) ? new IRI(value) : null;
    }

    public boolean isFixed() {
        String value = getAttributeValue(FIXED);
        return (value != null && value.equals(YES));
    }

    public Categories setFixed(boolean fixed) {
        complete();
        if (fixed && !isFixed())
            setAttributeValue(FIXED, YES);
        else if (!fixed && isFixed())
            removeAttribute(FIXED);
        return this;
    }

    public Categories setScheme(String scheme) {
      complete();
        return setAttributeValue(SCHEME, scheme==null?null:new IRI(scheme).toString());
    }

    public IRI getHref() {
        return _getUriValue(getAttributeValue(HREF));
    }

    public IRI getResolvedHref() {
        return _resolve(getResolvedBaseUri(), getHref());
    }

    public Categories setHref(String href) {
      complete();
      return setAttributeValue(HREF,href==null?null:(new IRI(href)).toString());
    }

    public boolean contains(String term) {
        return contains(term, null);
    }

    public boolean contains(String term, String scheme) {
        List<Category> categories = getCategories();
        IRI catscheme = getScheme();
        IRI uri = (scheme != null) ? new IRI(scheme) : catscheme;
        for (Category category : categories) {
            String t = category.getTerm();
            IRI s = (category.getScheme() != null) ? category.getScheme() : catscheme;
            if (t.equals(term) && ((uri != null) ? uri.equals(s) : s == null))
                return true;
        }
        return false;
    }

    public boolean isOutOfLine() {
        boolean answer = false;
        try {
            answer = getHref() != null;
        } catch (Exception e) {
        }
        return answer;
    }

}
