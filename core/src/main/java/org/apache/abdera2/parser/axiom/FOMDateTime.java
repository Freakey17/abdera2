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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.namespace.QName;
import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.model.DateTime;
import org.apache.abdera2.model.Element;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMDateTime extends FOMElement implements DateTime {

    private static final long serialVersionUID = -6611503566172011733L;
    private org.joda.time.DateTime value;

    public FOMDateTime(QName qname) {
        super(qname);
    }

    public FOMDateTime(QName qname, Date date) {
        this(qname);
        setDate(date);
    }

    public FOMDateTime(QName qname, Calendar calendar) {
        this(qname);
        setCalendar(calendar);
    }

    public FOMDateTime(QName qname, String value) {
        this(qname);
        setString(value);
    }

    public FOMDateTime(QName qname, long time) {
        this(qname);
        setTime(time);
    }

    public FOMDateTime(QName qname, org.joda.time.DateTime dateTime) {
        this(qname);
        setValue(dateTime);
    }

    public FOMDateTime(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    public FOMDateTime(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    public FOMDateTime(QName qname, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder)
        throws OMException {
        super(qname, parent, factory, builder);
    }

    public org.joda.time.DateTime getValue() {
        if (value == null) {
            String v = getText();
            if (v != null) {
                value = org.joda.time.DateTime.parse(v);
            }
        }
        return value;
    }

    public DateTime setValueNow() {
      return setValue(DateTimes.now());
    }
    
    public DateTime setValue(org.joda.time.DateTime dateTime) {
        complete();
        value = null;
        if (dateTime != null)
            ((Element)this).setText(DateTimes.format(dateTime));
        else
            _removeAllChildren();
        return this;
    }

    public DateTime setDate(Date date) {
        complete();
        value = null;
        if (date != null)
            ((Element)this).setText(DateTimes.format(date));
        else
            _removeAllChildren();
        return this;
    }

    public DateTime setCalendar(Calendar date) {
        complete();
        value = null;
        if (date != null)
            ((Element)this).setText(DateTimes.format(date));
        else
            _removeAllChildren();
        return this;
    }

    public DateTime setTime(long date) {
        complete();
        value = null;
        ((Element)this).setText(DateTimes.format(date));
        return this;
    }

    public DateTime setString(String date) {
        complete();
        value = null;
        if (date != null)
            ((Element)this).setText(DateTimes.format(date));
        else
            _removeAllChildren();
        return this;
    }

    public Date getDate() {
        org.joda.time.DateTime ad = getValue();
        return (ad != null) ? ad.toDate() : null;
    }

    public Calendar getCalendar() {
        org.joda.time.DateTime ad = getValue();
        return ad != null ? ad.toCalendar(Locale.getDefault()) : null;
    }

    public long getTime() {
        org.joda.time.DateTime ad = getValue();
        return ad != null ? ad.getMillis() : 0;
    }

    public String getString() {
        org.joda.time.DateTime ad = getValue();
        return (ad != null) ? DateTimes.format(ad) : null;
    }

}
