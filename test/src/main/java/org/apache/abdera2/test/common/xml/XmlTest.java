package org.apache.abdera2.test.common.xml;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.apache.abdera2.common.xml.XMLVersion;
import org.apache.abdera2.common.xml.XmlRestrictedCharReader;
import org.junit.Test;


public class XmlTest {

  @Test
  public void testXMLRestrictedCharReader() {
    try {
      char[] data = {0x0,'<','a',0x0, '/', '>'};
      
      StringReader sr = new StringReader(new String(data));
      XmlRestrictedCharReader r = 
        new XmlRestrictedCharReader(sr,XMLVersion.XML10);
      char[] buf = new char[6];
      int n = r.read(buf);
      assertEquals(4,n);
      assertEquals('<',buf[0]);
      assertEquals('a',buf[1]);
      assertEquals('/',buf[2]);
      assertEquals('>',buf[3]);
      assertEquals(0x0,buf[4]);
    } catch (Throwable t) {}
  }
  
}
