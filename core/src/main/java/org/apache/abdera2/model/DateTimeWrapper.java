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
package org.apache.abdera2.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.factory.Factory;

/**
 * An ElementWrapper implementation that can serve as the basis for Atom Date Construct based extensions.
 */
public abstract class DateTimeWrapper extends ElementWrapper implements DateTime {

    protected DateTimeWrapper(Element internal) {
        super(internal);
    }

    protected DateTimeWrapper(Factory factory, QName qname) {
        super(factory, qname);
    }

    public org.joda.time.DateTime getValue() {
        org.joda.time.DateTime value = null;
        String v = getText();
        if (v != null) {
            value = org.joda.time.DateTime.parse(v);
        }
        return value;
    }

    public DateTime setValue(org.joda.time.DateTime dateTime) {
        if (dateTime != null)
            setText(DateTimes.format(dateTime));
        else
            setText("");
        return this;
    }
    
    public DateTime setValueNow() {
      return this.setValue(DateTimes.now());
    }

    public DateTime setDate(Date date) {
        if (date != null)
            setText(DateTimes.format(date));
        else
            setText("");
        return this;
    }

    public DateTime setCalendar(Calendar date) {
        if (date != null)
            setText(DateTimes.format(date));
        else
            setText("");
        return this;
    }

    public DateTime setTime(long date) {
        setText(DateTimes.format(date));
        return this;
    }

    public DateTime setString(String date) {
        if (date != null)
            setText(DateTimes.format(date));
        else
            setText("");
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
