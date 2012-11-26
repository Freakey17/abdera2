package org.apache.abdera2.test.common.templates;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.abdera2.common.templates.DefaultingContext;
import org.apache.abdera2.common.templates.MapContext;
import org.apache.abdera2.common.templates.Route;
import org.apache.abdera2.common.templates.Template;
import org.apache.abdera2.common.anno.Name;
import org.junit.Test;

public class TemplateTest {

  private static final String template = "{scheme}://www{.host*}{/path*}{;g}{?a,b}{&c,d}{#e,f}";
  
  @Test
  public void templateTest() {
    
    MapContext map = new MapContext();
    map.put("scheme","http");
    map.put("host", new String[] {"example","org"});
    map.put("path", new String[] {"a","b","c","d"});
    map.put("a",1);
    map.put("b",2);
    map.put("c",3);
    map.put("d",4);
    map.put("e",5);
    map.put("f",6);
    map.put("g",7);
    assertEquals(
      "http://www.example.org/a/b/c/d;g=7?a=1&b=2&c=3&d=4#5,6",
      Template.expand(template,map));
  }
  
  @Test
  public void objectContextTest() {
    Foo foo = new Foo();
    assertEquals(
        "http://www.example.org/a/b/c/d;g=7?a=1&b=2&c=3&d=4#5,6",
        Template.expand(template,foo));
  }
  
  public static class Foo {
    @Name("scheme") public String thescheme = "http";
    public String[] getHost() {
      return new String[] {"example","org"};
    }
    public Iterable<String> getPath() {
      List<String> l = new ArrayList<String>();
      l.add("a");
      l.add("b");
      l.add("c");
      l.add("d");
      return l;
    }
    @Name("a")
    public int get_a() {
      return 1;
    }
    public Integer getB() {
      return Integer.valueOf(2);
    }
    public Integer getC() {
      return 3;
    }
    public int getD() {
      return 4;
    }
    public short getE() {
      return 5;
    }
    public short getF() {
      return 6;
    }
    public long getG() {
      return 7;
    }
  }
  
  @Test
  public void defaultingContextTest() {
    MapContext m1 = new MapContext();
    m1.put("a", "b");
    MapContext m2 = new MapContext();
    m2.put("b", "c");
    m2.put("c", "d");
    DefaultingContext dc = 
      new DefaultingContext(m2,m1);
    assertEquals(dc.resolve("a"),"b");
    assertEquals(dc.resolve("b"),"c");
    m2.put("a", "z"); // changing the main context
    assertEquals(dc.resolve("a"),"z");
    assertEquals(m1.resolve("a"),"b"); // does not change the default
  }
  
  @Test
  public void routeTest() {
    Route<String> route = new Route<String>("A","http://foo/:a/:b/:c");
    Map<String,String> map = route.parse("http://foo/1/2/3");
    assertEquals(map.get("a"),"1");
    assertEquals(map.get("b"),"2");
    assertEquals(map.get("c"),"3");
  }
}
