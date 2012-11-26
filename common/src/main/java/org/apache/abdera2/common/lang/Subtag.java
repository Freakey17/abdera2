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
package org.apache.abdera2.common.lang;

import java.io.Serializable;
import java.util.Locale;

import org.apache.abdera2.common.misc.MoreFunctions;

/**
 * A Language Tab Subtag. Instances are immutable and safe to use by
 * multiple threads.
 */
public final class Subtag 
  implements Serializable, Comparable<Subtag> {

    private static final long serialVersionUID = -4496128268514329138L;

    static Type wild(String name, Type other) {
      return name.equals("*") ? Type.WILDCARD : other;
    }
    
    public static Subtag language(String name) {
      return new Subtag(wild(name,Type.LANGUAGE), name);
    }
    
    public static Subtag language(String name, Subtag prev) {
      return new Subtag(wild(name,Type.LANGUAGE), name, prev);
    }
    
    public static Subtag extlang(String name) {
      return new Subtag(wild(name,Type.EXTLANG), name);
    }
    
    public static Subtag extlang(String name, Subtag prev) {
      return new Subtag(wild(name,Type.EXTLANG), name, prev);
    }
    
    public static Subtag script(String name) {
      return new Subtag(wild(name,Type.SCRIPT), name);
    }
    
    public static Subtag script(String name, Subtag prev) {
      return new Subtag(wild(name,Type.SCRIPT), name, prev);
    }
    
    public static Subtag region(String name) {
      return new Subtag(wild(name,Type.REGION), name);
    }
    
    public static Subtag region(String name, Subtag prev) {
      return new Subtag(wild(name,Type.REGION), name, prev);
    }
    
    public static Subtag variant(String name) {
      return new Subtag(wild(name,Type.VARIANT), name);
    }
    
    public static Subtag variant(String name, Subtag prev) {
      return new Subtag(wild(name,Type.VARIANT), name, prev);
    }
    
    public static Subtag singleton(String name) {
      return new Subtag(wild(name,Type.SINGLETON), name);
    }
    
    public static Subtag singleton(String name, Subtag prev) {
      return new Subtag(wild(name,Type.SINGLETON), name, prev);
    }
    
    public static Subtag extension(String name) {
      return new Subtag(wild(name,Type.EXTENSION), name);
    }
    
    public static Subtag extension(String name, Subtag prev) {
      return new Subtag(wild(name,Type.EXTENSION), name, prev);
    }
    
    public static Subtag privateuse(String name) {
      return new Subtag(wild(name,Type.PRIVATEUSE), name);
    }
    
    public static Subtag privateuse(String name, Subtag prev) {
      return new Subtag(wild(name,Type.PRIVATEUSE), name, prev);
    }
    
    public static Subtag grandfathered(String name) {
      return new Subtag(wild(name,Type.GRANDFATHERED), name);
    }
    
    public static Subtag grandfathered(String name, Subtag prev) {
      return new Subtag(wild(name,Type.GRANDFATHERED), name, prev);
    }
   
    public static Subtag simple(String name) {
      return new Subtag(wild(name,Type.SIMPLE), name);
    }
    
    public static Subtag simple(String name, Subtag prev) {
      return new Subtag(wild(name,Type.SIMPLE), name, prev);
    }
    
    public enum Type {
      LANGUAGE,
      EXTLANG,
      SCRIPT,
      REGION,
      VARIANT,
      SINGLETON,
      EXTENSION,
      PRIVATEUSE,
      GRANDFATHERED,
      WILDCARD,
      SIMPLE
    }

    private final Type type;
    private final String name;
    private Subtag prev;
    private Subtag next;
    private Subtag root;

    public Subtag(
      Type type, 
      String name) {
      this(
        type, 
        name, 
        null);
    }

    Subtag() {
      this(
        Type.WILDCARD, 
        "*");
    }

    /**
     * Create a Subtag
     */
    public Subtag(
      Type type, 
      String name, 
      Subtag prev) {
      this.type = type;
      this.name = name;
      this.prev = prev;
      if (prev != null) {
        prev.setNext(this);
        this.root = prev.root();
      } else this.root = null;
    }

    Subtag(Subtag copy, Subtag parent) {
      this(copy.type(),copy.name(),parent);
    }
    
    Subtag(
      Type type, 
      String name, 
      Subtag prev, 
      Subtag next,
      Subtag root) {
      this.type = type;
      this.name = name;
      this.prev = prev;
      this.next = next;
      this.root = root;
    }

    public Subtag root() {
      return root != null ? root : this;
    }
    
    public Type type() {
        return type;
    }

    public String name() {
        return toString();
    }

    public Subtag previous() {
      return prev;
    }

    void setPrevious(Subtag prev) {
      this.prev = prev;
    }
    
    void setNext(Subtag next) {
      this.next = next;
      if (next != null)
          next.setPrevious(this);
    }

    public Subtag next() {
      return next;
    }

    public String toString() {
      switch (type) {
        case LANGUAGE:
          return name.toLowerCase(Locale.US);
        case REGION:
          return name.toUpperCase(Locale.US);
        case SCRIPT:
          return toTitleCase(name);
        default:
          return name.toLowerCase(Locale.US);
      }
    }

    private static String toTitleCase(String string) {
      if (string == null || string.length() == 0)
        return string;
      char[] chars = string.toLowerCase(Locale.US).toCharArray();
      chars[0] = Character.toTitleCase(chars[0]);
      return new String(chars);
    }

    public int hashCode() {
      return MoreFunctions.genHashCode(
        1, name.toLowerCase(Locale.US), type);
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Subtag other = (Subtag)obj;
        if (other.type() == Type.WILDCARD || type() == Type.WILDCARD)
            return true;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equalsIgnoreCase(other.name))
            return false;
        if (other.type() == Type.SIMPLE || type() == Type.SIMPLE)
            return true;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    private boolean isX() {
      return "x".equalsIgnoreCase(name());
    }
    
    public boolean isSingleton() {
      return type == Type.SINGLETON;
    }
    
    public boolean isExtension() {
      return type == Type.EXTENSION;
    }
    
    public boolean isPrivateUse() {
      return type == Type.PRIVATEUSE;
    }
    
    boolean isExtensionOrPrivateUse() {
      return isExtension() || isPrivateUse();
    }
    
    public boolean isExtensionSingleton() {
      return isSingleton() && !isX();
    }
    
    public boolean isPrivateSingleton() {
      return isSingleton() && isX();
    }
        
    public SubtagSet extractExtensionGroup() {
      if (!isSingleton()) return null;
      Subtag c = this, p = root = new Subtag(this,null);
      while((c = c.next()) != null && c.isExtensionOrPrivateUse())
        p = new Subtag(c,p);
      return new SubtagSet(root) {
        private static final long serialVersionUID = 7508549925367514365L;        
      };
    }
    
    public static Subtag newWildcard() {
        return new Subtag();
    }
    
    public int compareTo(Subtag o) {
      int c = o.type.compareTo(type);
      return c != 0 ? c : o.name.compareTo(name);
  }
}
