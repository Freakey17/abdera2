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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.model.Source;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings("rawtypes")
public class FOMFeed extends FOMSource implements Feed {

    private static final long serialVersionUID = 4552921210185524535L;

    public FOMFeed() {
        super(Constants.FEED, new FOMDocument<Feed>(), new FOMFactory());
    }

    public FOMFeed(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    public FOMFeed(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    public FOMFeed(QName qname, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(qname, parent, factory, builder);
    }

    public FOMFeed(OMContainer parent, OMFactory factory) throws OMException {
        super(FEED, parent, factory);
    }

    public FOMFeed(OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(FEED, parent, factory, builder);
    }

    public List<Entry> getEntries() {
        return _getChildrenAsSet(ENTRY);
    }

    public Feed addEntry(Entry entry) {
        complete();
        addChild((OMElement)entry);
        return this;
    }

    public Entry addEntry() {
        complete();
        FOMFactory fomfactory = (FOMFactory)factory;
        return fomfactory.newEntry(this);
    }

    public Feed insertEntry(Entry entry) {
        complete();
        OMElement el = getFirstChildWithName(ENTRY);
        if (el == null) {
            addEntry(entry);
        } else {
            entry.setParentElement(this);
            el.insertSiblingBefore((OMElement)entry);
        }
        return this;
    }

    public Entry insertEntry() {
        complete();
        FOMFactory fomfactory = (FOMFactory)factory;
        Entry entry = fomfactory.newEntry((Feed)null);
        insertEntry(entry);
        return entry;
    }

    public Source getAsSource() {
        FOMSource source = (FOMSource)((FOMFactory)factory).newSource(null);
        for (Iterator<?> i = this.getChildElements(); i.hasNext();) {
            FOMElement child = (FOMElement)i.next();
            if (!child.getQName().equals(ENTRY)) {
                source.addChild((OMNode)child.clone());
            }
        }
        try {
            if (this.getBaseUri() != null) {
                source.setBaseUri(this.getBaseUri());
            }
        } catch (Exception e) {
        }
        return source;
    }

    @Override
    public void addChild(OMNode node) {
        if (isComplete() && node instanceof OMElement && !(node instanceof Entry)) {
            OMElement el = this.getFirstChildWithName(ENTRY);
            if (el != null) {
                el.insertSiblingBefore(node);
                return;
            }
        }
        super.addChild(node);
    }

    public Feed sortEntriesByUpdated(boolean new_first) {
        complete();
        sortEntries(new UpdatedComparator(new_first));
        return this;
    }

    public Feed sortEntriesByEdited(boolean new_first) {
        complete();
        sortEntries(new EditedComparator(new_first));
        return this;
    }

    public Feed sortEntries(Comparator<Entry> comparator) {
        complete();
        if (comparator == null)
            return this;
        List<Entry> entries = this.getEntries();
        Entry[] a = entries.toArray(new Entry[entries.size()]);
        Arrays.sort(a, comparator);
        for (Entry e : entries) {
            e.discard();
        }
        for (Entry e : a) {
            addEntry(e);
        }
        return this;
    }

    private static class EditedComparator 
      implements Comparator<Entry>, Serializable {
        private static final long serialVersionUID = -6721982957226015713L;
        private final boolean new_first;

        EditedComparator(boolean new_first) {
            this.new_first = new_first;
        }

        public int compare(Entry o1, Entry o2) {
            org.joda.time.DateTime d1 = o1.getEdited();
            org.joda.time.DateTime d2 = o2.getEdited();
            if (d1 == null)
                d1 = o1.getUpdated();
            if (d2 == null)
                d2 = o2.getUpdated();
            if (d1 == null && d2 == null)
                return 0;
            if (d1 == null && d2 != null)
                return -1;
            if (d1 != null && d2 == null)
                return 1;
            int r = d1.compareTo(d2);
            return (new_first) ? -r : r;
        }
    };

    private static class UpdatedComparator 
      implements Comparator<Entry>, Serializable {
        private static final long serialVersionUID = 8969184110080320529L;
        private final boolean new_first;

        UpdatedComparator(boolean new_first) {
            this.new_first = new_first;
        }

        public int compare(Entry o1, Entry o2) {
          org.joda.time.DateTime d1 = o1.getUpdated();
          org.joda.time.DateTime d2 = o2.getUpdated();
            if (d1 == null && d2 == null)
                return 0;
            if (d1 == null && d2 != null)
                return -1;
            if (d1 != null && d2 == null)
                return 1;
            int r = d1.compareTo(d2);
            return (new_first) ? -r : r;
        }
    };

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

    public List<Entry> getEntries(Selector selector) {
      return _getChildrenAsSet(ENTRY,selector);
    }
}
