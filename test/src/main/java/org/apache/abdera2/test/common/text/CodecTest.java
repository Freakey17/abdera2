package org.apache.abdera2.test.common.text;

import static org.junit.Assert.assertEquals;

import org.apache.abdera2.common.text.Codec;
import org.junit.Test;

public class CodecTest {

  @Test 
  public void codecTest() {
    String foo = "\u2020abcdefg";
    String bar = Codec.encode(foo,Codec.B);
    assertEquals("=?UTF-8?B?4oCgYWJjZGVmZw==?=", bar);
    assertEquals(foo, Codec.decode(bar));
    
    bar = Codec.encode(foo,Codec.Q);
    assertEquals("=?UTF-8?Q?=E2=80=A0abcdefg?=", bar);
    assertEquals(foo, Codec.decode(bar));
    
    bar = Codec.encode(foo,Codec.STAR);
    assertEquals("UTF-8''%E2%80%A0abcdefg", bar);
    assertEquals(foo, Codec.decode(bar));
  }
  
}
