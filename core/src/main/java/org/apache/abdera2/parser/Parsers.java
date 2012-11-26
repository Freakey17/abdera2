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
package org.apache.abdera2.parser;

import java.io.InputStream;
import java.io.Reader;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;

import com.google.common.base.Function;

public final class Parsers {

  private Parsers() {}
 
  public static interface InputStreamFunction<E extends Element> 
    extends Function<InputStream,Document<E>> {}
  
  public static interface ReaderFunction<E extends Element> 
    extends Function<Reader,Document<E>> {}
  
  public static <E extends Element>InputStreamFunction<E> forInputStream(
    final Parser parser,
    final String base,
    final ParserOptions options) {
    return new InputStreamFunction<E>() {
      public Document<E> apply(InputStream in) {
        return parser.parse(in, base, options);
      }
    };
  }
  
  public static <E extends Element>InputStreamFunction<E> forInputStream(
    final Parser parser, 
    final ParserOptions options) {
    return new InputStreamFunction<E>() {
      public Document<E> apply(InputStream in) {
        return parser.parse(in, options);
      }
    };
  }
  
  public static <E extends Element>InputStreamFunction<E> forInputStream(
    final Parser parser) {
    return new InputStreamFunction<E>() {
      public Document<E> apply(InputStream in) {
        return parser.parse(in);
      }
    };
  }
  
  public static <E extends Element>InputStreamFunction<E> forInputStream() {
    return new InputStreamFunction<E>() {
      public Document<E> apply(InputStream in) {
        return Abdera.getInstance().getParser().parse(in);
      }
    };
  }
  
  public static <E extends Element>InputStreamFunction<E> forInputStream(
    final String base,
    final ParserOptions options) {
    return new InputStreamFunction<E>() {
      public Document<E> apply(InputStream in) {
        return Abdera.getInstance().getParser().parse(in, base, options);
      }
    };
  }
  
  public static <E extends Element>InputStreamFunction<E> forInputStream(
    final ParserOptions options) {
    return new InputStreamFunction<E>() {
      public Document<E> apply(InputStream in) {
        return Abdera.getInstance().getParser().parse(in, options);
      }
    };
  }
  
  public static <E extends Element>ReaderFunction<E> forReader(
    final Parser parser,
    final String base,
    final ParserOptions options) {
    return new ReaderFunction<E>() {
      public Document<E> apply(Reader in) {
        return parser.parse(in, base, options);
      }
    };
  }
  
  public static <E extends Element>ReaderFunction<E> forReader(
    final Parser parser, 
    final ParserOptions options) {
    return new ReaderFunction<E>() {
      public Document<E> apply(Reader in) {
        return parser.parse(in, options);
      }
    };
  }
  
  public static <E extends Element>ReaderFunction<E> forReader(
    final Parser parser) {
    return new ReaderFunction<E>() {
      public Document<E> apply(Reader in) {
        return parser.parse(in);
      }
    };
  }
  
  public static <E extends Element>ReaderFunction<E> forReader() {
    return new ReaderFunction<E>() {
      public Document<E> apply(Reader in) {
        return Abdera.getInstance().getParser().parse(in);
      }
    };
  }
  
  public static <E extends Element>ReaderFunction<E> forReader(
    final String base,
    final ParserOptions options) {
    return new ReaderFunction<E>() {
      public Document<E> apply(Reader in) {
        return Abdera.getInstance().getParser().parse(in, base, options);
      }
    };
  }
  
  public static <E extends Element>ReaderFunction<E> forReader(
    final ParserOptions options) {
    return new ReaderFunction<E>() {
      public Document<E> apply(Reader in) {
        return Abdera.getInstance().getParser().parse(in, options);
      }
    };
  }
}
