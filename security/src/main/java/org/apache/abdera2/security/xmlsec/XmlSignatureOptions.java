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
package org.apache.abdera2.security.xmlsec;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.security.SignatureOptions;
public final class XmlSignatureOptions extends XmlSecurityOptions implements SignatureOptions {

  public static SignatureOptionsBuilder make() {
    return new XmlSignatureOptionsBuilder();
  }
  
  protected static final class XmlSignatureOptionsBuilder 
    extends SignatureOptionsBuilder {
  
    public SignatureOptions get() {
      return new XmlSignatureOptions(
        abdera,salg,skey,pkey,cert,refs.build(),signlinks,linkRels.build());
    }
    
  }
  
    private final PrivateKey signingKey;
    private final PublicKey publickey;
    private final X509Certificate cert;
    private final Iterable<String> linkrels;
    private final boolean signlinks;
    private final Iterable<String> references;
    private final String algo;

    protected XmlSignatureOptions(
      Abdera abdera, 
      String salg, 
      PrivateKey skey, 
      PublicKey pkey, 
      X509Certificate cert, 
      Iterable<String> refs, 
      boolean signlinks, 
      Iterable<String> rels) {
      super(abdera);
      this.signingKey = skey;
      this.publickey = pkey;
      this.cert = cert;
      this.linkrels = rels;
      this.signlinks = signlinks;
      this.references = refs;
      this.algo = salg;
    }
    
    public String getSigningAlgorithm() {
        return algo;
    }

    public PrivateKey getSigningKey() {
        return signingKey;
    }

    public X509Certificate getCertificate() {
        return cert;
    }

    public Iterable<String> getReferences() {
        return references;
    }

    public PublicKey getPublicKey() {
        return publickey;
    }

    public boolean isSignLinks() {
        return signlinks;
    }

    public Iterable<String> getSignLinkRels() {
        return this.linkrels;
    }

}
