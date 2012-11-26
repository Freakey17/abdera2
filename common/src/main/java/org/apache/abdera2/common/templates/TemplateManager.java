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
package org.apache.abdera2.common.templates;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.misc.MoreFunctions;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.*;
import static org.apache.abdera2.common.misc.MorePreconditions.*;
@SuppressWarnings("unchecked")
public class TemplateManager<T>
  implements Iterable<T> { 

  public static <M>Builder<M> make() {
    return new Builder<M>();
  }
  
  public static class Builder<T> {
    
    protected final ImmutableMap.Builder<T,Template> templates = 
      ImmutableMap.builder();
    protected boolean isiri;
    protected IRI base;
    protected final MultiContext.Builder defaultContexts = 
      MultiContext.make();
       
    public Builder() {}
    
    public <M extends Builder<T>>M add(T key, Template template) {
      this.templates.put(checkNotNull(key),checkNotNull(template));
      return (M)this;
    }
    
    public <M extends Builder<T>>M add(T key, String template) {
      return (M)add(checkNotNull(key), new Template(checkNotNull(template)));
    }
    
    public <M extends Builder<T>>M add(T key, Object template) {
      checkArgumentTypes(checkNotNull(template),Map.class,Collection.class);
      if (template instanceof Supplier)
        return (M) add(checkNotNull(key),((Supplier<?>)template).get());
      Template temp = 
        template instanceof Template ?
          (Template)template :
        new Template(template);
      return (M)add(key, temp);
    }
    
    public <M extends Builder<T>>M add(Map<T,Object> templates) {
      for (Map.Entry<T,Object> entry : checkNotNull(templates).entrySet())
        add(entry.getKey(),entry.getValue());
      return (M)this;
    }
    
    public <M extends Builder<T>>M asIri() {
      this.isiri = true;
      return (M)this;
    }
    
    public <M extends Builder<T>>M withDefaults(Context context) {
      this.defaultContexts.with(checkNotNull(context));
      return (M)this;
    }
    
    public <M extends Builder<T>>M withDefaults(MapContext context) {
      this.defaultContexts.with(checkNotNull(context));
      return (M)this;
    }
    
    public <M extends Builder<T>>M withDefaults(Map<String,Object> map) {
      this.defaultContexts.with(new MapContext(checkNotNull(map)));
      return (M)this;
    }
    
    public <M extends Builder<T>>M withDefaults(Object context) {
      this.defaultContexts.with(new ObjectContext(checkNotNull(context)));
      return (M)this;
    }
    
    public <M extends Builder<T>>M withBase(IRI iri) {;
      this.base = checkNotNull(iri);
      return (M)this;
    }
    
    public <M extends Builder<T>>M withBase(String iri) {
      return (M)withBase(new IRI(checkNotNull(iri)));
    }
    
    public TemplateManager<T> get() {
      return new TemplateManager<T>(
        templates.build(),isiri,base,defaultContexts.get());
    }
  }
  
  private final ImmutableMap<T,Template> templates;
  private final boolean isiri;
  private final IRI base;
  private final Context contextDefaults;

  protected TemplateManager(
    ImmutableMap<T,Template> templates, 
    boolean isiri, 
    IRI base, 
    Context contextDefaults) {
      this.templates = templates;
      this.isiri = isiri;
      this.base = base;
      this.contextDefaults = contextDefaults;
  }
    
  public Context getDefaultContext() {
    return this.contextDefaults;
  }
  
  public String expandAndResolve(T key, Object object, String base) {
    checkNotNull(key);
    checkNotNull(object);
    checkNotNull(base);
    IRI iri = expandAndResolve(key,object,new IRI(base));
    return iri != null ? iri.toString() : null;
  }
  
  public IRI expandAndResolve(T key, Object object, IRI base) {
    checkNotNull(key);
    checkNotNull(object);
    checkNotNull(base);
    String ex = expand(key,object);
    return ex != null ? 
        base == null ? 
            new IRI(ex) : 
            base.resolve(ex) : 
        null;    
  }
  
  public IRI expandAndResolve(T key, Object object) {
    return expandAndResolve(key,object,base);
  }
 
  public String expandAndResolve(T key, Context context, String base) {
    IRI iri = expandAndResolve(key,context,new IRI(base));
    return iri != null ? iri.normalize().toString() : null;
  }
  
  public IRI expandAndResolve(T key, Context context, IRI base) {
    String ex = expand(key,context);
    return ex != null ? 
        base == null ? 
            new IRI(ex) : 
            base.resolve(ex) : 
        null;    
  }
  
  public IRI expandAndResolve(T key, Context context) {
    return expandAndResolve(key,context,base);    
  }
  
  public String expand(T key, Object object) {
    checkNotNull(key);
    checkNotNull(object);
    if (!templates.containsKey(key))
      return null;
    Template template = templates.get(key);
    return template.expand(_wrap(_innerContext(object,isiri),contextDefaults));
  }
  
  public String expand(T key) {
    checkNotNull(key);
    checkNotNull(contextDefaults);
    return expand(key,contextDefaults);
  }
  
  public String expand(T key, Context context) {
    checkNotNull(key);
    checkNotNull(context);
    if (!templates.containsKey(key))
      return null;
    Template template = templates.get(key);
    return template.expand(_wrap(context,contextDefaults));
  }
  
  private static Context _innerContext(Object object, boolean isiri) {
    return object instanceof Context ? (Context)object : object instanceof Map
        ? new MapContext((Map<String,Object>)object, isiri) : new ObjectContext(object, isiri);
  }
  
  private static Context _wrap(Context context, Context contextDefaults) {
    return contextDefaults != null ? 
      new DefaultingContext(context,contextDefaults) : context;
  }

  public Iterator<T> iterator() {
    return templates.keySet().iterator();
  }

  @Override
  public int hashCode() {
    return MoreFunctions.genHashCode(1, contextDefaults, isiri, templates);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TemplateManager other = (TemplateManager) obj;
    if (contextDefaults == null) {
      if (other.contextDefaults != null)
        return false;
    } else if (!contextDefaults.equals(other.contextDefaults))
      return false;
    if (isiri != other.isiri)
      return false;
    if (templates == null) {
      if (other.templates != null)
        return false;
    } else if (!templates.equals(other.templates))
      return false;
    return true;
  }
 
  public Supplier<String> supplierFor(T key, Object context) {
    checkNotNull(key);
    checkNotNull(context);
    return new TMSupplier<T>(this,key,context);
  }
  
  public Supplier<String> supplierFor(T key) {
    checkNotNull(key);
    return new TMSupplier<T>(this,key,null);
  }
  
  private static class TMSupplier<T>
    implements Supplier<String> {
    private final T key;
    private final TemplateManager<T> tm;
    private final Object context;
    TMSupplier(TemplateManager<T> tm, T key, Object context) {
      this.key = key;
      this.tm = tm;
      this.context = context;
    }
    public String get() {
      return context != null ?
        tm.expand(key,context) : 
        tm.expand(key);
    }
  }
  
  public static <T>TemplateManager<T> fromMap(Map<T,Object> map) {
    checkNotNull(map);
    Builder<T> b = make();
    b.add(map);
    return b.get();
  }
}
