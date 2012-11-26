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
import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.model.selector.Selectors;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.parser.Parsers;
import org.apache.abdera2.writer.Writers;
import org.apache.abdera2.writer.Writers.WriterFunction;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class Parse {

    public static void main(String[] args) throws Exception {

        Parser parser = Abdera.getInstance().getParser();

        InputStream in = Parse.class.getResourceAsStream("/simple.xml");
        Document<Feed> doc = parser.parse(in);
        Feed feed = doc.getRoot();

        System.out.println(feed.getTitle());
        System.out.println(feed.getTitleType());
        System.out.println(feed.getAlternateLink().getResolvedHref());
        System.out.println(feed.getUpdated());
        System.out.println(feed.getAuthor().getName());
        System.out.println(feed.getId());

        Entry entry = feed.getEntries().get(0);

        System.out.println(entry.getTitle());
        System.out.println(entry.getTitleType());
        System.out.println(entry.getAlternateLink().getHref()); // relative URI
        System.out.println(entry.getAlternateLink().getResolvedHref()); // absolute URI resolved against Base URI
        System.out.println(entry.getId());
        System.out.println(entry.getUpdated());
        System.out.println(entry.getSummary());
        System.out.println(entry.getSummaryType());
        
        // Selectors can be used to filter out unwanted entries
        List<Entry> entries = 
          feed.getEntries(
            Selectors.updated(
              DateTimes.afterNow()));
        System.out.println(entries);
        

        // We can also use the Guava (com.google.common.*) Function interface
        in = Parse.class.getResourceAsStream("/simple.xml");
        Function<InputStream,Document<Feed>> pf = 
          Parsers.forInputStream();
        doc = pf.apply(in);
        
        // Which means we can chain things together in interesting ways...
        in = Parse.class.getResourceAsStream("/simple.xml");
        pf = Parsers.forInputStream();
        WriterFunction wf = Writers.forOutputStream("activity", System.out);
        Function<InputStream,Void> f = Functions.compose(wf, pf);
        f.apply(in);
    }

}
