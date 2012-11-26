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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.abdera2.activities.io.gson.GsonIO;
import org.apache.abdera2.common.anno.DefaultImplementation;
import org.apache.abdera2.common.misc.ExceptionHelper;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Primary interface for serializing/deserializing Activity objects. 
 * The write/parse operations on IO should be considered threadsafe, 
 * however, all initiatization of the IO should be completed prior 
 * to using the class to read or produce Activity Stream documents.
 * This means that all calls to addPropertyMapping and addObjectMapping
 * MUST be performed as part of the initialization code of the IO 
 * instance. Note that when using property mappings, conflicts may
 * occur when working with different objects that use different 
 * value types for similarly named properties, e.g. if you have 
 * mapped the property named "location" to a PlaceObject, but one
 * of the properties in your objects uses a string value for location, 
 * you will receive parse errors because the deserializer will
 * be unable to deserialize the property properly. The solution 
 * is to use two separate IO instances each configured with the 
 * appropriate property and object type mappings.
 */
@SuppressWarnings("unchecked")
public abstract class IO {

  @DefaultImplementation("org.apache.abdera2.activities.io.gson.GsonIO.Builder")
  public static abstract class Builder implements Supplier<IO> {
    
    protected ImmutableSet.Builder<TypeAdapter<?>> adapters = 
      ImmutableSet.builder();
    protected boolean autoclose;
    protected boolean prettyprint;
    protected String charset = "UTF-8";
    public Builder autoClose() {
      this.autoclose = true;
      return this;
    }
    public Builder prettyPrint() {
      this.prettyprint = true;
      return this;
    }
    public Builder charset(String charset) {
      this.charset = charset;
      return this;
    }
    
    public Builder adapter(TypeAdapter<?> adapter) {
      adapters.add(adapter);
      return this;
    }
    
    public Builder adapter(TypeAdapter<?>... adapters) {
      for (TypeAdapter<?> adapter : adapters)
        adapter(adapter);
      return this;
    }
    
    public Builder adapter(Iterable<TypeAdapter<?>> adapters) {
      for (TypeAdapter<?> adapter : adapters)
        adapter(adapter);
      return this;
    }
    
    /**
     * Adds a mapping of a property name to a specific value class. The 
     * serializer/deserializer will use this to select the appropriate 
     * type adapter for the property.
     */
    public abstract Builder property(String name, Class<?> _class);
    
    /**
     * Registers an appropriate objectType mapping for the object. This is
     * used to automatically select an appropriate class for individual 
     * "objectType" values.
     */
    @SuppressWarnings("rawtypes")
    public abstract <X extends ASObject.Builder>Builder object(Class<? extends X>... _class);
    
  }
  
  protected final boolean autoclose;
  protected final String charset;
  
  protected IO(Builder builder) {
    this.autoclose = builder.autoclose;
    this.charset = builder.charset;
  }
  
  /**
   * True if streams and writers should be automatically closed when
   * the IO instance is done with them. Applies to both reads and writes
   */
  public boolean getAutoClose() {
    return autoclose;
  }
  
  public String getDefaultCharset() {
    return charset;
  }
  
  public String write(
    ASBase base) {
    StringBuilder buf = 
      new StringBuilder();
    write(base, buf);
    return buf.toString();
  }
  
  public abstract void write(
    ASBase base, 
    Appendable writer);
  
  public void write(ASBase base, OutputStream out) {
    write(base,out,(String)null);
  }
  
  public void write(ASBase base, OutputStream out, String charset) {
    try {
      OutputStreamWriter writer = 
        new OutputStreamWriter(
          out,charset!=null?charset:this.charset);
      write(base,writer);
      writer.flush();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  
  public <T extends ASBase>T read(InputStream in, String charset) {
    try {
      return (T)read(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  
  public <T extends ASBase>T readAs(InputStream in, String charset, Class<T> _class) {
    return read(in,charset).as(_class);
  }
  
  public Activity readActivity(InputStream in, String charset) {
    try {
      return readActivity(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  public <T extends ASObject>Collection<T> readCollection(InputStream in, String charset) {
    try {
      return readCollection(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public <T extends ASObject> T readObject(InputStream in, String charset) {
    try {
      return (T)readObject(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  
  public <T extends ASObject>T readObjectAs(InputStream in, String charset, Class<T> _class) {
    return readObject(in,charset).as(_class);
  }
  
  public MediaLink readMediaLink(InputStream in, String charset) {
    try {
      return readMediaLink(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  public abstract <T extends ASBase>T read(Reader reader);
  public abstract <T extends ASBase>T read(String json);
  public abstract Activity readActivity(Reader reader);
  public abstract Activity readActivity(String json);
  public abstract <T extends ASObject>Collection<T> readCollection(Reader reader);
  public abstract <T extends ASObject>Collection<T> readCollection(String json);
  public abstract <T extends ASObject>T readObject(Reader reader);
  public abstract <T extends ASObject>T readObject(String json);
  public abstract MediaLink readMediaLink(Reader reader);
  public abstract MediaLink readMediaLink(String json);
  
  public <T extends ASBase>T readAs(Reader reader, Class<T> _class) {
    return read(reader).as(_class);
  }
  public <T extends ASBase>T readAs(String json, Class<T> _class) {
    return read(json).as(_class);
  }
  public <T extends ASObject>T readObjectAs(Reader reader, Class<T> _class) {
    return readObject(reader).as(_class);
  }
  public <T extends ASObject>T readObjectAs(String json, Class<T> _class) {
    return readObject(json).as(_class);
  }
  
  public static class CacheKey {
    private final int hash;
    public CacheKey(TypeAdapter<?>[] adapters) {
      this.hash = Arrays.hashCode(adapters);
    }
    public int hashCode() {
      return hash;
    }
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CacheKey other = (CacheKey) obj;
      if (hash != other.hash)
        return false;
      return true;
    }
  }
  
  private static final Map<CacheKey,IO> map = 
    new ConcurrentHashMap<CacheKey,IO>();
  
  private static synchronized IO get_cached(TypeAdapter<?>... adapters) {
    return map.get(new CacheKey(adapters));
  }
  
  private static synchronized void set_cached(IO io, TypeAdapter<?>... adapters) {
    map.put(new CacheKey(adapters),io);
  }

  public static Builder make() {
    return new GsonIO.Builder();
  }
  
  public static IO get(TypeAdapter<?>... adapters) { 
    IO io = get_cached(adapters);
    if (io == null) {
      io = new GsonIO.Builder().adapter(adapters).get();
      set_cached(io,adapters);
    }
    return io;
  }
  
  public void writeCollection(
    OutputStream out, 
    String charset,
    ASBase header,
    Iterable<ASObject> objects) {
    try {
      OutputStreamWriter outw = 
        new OutputStreamWriter(out,charset);
      writeCollection(outw,header,objects);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public abstract void writeCollection(
    Writer out,
    ASBase header,
    Iterable<ASObject> objects);
  
  public CollectionWriter getCollectionWriter(OutputStream out, String charset) {
    try {
      return getCollectionWriter(new OutputStreamWriter(out,charset));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public CollectionWriter getCollectionWriter(OutputStream out) {
    return getCollectionWriter(out, getDefaultCharset());
  }
  
  public abstract CollectionWriter getCollectionWriter(Writer out);
  
  
  
  
  
  public static interface Listener<X> {
    void onComplete(X item);
  }

  public <X extends ASBase>void read(
    final Reader reader, 
    final ExecutorService exec, 
    final Listener<X> listener) {
      process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) read(reader);
          }
        }, 
        listener);
  }
  
  public <X extends ASBase>Future<X> read(
    final Reader reader, 
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) read(reader);
          }
        });
  }
  
  public <X extends ASBase>void readAs(
    final Reader reader, 
    final Class<X> _class,
    final ExecutorService exec, 
    final Listener<X> listener) {
      process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) readAs(reader,_class);
          }
        }, 
        listener);
  }
  
  public <X extends ASBase>Future<X> readAs(
    final Reader reader, 
    final Class<X> _class,
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) readAs(reader,_class);
          }
        });
  }
  
  
  public <X extends ASBase>void read(
     
    final String reader, 
    final ExecutorService exec, 
    final Listener<X> listener) {
      process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) read(reader);
          }
        }, 
        listener);
  }
  
  public <X extends ASBase>Future<X> read(
     
    final String reader, 
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) read(reader);
          }
        });
  }
  
  public <X extends ASBase>void readAs(
     
    final String reader, 
    final Class<X> _class,
    final ExecutorService exec, 
    final Listener<X> listener) {
      process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) readAs(reader,_class);
          }
        }, 
        listener);
  }
  
  public <X extends ASBase>Future<X> readAs(
     
    final String reader, 
    final Class<X> _class,
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) readAs(reader,_class);
          }
        });
  }
  
  public <X extends ASBase>void read(
     
    final InputStream in,
    final String charset,
    final ExecutorService exec, 
    final Listener<X> listener) {
      process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) read(in,charset);
          }
        }, 
        listener);
  }
  
  public <X extends ASBase>Future<X> read(
     
    final InputStream in,
    final String charset,
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) read(in,charset);
          }
        });
  }
  
  public <X extends ASBase>void readAs(
     
    final InputStream in,
    final String charset,
    final Class<X> _class,
    final ExecutorService exec, 
    final Listener<X> listener) {
      process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) readAs(in,charset,_class);
          }
        }, 
        listener);
  }
  
  public <X extends ASBase>Future<X> readAs(
     
    final InputStream in,
    final String charset,
    final Class<X> _class,
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<X>() {
          public X call() throws Exception {
            return (X) readAs(in,charset,_class);
          }
        });
  }
  
  
  //---
  
  public void readActivity(
     
    final Reader reader, 
    final ExecutorService exec, 
    final Listener<Activity> listener) {
      process(
        exec,
        new Callable<Activity>() {
          public Activity call() throws Exception {
            return readActivity(reader);
          }
        }, 
        listener);
  }
  
  public Future<Activity> readActivity(
     
    final Reader reader, 
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<Activity>() {
          public Activity call() throws Exception {
            return readActivity(reader);
          }
        });
  }
  
  public void readActivity(
     
    final String reader, 
    final ExecutorService exec, 
    final Listener<Activity> listener) {
      process(
        exec,
        new Callable<Activity>() {
          public Activity call() throws Exception {
            return readActivity(reader);
          }
        }, 
        listener);
  }
  
  public Future<Activity> readActivity(
     
    final String reader, 
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<Activity>() {
          public Activity call() throws Exception {
            return readActivity(reader);
          }
        });
  }
    
  public void readActivity(
     
    final InputStream in,
    final String charset,
    final ExecutorService exec, 
    final Listener<Activity> listener) {
      process(
        exec,
        new Callable<Activity>() {
          public Activity call() throws Exception {
            return readActivity(in,charset);
          }
        }, 
        listener);
  }
  
  public Future<Activity> readActivity(
     
    final InputStream in,
    final String charset,
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<Activity>() {
          public Activity call() throws Exception {
            return readActivity(in,charset);
          }
        });
  }
 
  //---

  public void readMediaLink(
     
    final Reader reader, 
    final ExecutorService exec, 
    final Listener<MediaLink> listener) {
      process(
        exec,
        new Callable<MediaLink>() {
          public MediaLink call() throws Exception {
            return readMediaLink(reader);
          }
        }, 
        listener);
  }
  
  public Future<MediaLink> readMediaLink(
    final Reader reader, 
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<MediaLink>() {
          public MediaLink call() throws Exception {
            return readMediaLink(reader);
          }
        });
  }
  
  public void readMediaLink(
    final String reader, 
    final ExecutorService exec, 
    final Listener<MediaLink> listener) {
      process(
        exec,
        new Callable<MediaLink>() {
          public MediaLink call() throws Exception {
            return readMediaLink(reader);
          }
        }, 
        listener);
  }
  
  public Future<MediaLink> readMediaLink(
    final String reader, 
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<MediaLink>() {
          public MediaLink call() throws Exception {
            return readMediaLink(reader);
          }
        });
  }
    
  public void readMediaLink(
    final InputStream in,
    final String charset,
    final ExecutorService exec, 
    final Listener<MediaLink> listener) {
      process(
        exec,
        new Callable<MediaLink>() {
          public MediaLink call() throws Exception {
            return readMediaLink(in,charset);
          }
        }, 
        listener);
  }
  
  public Future<MediaLink> readMediaLink(
    final InputStream in,
    final String charset,
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<MediaLink>() {
          public MediaLink call() throws Exception {
            return readMediaLink(in,charset);
          }
        });
  }
  
  // --

  public <X extends ASObject>void readCollection(
    final Reader reader, 
    final ExecutorService exec, 
    final Listener<Collection<X>> listener) {
      process(
        exec,
        new Callable<Collection<X>>() {
          public Collection<X> call() throws Exception {
            return readCollection(reader);
          }
        }, 
        listener);
  }
  
  public <X extends ASObject>Future<Collection<X>> readCollection(
    final Reader reader, 
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<Collection<X>>() {
          public Collection<X> call() throws Exception {
            return readCollection(reader);
          }
        });
  }
  
  public <X extends ASObject>void readCollection(
    final String reader, 
    final ExecutorService exec, 
    final Listener<Collection<X>> listener) {
      process(
        exec,
        new Callable<Collection<X>>() {
          public Collection<X> call() throws Exception {
            return readCollection(reader);
          }
        }, 
        listener);
  }
  
  public <X extends ASObject>Future<Collection<X>> readCollection(
    final String reader, 
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<Collection<X>>() {
          public Collection<X> call() throws Exception {
            return readCollection(reader);
          }
        });
  }
    
  public <X extends ASObject>void readCollection(
    final InputStream in,
    final String charset,
    final ExecutorService exec, 
    final Listener<Collection<X>> listener) {
      process(
        exec,
        new Callable<Collection<X>>() {
          public Collection<X> call() throws Exception {
            return readCollection(in,charset);
          }
        }, 
        listener);
  }
  
  public <X extends ASObject>Future<Collection<X>> readCollection(
    final InputStream in,
    final String charset,
    final ExecutorService exec) {
      return process(
        exec,
        new Callable<Collection<X>>() {
          public Collection<X> call() throws Exception {
            return readCollection(in,charset);
          }
        });
  }
  
  // --
  
  public void write(
    final ASBase base, 
    final ExecutorService exec,
    final Listener<String> listener) {
    process(
      exec, 
      new Callable<String>() {
        public String call() throws Exception {
          return write(base);
        }
      }, 
      listener);
  }
  
  public Future<String> write(
    final ASBase base, 
    final ExecutorService exec) {
    return process(
      exec, 
      new Callable<String>() {
        public String call() throws Exception {
          return write(base);
        }
      });
  }
  
  public Future<Void> write(
    final ASBase base, 
    final Appendable out,
    final ExecutorService exec) {
    return process(
      exec, 
      new Callable<Void>() {
        public Void call() {
          write(base,out);
          return null;
        }
      });
  }
  
  public Future<Void> write(
    final ASBase base, 
    final OutputStream out,
    final ExecutorService exec) {
    return process(
      exec, 
      new Callable<Void>() {
        public Void call() {
          write(base,out);
          return null;
        }
      });
  }  
  
  public Future<Void> write(
    final ASBase base, 
    final OutputStream out,
    final String charset,
    final ExecutorService exec) {
    return process(
      exec, 
      new Callable<Void>() {
        public Void call() {
          write(base,out,charset);
          return null;
        }
      });
  } 
  
  public Future<Void> writeCollection(
    final Writer out, 
    final ASBase header, 
    final Iterable<ASObject> objects, 
    final ExecutorService exec) {
    return process(
      exec, 
      new Callable<Void>() {
        public Void call() throws Exception {
          writeCollection(out,header,objects);
          return null;
        }});
  }
  
  public Future<Void> writeCollection(
    final OutputStream out,
    final String charset,
    final ASBase header, 
    final Iterable<ASObject> objects, 
    final ExecutorService exec) {
    return process(
      exec, 
      new Callable<Void>() {
        public Void call() throws Exception {
          writeCollection(out,charset,header,objects);
          return null;
        }});
  }

  private <X>Future<X> process(
    ExecutorService executor, 
    Callable<X> resp) {
      ListeningExecutorService exec = 
        MoreExecutors.listeningDecorator(executor);
      return exec.submit(resp);
  }
  
  private <X>void process(
    ExecutorService executor, 
    Callable<X> resp, 
    final Listener<X> listener) {
      ListeningExecutorService exec = MoreExecutors.listeningDecorator(executor);
      final ListenableFuture<X> lf = exec.submit(resp);
      lf.addListener(
        new Runnable() {
          public void run() {
            try {
              listener.onComplete(lf.get());
            } catch (Throwable t) {
              t.printStackTrace();
              throw ExceptionHelper.propogate(t);
            }
          }
        }, 
        executor);
  }
  
  
}
