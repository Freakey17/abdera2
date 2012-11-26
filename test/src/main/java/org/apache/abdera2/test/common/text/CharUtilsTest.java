package org.apache.abdera2.test.common.text;

import org.apache.abdera2.common.text.CharUtils.Profile;
import org.apache.abdera2.common.text.CodepointMatcher;
import org.apache.abdera2.common.text.CodepointMatchers;
import org.apache.abdera2.common.text.InvalidCharacterException;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CharUtilsTest {

  @Test
  public void isAlphaTest() {
    CodepointMatcher cm = CodepointMatchers.isAlpha();
    for (char c = 'A'; c <= 'Z'; c++)
      assertTrue(cm.apply(c));
    for (char c = 'a'; c <= 'z'; c++)
      assertTrue(cm.apply(c));
    assertFalse(cm.apply('1'));
  }
  
  @Test
  public void isDigitTest() {
    CodepointMatcher cm = CodepointMatchers.isDigit();
    assertTrue(cm.apply('0'));
    assertTrue(cm.apply('1'));
    assertTrue(cm.apply('2'));
    assertTrue(cm.apply('3'));
    assertTrue(cm.apply('4'));
    assertTrue(cm.apply('5'));
    assertTrue(cm.apply('6'));
    assertTrue(cm.apply('7'));
    assertTrue(cm.apply('8'));
    assertTrue(cm.apply('9'));
    assertFalse(cm.apply('A'));
  }
  
  @Test
  public void verifyTest() {
    Profile.ALPHANUM.verify("test1");
  }
  
  @Test(expected=InvalidCharacterException.class)
  public void verifyFailureTest() {
    Profile.ALPHANUM.verify("test1,");
  }
}
