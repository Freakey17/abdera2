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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.abdera2.Abdera;

import com.google.common.collect.ImmutableSet;

/**
 * Provides access to the information necessary to signed an Abdera element
 */
public interface SignatureOptions extends SecurityOptions {

  public static abstract class SignatureOptionsBuilder
    extends SecurityOptions.Builder<SignatureOptions,SignatureOptionsBuilder> {
    
    protected Abdera abdera;
    protected String salg;
    protected PrivateKey skey;   
    protected X509Certificate cert;
    protected PublicKey pkey;
    protected ImmutableSet.Builder<String> refs = 
      ImmutableSet.builder();
    protected boolean signlinks;
    protected ImmutableSet.Builder<String> linkRels = 
      ImmutableSet.builder();
    
    public SignatureOptionsBuilder() {
      signingAlgorithm("http://www.w3.org/2000/09/xmldsig#dsa-sha1");
    }
    
    public SignatureOptionsBuilder abdera(Abdera abdera) {
      this.abdera = abdera;
      return this;
    }
    
    public SignatureOptionsBuilder signingAlgorithm(String alg) {
      this.salg = alg;
      return this;
    }
    
    public SignatureOptionsBuilder signingKey(PrivateKey key) {
      this.skey = key;
      return this;
    }
    
    public SignatureOptionsBuilder certificate(X509Certificate cert) {
      this.cert = cert;
      return this;
    }
    
    public SignatureOptionsBuilder publicKey(PublicKey key) {
      this.pkey = key;
      return this;
    }
    
    public SignatureOptionsBuilder ref(String ref) {
      refs.add(ref);
      return this;
    }
    
    public SignatureOptionsBuilder signLinks() {
      this.signlinks = true;
      return this;
    }
    
    public SignatureOptionsBuilder doNotSignLinks() {
      this.signlinks = false;
      return this;
    }
    
    public SignatureOptionsBuilder signLinkRel(String rel) {
      this.linkRels.add(rel);
      return this;
    }
    
  }
  
    String getSigningAlgorithm();

    /**
     * Return the private key with which to sign the element
     */
    PrivateKey getSigningKey();

    /**
     * Return the X.509 cert to associated with the signature
     */
    X509Certificate getCertificate();

    /**
     * Get the public key associated with the signature
     */
    PublicKey getPublicKey();

    Iterable<String> getReferences();

    /**
     * True if atom:link/@href and atom:content/@src targets should be included in the signature
     */
    boolean isSignLinks();

    /**
     * Get the list of link relations to sign
     */
    Iterable<String> getSignLinkRels();
}
