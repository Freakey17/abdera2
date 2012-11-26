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

import javax.xml.namespace.QName;

import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.model.Attribute;
import org.apache.axiom.om.OMAttribute;

public class FOMAttribute implements Attribute {

    private final OMAttribute attr;

    protected FOMAttribute(OMAttribute attr) {
        this.attr = attr;
    }

    public QName getQName() {
        return attr.getQName();
    }

    public String getText() {
        return attr.getAttributeValue();
    }

    public Attribute setText(String text) {
        attr.setAttributeValue(text);
        return this;
    }

    public Factory getFactory() {
        return (Factory)attr.getOMFactory();
    }

    @Override
    public int hashCode() {
      return MoreFunctions.genHashCode(1, attr);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FOMAttribute other = (FOMAttribute)obj;
        if (attr == null) {
            if (other.attr != null)
                return false;
        } else if (!attr.equals(other.attr))
            return false;
        return true;
    }

}
