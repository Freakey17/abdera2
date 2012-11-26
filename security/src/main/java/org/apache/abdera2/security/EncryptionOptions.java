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
package org.apache.abdera2.security;

import java.security.Key;

import org.apache.abdera2.Abdera;

/**
 * Provides access to the information necessary to encrypt or decrypt a document
 */
public interface EncryptionOptions extends SecurityOptions {

  public static abstract class EncryptionOptionsBuilder
    extends SecurityOptions.Builder<EncryptionOptions,EncryptionOptionsBuilder> {
    
    protected Abdera abdera;
    protected boolean includeKeyInfo;
    protected Key dek;
    protected Key kek;
    protected String kca;
    protected String dca;
    
    protected EncryptionOptionsBuilder() {
      keyCipherAlgorithm("http://www.w3.org/2001/04/xmlenc#kw-aes128");
      dataCipherAlgorithm("http://www.w3.org/2001/04/xmlenc#aes128-cbc");
      doNotIncludeKeyInfo();
    }
    
    public EncryptionOptionsBuilder abdera(Abdera abdera) {
      this.abdera = abdera;
      return this;
    }
    
    public EncryptionOptionsBuilder includeKeyInfo() {
      this.includeKeyInfo = true;
      return this;
    }
    
    public EncryptionOptionsBuilder doNotIncludeKeyInfo() {
      this.includeKeyInfo = false;
      return this;
    }
    
    public EncryptionOptionsBuilder dataEncryptionKey(Key key) {
      this.dek = key;
      return this;
    }
    
    public EncryptionOptionsBuilder keyEncryptionKey(Key key) {
      this.kek = key;
      return this;
    }
    
    public EncryptionOptionsBuilder keyCipherAlgorithm(String alg) {
      this.kca = alg;
      return this;
    }
    
    public EncryptionOptionsBuilder dataCipherAlgorithm(String alg) {
      this.dca = alg;
      return this;
    }
    
  }
  
    /**
     * Return the secret key used to encrypt/decrypt the document content
     */
    Key getDataEncryptionKey();

    /**
     * Return the secret key used to encrypt/decrypt the data encryption key
     */
    Key getKeyEncryptionKey();

    /**
     * Return the cipher algorithm used to decrypt/encrypt the data encryption key The default is
     * "http://www.w3.org/2001/04/xmlenc#kw-aes128"
     */
    String getKeyCipherAlgorithm();

    /**
     * Return the cipher algorithm used to decrypt/encrypt the document content The default is
     * "http://www.w3.org/2001/04/xmlenc#aes128-cbc"
     */
    String getDataCipherAlgorithm();

    /**
     * Return true if the encryption should include information about the key The default is false
     */
    boolean includeKeyInfo();

}
