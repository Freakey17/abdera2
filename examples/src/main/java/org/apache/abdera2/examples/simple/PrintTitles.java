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
package org.apache.abdera2.examples.simple;

import java.io.InputStream;
import java.util.List;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.parser.ParserOptions;
import org.apache.abdera2.parser.filter.WhiteListParseFilter;
import org.apache.abdera2.common.Constants;

/**
 * Illustrates the use of optimized-parsing using the WhiteListParseFilter. 
 * Using this mechanism, only the elements added to the ParseFilter will be 
 * parsed and added to the Feed Object Model instance. The resulting savings 
 * in memory and CPU costs is significant.
 */
public class PrintTitles {
    public static void main(String args[]) {

        Parser parser = Abdera.getInstance().getParser();

        InputStream in = Parse.class.getResourceAsStream("/simple.xml");

        ParserOptions opts = 
          parser.makeDefaultParserOptions()
            .filter(
              WhiteListParseFilter
                .make()
                .add(Constants.FEED)
                .add(Constants.ENTRY)
                .add(Constants.TITLE)
                .get()).get();

        Document<Feed> doc;

        try {
            doc = parser.parse(in, "", opts);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Feed feed = doc.getRoot();

        List<Entry> entries = 
          feed.getEntries();

        for (Entry e : entries)
          System.out.println(
            e.getTitle());
    }
}
