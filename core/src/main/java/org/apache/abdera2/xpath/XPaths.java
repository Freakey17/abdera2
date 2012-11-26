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
package org.apache.abdera2.xpath;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Base;

import com.google.common.base.Function;

@SuppressWarnings("unchecked")
public final class XPaths  {

  private XPaths() {}
  
  protected static abstract class XPathFunction<E extends Base, X>
    implements Function<E,X> {
    
    protected final XPath xpath;
    protected final String path;
    protected final Map<String,String> namespaces =
      new HashMap<String,String>();
    
    public XPathFunction(XPath xpath, String query) {
      this.xpath = xpath;
      this.path = query;
      this.namespaces.putAll(xpath.getDefaultNamespaces());     
    }
    
    public XPathFunction(String query) {
      this(Abdera.getInstance().getXPath(),query);
    }
    
    public XPathFunction(
      XPath xpath,
      String query,
      Map<String,String> namespaces) {
        this.xpath = xpath;
        this.path = query;
        this.namespaces.putAll(namespaces);
    }
    
    public XPathFunction(
      String query, 
      Map<String,String> namespaces) {
      this(Abdera.getInstance().getXPath(),query,namespaces);
    }
  }
  
  public static <E extends Base>XPathFunction<E, Boolean> booleanValueOf(final String query) {
    return new XPathFunction<E,Boolean>(query) {
      public Boolean apply(E input) {
        return xpath.booleanValueOf(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base>XPathFunction<E,Boolean> booleanValueOf(final XPath xpath, final String query) {
    return new XPathFunction<E,Boolean>(xpath,query) {
      public Boolean apply(E input) {
        return xpath.booleanValueOf(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base>XPathFunction<E,Boolean> booleanValueOf(final XPath xpath, final String query, Map<String,String> ns) {
    return new XPathFunction<E,Boolean>(xpath,query,ns) {
      public Boolean apply(E input) {
        return xpath.booleanValueOf(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base, X>XPathFunction<E,X> evaluate(final String query) {
    return new XPathFunction<E,X>(query) {
      public X apply(E input) {
        return (X)xpath.evaluate(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base, X>XPathFunction<E,X> evaluate(final XPath xpath, final String query) {
    return new XPathFunction<E,X>(xpath,query) {
      public X apply(E input) {
        return (X)xpath.evaluate(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base, X>XPathFunction<E, X> evaluate(final XPath xpath, final String query, Map<String,String> ns) {
    return new XPathFunction<E, X>(xpath,query,ns) {
      public X apply(E input) {
        return (X)xpath.evaluate(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base, X extends Number>XPathFunction<E,X> numericValueOf(final String query) {
    return new XPathFunction<E,X>(query) {
      public X apply(E input) {
        return (X)xpath.numericValueOf(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base, X extends Number>XPathFunction<E,X> numericValueOf(final XPath xpath, final String query) {
    return new XPathFunction<E,X>(xpath,query) {
      public X apply(E input) {
        return (X)xpath.numericValueOf(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base, X extends Number>XPathFunction<E,X> numericValueOf(final XPath xpath, final String query, Map<String,String> ns) {
    return new XPathFunction<E,X>(xpath,query,ns) {
      public X apply(E input) {
        return (X)xpath.numericValueOf(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base>XPathFunction<E,Collection<?>> selectNodes(final String query) {
    return new XPathFunction<E,Collection<?>>(query) {
      public Collection<?> apply(E input) {
        return xpath.selectNodes(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base>XPathFunction<E,Collection<?>> selectNodes(final XPath xpath, final String query) {
    return new XPathFunction<E,Collection<?>>(xpath,query) {
      public Collection<?> apply(E input) {
        return xpath.selectNodes(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base>XPathFunction<E,Collection<?>> selectNodes(final XPath xpath, final String query, Map<String,String> ns) {
    return new XPathFunction<E,Collection<?>>(xpath,query,ns) {
      public Collection<?> apply(E input) {
        return xpath.selectNodes(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base, X>XPathFunction<E,X> selectSingleNode(final String query) {
    return new XPathFunction<E,X>(query) {
      public X apply(E input) {
        return (X)xpath.selectSingleNode(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base, X>XPathFunction<E,X> selectSingleNode(final XPath xpath, final String query) {
    return new XPathFunction<E,X>(xpath,query) {
      public X apply(E input) {
        return (X)xpath.selectSingleNode(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base, X>XPathFunction<E,X> selectSingleNode(final XPath xpath, final String query, Map<String,String> ns) {
    return new XPathFunction<E,X>(xpath,query,ns) {
      public X apply(E input) {
        return (X)xpath.selectSingleNode(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base>XPathFunction<E,String> valueOf(final String query) {
    return new XPathFunction<E,String>(query) {
      public String apply(E input) {
        return xpath.valueOf(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base>XPathFunction<E,String> valueOf(final XPath xpath, final String query) {
    return new XPathFunction<E,String>(xpath,query) {
      public String apply(E input) {
        return xpath.valueOf(path, input, namespaces);
      }
    };
  }
  
  public static <E extends Base>XPathFunction<E,String> valueOf(final XPath xpath, final String query, Map<String,String> ns) {
    return new XPathFunction<E,String>(xpath,query,ns) {
      public String apply(Base input) {
        return xpath.valueOf(path, input, namespaces);
      }
    };
  }
}
