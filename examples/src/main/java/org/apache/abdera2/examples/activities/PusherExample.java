package org.apache.abdera2.examples.activities;

import static java.lang.String.format;
import static org.apache.abdera2.activities.model.Activity.makeActivity;
import static org.apache.abdera2.activities.model.objects.NoteObject.makeNote;
import static org.apache.abdera2.activities.model.objects.PersonObject.makePerson;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Activity.ActivityBuilder;
import org.apache.abdera2.activities.model.Verb;
import static org.apache.abdera2.common.misc.MoreExecutors2.getExitingExecutor;
import org.apache.abdera2.common.pusher.ChannelManager;
import org.apache.abdera2.common.pusher.Pusher;
import org.apache.abdera2.common.pusher.Receiver;
import org.apache.abdera2.common.pusher.SimpleChannelManager;
import org.apache.abdera2.common.pusher.SimpleListener;

public class PusherExample {

  private static final ActivityBuilder gen = 
    makeActivity()
    .actor(makePerson().displayName("joe").get())
    .verb(Verb.POST);
  
  public static void main(String... args) throws Exception {
    
    final ChannelManager cm = new SimpleChannelManager();
    ExecutorService exec = getExitingExecutor();
    final CountDownLatch latch = new CountDownLatch(3);
    exec.execute(
      new Runnable() {
        public void run() {
          Receiver<Activity> r = cm.getReceiver("foo");
          r.startListening(
            new SimpleListener<Activity>() {
              public void onItem(Activity t) {
                System.out.println(t.getObject().getDisplayName());
                latch.countDown();
              }
            }
          );
        }
      }
    );
    
    Pusher<Activity> pusher = cm.getPusher("foo");
    for (int n = 0; n < 3; n++) 
      pusher.push(
        gen.template()
          .set("object",
            makeNote()
              .displayName(format("My note #%d",n+1)))
        );
    
    latch.await();
    cm.shutdown();
    
  }
  
}
