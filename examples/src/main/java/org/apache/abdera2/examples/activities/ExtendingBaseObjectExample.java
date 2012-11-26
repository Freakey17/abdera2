package org.apache.abdera2.examples.activities;

import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.activities.model.objects.AdditionalEventProperties;
import org.apache.abdera2.activities.model.objects.EventObject.EventBuilder;
import static org.apache.abdera2.activities.model.objects.EventObject.makeEvent;
import static org.apache.abdera2.activities.model.Collection.makeCollection;
import static org.apache.abdera2.activities.model.objects.PersonObject.makePerson;

/**
 * Quick example that shows how new object types can be
 * created by extending the base object types.. in this
 * case, we create a hypothetical representation of a 
 * Google+ style "hangout" being hosted by user "james"
 * with one other user ("joe") in attendance.
 */
public class ExtendingBaseObjectExample {

  public static void main(String... args) throws Exception {
    makeEvent("hangout")
      .extend(    // dynamically extend the builder using the specified interface...
        AdditionalEventProperties.Builder.class)
          .host(makePerson("James").get())
          .performers(makePerson("Bob").get())
          .<EventBuilder>unwrap()
          .attending(
            makeCollection()
              .item(makePerson("Joe").get())
            .get()).get().writeTo(
              IO.get(),
              System.out,
              "UTF-8");
  }
  
}
