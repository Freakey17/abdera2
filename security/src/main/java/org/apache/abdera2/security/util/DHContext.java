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
package org.apache.abdera2.security.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.apache.abdera2.common.security.DHBase;
import org.apache.abdera2.security.Encryption;
import org.apache.abdera2.security.EncryptionOptions;
import org.apache.xml.security.encryption.XMLCipher;

/**
 * Implements the Diffie-Hellman Key Exchange details for both parties Party A: DHContext context_a = new DHContext();
 * String req = context_a.getRequestString(); Party B: DHContext context_b = new DHContext(req); EncryptionOptions
 * options = context_b.getEncryptionOptions(enc); // encrypt String ret = context_b.getResponseString(); Party A:
 * context_a.setPublicKey(ret); EncryptionOptions options = context_a.getEncryptionOptions(enc); // decrypt
 */
public class DHContext extends DHBase {

    private static final long serialVersionUID = -2717424739180671914L;

    public DHContext() {
      super();
    }

    public DHContext(String dh) {
        super(dh);
    }

    public EncryptionOptions getEncryptionOptions(Encryption enc) 
      throws InvalidKeyException, NoSuchAlgorithmException {
        return getEncryptionOptions(enc, XMLCipher.TRIPLEDES);
    }
    
    public EncryptionOptions getEncryptionOptions(Encryption enc, String alg) 
      throws InvalidKeyException, NoSuchAlgorithmException {
        return enc.getDefaultEncryptionOptions()
          .dataEncryptionKey(generateSecret())
          .dataCipherAlgorithm(alg)
          .get();
  }

}
