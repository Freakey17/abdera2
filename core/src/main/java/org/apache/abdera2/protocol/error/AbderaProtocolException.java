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
package org.apache.abdera2.protocol.error;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.protocol.ProtocolException;

public class AbderaProtocolException extends ProtocolException {

    private static final long serialVersionUID = 1017447143200419489L;
    private final Error error;

    public AbderaProtocolException(Error error) {
        super(error.getCode(),error.getMessage());
        this.error = error;
    }

    public AbderaProtocolException(Abdera abdera, int code, String message) {
        super(code,message);
        this.error = Error.create(abdera, code, message);
    }

    public Error getError() {
        return error;
    }

    @Override
    public int hashCode() {
      String message = error != null ? error.getMessage() : null;
      int code = error != null ? error.getCode() : 0;
      return MoreFunctions.genHashCode(1, message, code);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbderaProtocolException other = (AbderaProtocolException)obj;
        String message = error != null ? error.getMessage() : null;
        int code = error != null ? error.getCode() : 0;
        String omessage = other.error != null ? other.error.getMessage() : null;
        int ocode = other.error != null ? other.error.getCode() : 0;
        if (message == null) {
            if (omessage != null)
                return false;
        } else if (!message.equals(omessage))
            return false;
        if (code != ocode)
            return false;
        return true;
    }

}
