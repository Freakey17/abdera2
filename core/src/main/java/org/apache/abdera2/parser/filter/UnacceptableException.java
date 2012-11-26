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
package org.apache.abdera2.parser.filter;

import javax.xml.namespace.QName;

import org.apache.abdera2.common.misc.MoreFunctions;
import static com.google.common.base.Preconditions.checkNotNull;

public class UnacceptableException extends RuntimeException {

  private static final long serialVersionUID = -6923049484196141770L;

  private final QName element;
  private final QName attribute;
  
  protected UnacceptableException(QName element, QName attribute) {
    super(message(element,attribute));
    this.element = element;
    this.attribute = attribute;
  }

  public QName getElement() {
    return element;
  }
  
  public QName getAttribute() {
    return attribute;
  }
  
  private static String message(QName element, QName attribute) {
    if (element == null && attribute == null)
      return "Unacceptable element";
    else if (attribute == null)
      return String.format(
        "Unacceptable element [%s]", 
        checkNotNull(element).toString());
    else 
      return String.format(
        "Unacceptable element [%s,%s]", 
        checkNotNull(element).toString(), 
        attribute.toString());
  }

  @Override
  public int hashCode() {
    return MoreFunctions.genHashCode(1, attribute.hashCode(), element.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UnacceptableException other = (UnacceptableException) obj;
    if (attribute == null) {
      if (other.attribute != null)
        return false;
    } else if (!attribute.equals(other.attribute))
      return false;
    if (element == null) {
      if (other.element != null)
        return false;
    } else if (!element.equals(other.element))
      return false;
    return true;
  }
  
}
