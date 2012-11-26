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

import static java.lang.String.format;

import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.xml.XMLVersion;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * General utilities for dealing with Unicode characters
 */
public final class CharUtils {

    private CharUtils() {
    }
   
    public static enum Profile {
        NONE(CodepointMatcher.MATCH_NONE), 
        NONOP(CodepointMatcher.MATCH_ALL), 
        ALPHA(CodepointMatchers.isAlpha().negate()), 
        ALPHANUM(CodepointMatchers.isAlphaNum().negate()), 
        FRAGMENT(CodepointMatchers.isFragment().negate()), 
        IFRAGMENT(CodepointMatchers.isIFragment().negate()), 
        PATH(CodepointMatchers.isPath().negate()), 
        IPATH(CodepointMatchers.isIPath().negate()), 
        IUSERINFO(CodepointMatchers.isIUserInfo().negate()), 
        USERINFO(CodepointMatchers.isUserInfo().negate()), 
        QUERY(CodepointMatchers.isQuery().negate()), 
        IQUERY(CodepointMatchers.isIQuery().negate()), 
        SCHEME(CodepointMatchers.isScheme().negate()), 
        PATHNODELIMS(CodepointMatchers.isPathNoDelims().negate()), 
        IPATHNODELIMS(CodepointMatchers.isIPathNoDelims().negate()), 
        IPATHNODELIMS_SEG(
            CodepointMatcher.and(
              CodepointMatchers.isIPathNoDelims().negate(),
              CodepointMatcher.is('@',':').negate())),
        IREGNAME(CodepointMatchers.isRegName().negate()), 
        IHOST (CodepointMatchers.isIHost().negate()), 
        IPRIVATE(CodepointMatchers.isIPrivate().negate()), 
        RESERVED(CodepointMatchers.isReserved().negate()), 
        IUNRESERVED(CodepointMatchers.isIUnreserved().negate()), 
        UNRESERVED(CodepointMatchers.isUnreserved().negate()), 
        RESERVEDANDUNRESERVED(
          CodepointMatcher.and(
            CodepointMatchers.isUnreserved().negate(),
            CodepointMatchers.isReserved().negate()
          )), 
        RESERVEDANDIUNRESERVED(
          CodepointMatcher.and(
            CodepointMatchers.isIUnreserved().negate(),
            CodepointMatchers.isReserved().negate()
          )),
        XML1RESTRICTED(
          CodepointMatchers.isRestricted(XMLVersion.XML10)), 
        XML11RESTRICTED(
          CodepointMatchers.isRestricted(XMLVersion.XML11)), 
        RFC5987(CodepointMatchers.is5987().negate()), 
        TOKEN(CodepointMatchers.isToken().negate()), 
        SCHEMESPECIFICPART(
          CodepointMatcher.and(
            CodepointMatchers.isIUnreserved().negate(),
            CodepointMatchers.isReserved().negate(),
            CodepointMatchers.isIPrivate().negate(),
            CodepointMatchers.isPct().negate(),
            CodepointMatcher.is('#').negate())
            ), 
         AUTHORITY(
           CodepointMatcher.and(
             CodepointMatchers.isRegName().negate(),
             CodepointMatchers.isUserInfo().negate(),
             CodepointMatchers.isGenDelim().negate()));
        
        private final CodepointMatcher matcher;

        Profile(CodepointMatcher matcher) {
            this.matcher = matcher;
        }

        public CodepointMatcher matcher() {
            return matcher;
        }

        public boolean apply(int codepoint) {
            return matcher.apply(codepoint);
        }
        
        public void verify(CharSequence seq) {
          if (seq == null) return;
          matcher.verifyNot(CodepointIterator.getInstance(seq));
        }
        public boolean check(CharSequence seq) {
          if (seq == null) return false;
          return matcher.all(seq);
        }
    }

    public static String unwrap(String st, char x, char y) {
      if (st == null || st.length() == 0)
        return st;
      int n = 0, e = st.length();
      if (st.charAt(0) == x) n++;
      if (st.charAt(e-1) == y) e--;
      return st.substring(n,e);
    }
    
    public static String unquote(String s) {
      StringBuilder buf = new StringBuilder();
      int i = s.length();
      boolean quoted = false, escaped = false;
      for (int n = 0; n < s.length(); n++) {
        char c = s.charAt(n);
        if (n == 0 && c == '"') {
          quoted = true;
        } else if (!(quoted && n+1==i && !escaped && c == '"')) 
          buf.append(c);
        if (escaped) escaped = false;
        else if (c == '\\' && !escaped) 
          escaped = true;
      }
      return buf.toString();
    }

    public static String[] splitAndTrim(
        String value) {
          if (value == null || value.length() == 0)
            return new String[0];
          return unquote(value).split("\\s*,\\s*");
    }
    
    public static String quotedIfNotToken(String value) {
      return CodepointMatchers.isToken().all(value)?value:quoted(value,true);
    }
    
    public static String quotedIfNotToken(String value, boolean wrap) {
      return CodepointMatchers.isToken().all(value)?value:quoted(value,false);
    }

    public static String quoted(String val, boolean wrap) {
      StringBuilder buf = new StringBuilder();
      if (wrap) buf.append('"');
      int l = val.length();
      for (int n = 0; n < l; n++) {
        char c = val.charAt(n);
        if (c == '"')
          buf.append('\\');
        buf.append(c);
      }
      if (wrap) buf.append('"');
      return buf.toString();
    }

    public static int scanFor(char c, String text, int s, boolean errifnotws) {
      return scanFor(c,text,s,errifnotws,',');
    }
    
    public static int scanFor(char c, String text, int s, boolean errifnotws, char breakat) {
      boolean inquoted = false;
      int l = text.length();
      for (int n = s; n < l; n++) {
        char ch = text.charAt(n);
          if (ch == '"') inquoted = !inquoted;
          if (ch == breakat && !inquoted) return n;
          if (ch == c) return n;
          if (errifnotws && Character.isWhitespace(ch))
            throw new InvalidCharacterException(ch);
      }
      return -1;
    }
    
    public static boolean appendcomma(boolean exp, StringBuilder buf) {
      if (!exp) buf.append(", ");
      return exp ? !exp : exp;
    }
    
    public static void append(StringBuilder buf, String value) {
      if (buf.length() > 0)
          buf.append(", ");
      buf.append(value);
  }

    public static void appendwithsepif(boolean exp, StringBuilder buf, String value, Object... args) {
      if (exp) append(buf,format(value,args));
    }
    
  public static void appendif(boolean exp, StringBuilder buf, String value, Object... args) {
    if (exp) buf.append(format(value,args));
  }

  public static void appendif(boolean exp, StringBuilder buf, Iterable<String> items) {
    if (exp && !Iterables.isEmpty(items)) {
      buf.append("=\"");
      joiner.appendTo(buf,items);
      buf.append('"');
    } 
  }
  
  public static boolean not_empty(String val) {
    return val != null && val.length() > 0;
  }
  
  public static final Joiner joiner = Joiner.on(',').skipNulls();

  public static String unescape(String quoted) {
    StringBuilder buf = new StringBuilder();
    int i = quoted.length();
    for (int n = 0; n < i; n++) {
      char c = quoted.charAt(n);
      if (c != '\\') buf.append(c);
      else if (n < i-1 && quoted.charAt(n+1) == '\\') {
        buf.append(c);
        n++;
      }
    }
    return buf.toString();
  }

  public static byte[] utf8bytes(String s) {
    try {
      return s.getBytes("UTF-8");
    } catch (Throwable t) {
      throw ExceptionHelper.propogate(t);
    }
  }
}
