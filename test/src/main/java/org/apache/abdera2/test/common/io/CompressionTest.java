package org.apache.abdera2.test.common.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.abdera2.common.io.Compression;
import org.apache.abdera2.common.io.Compression.CompressionCodec;
import org.junit.Test;

public class CompressionTest {

  @Test
  public void gzipTest() {
    try {
    String orig = "ABCDEFG";
    int len = orig.getBytes("UTF-8").length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    OutputStream wout = Compression.wrap(out, new CompressionCodec[] {CompressionCodec.GZIP});
    wout.write(orig.getBytes("UTF-8"));
    wout.close();
    byte[] data = out.toByteArray();
    
    ByteArrayInputStream in = new ByteArrayInputStream(data);
    InputStream win = Compression.wrap(in, new CompressionCodec[] {CompressionCodec.GZIP});
    data = new byte[len+1];
    int r = win.read(data);
    assertEquals(len,r);
    String done = new String(data,0,r,"UTF-8");
    assertEquals(orig,done);
    } catch (Throwable t) {}
  }
  
  @Test
  public void deflateTest() {
    try {
    String orig = "ABCDEFG";
    int len = orig.getBytes("UTF-8").length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    OutputStream wout = Compression.wrap(out, new CompressionCodec[] {CompressionCodec.DEFLATE});
    wout.write(orig.getBytes("UTF-8"));
    wout.close();
    byte[] data = out.toByteArray();
    
    ByteArrayInputStream in = new ByteArrayInputStream(data);
    InputStream win = Compression.wrap(in, new CompressionCodec[] {CompressionCodec.DEFLATE});
    data = new byte[len+1];
    int r = win.read(data);
    assertEquals(len,r);
    String done = new String(data,0,r,"UTF-8");
    assertEquals(orig,done);
    } catch (Throwable t) {}
  }
  
}
