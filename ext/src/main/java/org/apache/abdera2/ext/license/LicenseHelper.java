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
package org.apache.abdera2.ext.license;

import java.util.Collections;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Link;
import org.apache.abdera2.model.Source;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Implementation of the Atom License Extension, RFC 4946
 */
public final class LicenseHelper {

    public static final String UNSPECIFIED_LICENSE = 
      "http://purl.org/atompub/license#unspecified";

    LicenseHelper() {
    }

    @SuppressWarnings("unchecked")
    public static Iterable<Link> getLicense(Base base, boolean inherited) {
        Iterable<Link> links = null;
        if (base instanceof Source) {
            links = ((Source)base).getLinks(Link.REL_LICENSE);
        } else if (base instanceof Entry) {
            Entry entry = (Entry)base;
            Source source = entry.getSource();
            Base parent = entry.getParentElement();
            links = entry.getLinks(Link.REL_LICENSE);
            if (inherited && !contains(links) && source != null) {
                links = getLicense(source, false);
            }
            
            if (inherited && !contains(links) && parent != null) {
                links = getLicense(parent, false);
            }
        }
        return links == null ? Collections.EMPTY_SET : links;
    }

    private static boolean contains(Iterable<Link> list) {
      if (list == null) return false;
      return !Iterables.isEmpty(list);
    }
    
    public static Iterable<Link> getLicense(Base base) {
        return getLicense(base, true);
    }

    public static Predicate<Base> hasUnspecifiedLicense() {
      return new Predicate<Base>() {
        public boolean apply(Base input) {
          return hasUnspecifiedLicense(input,false);
        }
      };
    }
    
    public static Predicate<Base> hasInheritedUnspecifiedLicense() {
      return new Predicate<Base>() {
        public boolean apply(Base input) {
          return hasUnspecifiedLicense(input,true);
        }
      };
    }
    
    public static Predicate<Base> hasLicense(final String uri) {
      return new Predicate<Base>() {
        public boolean apply(Base input) {
          return hasLicense(input,uri, false);
        }
      };
    }
    
    public static Predicate<Base> hasInheritedLicense(final String uri) {
      return new Predicate<Base>() {
        public boolean apply(Base input) {
          return hasLicense(input,uri, true);
        }
      };
    }
    
    public static Predicate<Base> hasLicense() {
      return new Predicate<Base>() {
        public boolean apply(Base input) {
          return hasLicense(input);
        }
      };
    }
    
    public static Predicate<Base> hasInheritedLicense() {
      return new Predicate<Base>() {
        public boolean apply(Base input) {
          return hasLicense(input,true);
        }
      };
    }
    
    public static boolean hasUnspecifiedLicense(Base base, boolean inherited) {
        return hasLicense(base, UNSPECIFIED_LICENSE, inherited);
    }

    public static boolean hasUnspecifiedLicense(Base base) {
        return hasUnspecifiedLicense(base, true);
    }

    public static boolean hasLicense(Base base, String iri, boolean inherited) {
        Iterable<Link> links = getLicense(base, inherited);
        IRI check = new IRI(iri);
        if (links != null) {
            for (Link link : links)
                if (link.getResolvedHref().equals(check))
                    return true;
        }
        return false;
    }

    public static boolean hasLicense(Base base, String iri) {
        return hasLicense(base, iri, true);
    }

    public static boolean hasLicense(Base base, boolean inherited) {
        Iterable<Link> links = getLicense(base, inherited);
        return !Iterables.isEmpty(links);
    }

    public static boolean hasLicense(Base base) {
        return hasLicense(base, true);
    }

    public static Link addUnspecifiedLicense(Base base) {
        if (hasUnspecifiedLicense(base, false))
            throw new IllegalStateException("Unspecified license already added");
        if (hasLicense(base, false))
            throw new IllegalStateException("Other licenses are already added.");
        return addLicense(base, UNSPECIFIED_LICENSE);
    }

    public static Link addLicense(Base base, String iri) {
        return addLicense(base, iri, null, null, null);
    }

    public static Link addLicense(Base base, String iri, String title) {
        return addLicense(base, iri, null, title, null);
    }

    public static Link addLicense(Base base, String iri, String type, String title, String hreflang) {
        if (hasLicense(base, iri, false))
            throw new IllegalStateException("License '" + iri + "' has already been added");
        if (hasUnspecifiedLicense(base, false))
            throw new IllegalStateException("Unspecified license already added");
        if (base instanceof Source) {
            return ((Source)base).addLink((new IRI(iri)).toString(), Link.REL_LICENSE, type, title, hreflang, -1);
        } else if (base instanceof Entry) {
            return ((Entry)base).addLink((new IRI(iri)).toString(), Link.REL_LICENSE, type, title, hreflang, -1);
        }
        return null;
    }

}
