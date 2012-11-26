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
package org.apache.abdera2.model.selector;

import java.util.Set;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.selector.AbstractSelector;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.model.Category;

import com.google.common.collect.ImmutableSet;

/**
 * Selector implementation that selects Category elements using the 
 * specified scheme(s).
 * @see org.apache.abdera2.common.selector.Selector
 */
public class CategorySchemeSelector 
  extends AbstractSelector<Category>
  implements Selector<Category> {

  public static Selector<Category> of(String... schemes) {
    return new CategorySchemeSelector(schemes);
  }
  
  public static Selector<Category> of(IRI... schemes) {
    return new CategorySchemeSelector(schemes);
  }
  
  private static final long serialVersionUID = 7008363856043465676L;
  private final Set<IRI> schemes;
  
  CategorySchemeSelector(String... schemes) {
    this.schemes = MoreFunctions.immutableSetOf(schemes, IRI.parser, IRI.class);
  }
  
  CategorySchemeSelector(IRI... schemes) {
    this.schemes = ImmutableSet.copyOf(schemes);
  }
  
  public boolean select(Object item) {
    if (!(item instanceof Category)) return false;
    return schemes.isEmpty() || schemes.contains(((Category)item).getScheme());
  }

}
