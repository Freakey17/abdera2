package org.apache.abdera2.examples.uritemplates;

import java.util.List;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.templates.QueryContext;
import org.apache.abdera2.common.templates.Template;

/**
 * This example shows the use of the new QueryContext object to parse 
 * the querystring out of an IRI, modify the query parameters, and 
 * generate a new IRI with the new parameters. This is particularly 
 * useful when an application needs to produce a modified querystring
 * that includes all of the original querystring parameters.
 */
public class QueryContextExample {

  @SuppressWarnings("unchecked")
  public static void main(String... args) throws Exception {
    
    IRI iri = new IRI("http://example.org/foo?a=b&c=d&e=f&e=g");
    
    QueryContext queryContext = new QueryContext(iri);
    
    System.out.println(queryContext); // show the parsed components
        
    queryContext.put("a", "zzz");
    queryContext.put("ext", "ext");
    List<String> list = (List<String>) queryContext.get("e");
    list.add("y");
    
    System.out.println(queryContext); // show the modified query context
    
    Template template = QueryContext.templateFromQuery(iri.toString(), false, queryContext);
    
    System.out.println(template); // show the generated query template
    
    System.out.println(template.expand(queryContext)); // expand with the modified query context;
  }
  
}
