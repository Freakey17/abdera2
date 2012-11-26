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
package org.apache.abdera2.factory;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ElementWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.google.common.base.Preconditions.*;

/**
 * <p>
 * Provides a base implementation for ExtensionFactory instances. By extending this, specific extension factories need
 * only to associate a QName with an implementation class, e.g.,
 * </p>
 * 
 * <pre>
 *  public class MyExtensionFactory
 *    extends AbstractExtensionFactory {
 * 
 *    private String NS = "http://example.org/foo/ns"; 
 *    private QName FOO = new QName(NS, "foo");
 * 
 *    public MyExtensionFactory() {
 *      super(NS);
 *      addImpl(FOO, Foo.class);
 *    }
 *  }
 *  
 *  public class Foo extends ElementWrapper { ... }
 * </pre>
 * 
 * <p>As an alternative to manually calling the addImpl method to register 
 * implementation classes, Annotations can be used by subclasses of the 
 * AbstractExtensionFactory class.</p>
 * 
 * <pre>@Namespace({"http://example.org/foo/ns"})
 @Impls({@Impl(Foo.class)})
 public final class MyExtensionFactory 
 extends AbstractExtensionFactory {}
 <br />
@QName(value="foo", ns="http://example.org/foo/ns")
public class Foo extends ElementWrapper { ... }
</pre>
 * 
 */
public abstract class AbstractExtensionFactory 
    implements ExtensionFactory {

    private final static Log log = LogFactory.getLog(AbstractExtensionFactory.class);
  
    /** The set of namespaces supported by this factory **/
    private final Set<String> namespaces = new HashSet<String>();
    
    /** A mapping of QNames to MimeTypes. **/
    private final Map<QName, String> mimetypes = new HashMap<QName, String>();
    
    /** The mapping of QNames to implementation classes **/
    private final Map<QName, Constructor<? extends ElementWrapper>> impls =
        new HashMap<QName, Constructor<? extends ElementWrapper>>();

    /**
     * The default constructor will automatically search the subclass
     * declaration for the appropriate Namespace and Impls annotations.
     */
    protected AbstractExtensionFactory() {
      this.namespaces.addAll(AnnoUtil.getNamespaces(this));
      addImpls(this,impls);
    }
    
    /**
     * Constructor that first calls the default no-arg constructor then
     * appends the additional zero or more namespaces.
     */
    protected AbstractExtensionFactory(String... namespaces) {
        this();
        for (String ns : namespaces)
            this.namespaces.add(ns);
    }

    @SuppressWarnings("unchecked")
    public <T extends Element> T getElementWrapper(Element internal) {
        T t = null;
        QName qname = internal.getQName();
        Constructor<? extends ElementWrapper> con = impls.get(qname);        
        if (con != null) {
          try {
            t = (T)con.newInstance(new Object[] {internal});
          } catch (Throwable e) {}
        }
        return t != null ? t : (T)internal;
    }

    /**
     * Associate a MIME media type for the specific QName
     */
    protected AbstractExtensionFactory addMimeType(QName qname, String mimetype) {
        mimetypes.put(qname, mimetype);
        return this;
    }

    /**
     * Add the class to the implementation map. The QName annotation MUST
     * be set to provide the QName for the class or the method will fail 
     * with an IllegalArgumentException.
     */
    protected AbstractExtensionFactory addImpl(Class<? extends ElementWrapper> impl) {
      QName qname = AnnoUtil.getQName(checkNotNull(impl));
      addImpl(checkNotNull(qname),impl);
      return this;
    }
    
    /**
     * Associate a QName with an implementation class. This version of the
     * method ignores the QName annotation and uses the provided qname
     */
    protected AbstractExtensionFactory addImpl(QName qname, Class<? extends ElementWrapper> impl) {
        checkNotNull(qname);
        checkNotNull(impl);
        log.debug(String.format("Adding implementation for [%s] : %s",qname.toString(),impl));
        impls.put(qname, 
          checkNotNull(constructor(impl), 
            "Missing Element Wrapper Constructor"));
        return this;
    }
   
    public <T extends Base> String getMimeType(T base) {
        Element element =
            base instanceof Element ? 
              (Element)base : 
              base instanceof Document ? 
                  ((Document<?>)base).getRoot() : 
                  null;
        QName qname = element != null ? element.getQName() : null;
        return element != null && qname != null ? mimetypes.get(qname) : null;
    }

    public Iterable<String> getNamespaces() {
        return namespaces;
    }

    public boolean handlesNamespace(String namespace) {
        return namespaces.contains(namespace);
    }

    private static void addImpls(Object obj, Map<QName, Constructor<? extends ElementWrapper>> map) {
      if (obj == null) return;
      Class<?> _class = obj instanceof Class ? (Class<?>)obj : obj.getClass();
      if (_class.isAnnotationPresent(Impls.class)) {
        log.debug("@Impls annotation found... processing");
        Impls impls = _class.getAnnotation(Impls.class);
        Impl[] imps = impls.value();
        for (Impl impl : imps) {
          log.debug(String.format("Processing >> %s",impl.value().getName()));
          QName qname = AnnoUtil.qNameFromAnno(impl.qname());
          Class<? extends ElementWrapper> _impl = impl.value();
          if (qname == null) {
            if (_impl.isAnnotationPresent(org.apache.abdera2.common.anno.QName.class)) {
              org.apache.abdera2.common.anno.QName qn = 
                _impl.getAnnotation(org.apache.abdera2.common.anno.QName.class);
              qname = AnnoUtil.qNameFromAnno(qn);
            }
          }
          if (qname != null) {
            log.debug(String.format("  Discovered QName: %s", qname.toString()));
            Constructor<? extends ElementWrapper> con = 
              constructor(_impl);
            if (con != null) {
              map.put(qname,con);
            } else log.debug("  An appropriate ElementWrapper constructor could not be found! Ignoring implementation class");
          } else log.debug("  A QName could not be found. Ignoring implementation class");
        }
      }
    }
    
    private static Constructor<? extends ElementWrapper> constructor(Class<? extends ElementWrapper> _class) {
      try {
        return _class.getConstructor(new Class[] {Element.class});
      } catch (Throwable t) {
        log.error("Error retrieving constructor...",t);
        return null;
      }
    }
    
    @Override
    public int hashCode() {
      return MoreFunctions.genHashCode(1, impls, mimetypes, namespaces);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      AbstractExtensionFactory other = (AbstractExtensionFactory) obj;
      if (impls == null) {
        if (other.impls != null)
          return false;
      } else if (!impls.equals(other.impls))
        return false;
      if (mimetypes == null) {
        if (other.mimetypes != null)
          return false;
      } else if (!mimetypes.equals(other.mimetypes))
        return false;
      if (namespaces == null) {
        if (other.namespaces != null)
          return false;
      } else if (!namespaces.equals(other.namespaces))
        return false;
      return true;
    }



    /**
     * Specifies a mapping between a QName and an implementation class
     */
    @Retention(RUNTIME)
    @Target( {TYPE})
    public static @interface Impl {
      org.apache.abdera2.common.anno.QName qname() 
        default @org.apache.abdera2.common.anno.QName("");
      Class<? extends ElementWrapper> value();
    }

    /**
     * A collection of Impl annotation
     */
    @Retention(RUNTIME)
    @Target({TYPE})
    public static @interface Impls {
      Impl[] value();
    }
}
