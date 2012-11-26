package org.apache.abdera2.test.common.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.apache.abdera2.common.io.CharsetSniffingInputStream;
import org.junit.Test;

public class CharsetSniffingInputStreamTest {

  @Test
  public void charsetSniffingInputStreamTest() {
    try {
      byte[] data = 
        {0xFFFFFFEF,  // UTF-8
         0xFFFFFFBB,
         0xFFFFFFBF,'A','B','C'};
      
      ByteArrayInputStream in = 
        new ByteArrayInputStream(data);
      CharsetSniffingInputStream c = 
        new CharsetSniffingInputStream(in,false);
      assertEquals("UTF-8",c.getEncoding());
      byte[] buf = new byte[5];
      int r = c.read(buf);
      assertEquals(3,r);
      assertEquals('A',buf[0]);
      assertEquals('B',buf[1]);
      assertEquals('C',buf[2]);
      assertEquals(0x0,buf[3]);
    } catch (Throwable t) {}
  }
  
}
