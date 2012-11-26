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

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.Discover;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.util.Configuration;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * The AbderaSecurity class provides the entry point for using XML Digital Signatures and XML Encryption with Abdera.
 */
public class Security {

    private final Abdera abdera;
    private final Encryption encryption;
    private final Signature signature;

    public Security() {
        this(Abdera.getInstance());
    }

    public Security(Abdera abdera) {
        this.abdera = abdera;
        this.encryption = newEncryption();
        this.signature = newSignature();
    }

    public Security(Configuration config) {
        this(new Abdera(config));
    }

    private Abdera getAbdera() {
        return abdera;
    }

    /**
     * Acquire a new XML Encryption provider instance
     */
    public Encryption newEncryption() {
        return (Encryption)Discover.locate(Encryption.class,
                                           "org.apache.abdera2.security.xmlsec.XmlEncryption",
                                           getAbdera());
    }

    /**
     * Acquire a shared XML Encryption provider instance
     */
    public Encryption getEncryption() {
        return encryption;
    }

    /**
     * Acquire a new XML Digital Signature provider instance
     */
    public Signature newSignature() {
        return (Signature)Discover.locate(Signature.class,
                                          "org.apache.abdera2.security.xmlsec.XmlSignature",
                                          getAbdera());
    }

    /**
     * Acquire a shared XML Digital Signature provider instance
     */
    public Signature getSignature() {
        return signature;
    }

    public <T extends Element,E extends Element>Function<Document<T>,Document<E>> encryptor(final EncryptionOptions options) {
      return new Function<Document<T>,Document<E>>() {
        public Document<E> apply(Document<T> input) {
          return getEncryption().<E>encrypt(input, options);
        }
      };
    }
    
    public <T extends Element,E extends Element>Function<Document<T>,Document<E>> decryptor(final EncryptionOptions options) {
      return new Function<Document<T>,Document<E>>() {
        @SuppressWarnings("unchecked")
        public Document<E> apply(Document<T> input) {
          Encryption enc = getEncryption();
          if (!enc.isEncrypted(input)) return (Document<E>)input;
          return enc.<E>decrypt(input, options);
        }
      };
    }
    
    public <T extends Element>Function<T,T> signer(final SignatureOptions options) {
      return new Function<T,T>() {
        public T apply(T input) {
          return getSignature().sign(input, options);
        }
      };
    }
    
    public <T extends Element>Function<T,Boolean> verifier() {
      return verifier(null);
    }
    
    public <T extends Element>Function<T,Boolean> verifier(final SignatureOptions options) {
      return new Function<T,Boolean>() {
        public Boolean apply(T input) {
          return getSignature().verify(input, options);
        }
      };
    }
        
    public <T extends Element>Predicate<T> isVerified() {
      return isVerified(null);
    }
    
    public <T extends Element>Predicate<T> isVerified(final SignatureOptions options) {
      return new Predicate<T>() {
        public boolean apply(T input) {
          return  verifier(options).apply(input);
        }
      };
    }
    
    public boolean verified(Base e) {
      return isVerified().apply(e instanceof Document ? ((Document<?>)e).getRoot() : (Element)e);
    }
    
    public boolean notVerified(Base e) {
      return !verified(e);
    }
    
    public boolean verified(Base e, SignatureOptions options) {
      return isVerified(options).apply(e instanceof Document ? ((Document<?>)e).getRoot() : (Element)e);
    }
    
    public boolean notVerified(Base e, SignatureOptions options) {
      return !verified(e,options);
    }
}
