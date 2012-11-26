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
package org.apache.abdera2.ext.html;

import java.io.Reader;

import javax.xml.stream.XMLStreamReader;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.model.Div;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.parser.AbstractParser;
import org.apache.abdera2.parser.ParseException;
import org.apache.abdera2.parser.ParserOptions;
import org.apache.abdera2.parser.ParserOptions.Builder;

@Name("html")
public class HtmlParser extends AbstractParser {

    public HtmlParser() {
        this(null);
    }

    public HtmlParser(Abdera abdera) {
        super(abdera);
    }

    @Override
    protected Builder initDefaultParserOptions() {
        return ParserOptions.make();
    }

    @SuppressWarnings("unchecked")
    public <T extends Element> Document<T> parse(Reader in, String base, ParserOptions options) throws ParseException {
        boolean fragment = options.isFragment();
        Document<T> doc = null;
        if (fragment) {
            Div div = HtmlHelper.parse(abdera, in);
            doc = this.getFactory().newDocument();
            doc.setRoot((T)div);
        } else {
            doc = (Document<T>)HtmlHelper.parseDocument(abdera, in);
        }
        if (base != null)
            doc.setBaseUri(base);
        return doc;
    }

    public <T extends Element> Document<T> parse(XMLStreamReader reader) throws ParseException {
        return null;
    }

    public <T extends Element> Document<T> parse(XMLStreamReader reader, String base, ParserOptions options)
        throws ParseException {
        return null;
    }

}
