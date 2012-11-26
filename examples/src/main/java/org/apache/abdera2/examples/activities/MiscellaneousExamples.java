package org.apache.abdera2.examples.activities;

import org.apache.abdera2.activities.extra.ASContext;
import org.apache.abdera2.activities.extra.Extra;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Activity.ActivityBuilder;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.common.templates.Template;

public class MiscellaneousExamples {

  public static void main(String... args) throws Exception {
    
    // Every object and activity has an "id" property.. when
    // an object gets passed around through multiple parties,
    // the "id" of the object may change but the object itself
    // stays the same. The changing id can make it difficult to
    // detect duplicates of the same object so the Activity
    // Streams specification provides two properties called
    // "downstreamDuplicates" and "upstreamDuplicates", both of
    // which are arrays that can be used to record to known 
    // id's of a single object. A basic equals() check on the
    // Activity cannot determine if the objects are actually
    // duplicates with different ids. For that, we provide 
    // two alternative Equivalence functions, one that bases
    // it's decision solely on the values of the "id" properties,
    // and another that properly compares all known ids recorded
    // using the "id", "downstreamDuplicates" and "upsteamDuplicates"
    // property values.
    
    // first we create two activities that have different "id" values,
    // but share common values in their downstream and upstream duplicates.
    // basically, we're saying that an object with the "id":"baz" is a 
    // known derivative of the first Activity object we create with "id":"foo", 
    // and we're saying that an Activity with the "id":"bar" is itself a 
    // derivative of "id":"baz" .... so even those our two activities
    // do not know about each other directly, we can related the two
    // and detect that they are likely duplicates of one another
    
    Activity a1 = 
      Activity.makeActivity()
        .id("foo")
        .downstreamDuplicate("baz")
        .get();
    
    Activity a2 = 
      Activity.makeActivity()
       .id("bar")
       .upstreamDuplicate("baz")
       .get();
    
    System.out.println(Extra.IDENTITY_EQUIVALENCE.equivalent(a1, a2)); // false
    System.out.println(Extra.IDENTITY_WITH_DUPLICATES_EQUIVALENCE.equivalent(a1, a2)); // true
    
    
    // All of the Activity objects are immutable thread-safe instances,
    // which means editing the data in an object is a bit more difficult 
    // that just calling a setter...
    // suppose we want to add a property to one of the activities
    // we created above... we can do so by using the activity as a template
    // for creating a new Activity object
    
    Activity a3 = a1.<Activity,ActivityBuilder>template().set("foo", "bar").get();
    a3.writeTo(System.out);
    
    // it's also possible to merge two objects into a forth of the 
    // same or different type... when merging, properties contained
    // in the passed in object will replace those contained in the 
    // original source object. The merge is not deep...
    
    Activity a4 = a1.as(Activity.class, a3);
    a4.writeTo(System.out);
    
    
    
    // Working with URI Templates.. the ASContext class
    // provides a URI Template Context implementation
    // that wraps an Activity Streams object, making it 
    // easier to construct IRI/URI's using properties
    // contained within an activity stream. For instance,
    // the Google+ API specifies a "nextPageToken" property
    // within the root collection object that is used to
    // construct the URL for the next page in a paged 
    // collection of stream documents. Using ASContext,
    // it's a simple matter to pull that out and construct
    // the url for the next page.
    
    Collection<Activity> collection = 
      Collection.<Activity>makeCollection()
        .set("nextPageToken", "foo")
        .get();
    
    Template template = 
      new Template("http://example.org/stuff{?nextPageToken}");
    
    System.out.println(
      template.expand(
        new ASContext(collection)));
    
  }
  
}
