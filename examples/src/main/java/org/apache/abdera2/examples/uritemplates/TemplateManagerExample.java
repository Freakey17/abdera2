package org.apache.abdera2.examples.uritemplates;

import org.apache.abdera2.common.templates.MapContext;
import org.apache.abdera2.common.templates.TemplateManager;

/**
 * This example shows the use of the new TemplateManager mechanism for 
 * expanding templates based on a key. This is particularly useful 
 * for applications that require a consistent strategy for URI/IRI 
 * generation
 */
public class TemplateManagerExample {

  public enum Keys { A,B }
  
  public static void main(String... args) throws Exception {
    
    MapContext defaults = new MapContext();
    defaults.put("a", "foo");
    defaults.put("b", "bar");
    
    TemplateManager<Keys> tm = 
      TemplateManager.<Keys>make()
        .withDefaults(defaults)
        .add(Keys.A, "http://example.org{?a}")
        .add(Keys.B, "https://example.org{?b}")
        .get();
    
    System.out.println("Template for Keys.A using Default Context: " + tm.expand(Keys.A));
    System.out.println("Template for Keys.B using Default Context: " + tm.expand(Keys.B));
    
    MapContext other = new MapContext();
    other.put("b", "baz"); // override the default context value
    
    System.out.println("Template for Keys.A using Other Context: " + tm.expand(Keys.A, other));
    System.out.println("Template for Keys.B using Other Context: " + tm.expand(Keys.B, other));
  }
  
}
