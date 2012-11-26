package org.apache.abdera2.test.writer;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Person;
import org.apache.abdera2.xpath.XPath;
import org.junit.Test;

public class XPathTest {

  @Test
  public void xpathTest() {
    Abdera abdera = Abdera.getInstance();
    Entry entry = abdera.newEntry();
    entry.addAuthor("James");
    entry.addAuthor("Joe");
    XPath xpath = abdera.getXPath();
    List<?> list = xpath.selectNodes("/a:entry/a:author[a:name=\"James\"]", entry);
    Person person = (Person) list.get(0);
    assertEquals("James",person.getName());
  }
  
}
