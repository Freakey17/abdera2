package org.apache.abdera2.test.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;

import javax.xml.namespace.QName;
import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.parser.ParserFactory;
import org.apache.abdera2.parser.ParserOptions;
import org.apache.abdera2.parser.filter.BlackListParseFilter;
import org.apache.abdera2.parser.filter.ParseFilter;
import org.junit.Test;

public class ParserTest {

  @Test
  public void testParser() {
    String s = "<a><b/>\u0000<c/></a>";
    Abdera abdera = Abdera.getInstance();
    Parser parser = abdera.getParser();
    assertNotNull(parser);
    ParserOptions options = 
      parser.makeDefaultParserOptions()
       .filterRestrictedCharacters().get();// invalid xml char will be filtered 
    assertNotNull(options);
    Document<Element> doc = parser.parse(new StringReader(s),options);
    assertNotNull(doc);
    Element root = doc.getRoot();
    assertNotNull(root);
    assertEquals("a", root.getQName().getLocalPart());
    
    Element d = root.getFirstChild();
    assertEquals("b", d.getQName().getLocalPart());
    
    // Test Parse Filtering
    ParseFilter filter = 
      BlackListParseFilter
        .make()
        .add(new QName("b"))
        .get();
    options = ParserOptions
      .from(options)
      .filter(filter)
      .get();
    
    doc = parser.parse(new StringReader(s),options);
    root = doc.getRoot();
    Element c = root.getFirstChild();
    assertEquals("c", c.getQName().getLocalPart());
  }
  
  @Test
  public void testParserFactory() {
    Abdera abdera = Abdera.getInstance();
    ParserFactory pf = abdera.getParserFactory();
    Parser parser = pf.getParser("html");
    assertNotNull(parser);
  }
  
}
