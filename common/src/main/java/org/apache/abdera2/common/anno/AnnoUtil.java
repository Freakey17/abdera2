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
package org.apache.abdera2.common.anno;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.abdera2.common.misc.MoreFunctions;

import com.google.common.base.Equivalence;
import com.google.common.base.Predicate;

public final class AnnoUtil {

  private AnnoUtil() {}
  
  /**
   * Retrieves the value of the Name attribute from the specified 
   * item. If the item is an instance object, the name is pulled from it's
   * Class. If item is null, returns null.
   */
  public static String getName(Object item) {
    if (item == null) return null;
    Class<?> _class = 
      item instanceof Class ? (Class<?>)item :
      item.getClass();
    return _class.isAnnotationPresent(Name.class) ?
      _class.getAnnotation(Name.class).value() : 
      _class.getSimpleName().toLowerCase();
  }

  /**
   * Retrieve the default implementation for the specified Class.
   */
  public static String getDefaultImplementation(Class<?> _class) {
    if (_class == null) return null;
    String _default = null;
    if (_class.isAnnotationPresent(DefaultImplementation.class)) {
      DefaultImplementation di = 
        _class.getAnnotation(DefaultImplementation.class);
      _default = di.value();
    }
    return _default;
  }

  /**
   * Retrieve the Version annotation from the specified item
   */
  public static Version getVersion(Object item) {
    if (item == null) return null;
    Class<?> _class = 
      item instanceof Class ? (Class<?>)item :
      item.getClass();
    return _class.isAnnotationPresent(Version.class) ?
      _class.getAnnotation(Version.class) :
      null;
  }

  /**
   * Returns the Namespace URIs handled by this Extension Factory
   * 
   * @return A List of Namespace URIs Supported by this Extension
   */
  public static Set<String> getNamespaces(Object obj) {
    if (obj == null) return Collections.<String>emptySet();
    Class<?> _class = 
      obj instanceof Class ? (Class<?>)obj :
      obj.getClass();
    Set<String> ns = new HashSet<String>();
    if (_class.isAnnotationPresent(Namespace.class))
      for (String n : _class.getAnnotation(Namespace.class).value())
        ns.add(n);
    return Collections.unmodifiableSet(ns);
  }

  /**
   * Returns true if the given object "handles" the given namespace based
   * on values specified using the Namespace annotation
   */
  public static boolean handlesNamespace(String namespace, Object obj) {
    Set<String> set = getNamespaces(obj);
    return set.contains(namespace);
  }
  
  /**
   * Retrieve a javax.xml.namespace.QName from a class using the QName annotation.
   */
  public static QName qNameFromAnno(org.apache.abdera2.common.anno.QName impl) {
    if (impl == null) return null;
    QName result = null;
    String name = impl.value();
      String ns = impl.ns();
      String pfx = impl.pfx();
      if (pfx != null && pfx.length() > 0)
        result = new QName(ns,name,pfx);
      else if (ns != null && ns.length() > 0)
        result = new QName(ns,name);
      else if (name != null && name.length() > 0)
        result = new QName(name);
    return result;
  }
  
  /**
   * Retrieve a javax.xml.namespace.QName from an instance object using the 
   * QName annotation
   */
  public static QName getQName(Object obj) {
    if (obj == null) return null;
    Class<?> _class = 
      obj instanceof Class ? 
        (Class<?>)obj : obj.getClass();
    if (_class.isAnnotationPresent(org.apache.abdera2.common.anno.QName.class))
      return qNameFromAnno(_class.getAnnotation(org.apache.abdera2.common.anno.QName.class));
    return null;
  }
  
  public static Equivalence<Version> versionEquivalence() {
    return versionEquivalence(true);
  }
  
  public static Equivalence<Version> versionEquivalence(final boolean ignoreStatus) {
    return new Equivalence<Version>() {
      protected boolean doEquivalent(Version a, Version b) {
        if (!a.name().equalsIgnoreCase(b.name())) return false;
        if (!a.uri().equals(b.uri())) return false;
        if (!a.value().equals(b.value())) return false;
        if (!ignoreStatus && !a.status().equals(b.status())) return false;
        int cmp = a.major() - b.major();
        if(cmp == 0 && a.minor() > -1 && b.minor() > -1)
          cmp = a.minor() - b.minor();
        if(cmp == 0 && a.revision() > -1 && b.revision() > -1)
          cmp = a.revision() - b.revision();
        return cmp == 0;
      }
      protected int doHash(Version t) {
        return t.hashCode();
      }
    };
  }
  
  public static final Predicate<Version> equalOrGreater(
    final Version version) {
    return new Predicate<Version>() {
      public boolean apply(Version input) {
        int cmp = input.major() - version.major();
        if(cmp == 0)
          if(input.minor() > -1 && version.minor() > -1)
            cmp = input.minor() - version.minor();
          else
            cmp = input.minor();
        if(cmp == 0)
          if(input.revision() > -1 && version.revision() > -1)
            cmp = input.revision() - version.revision();
          else
            cmp = input.revision();
        return cmp <= 0;
      }
    };
  }
  
  public static final Comparator<Version> VERSION_COMPARATOR =
    new Comparator<Version>() {
      public int compare(Version v1, Version v2) {
        int cmp = v1.major() - v2.major();
        if(cmp == 0)
          cmp = v1.minor() - v2.minor();
        if(cmp == 0)
          cmp = v1.revision() - v2.revision();
        return cmp;
      }
  };

  
  public static final Version version(String value, String name, String uri) {
    return new VersionImpl(value,name,uri,0,0,0,Version.Status.STABLE);
  }
  
  public static final Version version(
    String value, 
    String name, 
    String uri, 
    int major, 
    int minor, 
    int revision,
    Version.Status status) {
    return new VersionImpl(
      value,
      name,
      uri,
      major,
      minor,
      revision,
      status);
  }
  
  private static class VersionImpl 
    implements Version {

      @Override
    public int hashCode() {
      return MoreFunctions.genHashCode(1,
        major,minor,name,revision,uri,value,status);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      VersionImpl other = (VersionImpl) obj;
      if (major != other.major)
        return false;
      if (minor != other.minor)
        return false;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      if (revision != other.revision)
        return false;
      if (uri == null) {
        if (other.uri != null)
          return false;
      } else if (!uri.equals(other.uri))
        return false;
      if (value == null) {
        if (other.value != null)
          return false;
      } else if (!value.equals(other.value))
        return false;
      if (status == null) {
        if (other.status != null)
          return false;
      } else if (!status.equals(other.status))
        return false;
      return true;
    }

      private final String value;
      private final String name;
      private final String uri;
      private final int major,minor,revision;
      private final Version.Status status;
      
      VersionImpl(
        String value, 
        String name, 
        String uri, 
        int major, 
        int minor, 
        int revision,
        Version.Status status) {
        this.value = value;
        this.name = name;
        this.uri = uri;
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.status = status;
      }
      
      public Class<? extends Annotation> annotationType() {
        return Version.class;
      }

      public String value() {
        return value;
      }

      public String name() {
        return name;
      }

      public String uri() {
        return uri;
      }

      public int major() {
        return major;
      }

      public int minor() {
        return minor;
      }

      public int revision() {
        return revision;
      }

      public Status status() {
        return status;
      }
      
  }
}
