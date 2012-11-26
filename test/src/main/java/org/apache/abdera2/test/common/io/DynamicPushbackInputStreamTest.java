package org.apache.abdera2.test.common.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.apache.abdera2.common.io.DynamicPushbackInputStream;
import org.junit.Test;

public class DynamicPushbackInputStreamTest {

  @Test
  public void dynamicPushbackInputStreamTest() {
    try {
      byte[] data = {'A','B','C'};
      ByteArrayInputStream in = 
        new ByteArrayInputStream(data);
      DynamicPushbackInputStream din = 
        new DynamicPushbackInputStream(in);
      byte[] buf = new byte[2];
      int r = din.read(buf);
      assertEquals(2,r);
      assertEquals('A',buf[0]);
      din.unread(1);
      din.read(buf);
      assertEquals('B',buf[0]);
    } catch (Throwable t) {}
  }
  
}
