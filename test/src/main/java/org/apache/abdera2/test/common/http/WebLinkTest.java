package org.apache.abdera2.test.common.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.abdera2.common.http.WebLink;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;

import com.google.common.collect.Iterables;
import org.apache.abdera2.common.text.InvalidCharacterException;

public class WebLinkTest {

  @Test
  public void simplecss() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals(new IRI("simple.css"),link.getIri());
    assertThat(link.getRel(),hasItem("stylesheet"));
  }
  
  @Test(expected=InvalidCharacterException.class)
  public void simplecssreversed() {
    WebLink.parse("rel=stylesheet; <fail.css>");
  }
 
  @Test
  public void simplecsssq() {
    Iterable<WebLink> il = WebLink.parse("<fail.css>; rel='stylesheet'");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertThat(link.getRel(),not(hasItem("stylesheet"))); // it will be 'stylesheet', which is incorrect
  }
  
  @Test
  public void simplecssmrel() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; rel=\"foobar stylesheet\"");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals(new IRI("simple.css"),link.getIri());
    assertThat(link.getRel(),hasItems("stylesheet","foobar"));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void simplecssmlink() {
    Iterable<WebLink> il = WebLink.parse("<foo>; rel=bar, <simple.css>; rel=stylesheet");
    assertEquals(2,Iterables.size(il));
    for (WebLink link : il) {
      assertThat(link.getIri().toString(), anyOf(is("foo"),is("simple.css")));
      assertThat(link.getRel(), anyOf(hasItems("bar"),hasItems("stylesheet")));
    }
  }
  
  @Test
  public void simplecssanchr() {
    Iterable<WebLink> il = WebLink.parse("<fail.css>; anchor=\"http://example.com/\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("fail.css",link.getIri().toString());
    assertEquals("http://example.com/fail.css",link.getResolvedIri(new IRI("http://foo.com/")).toString());
    // this is a redflag! this IRI resolved to a different base URI than
    // what was passed in! applications need to take great care as this
    // could be a possible attack vector!
  }
  
  @Test
  public void simplecssanchrsame() {
    Iterable<WebLink> il = WebLink.parse("<fail.css>; anchor=\"\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("fail.css",link.getIri().toString());
    assertEquals("http://foo.com/fail.css",link.getResolvedIri(new IRI("http://foo.com/")).toString());
    // this is an appropriate response
  }
  
  @Test
  public void simplecssanchrsame2() {
    Iterable<WebLink> il = WebLink.parse("<fail.css>; anchor=\"\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("fail.css",link.getIri().toString());
    assertEquals("http://foo.com/fail.css",link.getResolvedIri(new IRI("http://foo.com/#foo")).toString());
    // this is an appropriate response
  }  
  
  @Test
  public void simplecssanchrsamefrag() {
    Iterable<WebLink> il = WebLink.parse("<fail.css>; anchor=\"#foo\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("fail.css",link.getIri().toString());
    assertEquals("http://foo.com/fail.css",link.getResolvedIri(new IRI("http://foo.com/")).toString());
    // this is an appropriate response
  }  
  
  @Test
  public void simplecssanchrsamefrag2() {
    Iterable<WebLink> il = WebLink.parse("<fail.css>; anchor=\"#foo\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("fail.css",link.getIri().toString());
    assertEquals("http://foo.com/fail.css",link.getResolvedIri(new IRI("http://foo.com/#foo")).toString());
    // this is an appropriate response
  }  
  
  @Test
  public void simplexslttypenotype() {
    Iterable<WebLink> il = WebLink.parse("<simple.xslt>; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("simple.xslt",link.getIri().toString());
    assertThat(link.getRel(),hasItem("stylesheet"));
  }
  
  @Test
  public void simplexslttypedepr() {
    Iterable<WebLink> il = WebLink.parse("<simple.xslt>; rel=stylesheet; type=\"text/xsl\"");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("simple.xslt",link.getIri().toString());
    assertThat(link.getRel(),hasItem("stylesheet"));
    assertTrue(MimeTypeHelper.isMatch(link.getMediaType(), MimeTypeHelper.create("text/xsl")));
  }
     
  @Test
  public void simplexslttypedepr2() {
    Iterable<WebLink> il = WebLink.parse("<simple.xslt.asis>; rel=stylesheet; type=\"text/xsl\"");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("simple.xslt.asis",link.getIri().toString());
    assertThat(link.getRel(),hasItem("stylesheet"));
    assertTrue(MimeTypeHelper.isMatch(link.getMediaType(), MimeTypeHelper.create("text/xsl")));
  }
  
  @Test
  public void simplexslttypeoff() {
    Iterable<WebLink> il = WebLink.parse("<simple.xslt>; rel=stylesheet; type=\"application/xslt+xml\"");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("simple.xslt",link.getIri().toString());
    assertThat(link.getRel(),hasItem("stylesheet"));
    assertTrue(MimeTypeHelper.isMatch(link.getMediaType(), MimeTypeHelper.create("application/xslt+xml")));
  }
  
  @Test
  public void simplecsstitle() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; rel=stylesheet; title=\"A simple CSS stylesheet\"");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("simple.css",link.getIri().toString());
    assertThat(link.getRel(),hasItem("stylesheet"));
    assertEquals(link.getTitle(),"A simple CSS stylesheet");
  }
  
  @Test
  public void simplecsstitleq() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; rel=stylesheet; title=\"title with a DQUOTE \\\" and backslash: \\\\\"");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("title with a DQUOTE \" and backslash: \\",link.getTitle());
  }
  
  @Test
  public void simplecsstitleq2() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; title=\"title with a DQUOTE \\\" and backslash: \\\\\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("title with a DQUOTE \" and backslash: \\",link.getTitle());
  }
  
  @Test
  public void simplecsstitletok() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; rel=stylesheet; title=AsimpleCSSstylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("AsimpleCSSstylesheet",link.getTitle());
    // not strictly allowed per the spec
  }
  
  @Test
  public void simplecsstitle5987() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; rel=stylesheet; title*=UTF-8''stylesheet-%E2%82%AC");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("stylesheet-\u20AC",link.getTitle());
  }
  
  @Test
  public void simplecsstitle5987r() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; title*=UTF-8''stylesheet-%E2%82%AC; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("stylesheet-\u20AC",link.getTitle());
  } 
  
  @Test
  public void simplecsstitle5987iso88591() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; title*=iso-8859-1''stylesheet-%E4; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("stylesheet-\u00E4",link.getTitle());
  } 
  
  @Test
  public void simplecsstitle5987noenc() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; title*=''A%20simple%20CSS%20stylesheet; title=\"fallback title\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("fallback title", link.getTitle());
    // this passes, but not for the reason its supposed to
  }
  
  @Test
  public void simplecsstitle5987parseerror() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; title*=foobar; title=\"fallback title\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("fallback title", link.getTitle());
 // this passes, but not for the reason its supposed to
  }
  
  @Test
  public void simplecsstitle5987parseerror2() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; title*=UTF-8''foobar%; title=\"fallback title\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("fallback title", link.getTitle());
 // this passes, but not for the reason its supposed to
  }
  
  @Test
  public void simpleext() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; ext=foo; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("foo",link.getParam("ext"));
  }
  
  @Test
  public void simpleextq() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; ext=\"\\\"\"; rel=stylesheet");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertEquals("\"",link.getParam("ext"));
  }
  
// This isn't supported
//  @Test
//  public void simpleexta() {
//    Iterable<WebLink> il = WebLink.parse("<simple.css>; ext1='start; rel=stylesheet; ext2=end'");
//    assertEquals(1,Iterables.size(il));
//    WebLink link = il.iterator().next();
//    System.out.println(link.getParam("ext1"));
//  }
  
  @Test
  public void simpleextrel() {
    Iterable<WebLink> il = WebLink.parse("<simple.css>; rel=\"http://example.com/myrel stylesheet\"");
    assertEquals(1,Iterables.size(il));
    WebLink link = il.iterator().next();
    assertThat(link.getRel(),hasItems("stylesheet","http://example.com/myrel"));
  }
  
  @Test
  public void simplecss2() {
    Iterable<WebLink> il = WebLink.parse("<ybg.css>; rel=stylesheet, <simple.css>; rel=stylesheet");
    assertEquals(2, Iterables.size(il));
  }
  
  @Test
  public void simplecssafterother() {
    Iterable<WebLink> il = WebLink.parse("<ybf.css>; rel=foobar, <simple.css>; rel=stylesheet");
    assertEquals(2, Iterables.size(il));
  }
}
