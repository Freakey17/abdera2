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

import static org.apache.abdera2.common.text.CodepointMatcher.*;

import org.apache.abdera2.common.xml.XMLVersion;

/**
 * Variety of Codepoint Matcher implementations... most deal with 
 * URI/IRI validation requirements
 */
public final class CodepointMatchers {

  public static CodepointMatcher isAlpha() {
    return new OrCodepointMatcher(
      inRange('A','Z'),
      inRange('a','z')
    );
  }
  
  public static CodepointMatcher isDigit() {
    return inRange('0','9');
  }
  
  public static CodepointMatcher isAlphaNum() {
    return new OrCodepointMatcher(
      inRange('A','Z'),
      inRange('a','z'),
      inRange('0','9'));
  }
  
  public static CodepointMatcher isHex() {
    return new OrCodepointMatcher(
      inRange('0','9'),
      inRange('a','f'),
      inRange('A','F'));
  }
  
  public static CodepointMatcher isPct() {
    return new OrCodepointMatcher(
      is('%'),
      isHex());
  }
  
  public static CodepointMatcher is5987() {
    return new OrCodepointMatcher(
      isAlphaNum(),
      is('!','#','$','&','+','-','.','^','_','`','|','~')
    );
  }
  
  public static CodepointMatcher isMark() {
    return is('-','_','.','!','~','*','\\','\'','(',')','`');
  }

  public static CodepointMatcher isUnreserved() {
    return or(
      isAlphaNum(),
      is('-','.','_','~','`'));
  }

  public static CodepointMatcher isGenDelim() {
    return is(':','/','?','#','[',']','@');
  }

  public static CodepointMatcher isSubDelim() {
    return is('!','$','&','\'','(',')','*','+',',',';','=','\\');
  }

  public static CodepointMatcher isReserved() {
    return or(isGenDelim(),isSubDelim());
  }

  public static CodepointMatcher isPchar() {
    return or(
      isUnreserved(),
      is(':','@','&','=','+','$',','));
  }

  public static CodepointMatcher isPath() {
    return or(
      isPchar(),
      is(';','/','%',','));
  }

  public static CodepointMatcher isPathNoDelims() {
    return and(
      isPath(),
      isGenDelim().negate());
  }
  
  public static CodepointMatcher isScheme() {
    return or(
      isAlphaNum(),
      is('+','-','.'));
  }

  public static CodepointMatcher isUserInfo() {
    return or(
      isUnreserved(),
      isSubDelim(),
      isPct());
  }

  public static CodepointMatcher isQuery() {
    return or(
      isPchar(),
      is(';','/','?','%'));
  }

  public static CodepointMatcher isFragment() {
    return or(
      isPchar(),
      is('/','?','%'));
  }

  // TODO: Would inv_set be better here?
  public static CodepointMatcher isUcsChar() {
    return or(
        inRange('\u00A0', '\uD7FF'),
        inRange('\uF900', '\uFDCF'),
        inRange('\uFDF0', '\uFFEF'),
        inRange(0x10000, 0x1FFFD),
        inRange(0x20000, 0x2FFFD),
        inRange(0x30000, 0x3FFFD),
        inRange(0x40000, 0x4FFFD),
        inRange(0x50000, 0x5FFFD),
        inRange(0x60000, 0x6FFFD),
        inRange(0x70000, 0x7FFFD),
        inRange(0x80000, 0x8FFFD),
        inRange(0x90000, 0x9FFFD),
        inRange(0xA0000, 0xAFFFD),
        inRange(0xB0000, 0xBFFFD),
        inRange(0xC0000, 0xCFFFD),
        inRange(0xD0000, 0xDFFFD),
        inRange(0xE1000, 0xEFFFD)
      );
  }

  public static CodepointMatcher isIPrivate() {
    return or(
      inRange('\uE000','\uF8FF'),
      inRange(0xF0000,0xFFFFD),
      inRange(0x100000,0x10FFFD));
  }

  public static CodepointMatcher isIUnreserved() {
    return or(
      isAlphaNum(),
      isMark(),
      isUcsChar());
  }

  public static CodepointMatcher isIPchar() {
    return or(
      isIUnreserved(),
      isSubDelim(),
      is(':','@','&','=','+','$')
    );
  }

  public static CodepointMatcher isIPath() {
    return or(
      isIPchar(),
      is(';','/','%',','));
  }
  
  public static CodepointMatcher isIPathNoDelims() {
    return and(
      isIPath(),
      isGenDelim().negate());
  }

  public static CodepointMatcher isIQuery() {
    return or(
      isIPchar(),
      isIPrivate(),
      is(';','/','?','%'));
  }
  
  public static CodepointMatcher isIFragment() {
    return or(
      isIPchar(),
      isIPrivate(),
      is('/','?','%'));
  }
  
  public static CodepointMatcher isIRegName() {
    return or(
      isIUnreserved(),
      is('!','$','&','\'','(',')','*','+',',',';','=','"'));
  }
  
  public static CodepointMatcher isIpLiteral() {
    return or(
      isHex(),
      is(':','[',']'));
  }

  public static CodepointMatcher isIHost() {
    return or(
      isIRegName(),
      isIpLiteral());
  }

  public static CodepointMatcher isRegName() {
    return or(
        isUnreserved(),
        is('!','$','&','\'','(',')','*','+',',',';','=','"'));
  }

  public static CodepointMatcher isIUserInfo() {
    return or(
      isIUnreserved(),
      is(';',':','&','=','+','$',','));
  }
  
  public static CodepointMatcher isIServer() {
    return or(
      isIUserInfo(),
      isIRegName(),
      isAlphaNum(),
      is('.',':','@','[',']','%','-'));
  }
  
  public static CodepointMatcher isToken() {
    return and(
      isAscii(),
      isCtl().negate(),
      isSep().negate());
  }
  
  public static CodepointMatcher isAscii() {
    return inRange(0,127);
  }
  
  public static CodepointMatcher isCtl() {
    return or(inRange(0,31),is(127));
  }
  
  public static CodepointMatcher isSep() {
    return is(
      '(',')','<','>','@',',',';',':','\\',
      '"','/','[',']','?','=','{','}',32,9);
  }
  
  // Inversion set
  private static int[] RESTRICTED_SET_v1 = {0, 9, 11, 13, 14, 32, 55296, 57344, 65534, 65536};
  private static int[] RESTRICTED_SET_v11 = {11, 13, 14, 32, 127, 160, 55296, 57344, 65534, 65536};
  
  public static CodepointMatcher isRestricted(XMLVersion version) {
    return CodepointMatcher.inInversionSet(
      version == XMLVersion.XML10 ? 
        RESTRICTED_SET_v1 : 
        RESTRICTED_SET_v11);
  }

}
