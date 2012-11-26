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

import org.apache.abdera2.common.selector.AbstractSelector;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.model.Link;

import com.google.common.collect.ImmutableSet;

/**
 * Selector implementation that selects Link elements 
 * that match a given set of rel attribute values
 * @see org.apache.abdera2.common.selector.Selector
 */
public class LinkRelSelector 
extends AbstractSelector<Link>
implements Selector<Link> {

  public static Selector<Link> of(String... rels) {
    return new LinkRelSelector(rels);
  }
  
  private static final long serialVersionUID = 7008363856043465676L;
  private final Set<String> rels;
  
  LinkRelSelector(String... rels) {
    this.rels = ImmutableSet.copyOf(rels);
  }

  public boolean select(Object item) {
    if (!(item instanceof Link)) return false;
    Link link = (Link)item;
    String rel = link.getRel();
    rel = rel == null || 
          rel.length() == 0 ? 
              Link.REL_ALTERNATE : rel;
    rel = rel.toLowerCase();
    return rels.isEmpty() || 
           rels.contains(rel) || 
           rels.contains(Link.Helper.getRelEquiv(rel));
  }


}
