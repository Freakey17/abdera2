package org.apache.abdera2.test.common.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.lang.Range;
import org.junit.Test;

public class LangTest {

  @Test
  public void langTest() {
    Lang lang = new Lang("fr-Latn-CA-A-123-B-456-789-X-ZZZ");
    assertEquals("fr",lang.language().name());
    assertEquals("Latn",lang.script().name());
    assertEquals("CA",lang.region().name());
    assertEquals("a",lang.extension().name());
    assertEquals("123",lang.extension().next().name());
    assertEquals("x",lang.privateUse().name());
    assertEquals("zzz",lang.privateUse().next().name());
    Range range = new Range("*-Latn-CA",true);
    assertTrue(range.matches(lang));
  }
  
}
