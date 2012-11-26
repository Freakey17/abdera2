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
package org.apache.abdera2.common.text;

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.net.BCodec;
import org.apache.commons.codec.net.QCodec;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * Provides utility classes for the legacy B and Q Encodings (RFC 2047)
 * historically used to encoded non-ascii characters within the headers of HTTP messages.
 * Also provides an implementation of the newer, easier to understand and implement
 * mechanism for encoding Header parameters defined by RFC 5987 (what I call 
 * the "Star Encoding" here due to the use of the * character within the encoding.
 * 
 * The Star Encoding is assumed to be the default when encoding data, but 
 * all codecs will be tried transparently when decoding. 
 */
public enum Codec {
    B, 
    Q,
    STAR;
    
    private String _encode(String value) {
      return _encode(value, DEFAULT_CHARSET);
    }
    
    private String _encode(String value, String charset) {
      if (value == null)
          return null;
      try {
          StringEncoder e = null;
          switch (this) {
              case Q: e = new QCodec(charset); break;
              case B: e = new BCodec(charset); break;
              default: e = new StarCodec(charset); break;
          }
          return e.encode(value);
      } catch (Exception e) {
          return value;
      }
    }
    
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static String encode(String value, String charset, Codec codec) {
      return codec._encode(value, charset);
    }
    
    public static String encode(String value, Codec codec) {
      return codec._encode(value);
    }
    
    public static String encode(String value) {
      return STAR._encode(value);
    }
    
    public static String encode(String value, String charset) {
      return STAR._encode(value, charset);
    }
    
    private static final Set<StringDecoder> codecs =
      ImmutableSet.<StringDecoder>of(
        new StarCodec(),
        new BCodec(),
        new QCodec());
    
    public static String decode(String value) {
        if (value == null)
          return null;
        for (StringDecoder dec : codecs) {
          try {
            return dec.decode(value);
          } catch (DecoderException de) {
          } catch (Exception e) {
            break;
          }
        }
        return value;
    }

    public static class StarCodec 
      implements StringEncoder, 
                 StringDecoder {
      
      public final String charset;
      
      public StarCodec() {
        this("UTF-8");
      }
      
      public StarCodec(String charset) {
        this.charset = charset;
      }

      public Object encode(Object object) throws EncoderException {
        if (object == null)
          return null;
        if (object instanceof String)
          return encode((String)object);
        throw new EncoderException();
      }

      public Object decode(Object object) throws DecoderException {
        if (object == null)
          return null;
        if (object instanceof String)
          return decode((String)object);
        throw new DecoderException();
      }

      public String decode(String string) throws DecoderException {
        try {
          String[] parts = string.split("'",3);
          if (parts.length != 3) 
            throw new DecoderException();
          return UrlEncoding.decode(parts[2], parts[0]);
        } catch (Throwable t) {
          throw new DecoderException(t.getMessage(),t);
        }
      }

      public String encode(String string) throws EncoderException {
        if (!needs(string)) return string;
        StringBuilder buf = new StringBuilder();
        buf.append(charset).append("''"); // we're not encoding language data
        if (!charset.equalsIgnoreCase("UTF-8")) {
          try {
            ByteArrayInputStream in = 
              new ByteArrayInputStream(string.getBytes(charset));
            buf.append(UrlEncoding.encode(in, charset, CharUtils.Profile.RFC5987));
          } catch (Throwable t) {
            throw new EncoderException(t.getMessage());
          }
        } else   
          buf.append(UrlEncoding.encode(string, CharUtils.Profile.RFC5987));
        return buf.toString();
      }
 
      private static final CodepointMatcher CHECKER =
          CodepointMatcher.and(
            CodepointMatchers.isToken().negate(),
            CodepointMatcher.isNot(0x20));
      
      private boolean needs(String string) {
        for (char c : string.toCharArray())
          if (CHECKER.apply(c)) 
            return true;
        return false;
      }
    }
  
  @SuppressWarnings("synthetic-access")
  public static Function<String,String> decode() {
    return new DecodeFunction();
  }
  
  public static Function<String,String> encodeB(String charset) {
    return new EncodeFunction(Codec.B,charset);
  }
  
  public static Function<String,String> encodeB() {
    return encodeB("UTF-8");
  }
  
  public static Function<String,String> encodeQ(String charset) {
    return new EncodeFunction(Codec.Q,charset);
  }
  
  public static Function<String,String> encodeQ() {
    return encodeQ("UTF-8");
  }
  
  public static Function<String,String> encodeStar() {
    return new EncodeFunction(Codec.STAR, "UTF-8");
  }
    
  public static Function<String,String> encodeStar(String charset) {
    return new EncodeFunction(Codec.STAR, charset);
  }
  
  private static class EncodeFunction 
    implements Function<String,String> {
    private final Codec c;
    private final String charset;
    EncodeFunction(Codec c, String charset) {
      this.c = c;
      this.charset = charset;
    }
    public String apply(String input) {
      return encode(input,charset,c);
    }
  }
  
  private static class DecodeFunction
    implements Function<String,String> {
    public String apply(String input) {
      if (input == null) return null;
      return decode(input);
    }
  }
  
}