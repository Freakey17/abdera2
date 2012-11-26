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

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.parser.AbstractParser;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.parser.ParserFactory;

@SuppressWarnings("unchecked")
public class FOMParserFactory implements ParserFactory {

    private final Abdera abdera;
    private final Map<String, Parser> parsers;

    public FOMParserFactory() {
        this(Abdera.getInstance());
    }

    public FOMParserFactory(Abdera abdera) {
        this.abdera = abdera;
        Map<String, Parser> p = getAbdera().getConfiguration().getParsers();
        this.parsers = (p != null) ? p : new HashMap<String, Parser>();
    }

    protected Abdera getAbdera() {
        return abdera;
    }

    public <T extends Parser> T getParser() {
        return (T)getAbdera().getParser();
    }

    public <T extends Parser> T getParser(String name) {
        Parser parser = (T)((name != null) ? getParsers().get(name.toLowerCase()) : getParser());
        if (parser instanceof AbstractParser) {
            ((AbstractParser)parser).setAbdera(abdera);
        }
        return (T)parser;
    }

    private Map<String, Parser> getParsers() {
        return parsers;
    }

}
