package org.apache.abdera2.examples.activities;

import org.apache.abdera2.common.anno.Context;
import org.apache.abdera2.common.anno.Param;
import org.apache.abdera2.common.anno.URITemplate;

import org.apache.abdera2.activities.client.ActivitiesClient;
import org.apache.abdera2.activities.model.ASDocument;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import static org.apache.abdera2.common.templates.Template.expandAnnotated;
import static java.lang.System.out;
import static java.lang.String.format;

@URITemplate("https://www.googleapis.com/plus/v1/people/{userid}/activities/{collection}{?key}&fields={+fields}")
@Context({
  @Param(name="key",value="{your api key}"),
  @Param(name="collection",value="public"),
  @Param(name="fields",value="items/title")})
public class GooglePlusExample   {
  
  public final String userid = "{user id}";
    
  public static void main(String... args) throws Exception {
    ActivitiesClient cl = 
      new ActivitiesClient();
    try {
      ASDocument<Collection<Activity>> doc = 
        cl.<Activity,Collection<Activity>>getCollection(
          expandAnnotated(
            new GooglePlusExample()));
      Collection<Activity> c = doc.getRoot();
      out.println(c.getProperty("title"));
      int n = 1;
      for (Activity a : c.getItems())
        out.println(
          format("%d. %s", n++, a.getTitle()));
    } finally {      
      cl.shutdown(); 
    }
  }
}
