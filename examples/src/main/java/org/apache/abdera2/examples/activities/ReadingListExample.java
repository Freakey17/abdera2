package org.apache.abdera2.examples.activities;

import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Activity.ActivityBuilder;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.IO;

import static org.apache.abdera2.activities.model.Verb.SAVE;
import static org.apache.abdera2.activities.model.Verb.CONSUME;
import static org.apache.abdera2.activities.model.objects.Objects.EBOOK;
import static org.apache.abdera2.activities.model.objects.Objects.HARDCOVER;
import static org.apache.abdera2.activities.model.Activity.makeActivity;
import static org.apache.abdera2.activities.model.objects.BookObject.makeBook;
import static org.apache.abdera2.activities.model.objects.PersonObject.makePerson;

/**
 * Example that shows a simple practical use of an activity stream
 * to represent a user activity on their reading list. For instance,
 * the "save" verb can be used to indicate that a user has added a 
 * book to their reading list, while the "read" verb can indicate 
 * that the user has read a book from their reading list
 */
public class ReadingListExample {

  public static void main(String... args) throws Exception {
    
    // Building an activity stream for a reading list
    IO io = IO.get();
    
    ActivityBuilder a = 
      makeActivity()
        .actor(makePerson("James"));
 
    Collection.<Activity>makeCollection()
    
    // Add a book we want to read
    .item(
      a.template()
       .set("verb", SAVE)
       .set("object", 
         makeBook()
           .displayName("The Cat in the Hat"))
       .set("format", EBOOK))
    
    // Add a book we just finished
    .item(
      a.template()
       .set("verb", CONSUME)
       .set("object", 
         makeBook()
           .displayName("Meditations on the Method")
           .author(makePerson("Rene Descartes")))
       .set("format", HARDCOVER))
    
    .get().writeTo(io,System.out);
    
  }
  
}
