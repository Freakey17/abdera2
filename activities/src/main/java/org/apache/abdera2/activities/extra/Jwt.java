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
package org.apache.abdera2.activities.extra;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.common.security.HashHelper;
import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility class for Generating/Validating JSON Web Tokens 
 */
public final class Jwt {

  private Jwt() {}
  
  private static enum Type {
    HMAC,SIG
  }
  
  public static enum Alg {
    HS256("HmacSHA256",Type.HMAC),
    HS384("HmacSHA384",Type.HMAC),
    HS512("HmacSHA512",Type.HMAC),
    RS256("SHA256withRSA",Type.SIG),
    RS384("SHA384withRSA",Type.SIG),
    RS512("SHA512withRSA",Type.SIG),
    ES256("SHA256withECDSA",Type.SIG),  
    ES384("SHA384withECDSA",Type.SIG),  
    ES512("SHA512withECDSA",Type.SIG);
    
    private final String internal;
    private final Type type;
    
    Alg(String internal,Type type) {
      this.internal = internal;
      this.type = type;
    }
    
    public String sig(Key key, byte[] mat) {
      switch(this.type) {
      case HMAC:
        return HashHelper.hmac(key, internal, mat);
      case SIG:
        return HashHelper.sig((PrivateKey)key,internal,mat);
      default:
        throw new UnsupportedOperationException();
      }
    }

    public boolean val(Key key, byte[] mat, byte[] dat) {
      try {
        switch(this.type) {
        case HMAC:
          return HashHelper.hmacval(key, internal, mat, dat);
        case SIG: {
          return HashHelper.sigval((PublicKey)key, internal, mat, dat);
        }
        default:
          throw new UnsupportedOperationException();
        }
      } catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }
    
  }

  public static String generate(Key key, byte[] claim, Supplier<ASBase> header) {
    return generate(key,claim,checkNotNull(header).get());
  }
  
  public static String generate(Key key, byte[] claim, ASBase header) {
    return generate(Alg.HS256, key, claim, header);
  }
  
  public static String generate(Key key, byte[] claim) {
    return generate(Alg.HS256, key, claim);
  }
  
  public static String generate(Alg alg, Key key, byte[] claim, ASBase header) {
    return generate(IO.get(), alg, key, claim, header);
  }
  
  public static String generate(Alg alg, Key key, byte[] claim) {
    return generate(IO.get(), alg, key, claim);
  }
  
  public static String generate(Key key, ASBase claim, ASBase header) {
    return generate(Alg.HS256, key, claim, header);
  }
  
  public static String generate(Key key, ASBase claim) {
    return generate(Alg.HS256, key, claim);
  }
  
  public static String generate(Alg alg, Key key, ASBase claim, ASBase header) {
    return generate(IO.get(),alg,key,claim,header);
  }
  
  public static String generate(Alg alg, Key key, ASBase claim) {
    return generate(IO.get(), alg, key, claim);
  }
  
  public static String generate(IO io, Alg alg, Key key, ASBase claim, ASBase header) {
    try {
      return generate(
        io,
        alg,
        key,
        checkNotNull(io).write(checkNotNull(claim)).getBytes("UTF-8"),
        header);
    } catch (Throwable t) {
      if (t instanceof RuntimeException)
        throw (RuntimeException)t;
      else throw new RuntimeException(t);
    }   
  }
  
  public static String generate(IO io, Alg alg, Key key, ASBase claim) {
    return generate(io,alg,key,claim,ASBase.make().set("alg", alg).get());
  }
  
  public static String generate(IO io, Alg alg, Key key, byte[] claim) {
    return generate(io,alg,key,claim,ASBase.make().set("alg", alg).get());
  }
  
  public static String generate(IO io, Alg alg, Key key, byte[] claim, ASBase header) {
    try {
      checkNotNull(header);
      checkNotNull(header.getProperty("alg"));
      StringBuilder buf = new StringBuilder();
      String _header = Base64.encodeBase64URLSafeString(
        checkNotNull(io).write(header).getBytes("UTF-8"));
      String _claim = Base64.encodeBase64URLSafeString(claim);
      buf.append(_header).append('.').append(_claim);
      String mat = buf.toString();
      buf.append('.').append(alg.sig(key, mat.getBytes("UTF-8")));
      return buf.toString();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
  
  public static boolean validate(Key key, String jwt) throws IOException {
    return validate(key, IO.get(),jwt);
  }
  
  public static boolean validate(Key key, IO io,String jwt) throws IOException {
    String[] split = jwt.split("\\.",3);
    if (split.length < 3)
      return false; // cannot validate without a signature
    ASBase header = dec(io,split[0]);
    StringBuilder buf = new StringBuilder();
    buf.append(split[0]).append('.').append(split[1]);
    byte[] dat = buf.toString().getBytes("UTF-8");
    byte[] chk = Base64.decodeBase64(split[2]);
    Object o = header.getProperty("alg");
    Alg alg = o instanceof Alg ? (Alg)o : Alg.valueOf(o.toString());
    return alg.val(key, dat, chk);
  }
  
  private static ASBase dec(IO io, String enc) throws UnsupportedEncodingException {
    byte[] data = Base64.decodeBase64(enc);
    return io.readObject(new String(data,0,data.length, "UTF-8"));
  }
  
  public static ASBase getClaimIfValid(IO io, Key key, String jwt) throws IOException {
    if (!validate(key,io,jwt)) return null;
    String[] parts = jwt.split("\\.",3);
    return dec(io, parts[1]);
  }
  
  public static ASBase getClaimIfValid(Key key, String jwt) throws IOException {
    return getClaimIfValid(IO.get(),key,jwt);
  }
  
  public static byte[] getDataIfValid(Key key, String jwt) throws IOException {
    if (!validate(key,jwt)) return new byte[0];
    String[] parts = jwt.split("\\.",3);
    return Base64.decodeBase64(parts[1].getBytes("UTF-8"));
  }
  
  public static ASBase getHeader(String jwt) throws IOException {
    return getHeader(IO.get(),jwt);
  }
  
  public static ASBase getHeader(IO io, String jwt) throws IOException {
    String[] parts = jwt.split("\\.",3);
    return dec(io, parts[0]);
  }
}
