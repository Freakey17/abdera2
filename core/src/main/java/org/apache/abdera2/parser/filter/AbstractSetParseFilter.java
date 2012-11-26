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
package org.apache.abdera2.parser.filter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;


/**
 * ParseFilter's determine which elements and attributes are acceptable 
 * within a parsed document. They are set via the ParserOptions.setParseFilter 
 * method.
 * 
 * New in 2.0, the AbstractSetParseFilter is not synchronized and should not
 * be used by multiple threads
 */
public abstract class AbstractSetParseFilter 
  extends AbstractParseFilter 
  implements Cloneable {

  @SuppressWarnings("unchecked")
    public static abstract class Builder<E extends AbstractSetParseFilter> 
      extends AbstractParseFilter.Builder<E> {
      
      transient Set<QName> 
        qnames = new HashSet<QName>();
      transient Map<QName, Set<QName>> 
        attributes = new HashMap<QName, Set<QName>>();     
      
      public <X extends Builder<E>>X add(QName qname) {
        if (!qnames.contains(qname))
          qnames.add(qname);
        return (X)this;
      }
      
      public <X extends Builder<E>>X add(QName parent, QName attribute) {
        if (attributes.containsKey(parent)) {
            Set<QName> attrs = attributes.get(parent);
            if (!attrs.contains(attribute))
                attrs.add(attribute);
        } else {
            Set<QName> attrs = new HashSet<QName>();
            attrs.add(attribute);
            attributes.put(parent, attrs);
        }
        return (X)this;
      }
      
    }
  
    private static final long serialVersionUID = -758691949740569208L;

    private transient final Set<QName> qnames;
    private transient final Map<QName, Set<QName>> attributes;

    protected AbstractSetParseFilter(Builder<?> builder) {
      super(builder);
      this.qnames = ImmutableSet.copyOf(builder.qnames);
      this.attributes = ImmutableMap.copyOf(builder.attributes);
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean contains(QName qname) {
      return qnames.contains(qname);
    }

    public boolean contains(QName qname, QName attribute) {  
      return attributes.containsKey(qname) ?
        attributes.get(qname).contains(attribute) : 
        false;
    }

    public abstract boolean acceptable(QName qname);

    public abstract boolean acceptable(QName qname, QName attribute);

    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        // qnames field
        assert qnames != null;
        out.writeInt(qnames.size());
        for (QName q : qnames) {
            out.writeObject(q);
        }

        // attributes field
        assert attributes != null;
        out.writeInt(attributes.size());
        for (Map.Entry<QName, Set<QName>> e : attributes.entrySet()) {
            out.writeObject(e.getKey());
            final Set<QName> v = e.getValue();
            assert v != null;
            out.writeInt(v.size());
            for (QName q : v)
              out.writeObject(q);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // qnames field
        final int qnamesSize = in.readInt();
        //qnames = new HashSet<QName>(qnamesSize);
        for (int i = 0; i < qnamesSize; i++)
          qnames.add((QName)in.readObject());

        // attributes field
        final int attributesSize = in.readInt();
        //attributes = new HashMap<QName, Set<QName>>();
        for (int i = 0; i < attributesSize; i++) {
            final QName k = (QName)in.readObject();
            final int vSize = in.readInt();
            final Set<QName> v = new HashSet<QName>(vSize);
            for (int j = 0; j < vSize; j++)
                v.add((QName)in.readObject());
            attributes.put(k, v);
        }
    }
}
