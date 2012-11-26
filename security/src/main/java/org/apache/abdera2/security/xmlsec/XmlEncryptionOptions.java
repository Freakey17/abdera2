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

import java.security.Key;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.security.EncryptionOptions;

public final class XmlEncryptionOptions extends XmlSecurityOptions implements EncryptionOptions {
  
  public static EncryptionOptionsBuilder make() {
    return new XmlEncryptionOptionsBuilder();
  }
  
  protected static final class XmlEncryptionOptionsBuilder 
    extends EncryptionOptionsBuilder {

    public EncryptionOptions get() {
      return new XmlEncryptionOptions(abdera,dek,kek,kca,dca,includeKeyInfo);
    }
    
  }
  
    private final Key dek;
    private final Key kek;
    private final String kca;
    private final String dca;
    private final boolean setki;

    protected XmlEncryptionOptions(
      Abdera abdera,
      Key dek, 
      Key kek, 
      String kca, 
      String dca, 
      boolean setki) {
        super(abdera);
        this.dek = dek;
        this.kek = kek;
        this.kca = kca;
        this.dca = dca;
        this.setki = setki;
    }

    public Key getDataEncryptionKey() {
        return dek;
    }

    public Key getKeyEncryptionKey() {
        return kek;
    }

    public String getKeyCipherAlgorithm() {
        return kca;
    }

    public String getDataCipherAlgorithm() {
        return dca;
    }

    public boolean includeKeyInfo() {
        return setki;
    }
}
