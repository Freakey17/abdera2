package org.apache.abdera2.examples.activities;

import static org.apache.abdera2.activities.extra.Extra.*;
import org.apache.abdera2.activities.model.Activity;
import static org.apache.abdera2.activities.model.objects.NoteObject.makeNote;
import static org.apache.abdera2.activities.model.objects.PersonObject.makePerson;
import static org.apache.abdera2.activities.model.objects.Objects.FRIENDS;
import static org.apache.abdera2.activities.model.objects.Objects.NETWORK;

public class RepliesAndTargetingExample {

  public static void main(String... args) throws Exception {
    
    Activity activity = 
      Activity.makeActivity()
        .to(makePerson("bob"))
        .cc(FRIENDS("Colleagues"))
        .bto(makePerson("sally"))
        .bcc(NETWORK)
        .inReplyTo(
          makeNote()
            .id("urn:foo:note:1"))
        .get();
    
    activity.writeTo(System.out);
    
    System.out.println(isToMe().apply(activity));
    System.out.println(isTo(makePerson().displayName("bob").get()).apply(activity));
    
    /**
     * {"to":[
     *   {"displayName":"bob",
     *    "objectType":"person"}
     *  ],
     *  "inReplyTo":[
     *    {"id":"urn:foo:note:1",
     *     "objectType":"note"}
     *  ],
     *  "bto":[
     *    {"displayName":"sally",
     *     "objectType":"person"}
     *  ],
     *  "bcc":[
     *    {"objectType":"@network"}
     *  ],
     *  "objectType":"activity",
     *  "cc":[
     *    {"id":"Colleagues",
     *     "objectType":"@friends"}
     *  ]
     * }
     */
  }
  
}
