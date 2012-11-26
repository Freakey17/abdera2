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
package org.apache.abdera2.writer;

import java.io.Closeable;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.model.Base;

import com.google.common.base.Function;

public final class Writers {

  private Writers() {}
  
  public static abstract class WriterFunction 
    implements Function<Base,Void> { 
    protected final Closeable closeable;
    protected WriterFunction(Closeable closeable) {
      this.closeable = closeable;
    }
    public WriterFunction closing() {
      return new ClosingWriterFunction(this);
    }
  }
  private static class ClosingWriterFunction
    extends WriterFunction
    implements Function<Base,Void> {
      private final WriterFunction function;
      public ClosingWriterFunction(
          WriterFunction function) {
        super(function.closeable);
        this.function = function;
      }
      public Void apply(Base input) {
        try {
          function.apply(input);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        } finally {
          try {
            closeable.close();
          } catch (Throwable t) {
            throw ExceptionHelper.propogate(t);
          }
        }
      }
  }

  public static WriterFunction forWriter(
    final java.io.Writer w) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          Abdera.getInstance().getWriter().writeTo(input, w);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
  
  public static WriterFunction forWriter(
    final Writer writer, 
    final java.io.Writer w) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          writer.writeTo(input, w);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
  
  public static WriterFunction forWriter(
    final Writer writer, 
    final java.io.Writer w, 
    final WriterOptions options) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          writer.writeTo(input, w, options);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
  
  public static WriterFunction forWriter(
    final String writer, 
    final java.io.Writer w) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          Abdera.getInstance().getWriterFactory().getWriter(writer).writeTo(input, w);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
  
  public static WriterFunction forWriter(
    final String writer, 
    final java.io.Writer w, 
    final WriterOptions options) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          Abdera.getInstance().getWriterFactory().getWriter(writer).writeTo(input, w, options);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
  
  
  public static WriterFunction forOutputStream(
    final java.io.OutputStream w) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          Abdera.getInstance().getWriter().writeTo(input, w);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
  
  public static WriterFunction forOutputStream(
    final Writer writer, 
    final java.io.OutputStream w) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          writer.writeTo(input, w);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
  
  public static WriterFunction forOutputStream(
    final Writer writer, 
    final java.io.OutputStream w, 
    final WriterOptions options) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          writer.writeTo(input, w, options);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
  
  public static WriterFunction forOutputStream(
    final String writer, 
    final java.io.OutputStream w) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          Abdera.getInstance().getWriterFactory().getWriter(writer).writeTo(input, w);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
  
  public static WriterFunction forOutputStream(
    final String writer, 
    final java.io.OutputStream w, 
    final WriterOptions options) {
    return new WriterFunction(w) {
      public Void apply(Base input) {
        try {
          Abdera.getInstance().getWriterFactory().getWriter(writer).writeTo(input, w, options);
          return null;
        } catch (Throwable t) {
          throw ExceptionHelper.propogate(t);
        }
      }
    };
  }
}
