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
package org.apache.abdera2.examples.security;


import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.security.KeyHelper;
import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.security.Security;
import org.apache.abdera2.security.Encryption;
import org.apache.abdera2.security.EncryptionOptions;
import org.apache.abdera2.security.util.DHContext;
import org.joda.time.DateTime;

public class DHEnc {

    public static void main(String[] args) throws Exception {

        Abdera abdera = Abdera.getInstance();

        // Register the bouncy castle jce provider
        KeyHelper.prepareDefaultJceProvider();

        // Create the entry to encrypt
        Security absec = new Security(abdera);
        Factory factory = abdera.getFactory();

        Entry entry = factory.newEntry();
        entry.setId("http://example.org/foo/entry");
        entry.setUpdated(DateTime.now());
        entry.setTitle("This is an entry");
        entry.setContentAsXhtml("This <b>is</b> <i>markup</i>");
        entry.addAuthor("James");
        entry.addLink("http://www.example.org");

        // Prepare the Diffie-Hellman Key Exchange Session
        // There are two participants in the session, A and B
        // Each has their own DHContext. A creates their context and
        // sends the request key parameters to B. B uses those parameters
        // to create their context, the returns it's public key
        // back to A.
        DHContext context_a = new DHContext();
        DHContext context_b = new DHContext(context_a.getRequestString());
        context_a.setPublicKey(context_b.getResponseString());

        // Prepare the encryption options
        Encryption enc = absec.getEncryption();

        // Encrypt the document using A's DHContext
        EncryptionOptions options = context_a.getEncryptionOptions(enc);
        Document<?> enc_doc = enc.encrypt(entry.getDocument(), options);

        enc_doc.writeTo(System.out);

        System.out.println("\n\n");

        // Decrypt the document using B's DHContext
        options = context_b.getEncryptionOptions(enc);
        Document<Entry> entry_doc = enc.decrypt(enc_doc, options);

        entry_doc.writeTo(System.out);

    }

}
