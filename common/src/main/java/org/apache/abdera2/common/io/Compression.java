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
package org.apache.abdera2.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

import com.google.common.collect.Iterables;

import static org.apache.abdera2.common.text.CharUtils.splitAndTrim;
import static com.google.common.base.Preconditions.*;

/**
 * Appropriately wraps inputstream and outputstream instances for 
 * transparent data (de)compression using either the gzip or deflate
 * methods.
 */
public final class Compression {

    public enum CompressionCodec {
        GZIP, XGZIP, DEFLATE;

        public static CompressionCodec value(String encoding) {
          checkNotNull(encoding);
            return valueOf(encoding.toUpperCase().replaceAll("-", ""));
        }

        public OutputStream wrap(OutputStream out) throws IOException {
          switch (this) {
            case XGZIP:
            case GZIP:
              return new GZIPOutputStream(out);
            case DEFLATE:
              return new DeflaterOutputStream(out);
            default: throw new IllegalArgumentException(
              "Unknown Compression Codec");
          }          
        }
     
        public InputStream wrap(InputStream in) throws IOException {
          switch (this) {
            case GZIP:
            case XGZIP:
                return new GZIPInputStream(in);
            case DEFLATE:
                return new InflaterInputStream(in);
            default: throw new IllegalArgumentException(
              "Unknown Compression Codec");
          }
        }
    }

    public static CompressionCodec getCodec(String name) {
      CompressionCodec codec = null;
      if (name == null)
          return null;
      try {
          codec = CompressionCodec.valueOf(name.toUpperCase().trim());
      } catch (Exception e) {}
      return codec;
    }

    private static void checkCodecs(boolean exp) {
      checkArgument(exp, "At least one codec must be specified");
    }
    
    /**
     * Wrap an OutputStream of data so it can be automatically
     * compressed as it is written. If multiple compression codecs have
     * been applied, they will be layered accordingly
     */
    public static OutputStream wrap(
      OutputStream out, 
      Iterable<CompressionCodec> codecs) 
        throws IOException {
      return wrap(out,Iterables.toArray(codecs,CompressionCodec.class));
    }
    
    /**
     * Wrap an OutputStream of data so it can be automatically
     * compressed as it is written. If multiple compression codecs have
     * been applied, they will be layered accordingly
     */
    public static OutputStream wrap(
        OutputStream out, 
        CompressionCodec... codecs)
        throws IOException {
      checkNotNull(out);
      for (int n = codecs.length - 1; n >= 0; n--)
        out = codecs[n].wrap(out);
      return out;      
    }

    /**
     * Wrap an OutputStream of data so it can be automatically
     * compressed as it is written. If multiple compression codecs have
     * been applied, they will be layered accordingly
     */
    public static OutputStream wrap(
        OutputStream out, 
        CompressionCodec codec,
        CompressionCodec... codecs)
        throws IOException {
        checkNotNull(out);
        checkCodecs(codec != null);
        return codec.wrap(wrap(out,codecs));
    }

    /**
     * Wrap an InputStream of compressed data so it can be automatically
     * decompressed as it is read. If multiple compression codecs have
     * been applied, they will be layered accordingly
     */
    public static InputStream wrap(
      InputStream in, 
      Iterable<CompressionCodec> codecs) 
        throws IOException {
      return wrap(in, Iterables.toArray(codecs, CompressionCodec.class));
    }
    
    /**
     * Wrap an InputStream of compressed data so it can be automatically
     * decompressed as it is read. If multiple compression codecs have
     * been applied, they will be layered accordingly
     */
    public static InputStream wrap(
      InputStream in, 
      CompressionCodec... codecs)
      throws IOException {
      checkNotNull(in);
      if (codecs == null || codecs.length == 0) return in;
      for (int n = codecs.length - 1; n >= 0; n--)
        in = codecs[n].wrap(in);
      return in;
    }

    /**
     * Wrap an InputStream of compressed data so it can be automatically
     * decompressed as it is read. If multiple compression codecs have
     * been applied, they will be layered accordingly
     */
    public static InputStream wrap(
        InputStream in, 
        CompressionCodec codec,
        CompressionCodec... codecs) 
          throws IOException {  
        checkNotNull(in);
        checkCodecs(codec != null);
        return codec.wrap(wrap(in,codecs));
    }

    /**
     * Wrap an InputStream of compressed data so it can be automatically
     * decompressed as it is read. If multiple compression codecs have
     * been applied, they will be layered accordingly
     */
    public static InputStream wrap(
        InputStream in, 
        String ce) 
          throws IOException {
        checkNotNull(in);
        String[] encodings = splitAndTrim(ce);
        checkCodecs(encodings.length > 0);
        for (int n = encodings.length - 1; n >= 0; n--) {
          CompressionCodec encoding = 
            getCodec(encodings[n]);
          checkNotNull(encoding,"Invalid Compression Codec");
          in = encoding.wrap(in);
        }
        return in;
    }

    /**
     * Generates a description of the compression codecs used in a manner
     * that conforms with the HTTP Content-Encoding and Transfer-Encoding
     * mechanisms, that is, the codecs are listed in the order they will 
     * be applied to the data
     */
    public static String describe(
        CompressionCodec codec, 
        CompressionCodec... codecs) {
        checkCodecs(codec != null || codecs.length > 0);
        int i = 0;
        if (codec == null) {
          codec = codecs[0];
          i = 1;
        }
        StringBuilder buf = new StringBuilder("\"");
        buf.append(codec.name().toLowerCase());
        for (int n = codecs.length - 1; n >= i; n--)
          buf.append(',')
             .append(codecs[n].name().toLowerCase());
        buf.append('"');
        return buf.toString();
    }
    
}
