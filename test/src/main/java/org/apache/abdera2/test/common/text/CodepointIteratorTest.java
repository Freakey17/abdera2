package org.apache.abdera2.test.common.text;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import org.apache.abdera2.common.text.CodepointIterator;
import org.junit.Test;

public class CodepointIteratorTest {

  @Test(expected=NoSuchElementException.class)
  public void codepointIteratorTest() {
    // test supplemental codepoints...
    char[] chars = {'A','B',0xD800,0xDEB7,'C','D'};
    CodepointIterator ci = CodepointIterator.getInstance(chars);
    assertEquals(Integer.valueOf(65),ci.next());
    assertEquals(Integer.valueOf(66),ci.next());
    assertEquals(Integer.valueOf(66231),ci.next());
    assertEquals(Integer.valueOf(67),ci.next());
    assertEquals(Integer.valueOf(68),ci.next());
    ci.next(); // will throw exception
  }
  
}
