package org.apache.abdera2.test.common.iri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.abdera2.common.iri.IRI;
import org.junit.Test;

public class IriTest {

  @Test
  public void testIri() {
    IRI iri = new IRI("http://www.example.org:81/Bar/%3F/foo/?foo#bar");
    assertEquals("http",iri.getScheme());
    assertEquals("www.example.org",iri.getHost());
    assertEquals(81,iri.getPort());
    assertEquals("/Bar/%3F/foo/",iri.getPath());
    assertEquals("foo",iri.getQuery());
    assertEquals("bar",iri.getFragment());
    assertTrue(iri.isAbsolute());
  }
  
  @Test
  public void testUriNormalization() {
      assertEquals("http://www.example.org/Bar/%3F/foo/", IRI
          .normalizeString("HTTP://www.EXAMPLE.org:80/foo/../Bar/%3f/./foo/."));
      assertEquals("https://www.example.org/Bar/%3F/foo/", IRI
          .normalizeString("HTTPs://www.EXAMPLE.org:443/foo/../Bar/%3f/./foo/."));
      assertEquals("http://www.example.org:81/Bar/%3F/foo/", IRI
          .normalizeString("HTTP://www.EXAMPLE.org:81/foo/../Bar/%3f/./foo/."));
      assertEquals("https://www.example.org:444/Bar/%3F/foo/", IRI
          .normalizeString("HTTPs://www.EXAMPLE.org:444/foo/../Bar/%3f/./foo/."));
  }
  
}
