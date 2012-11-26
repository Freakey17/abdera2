package org.apache.abdera2.test.common.http;

import org.apache.abdera2.common.http.Preference;
import static org.apache.abdera2.common.http.Preference.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.google.common.collect.Iterables;

public class PreferenceTest {

  @Test
  public void testPreference() {
    
    String prefs = 
      Preference.toString(
        PREF_LENIENT, 
        WAIT(10), 
        PREF_RETURN_ASYNCH,
        make("A","B").param("B", "c").param("C","fo\u00F6 bar baz").get());

    assertEquals("lenient,wait=10,return-asynch,a=B;b=c;c*=UTF-8''fo%C3%B6%20bar%20baz",prefs);
    
    Iterable<Preference> list = parse(prefs);
    assertTrue(contains(list, WAIT(10)));
    assertTrue(contains(list, PREF_LENIENT));
    assertTrue(contains(list, PREF_RETURN_ASYNCH));
    assertTrue(contains(list, make("A").get()));
    
    Preference wait = get(list,"wait");
    assertEquals(10,wait.getIntValue());
    
    Preference a = get(list,"a");
    assertEquals("B",a.getValue());
    assertTrue(a.hasParam("b"));
    assertEquals("c",a.getParam("B"));
    assertEquals("fo\u00F6 bar baz", a.getParam("C"));    
    list = parse("lenient , wait=;foo=");
    assertEquals(2,Iterables.size(list));
    
  }
  
  @Test
  public void simplepref() {
    Iterable<Preference> list = parse("lenient");
    assertEquals(1,Iterables.size(list));
    Preference pref = list.iterator().next();
    assertEquals("lenient",pref.getToken());
  }
  
  @Test
  public void simpleprefval() {
    Iterable<Preference> list = parse("wait=10");
    assertEquals(1,Iterables.size(list));
    Preference pref = list.iterator().next();
    assertEquals("wait",pref.getToken());
    assertEquals(10,pref.getIntValue());
  }
  
  @Test
  public void simpleprefvalq() {
    Iterable<Preference> list = parse("foo=\"bar\"");
    assertEquals(1,Iterables.size(list));
    Preference pref = list.iterator().next();
    assertEquals("foo",pref.getToken());
    assertEquals("bar",pref.getValue());
  }
  
  @Test
  public void simpleprefparam() {
    Iterable<Preference> list = parse("foo; bar");
    assertEquals(1,Iterables.size(list));
    Preference pref = list.iterator().next();
    assertEquals("foo", pref.getToken());
    assertTrue(pref.hasParam("bar"));
    assertEquals("",pref.getParam("bar"));
  }
  
  @Test
  public void simpleprefparamval() {
    Iterable<Preference> list = parse("foo; bar=1");
    assertEquals(1,Iterables.size(list));
    Preference pref = list.iterator().next();
    assertEquals("foo", pref.getToken());
    assertTrue(pref.hasParam("bar"));
    assertEquals(1,pref.getIntParam("bar"));
  }
  
  @Test
  public void simpleprefparamvalq() {
    Iterable<Preference> list = parse("foo; bar=\"testing \\\" testing \\\\\"");
    assertEquals(1,Iterables.size(list));
    Preference pref = list.iterator().next();
    assertEquals("foo", pref.getToken());
    assertTrue(pref.hasParam("bar"));
    assertEquals("testing \" testing \\",pref.getParam("bar"));
  }
  
  @Test
  public void simpleprefvalparamval() {
    Iterable<Preference> list = parse("foo=10; bar=\"testing \\\" testing \\\\\"");
    assertEquals(1,Iterables.size(list));
    Preference pref = list.iterator().next();
    assertEquals("foo", pref.getToken());
    assertEquals(10, pref.getIntValue());
    assertTrue(pref.hasParam("bar"));
    assertEquals("testing \" testing \\",pref.getParam("bar"));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void multipref() {
    Iterable<Preference> list = parse("lenient, wait=10; bar=\"foo\"");
    assertEquals(2, Iterables.size(list));
    for (Preference pref : list) {
      assertThat(pref.getToken(),anyOf(is("lenient"),is("wait")));
      if ("wait".equals(pref.getToken())) {
        assertEquals(10,pref.getIntValue());
        assertTrue(pref.hasParam("bar"));
        assertEquals("foo",pref.getParam("bar"));
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void multiprefr() {
    Iterable<Preference> list = parse("wait=10; bar=\"foo\", lenient");
    assertEquals(2, Iterables.size(list));
    for (Preference pref : list) {
      assertThat(pref.getToken(),anyOf(is("lenient"),is("wait")));
      if ("wait".equals(pref.getToken())) {
        assertEquals(10,pref.getIntValue());
        assertTrue(pref.hasParam("bar"));
        assertEquals("foo",pref.getParam("bar"));
      }
    }
  }
}
