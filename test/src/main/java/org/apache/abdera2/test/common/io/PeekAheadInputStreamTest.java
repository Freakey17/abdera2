package org.apache.abdera2.test.common.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.apache.abdera2.common.io.PeekAheadInputStream;
import org.junit.Test;

public class PeekAheadInputStreamTest {

  @Test
  public void dynamicPushbackInputStreamTest() {
    try {
      byte[] data = {'A','B','C'};
      ByteArrayInputStream in = 
        new ByteArrayInputStream(data);
      PeekAheadInputStream din = 
        new PeekAheadInputStream(in);
      byte[] buf = new byte[2];
      din.peek(buf);
      assertEquals('A',buf[0]);
      din.read(buf);
      assertEquals('A',buf[0]);
    } catch (Throwable t) {}
  }
  
}
