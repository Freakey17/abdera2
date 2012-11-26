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
package org.apache.abdera2.examples.uritemplates;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.templates.Template;
import org.apache.abdera2.common.anno.Context;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.anno.Param;
import org.apache.abdera2.common.anno.URITemplate;
import org.apache.abdera2.writer.StreamWriter;

public class AtomLinkTemplate {

    public static void main(String... args) throws Exception {

        Abdera abdera = Abdera.getInstance();
        abdera.create(StreamWriter.class)
              .setOutputStream(System.out)
              .startDocument()
              .startFeed()
                .writeBase("http://example.org")
                .writeLink(getPage(1, 10), "current")
                .writeLink(getPage(2, 10), "self")
                .writeLink(getPage(1, 10), "previous")
                .writeLink(getPage(3, 10), "next")
                .writeLink(getPage(1, 10), "first")
                .writeLink(getPage(10, 10), "last")
              .endFeed()
              .endDocument()
              .flush();

    }

    private static String getPage(int page, int count) {
        return Template.expandAnnotated(new PagingLink(page, count));
    }

    @URITemplate("{/view}{?page,count}")
    @Context(@Param(name="view",value="entries"))
    public static class PagingLink {
        private final int page;
        private final int count;
        public PagingLink(int page, int count) {
            this.page = page;
            this.count = count;
        }
        public int getPage() {
            return page;
        }
        @Name("count")
        public int getPageSize() {
            return count;
        }
    }

}
