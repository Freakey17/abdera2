package org.apache.abdera2.test.common.io;

import static org.junit.Assert.assertEquals;

import java.io.CharArrayReader;

import org.apache.abdera2.common.io.FilteredCharReader;
import org.apache.abdera2.common.text.CharUtils.Profile;
import org.junit.Test;

public class FilteredCharReaderTest {

  @Test
  public void filteredCharReaderTest() {
    try {
      char[] cs = {0x0,'A',0x0,'B'};
      CharArrayReader car = new CharArrayReader(cs);
      FilteredCharReader fcar = new FilteredCharReader(car,Profile.XML1RESTRICTED);
      char[] data = new char[2];
      fcar.read(data);
      assertEquals('A',data[0]);
      assertEquals('B',data[1]);
    } catch (Throwable t) {}
  }
  
}
