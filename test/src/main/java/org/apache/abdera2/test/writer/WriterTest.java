package org.apache.abdera2.test.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Person;
import org.apache.abdera2.writer.StreamWriter;
import org.apache.abdera2.writer.Writer;
import org.junit.Test;

public class WriterTest {

  @Test
  public void writerTest() throws IOException {
    Abdera abdera = Abdera.getInstance();
    Entry entry = abdera.newEntry();
    Writer writer = abdera.getWriter();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writer.writeTo(entry, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Document<Entry> doc = abdera.getParser().parse(in);
    entry = doc.getRoot();
    assertNotNull(entry);
  }
  
  @Test
  public void streamWriterTest() throws IOException {
    Abdera abdera = Abdera.getInstance();
    StreamWriter sw = abdera.create(StreamWriter.class);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    sw.setOutputStream(out);
    sw.startDocument()
      .startEntry()
      .writeAuthor("James")
      .endEntry()
      .endDocument()
      .close();
    ByteArrayInputStream in = 
      new ByteArrayInputStream(out.toByteArray());
    Document<Entry> doc = abdera.getParser().parse(in);
    Entry entry = doc.getRoot();
    Person person = entry.getAuthor();
    assertEquals("James",person.getName());
  }
}
