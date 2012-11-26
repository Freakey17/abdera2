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
package org.apache.abdera2.activities.model;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.apache.abdera2.activities.extra.Difference;
import org.apache.abdera2.activities.extra.Extra;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.misc.Pair;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.common.selector.AbstractSelector;

import static org.apache.abdera2.common.misc.MoreFunctions.*;
import static com.google.common.base.Preconditions.*;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import static com.google.common.collect.Maps.filterEntries;

/**
 * Root of the Activity Streams object hierarchy, provides the core property
 * management and can be used to represent simple, untyped objects.
 */
@SuppressWarnings("unchecked")
public class ASBase 
  implements Iterable<String>, Cloneable, Serializable {

  private static final long serialVersionUID = 43257542522967L;
  
  public final static Logger log = Logger.getLogger(ASBase.class.getName());
  
  public static ASBuilder make() {
    return new ASBuilder();
  }
  
  public static ASBuilder make(boolean experimental) {
    ASBuilder builder = make();
    return experimental ?
      builder.experimental() :
      builder;
  }
  
  public static class ASBuilder extends Builder<ASBase,ASBuilder> {
    public ASBuilder() {
      super(ASBase.class,ASBuilder.class);
    }
    public ASBuilder(Map<String,Object> map) {
      super(map,ASBase.class,ASBuilder.class);
    }    
  }
  
  public static <X extends ASBase, M extends Builder<X,M>>Function<Object[],M> createBuilder(
      final Class<M> _m) {
      return new Function<Object[],M>() {
        public M apply(Object[] input) {
          try {
            if (input != null) {
              Constructor<M> c = _m.getConstructor(Map.class);
              c.setAccessible(true);
              return c.newInstance(input);
            } else {
              return _m.newInstance();
            }
          } catch (Throwable t) {
            throw ExceptionHelper.propogate(t);
          }
        }
      };
    }
  
  public static abstract class Builder<X extends ASBase, M extends Builder<X,M>>
    implements Supplier<X> {

    protected final ImmutableMap.Builder<String,Object> map = 
      ImmutableMap.builder();
    private final Function<Object[],X> con;
    private final Function<Object[],M> bld;
    private boolean experimental = false; // true if experimental extensions have been enabled on this builder
    private ImmutableMultimap.Builder<String, IRI> links = ImmutableMultimap.builder();
    private boolean y = false;
    
    protected Builder(Class<X> _class, Class<M> _builder) {
      con = createInstance(_class, Map.class);
      bld = createBuilder(_builder);
    }
        
    protected Builder(Map<String,Object> map,Class<X> _class, Class<M> _builder) {
      this(_class,_builder);
      this.map.putAll(map);
    }
    
    /**
     * Enables experimental features on the builder. Abdera supports a number
     * of non-standard, experimental features such as object reactions, $rel
     * links and metadata conventions that are not a part of the core Activity
     * Streams specification. In order to use the builder to work with these
     * extensions, you must first call the experimental() method on the builder
     * to enable them. 
     */
    public M experimental() {
      log.warning(
        String.format(
          "Non-standard, experimental features have been enabled in a builder instance. [%s]", 
          getClass().getSimpleName()));
      this.experimental = true;
      return (M)this;
    }
    
    protected boolean isExperimentalEnabled() {
      return experimental;
    }
    
    protected Object val(Object val) {
      if (val == null) return null;
      else if (val instanceof Supplier)
        return val(((Supplier<?>)val).get());
      else if (val instanceof Optional)
        return val(((Optional<?>)val).get());
      else if (val instanceof Future) {
        try {
          return val(((Future<?>)val).get());
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      } else if (val instanceof Callable) {
        try {
          return val(((Callable<?>)val).call());
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      } else if (val instanceof Reference) {
        return val(((Reference<?>)val).get());
      } else return val;
    }
    
    public M set(String name, Object val) {
      val = val(val);
      if (val != null)
        map.put(name,val);
      return (M)this;
    }
    public M set(Pair<String,? extends Object> pair) {
      if (pair == null) return (M)this;
      return set(pair.first(),pair.second());
    }
    public M set(Iterable<Pair<String,? extends Object>> pairs) {
      if (pairs == null) return (M)this;
      for (Pair<String,? extends Object> pair : pairs)
        set(pair);
      return (M)this;
    }
    public M set(Pair<String,? extends Object>... pairs) {
      if (pairs == null) return (M)this;
      for (Pair<String,? extends Object> pair : pairs)
        set(pair);
      return (M)this;
    }
    public M set(Map<String,? extends Object> map) {
      if (map == null) return (M)this;
      for (Map.Entry<String,? extends Object> entry : ImmutableMap.copyOf(map).entrySet())
        set(entry);
      return (M)this;
    }
    public M set(Map.Entry<String, ? extends Object> entry) {
      if (entry == null) return (M)this;
      return set(entry.getKey(),entry.getValue());
    }
    public M set(ASBase other) {
      for (String field : other) 
        set(field,other.getProperty(field));
      return (M)this;
    }
    public M lang(String lang) {
      return lang(new Lang(lang));
    }
    public M lang(Lang lang) {
      set("@language",lang);
      return (M)this;
    }
    public M base(String iri) {
      return base(new IRI(checkNotNull(iri)));
    }
    public M base(IRI iri) {
      set("@base",iri);
      return (M)this;
    }
    /**
     * Add a new $rel style link field to the object. This is an
     * experimental feature. You must call experimental() before
     * calling this method
     */
    public M link(String rel, IRI url) {
      checkState(isExperimentalEnabled(),"Experimental features not yet enabled. Call experimental() first.");
      y = true;
      links.put(rel, url);
      return (M)this;
    }

    /**
     * Add a new $rel style link field to the object. This is an
     * experimental feature. You must call experimental() before
     * calling this method
     */
    public M link(String rel, String url) {
      return link(rel, new IRI(checkNotNull(url)));
    }
    /**
     * Add a new metadata field to the object. This is an 
     * experimental feature. You must call experimental() before
     * calling this method
     */
    public M meta(String token, Object val) {
      return meta(token,val,false);
    }
    /**
     * Add a new metadata field to the object. This is an 
     * experimental feature. You must call experimental() before
     * calling this method
     */
    public M meta(String token, Object val, boolean isPrivate) {
      checkState(isExperimentalEnabled(),"Experimental features not yet enabled. Call experimental() first.");
      set(String.format("%s%s",isPrivate?"_":"@",token),val);
      return (M)this;
    }
    protected void preGet() {
      if (y && isExperimentalEnabled()) {
        ImmutableMultimap<String,IRI> map = links.build();
        for (String key : map.keySet()) {
          Iterable<IRI> links = map.get(key);
          if (links == null) continue;
          int s = Iterables.size(links);
          set(String.format("$%s",key.toLowerCase()),
            s == 1 ? 
              links.iterator().next() : 
              links.iterator());
        }
      }
    }
    public X get() {
      preGet();
      return con.apply(array(map.build()));
    }
    public <N>N extend(Class<N> as) {
      checkArgument(as.isInterface(),"Extension is not an interface!");
      return (N) Extra.extendBuilder(this,as);
    }
    public M template() {
      return bld.apply(array(map.build()));
    }
  }

  protected final Map<String,Object> exts;
  private final transient Function<Object[],?> builder;
  
  public ASBase(Map<String,Object> map) {
    this.exts = map;
    this.builder = createBuilder(ASBuilder.class);
  }
  
  protected <X extends ASBase, M extends Builder<X,M>>ASBase(Map<String,Object> map, Class<M> _class, Class<X> _obj) {
    this.exts = ImmutableMap.copyOf(map);
    this.builder = createBuilder(_class);
  }
  
  public Lang getLang() {
    Object lang = getProperty("@language");
    return lang instanceof Lang ? (Lang)lang : new Lang(lang.toString());
  }
  
  public IRI getBase() {
    Object base = getProperty("@base");
    return base instanceof IRI ? (IRI)base : new IRI(base.toString());
  }
  
  public <T>T getProperty(String name) {
    return (T)exts.get(name);
  }
  
  protected int getPropertyInt(String name) {
    Object obj = exts.get(name);
    if (obj instanceof Integer)
      return (Integer)obj;
    else return Integer.parseInt(obj.toString());
  }
  
  /**
   * Return the value of the named property, using the specified
   * Transform Function to translate the properties value
   */
  public <T,R>R getProperty(String name, Function<T,R> transform) {
    return (R)transform.apply(this.<T>getProperty(name));
  }
  
  /**
   * Return a listing of all the properties in this object
   */
  public Iterator<String> iterator() {
    return exts.keySet().iterator();
  }
  
  public <X extends ASBase, M extends Builder<X,M>>M template() {
    return (M)builder.apply(array(exts));
  }
  
  public <X extends ASBase, M extends Builder<X,M>>M template(Selector<Map.Entry<String,Object>> predicate) {
    return (M)builder.apply(array(filterEntries(exts, predicate)));
  }
  
  public <X extends ASBase, M extends Builder<X,M>>M templateWith(ASBase other) {
    M builder = this.<X,M>template(withoutFields(other));
    for (String field : other)
      builder.set(field,other.getProperty(field));
    return builder;
  }
  
  public <X extends ASBase, M extends Builder<X,M>>M templateWith(Map<String,Object> other) {
    ImmutableMap<String,Object> copy = 
      other instanceof ImmutableMap ? 
        (ImmutableMap<String,Object>)other : 
        ImmutableMap.copyOf(other);
    M builder = this.<X,M>template(withoutFields(copy.keySet()));
    for (Map.Entry<String,Object> entry : copy.entrySet())
      builder.set(entry.getKey(),entry.getValue());
    return builder;
  }
  
  public static final Selector<Map.Entry<String, Object>> withAllFields = 
    new AbstractSelector<Map.Entry<String, Object>>(){
      public boolean select(Object item) {
        return true;
      } 
  };
  
  public static final Selector<Map.Entry<String, Object>> withNoFields = 
    new AbstractSelector<Map.Entry<String, Object>>(){
      public boolean select(Object item) {
        return false;
      } 
  };

  public static Selector<Map.Entry<String, Object>> withFields(Iterable<String> names) {
    final ImmutableSet<String> list = ImmutableSet.copyOf(names);
    return new AbstractSelector<Map.Entry<String, Object>>() {
      public boolean select(Object item) {
        Map.Entry<String,Object> entry = (Entry<String, Object>) item;
        return list.contains(entry.getKey());
      }
    };
  }
  
  public static Selector<Map.Entry<String, Object>> withFields(String... names) {
    final ImmutableSet<String> list = ImmutableSet.copyOf(names);
    return new AbstractSelector<Map.Entry<String, Object>>() {
      public boolean select(Object item) {
        Map.Entry<String,Object> entry = (Entry<String, Object>) item;
        return list.contains(entry.getKey());
      }
    };
  }
  
  public static Selector<Map.Entry<String, Object>> withoutFields(Iterable<String> names) {
    final ImmutableSet<String> list = ImmutableSet.copyOf(names);
    return new AbstractSelector<Map.Entry<String, Object>>() {
      public boolean select(Object item) {
        Map.Entry<String,Object> entry = (Entry<String, Object>) item;
        return !list.contains(entry.getKey());
      }
    };
  }
  
  public static Selector<Map.Entry<String, Object>> withoutFields(String... names) {
    final ImmutableSet<String> list = ImmutableSet.copyOf(names);
    return new AbstractSelector<Map.Entry<String, Object>>() {
      public boolean select(Object item) {
        Map.Entry<String,Object> entry = (Entry<String, Object>) item;
        return !list.contains(entry.getKey());
      }
    };
  }
  
  @Override
  public int hashCode() {
    return genHashCode(1,exts);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;    
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ASBase other = (ASBase) obj;
    if (exts == null) {
      if (other.exts != null)
        return false;
    } else if (!exts.equals(other.exts))
      return false;
    return true;
  }

  /** 
   * if we already implement the type, just return 
   * a cast to that type... otherwise, create a 
   * new instance and copy all the properties over 
   **/
  public <T extends ASBase>T as(Class<T> type) {
    try {
      if (type.isAssignableFrom(this.getClass()))
        return type.cast(this);
      return createInstance(type,Map.class).apply(array(exts));
    } catch (Throwable t) {
      throw ExceptionHelper.propogate(t);
    }
  }
  
  /** 
   * if we already implement the type, just return 
   * a cast to that type... otherwise, create a 
   * new instance and copy all the properties over 
   **/
  public <T extends ASBase>T as(Class<T> type,Selector<Map.Entry<String, Object>> filter) {
    try {
      return createInstance(type,Map.class).apply(array(filterEntries(exts,filter)));
    } catch (Throwable t) {
      throw ExceptionHelper.propogate(t);
    }
  }
  
  @SuppressWarnings("rawtypes")
  public <T extends ASBase>T as(Class<T> type, ASBase other) {
    Builder builder = 
      as(type,withoutFields(other))
        .template();
    for (String field : other)
        builder.set(field,other.getProperty(field));
    return (T)builder.get();
  }
  
  @SuppressWarnings("rawtypes")
  public <T extends ASBase>T as(Class<T> type, Map<String,Object> other) {
    ImmutableMap<String,Object> copy = 
      other instanceof ImmutableMap ?
        (ImmutableMap<String,Object>)other :
        ImmutableMap.copyOf(other);
    Builder builder = as(type,withoutFields(copy.keySet()))
      .template();
    for (Entry<String,Object> entry : copy.entrySet())
      builder.set(entry.getKey(),entry.getValue());
    return (T)builder.get();
  }
  
  /**
   * Returns this object wrapped with the specified interface.
   * The argument MUST be an interface. This is used as a means
   * of extending the object in a type-safe manner. Instead of 
   * calling getProperty("foo"), you can define an 
   * extension interface with the methods getFoo()
   */
  public <T>T extend(Class<T> as) {
    checkArgument(as.isInterface(),"Extension is not an interface!");
    return (T) Extra.extend(this,as);
  }
 
  public boolean has(String name) {
    return exts.containsKey(name);
  }
  
  public String toString() {
    return IO.get().write(this);
  }
  
  public void writeTo(IO io, Writer out) {
    io.write(this,out);
  }
  
  public void writeTo(IO io, OutputStream out, String charset) {
    io.write(this,out,charset);
  }
  
  public void writeTo(IO io, OutputStream out) {
    io.write(this,out,"UTF-8");
  }
  
  public void writeTo(Writer out) {
    IO.get().write(this,out);
  }
  
  public void writeTo(Writer out, TypeAdapter<?>... adapters) {
    IO.get(adapters).write(this,out);
  }
  
  public void writeTo(OutputStream out, String charset) {
    try {
      OutputStreamWriter writer = 
        new OutputStreamWriter(out,charset);
      writeTo(writer);
      writer.flush();
    } catch (Throwable t) {}
  }

  public void writeTo(OutputStream out, String charset, TypeAdapter<?>... adapters) {
    try {
      OutputStreamWriter writer =
        new OutputStreamWriter(out,charset);
      writeTo(writer,adapters);
      writer.flush();
    } catch (Throwable t) {}
  }
  
  public void writeTo(OutputStream out) {
    writeTo(out,"UTF-8");
  }

  public void writeTo(OutputStream out, TypeAdapter<?>... adapters) {
    writeTo(out,"UTF-8",adapters);
  }
      
  protected static <T>Iterable<T> checkEmpty(Iterable<T> i) {
    return i == null ?
      Collections.<T>emptySet() : i;
  }
  
  @SuppressWarnings("rawtypes")
  /**
   * ASBase.clone() differs from the standard implementation of 
   * Object.clone() in that it uses the ASBase objects template()
   * method to create a new immutable copy of this object.
   */
  public Object clone() throws CloneNotSupportedException {
    return this.<ASBase,Builder>template().get();
  }
  
  public Map<String,Object> toMap() {
    return exts;
  }
  
  public Map<String,Object> toMap(
    Selector<Map.Entry<String,Object>> filter) {
      return Maps.filterEntries(exts, filter);
  }
  
  public Difference diff(ASBase other) {
    return Difference.diff(this,other);
  }
}


