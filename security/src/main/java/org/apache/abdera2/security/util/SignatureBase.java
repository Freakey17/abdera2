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

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ExtensibleElement;
import org.apache.abdera2.security.Signature;

public abstract class SignatureBase extends SecurityBase implements Signature {

    protected SignatureBase(Abdera abdera) {
        super(abdera);
    }

    public boolean isSigned(Element element) {
        if (element instanceof ExtensibleElement)
            return ((ExtensibleElement)element).getExtension(Constants.SIGNATURE) != null;
        else
            return false;
    }

}
