package org.apache.abdera2.test;

import static org.junit.Assert.assertNotNull;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.writer.StreamWriter;
import org.junit.Test;

public class AbderaTest {

  @Test
  public void abderaTest() {
    Abdera abdera = Abdera.getInstance();
    assertNotNull(abdera);
    assertNotNull(abdera.getConfiguration());
    assertNotNull(abdera.getFactory());
    assertNotNull(abdera.getParser());
    assertNotNull(abdera.getParserFactory());
    assertNotNull(abdera.getWriter());
    assertNotNull(abdera.getWriterFactory());
    assertNotNull(abdera.getXPath());
    assertNotNull(abdera.newCategories());
    assertNotNull(abdera.newEntry());
    assertNotNull(abdera.newError());
    assertNotNull(abdera.newFeed());
    assertNotNull(abdera.newService());
    assertNotNull(abdera.create(StreamWriter.class));
  }
  
}
