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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.nio.CharBuffer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.templates.Context;
import org.apache.abdera2.common.templates.Expression;
import org.apache.abdera2.common.templates.Operation;
import org.apache.abdera2.common.templates.Expression.VarSpec;
import org.apache.abdera2.common.text.CharUtils;
import org.apache.abdera2.common.text.UrlEncoding;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.ibm.icu.text.Normalizer2;
import static com.google.common.base.Preconditions.*;
import static org.apache.abdera2.common.text.CharUtils.*;

@SuppressWarnings("unchecked")
public abstract class Operation implements Serializable {
  
    private static final long serialVersionUID = -1734350302144527120L;
    public abstract String evaluate(Expression exp, Context context);

    private static Map<String, Operation> operations = 
      ImmutableMap
        .<String,Operation>builder()
          .put("", new DefaultOperation())
          .put("+", new ReservedExpansionOperation())
          .put("#", new FragmentExpansionOperation())
          .put(".", new DotExpansionOperation())
          .put("/", new PathExpansionOperation())
          .put(";", new PathParamExpansionOperation())
          .put("?", new FormExpansionOperation())
          .put("&", new QueryExpansionOperation())
        .build();

    public static Operation get(String name) {
      name = name != null ? name : "";
      checkArgument(operations.containsKey(name));
      return operations.get(name);
    }

    protected static String eval(
        VarSpec varspec, 
        Context context,
        boolean reserved, 
        String explodeDelim, 
        String explodePfx) {
        String name = checkNotNull(varspec).getName();
        Object rep = checkNotNull(context).resolve(name);
        checkNotNull(varspec);
        String val = toString(
            rep, 
            context, 
            reserved, 
            varspec.isExplode(), 
            explodeDelim, 
            explodePfx,
            varspec.getLength());
        return val;
    }
    
    private static CharSequence trim(CharSequence val, int len) {
      if (val != null && len > -1 && val.length() > len)
        val = val.subSequence(0,len);
      return val;
    }
    
    private static String normalize(CharSequence s) {
      return Normalizer2.getInstance(
        null, 
        "nfc", 
        Normalizer2.Mode.COMPOSE)
          .normalize(s);
    }
    
    private static <T>void appendPrim(
      T obj, 
      int len, 
      StringBuilder buf, 
      boolean explode, 
      String exp, 
      String explodePfx) {
      appendif(buf.length()>0,buf,exp);
      appendif(explode && explodePfx != null, buf, explodePfx);
      buf.append(trim(String.valueOf(obj),len));
    }
    
    private static String toString(
        Object val, 
        Context context, 
        boolean reserved, 
        boolean explode, 
        String explodeDelim, 
        String explodePfx,
        int len) {
        if (val == null)
            return null;
        String exp = explode && explodeDelim != null ? explodeDelim : ",";
        if (val.getClass().isArray()) {
            if (val instanceof byte[]) {
                return UrlEncoding.encode((byte[])val);
            } else if (val instanceof char[]) {
                String chars = (String)trim(new String((char[])val),len);
                return !reserved ?
                  UrlEncoding.encode(
                    normalize(chars), 
                    context.isIri()
                      ? CharUtils.Profile.IUNRESERVED : 
                        CharUtils.Profile.UNRESERVED) :
                  UrlEncoding.encode(
                      normalize(chars),
                      context.isIri()
                        ? CharUtils.Profile.RESERVEDANDIUNRESERVED : 
                          CharUtils.Profile.RESERVEDANDUNRESERVED);                
            } else if (val instanceof short[]) {
                StringBuilder buf = new StringBuilder();
                for (short obj : (short[])val)
                  appendPrim(obj,len,buf,explode,exp,explodePfx);
                return buf.toString();
            } else if (val instanceof int[]) {
                StringBuilder buf = new StringBuilder();
                for (int obj : (int[])val)
                  appendPrim(obj,len,buf,explode,exp,explodePfx);
                return buf.toString();
            } else if (val instanceof long[]) {
                StringBuilder buf = new StringBuilder();
                for (long obj : (long[])val)
                  appendPrim(obj,len,buf,explode,exp,explodePfx);
                return buf.toString();
            } else if (val instanceof double[]) {
                StringBuilder buf = new StringBuilder();
                for (double obj : (double[])val)
                  appendPrim(obj,len,buf,explode,exp,explodePfx);
                return buf.toString();
            } else if (val instanceof float[]) {
                StringBuilder buf = new StringBuilder();
                for (float obj : (float[])val)
                  appendPrim(obj,len,buf,explode,exp,explodePfx);
                return buf.toString();
            } else if (val instanceof boolean[]) {
                StringBuilder buf = new StringBuilder();
                for (boolean obj : (boolean[])val)
                  appendPrim(obj,len,buf,explode,exp,explodePfx);
                return buf.toString();
            } else {
                StringBuilder buf = new StringBuilder();
                for (Object obj : (Object[])val) {
                  appendif(buf.length()>0,buf,exp);
                  appendif(explode && explodePfx != null, buf, explodePfx);
                  buf.append(toString(obj, context, reserved, false, null, null, len));
                }
                return buf.toString();
            }
        } else if (val instanceof InputStream) {
            try {
              if (len > -1) {
                byte[] buf = new byte[len];
                int r = ((InputStream)val).read(buf);
                return r > 0 ?
                  UrlEncoding.encode(buf,0,r) : "";
              } else
                return UrlEncoding.encode((InputStream)val);
            } catch (IOException e) {
              throw ExceptionHelper.propogate(e);
            }
        } else if (val instanceof Readable) {
            try { 
              if (len > -1) {
                CharBuffer buf = CharBuffer.allocate(len);
                int r = ((Readable)val).read(buf);
                buf.limit(r);
                buf.position(0);
                val = buf;
              }
              return !reserved ?
                UrlEncoding.encode(
                  (Readable)val, 
                  "UTF-8", 
                  context.isIri() ? 
                    CharUtils.Profile.IUNRESERVED : 
                    CharUtils.Profile.UNRESERVED) :
                    UrlEncoding.encode(
                      (Readable)val,
                      "UTF-8",
                      context.isIri() ?
                        CharUtils.Profile.RESERVEDANDIUNRESERVED :
                        CharUtils.Profile.RESERVEDANDUNRESERVED);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
        } else if (val instanceof CharSequence) {
            val = trim(normalize((CharSequence)val),len);
            return encode((CharSequence)val, context.isIri(), reserved);
        } else if (val instanceof Byte) {
            return UrlEncoding.encode(((Byte)val).byteValue());
        } else if (val instanceof Context) {
          StringBuilder buf = new StringBuilder();
          Context ctx = (Context) val;
          for (String name : ctx) {
            String _val = toString(ctx.resolve(name), context, reserved, false, null, null, len);
            appendif(buf.length()>0,buf,exp);
            buf.append(name)
               .append(explode ? '=' : ',')
               .append(_val);
          }
          return buf.toString();
        } else if (val instanceof Iterable) {
            StringBuilder buf = new StringBuilder();
            for (Object obj : (Iterable<Object>)val) {
              appendif(buf.length()>0,buf,exp);
              appendif(explode && explodePfx != null,buf,explodePfx);
              buf.append(toString(obj, context, reserved, false, null, null, len));
            }
            return buf.toString();
        } else if (val instanceof Iterator) {
          StringBuilder buf = new StringBuilder();
          Iterator<Object> i = (Iterator<Object>)val;
          while (i.hasNext()) {
              Object obj = i.next();
              appendif(buf.length()>0,buf,exp);
              appendif(explode && explodePfx != null,buf,explodePfx);
              buf.append(toString(obj, context, reserved, false, null, null, len));
          }
          return buf.toString();
        } else if (val instanceof Enumeration) {
          StringBuilder buf = new StringBuilder();
          Enumeration<Object> i = (Enumeration<Object>)val;
          while (i.hasMoreElements()) {
              Object obj = i.nextElement();
              appendif(buf.length()>0,buf,exp);
              appendif(explode && explodePfx != null,buf,explodePfx);
              buf.append(toString(obj, context, reserved, false, null, null, len));
          }
          return buf.toString();
        } else if (val instanceof Map) {
            StringBuilder buf = new StringBuilder();
            Map<Object,Object> map = (Map<Object,Object>)val;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
              String _key = toString(entry.getKey(), context, reserved, false, null, null, len);
              String _val = toString(entry.getValue(), context, reserved, false, null, null, len);
              appendif(buf.length()>0,buf,exp);
              buf.append(_key)
                 .append(explode ? '=' : ',')
                 .append(_val);
            }
            return buf.toString();
        } else if (val instanceof Supplier) {
          return toString(((Supplier<?>)val).get(), context, reserved, explode, explodeDelim, explodePfx, len);
        } else if (val instanceof Optional) {
          Optional<?> o = (Optional<?>) val;
          return toString(o.orNull(), context, reserved, explode, explodeDelim, explodePfx, len);
        } else if (val instanceof Multimap) {
          Multimap<?,?> mm = (Multimap<?,?>)val;
          return toString(mm.asMap(), context, reserved, explode, explodeDelim, explodePfx, len);
        } else if (val instanceof Callable) {
          Callable<Object> callable = (Callable<Object>) val;
          try {
            return toString(callable.call(), context, reserved, explode, explodeDelim, explodePfx, len);
          } catch (Exception e) {
            throw ExceptionHelper.propogate(e);
          }
        } else if (val instanceof Reference) {
          Reference<Object> ref = (Reference<Object>) val;
          return toString(ref.get(), context, reserved, explode, explodeDelim, explodePfx, len);
        } else if (val instanceof Future) {
          try {
            Future<Object> future = (Future<Object>) val;
            return toString(future.get(), context, reserved, explode, explodeDelim, explodePfx, len);
          } catch (Throwable e) {
            throw ExceptionHelper.propogate(e);
          }
        } else {
            if (val != null)
              val = normalize(val.toString());
            return encode(val != null ? val.toString() : null, context.isIri(), reserved);
        }
    }

    private static String encode(CharSequence val, boolean isiri, boolean reserved) {
        String v = normalize(val);
        return !reserved ?
          UrlEncoding.encode(v, 
            isiri
              ? CharUtils.Profile.IUNRESERVED : 
                CharUtils.Profile.UNRESERVED) :
          UrlEncoding.encode(v, 
              isiri
              ? CharUtils.Profile.RESERVEDANDIUNRESERVED : 
                CharUtils.Profile.RESERVEDANDUNRESERVED);
    }

    /**
     * Simple String Expansion ({VAR})
     */
    static final class DefaultOperation extends Operation {
      private static final long serialVersionUID = 8676696520810767327L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (VarSpec varspec : exp) {
              if (!first) buf.append(',');
              String val = eval(varspec, context, false, ",", null);
              buf.append(val != null ? val : "");
              first = false;
            }
            return buf.toString();
        }
    }
    
    /**
     * Reserved Expansion Operation ({+VAR})
     */
    static final class ReservedExpansionOperation extends Operation {
        private static final long serialVersionUID = 1736980072492867748L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (VarSpec varspec : exp) {
              if (!first) buf.append(',');
              String val = eval(varspec, context, true, ",", null);
              buf.append(val != null ? val : "");
              first = false;
            }
            return buf.toString();
        }
    }

    /**
     * Fragment Expansion Operation ({#VAR})
     */
    static final class FragmentExpansionOperation extends Operation {
        private static final long serialVersionUID = -2207953454022197435L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (VarSpec varspec : exp) {
              if (!first) buf.append(',');
              String val = eval(varspec, context, true, ",", null);
              if (first && val != null)
                buf.append('#');
              buf.append(val != null ? val : "");
              first = false;
            }
            return buf.toString();
        }
    }    

    /**
     * Dot Expansion Operation ({.VAR})
     */
    static final class DotExpansionOperation extends Operation {
        private static final long serialVersionUID = -4357734926260213270L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, true, ".", null);
              if (val != null)
                buf.append('.');
              buf.append(val != null ? val : "");
            }
            return buf.toString();
        }
    } 
    

    /**
     * Path Expansion Operation ({/VAR})
     */
    static final class PathExpansionOperation extends Operation {
        private static final long serialVersionUID = 5578346646541533713L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, false, "/", null);
              if (val != null)
                buf.append('/');
              buf.append(val != null ? val : "");
            }
            return buf.toString();
        }
    }
    
    /**
     * Path Param Expansion Operation ({;VAR})
     */
    static final class PathParamExpansionOperation extends Operation {
        private static final long serialVersionUID = 4556090632293646419L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, false, ";", String.format("%s=",varspec.getName()));
              if (val != null)
                buf.append(';');
              if (!varspec.isExplode()) {
                if (val != null)
                  buf.append(varspec.getName());
                if (val != null && val.length() > 0)
                  buf.append("=");
              }
              buf.append(val != null ? val : "");
            }
            return buf.toString();
        }
    }
    
    /**
     * Form Expansion Operation ({?VAR})
     */
    static final class FormExpansionOperation extends Operation {  
        private static final long serialVersionUID = -2166695868296435715L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            buf.append("?");
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, false, "&",  String.format("%s=",varspec.getName())); // Per Draft Seven (http://tools.ietf.org/html/draft-gregorio-uritemplate-07)
              if (context.contains(varspec.getName())) {
                if (!first && val != null) buf.append('&');
                if ((val != null && !varspec.isExplode()) || varspec.isNoval()) {
                  buf.append(varspec.getName());
                }
                if (val != null && !varspec.isExplode() && (!varspec.isNoval() || val.length() > 0) )
                  buf.append("=");
                if (val != null && val.length() > 0)
                  buf.append(val);
              }
              first = false;
            } 
            return buf.toString();
        }
    }
    
    /**
     * Query Expansion Operation ({&VAR})
     */
    static final class QueryExpansionOperation extends Operation {
        private static final long serialVersionUID = 4029538625501399067L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, false, "&",  String.format("%s=",varspec.getName())); // Per Draft Seven (http://tools.ietf.org/html/draft-gregorio-uritemplate-07)
              if (context.contains(varspec.getName())) {
                if (varspec.isExplode()) buf.append('&');
                if ((val != null && !varspec.isExplode()) || varspec.isNoval())
                  buf.append('&').append(varspec.getName());
                if (val != null && !varspec.isExplode() && (!varspec.isNoval() || val.length() > 0) )
                  buf.append("=");
                  if (val != null && val.length() > 0)
                    buf.append(val);
                }
            }
            return buf.toString();
        }
    }
}
