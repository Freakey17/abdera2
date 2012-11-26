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
package org.apache.abdera2.ext.media;

import javax.activation.MimeType;

import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ExtensibleElementWrapper;
import org.apache.abdera2.common.anno.QName;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;

import static org.apache.abdera2.ext.media.MediaConstants.*;

@QName(value=LN_CONTENT, 
    ns=MEDIA_NS,
    pfx=MEDIA_PREFIX)
public class MediaContent extends ExtensibleElementWrapper {

    public MediaContent(Element internal) {
        super(internal);
    }

    public MediaContent(Factory factory) {
        super(factory, CONTENT);
    }

    public IRI getUrl() {
        String url = getAttributeValue("url");
        return (url != null) ? new IRI(url) : null;
    }

    public void setUrl(String url) {
        if (url != null)
            setAttributeValue("url", (new IRI(url)).toString());
        else
            removeAttribute("url");
    }

    public long getFilesize() {
        String size = getAttributeValue("filesize");
        return (size != null) ? Long.parseLong(size) : -1;
    }

    public void setFilesize(long size) {
        if (size > -1)
            setAttributeValue("filesize", String.valueOf(size));
        else
            removeAttribute("filesize");
    }

    public MimeType getType() {
      String type = getAttributeValue("type");
      return type != null ? 
        MimeTypeHelper.unmodifiableMimeType(type) : 
        null;
    }

    public void setType(String type) {
      if (type != null)
        setAttributeValue("type", type);
      else
        removeAttribute("type");
    }

    public Medium getMedium() {
        String medium = getAttributeValue("medium");
        return (medium != null) ? Medium.valueOf(medium.toUpperCase()) : null;
    }

    public void setMedium(Medium medium) {
        if (medium != null)
            setAttributeValue("medium", medium.name().toLowerCase());
        else
            removeAttribute("medium");
    }

    public boolean isDefault() {
        String def = getAttributeValue("isDefault");
        return (def != null) ? def.equalsIgnoreCase("true") : false;
    }

    public Expression getExpression() {
        String exp = getAttributeValue("expression");
        return (exp != null) ? Expression.valueOf(exp.toUpperCase()) : null;
    }

    public void setExpression(Expression exp) {
        if (exp != null)
            setAttributeValue("expression", exp.name().toLowerCase());
        else
            removeAttribute("expression");
    }

    public int getBitrate() {
        String bitrate = getAttributeValue("bitrate");
        return (bitrate != null) ? Integer.parseInt(bitrate) : -1;
    }

    public void setBitrate(int bitrate) {
        if (bitrate > -1)
            setAttributeValue("bitrate", String.valueOf(bitrate));
        else
            removeAttribute("bitrate");
    }

    public int getFramerate() {
        String framerate = getAttributeValue("framerate");
        return (framerate != null) ? Integer.parseInt(framerate) : -1;
    }

    public void setFramerate(int framerate) {
        if (framerate > -1)
            setAttributeValue("framerate", String.valueOf(framerate));
        else
            removeAttribute("framerate");
    }

    public double getSamplingRate() {
        String rate = getAttributeValue("samplingrate");
        return (rate != null) ? Double.parseDouble(rate) : -1;
    }

    public void setSamplingRate(double samplingrate) {
        if (samplingrate > Double.parseDouble("-1"))
            setAttributeValue("samplingrate", String.valueOf(samplingrate));
        else
            removeAttribute("samplingrate");
    }

    public int getChannels() {
        String c = getAttributeValue("channels");
        return (c != null) ? Integer.parseInt(c) : -1;
    }

    public void setChannels(int channels) {
        if (channels > -1)
            setAttributeValue("channels", String.valueOf(channels));
        else
            removeAttribute("channels");
    }

    public int getDuration() {
        String c = getAttributeValue("duration");
        return (c != null) ? Integer.parseInt(c) : -1;
    }

    public void setDuration(int duration) {
        if (duration > -1)
            setAttributeValue("duration", String.valueOf(duration));
        else
            removeAttribute("duration");
    }

    public int getWidth() {
        String width = getAttributeValue("width");
        return (width != null) ? Integer.parseInt(width) : -1;
    }

    public void setWidth(int width) {
        if (width > -1) {
            setAttributeValue("width", String.valueOf(width));
        } else {
            removeAttribute("width");
        }
    }

    public int getHeight() {
        String height = getAttributeValue("height");
        return (height != null) ? Integer.parseInt(height) : -1;
    }

    public void setHeight(int height) {
        if (height > -1) {
            setAttributeValue("height", String.valueOf(height));
        } else {
            removeAttribute("height");
        }
    }

    public String getLang() {
        return getAttributeValue("lang");
    }

    public void setLang(String lang) {
        if (lang != null)
            setAttributeValue("lang", lang);
        else
            removeAttribute("lang");
    }

}
