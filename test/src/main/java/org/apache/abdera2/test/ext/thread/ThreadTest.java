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
package org.apache.abdera2.test.ext.thread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.ext.thread.InReplyTo;
import org.apache.abdera2.ext.thread.ThreadConstants;
import org.apache.abdera2.ext.thread.ThreadHelper;
import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.model.Entry;
import org.junit.Test;

public class ThreadTest {

    @Test
    public void testThread() throws Exception {

        Abdera abdera = Abdera.getInstance();
        Factory factory = abdera.getFactory();
        Entry e1 = factory.newEntry();
        Entry e2 = factory.newEntry();

        e1.setId("tag:example.org,2006:first");
        e2.setId("tag:example.org,2006:second");

        ThreadHelper.addInReplyTo(e2, e1); // e2 is a response to e1
        assertNotNull(e2.getExtension(ThreadConstants.IN_REPLY_TO));
        InReplyTo irt = e2.getExtension(ThreadConstants.IN_REPLY_TO);
        assertEquals(e1.getId(), irt.getRef());

    }

}
