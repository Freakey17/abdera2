package org.apache.abdera2.test.common.http;

import static org.junit.Assert.assertEquals;

import org.apache.abdera2.common.http.QualityHelper;
import org.apache.abdera2.common.http.QualityHelper.QToken;
import org.junit.Test;

public class QualityHelperTest {

  @Test
  public void qualityTest() {
    String qs = "A;q=0.1, B;q=0.7, C;q=0.5, D";
    QToken[] tokens = QualityHelper.orderByQ(qs);
    assertEquals(4,tokens.length);
    assertEquals("D",tokens[0].token());
    assertEquals("B",tokens[1].token());
    assertEquals("C",tokens[2].token());
    assertEquals("A",tokens[3].token());
  }
  
}
