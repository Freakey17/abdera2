package org.apache.abdera2.test.common.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.apache.abdera2.common.io.MultipartInputStream;
import org.junit.Test;

public class MultipartInputStreamTest {

  @Test
  public void multipartInputStreamTest() {
    try {
      String data = "MIME-Version: 1.0\nContent-Type: multipart/mixed; boundary=\"frontier\"\n\nThis is a message with multiple parts in MIME format.\n--frontier\nContent-Type: text/plain\n\nThis is the body of the message.";
      ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));
      MultipartInputStream mis = new MultipartInputStream(in,"--frontier".getBytes("UTF-8"));
      
      int r = -1;
      byte[] buf = new byte[100];
      r = mis.read(buf);
      String s = new String(buf,0,r,"UTF-8");

      assertEquals("MIME-Version: 1.0\nContent-Type: multipart/mixed; boundary=\"frontier\"\n\nThis is a message with multiple parts in MIME format.\n",s);
      
      r = -1;
      buf = new byte[10];
      r = mis.read(buf);
      s = new String(buf,0,r,"UTF-8");
      
      assertEquals("Content-Type: text/plain\n\nThis is the body of the message.",s);
      
    } catch (Throwable t) {}
  }
  
}
