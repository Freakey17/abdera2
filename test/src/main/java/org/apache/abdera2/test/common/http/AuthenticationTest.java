package org.apache.abdera2.test.common.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Iterator;

import org.apache.abdera2.common.http.Authentication;
import org.junit.Test;

import com.google.common.collect.Iterables;
@SuppressWarnings("unchecked")
public class AuthenticationTest {

  @Test
  public void basicTest() {
    Authentication auth = Authentication.basic("a", "b");
    assertEquals("basic",auth.getScheme());
    assertEquals("YTpi",auth.getBase64Token());
  }
  
  @Test
  public void parseTest() {
    Iterable<Authentication> ia = Authentication.parse("Custom a=b, Custom2 xyz, Custom3 c=\"foo\"");
    Iterator<Authentication> i = ia.iterator();
    Authentication auth = i.next();
    assertEquals("custom",auth.getScheme());
    assertTrue(auth.hasParam("a"));
    assertEquals("b",auth.getParam("a"));
    
    auth = i.next();
    assertEquals("custom2",auth.getScheme());
    assertEquals("xyz",auth.getBase64Token());
    
    auth = i.next();
    assertEquals("custom3",auth.getScheme());
    assertTrue(auth.hasParam("c"));
    assertEquals("foo",auth.getParam("c"));
  }
  
  @Test
  public void simplebasic() {
    Iterable<Authentication> ia = Authentication.parse("Basic realm=\"foo\"");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("foo",auth.getParam("realm"));
  }
  
  @Test
  public void simplebasictok() {
    Iterable<Authentication> ia = Authentication.parse("Basic realm=foo");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("foo",auth.getParam("realm"));
  }
  
  @Test
  public void simplebasiccomma() {
    Iterable<Authentication> ia = Authentication.parse("Basic , realm=foo");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("foo",auth.getParam("realm"));
  }
  
  @Test
  public void simplebasiccomma2() {
    // technically this is invalid, but trying to be liberal in what we accept
    Iterable<Authentication> ia = Authentication.parse("Basic, realm=foo");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("foo",auth.getParam("realm"));
  }
  
  @Test
  public void simplebasicnorealm() {
    // technically this is invalid, but trying to be liberal in what we accept.. it's useless without a realm tho
    Iterable<Authentication> ia = Authentication.parse("Basic");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void simplebasic2realms() {
    // can't have duplicated parameters
    Authentication.parse("Basic realm=\"foo\", realm=\"bar\"");
  }
  
  @Test
  public void simplebasicwsrealm() {
    // technically this is invalid, but trying to be liberal in what we accept
    Iterable<Authentication> ia = Authentication.parse("Basic realm = \"foo\" ");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("foo",auth.getParam("realm"));
  }
  
  @Test
  public void simplebasicrealmsqc() {
    // technically this is invalid, but trying to be liberal in what we accept
    Iterable<Authentication> ia = Authentication.parse("Basic realm = \"\\f\\o\\o\" ");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("foo",auth.getParam("realm"));
  }
  
  @Test
  public void simplebasicrealmsqc2() {
    // technically this is invalid, but trying to be liberal in what we accept
    Iterable<Authentication> ia = Authentication.parse("Basic realm = \"\\\"foo\\\"\" ");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("\"foo\"",auth.getParam("realm"));
  }
  
  @Test
  public void simplebasicnewparam1() {
    Iterable<Authentication> ia = Authentication.parse("Basic realm=\"foo\", bar=\"xyz\",, a=b,,,c=d");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertTrue(auth.hasParam("bar"));
    assertTrue(auth.hasParam("a"));
    assertTrue(auth.hasParam("c"));
    assertEquals("foo",auth.getParam("realm"));
    assertEquals("xyz",auth.getParam("bar"));
    assertEquals("b",auth.getParam("a"));
    assertEquals("d",auth.getParam("c"));
  }
  
  @Test
  public void simplebasicnewparam2() {
    Iterable<Authentication> ia = Authentication.parse("Basic bar=\"xyz\", realm=\"foo\"");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertTrue(auth.hasParam("bar"));
    assertEquals("foo",auth.getParam("realm"));
    assertEquals("xyz",auth.getParam("bar"));
  }
  
  @Test
  public void simplebasicrealmiso88591() {
    Iterable<Authentication> ia = Authentication.parse("Basic realm=\"foo-\u00E4\"");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("foo-\u00E4",auth.getParam("realm"));
  }
  
  @Test
  public void simplebasicrealmutf8() {
    Iterable<Authentication> ia = Authentication.parse("Basic realm=\"foo-\u00E4\u00A4\"");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("foo-\u00E4\u00A4",auth.getParam("realm"));
  }

  @Test
  public void simplebasicrealmrfc2047() {
    Iterable<Authentication> ia = Authentication.parse("Basic realm=\"=?ISO-8859-1?Q?foo-=E4?=\"");
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("foo-\u00E4",auth.getParam("realm"));
  }
  
  @Test
  public void multibasicunknown() {
    Iterable<Authentication> ia = Authentication.parse("Basic realm=\"basic\", Newauth realm=\"newauth\"");
    assertEquals(2,Iterables.size(ia));
    for (Authentication auth : ia) {
      assertThat(auth.getScheme(),anyOf(is("basic"),is("newauth")));
      assertEquals(auth.getScheme(), auth.getParam("realm"));
    }
  }
  
  @Test
  public void multibasicunknown2() {
    Iterable<Authentication> ia = Authentication.parse("Newauth realm=\"newauth\", Basic realm=\"basic\"");
    assertEquals(2,Iterables.size(ia));
    for (Authentication auth : ia) {
      assertThat(auth.getScheme(),anyOf(is("basic"),is("newauth")));
      assertEquals(auth.getScheme(), auth.getParam("realm"));
    }
  }
  
  @Test
  public void multibasicempty() {
    Iterable<Authentication> ia = Authentication.parse(",Basic realm=\"basic\"");
    assertEquals(1,Iterables.size(ia));
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
  }
  
  @Test
  public void multibasicqs() {
    Iterable<Authentication> ia = Authentication.parse("Newauth realm=\"apps\", type=1, title=\"Login to \\\"apps\\\"\", Basic realm=\"simple\"");
    assertEquals(2,Iterables.size(ia));
    for (Authentication auth : ia) {
      assertThat(auth.getScheme(),anyOf(is("basic"),is("newauth")));
      if ("basic".equalsIgnoreCase(auth.getScheme())) {
        assertEquals("simple",auth.getParam("realm"));
      } else if ("newauth".equalsIgnoreCase(auth.getScheme())) {
        assertEquals("apps",auth.getParam("realm"));
        assertEquals("1",auth.getParam("type"));
        assertEquals("Login to \"apps\"", auth.getParam("title"));
      }
    }
  }
  
  @Test
  public void unknown() {
    Iterable<Authentication> ia = Authentication.parse("Newauth realm=\"newauth\"");
    assertEquals(1,Iterables.size(ia));
    Authentication auth = ia.iterator().next();
    assertEquals("newauth",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
  }
  
  @Test
  public void disguisedrealm() {
    Iterable<Authentication> ia = Authentication.parse("Basic foo=\"realm=nottherealm\", realm=\"basic\"");
    assertEquals(1,Iterables.size(ia));
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("basic",auth.getParam("realm"));
  }
  
  @Test
  public void disguisedrealm2() {
    Iterable<Authentication> ia = Authentication.parse("Basic nottherealm=\"nottherealm\", realm=\"basic\"");
    assertEquals(1,Iterables.size(ia));
    Authentication auth = ia.iterator().next();
    assertEquals("basic",auth.getScheme());
    assertTrue(auth.hasParam("realm"));
    assertEquals("basic",auth.getParam("realm"));
  }
  
}
