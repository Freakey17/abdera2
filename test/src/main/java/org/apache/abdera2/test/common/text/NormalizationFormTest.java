package org.apache.abdera2.test.common.text;

import static org.junit.Assert.assertEquals;

import org.apache.abdera2.common.text.NormalizationForm;
import org.junit.Test;

public class NormalizationFormTest {

  @Test
  public void normalizationFormTest() {
    
    String data = "A\u030ABCDE\u0073\u0323\u0307";
    
    String C = NormalizationForm.C.normalize(data);

    assertEquals(data.charAt(0),'A');
    assertEquals(C.charAt(0),0x00C5);
    assertEquals(C.charAt(C.length()-1),0x1E69);
    assertEquals(data.length()-3,C.length());
    
    String D = NormalizationForm.D.normalize(C);
    
    assertEquals(D.charAt(0),'A');
    assertEquals(D.charAt(1),0x030A);
    
    data = "\u1E9B\u0323";
    
    String KC = NormalizationForm.KC.normalize(data);
    assertEquals(1,KC.length());
    assertEquals(0x1E69,KC.charAt(0));
    
    String KD = NormalizationForm.KD.normalize(data);
    assertEquals(3,KD.length());
    assertEquals(0x0073,KD.charAt(0));
    assertEquals(0x0323,KD.charAt(1));
    assertEquals(0x0307,KD.charAt(2));
    
    D = NormalizationForm.D.normalize(data);
    assertEquals(3,D.length());
    assertEquals(0x017F,D.charAt(0));
    assertEquals(0x0323,D.charAt(1));
    assertEquals(0x0307,D.charAt(2));
    
    C = NormalizationForm.C.normalize(data);
    assertEquals(2,C.length());
    assertEquals(0x1E9B,C.charAt(0));
    assertEquals(0x0323,C.charAt(1));
  }
  
}
