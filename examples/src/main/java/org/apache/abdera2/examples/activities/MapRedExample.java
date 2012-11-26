package org.apache.abdera2.examples.activities;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;
import static org.apache.abdera2.common.misc.MapRed.asFunction;
import static org.apache.abdera2.common.misc.MapRed.compose;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.common.misc.MapRed;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.misc.MapRed.Collector;
import org.apache.abdera2.common.misc.MapRed.Mapper;
import org.apache.abdera2.common.misc.Pair;
import org.apache.abdera2.common.misc.Pair.PairBuilder;
import org.apache.abdera2.common.misc.MapRed.ReducerFunction;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.parser.ParserOptions;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * A basic example showing the use of the org.apache.abdera2.common.misc.MapRed
 * lightweight MapReduce functionality. This class is designed to provide a 
 * general purpose, *lightweight*, *basic*, *simple* MapReduce capability 
 * designed around working with relatively *small* data sets for basic analysis
 * operations. It is NOT designed to provide an alternative to a full MapReduce
 * implementation such as Hadoop. 
 * 
 * The MapRed class has been tightly integrated with the Guava Libraries
 * Function interface to make it possible to encapsulate a mapreduce 
 * operation within a single Function object. 
 * 
 * In this example, we first pull an Atom feed and convert that into an 
 * Activity Streams. Second, we prepare the input data for the 
 * MapReduce operation which is composed statically and stored in a 
 * final static Function variable. Third, we invoke the Function with 
 * the input data asynchronously using an ExecutorService. The main 
 * thread waits for the operation to complete, then iterates the output,
 * which, in this case, is a summarizaton of the total number of posts
 * per author in the original Atom feed.
 */
public class MapRedExample {
  // Prepare the various functions and store them as static final variables
  
  private final static Function<
    Iterable<Pair<Void, Activity>>, 
    Iterable<Pair<String, Iterable<Integer>>>> f1 =
      compose(
        new MyMapper(),
        MapRed.<String,ASObject>countingReducer()
      );
  
  private final static ReducerFunction<String,Integer,Integer,String> f2 =
        asFunction(MapRed.<String,Integer>invertReducer(), 
        Collections.<Integer>reverseOrder()); 
    
  private final static Function<
      Iterable<Pair<Void, Activity>>, 
      Iterable<Pair<Integer,Iterable<String>>>> f3 = 
        Functions.compose(f2,f1);
     
  private final static ExecutorService exec = 
      getExitingExecutorService(
        (ThreadPoolExecutor) Executors.newCachedThreadPool());
    
  private final static Function<
      Iterable<Pair<Void, Activity>>, 
      Future<Iterable<Pair<Integer,Iterable<String>>>>> ff = 
      MoreFunctions.<
        Iterable<Pair<Void, Activity>>, 
        Iterable<Pair<Integer,Iterable<String>>>>futureFunction(f3,exec);
  
  public static void main(String... args) throws Exception {
    // Read an Atom Feed... this part isn't required.. the mapred stuff
    // works on any activity stream source, this just gives us some 
    // interesting input material
    Abdera abdera = Abdera.getInstance();
    URL url = new URL("http://planet.intertwingly.net/atom.xml");
    Parser parser = abdera.getParser();
    ParserOptions options = 
      parser.makeDefaultParserOptions().charset("UTF-8").get();
    Document<Feed> doc = abdera.getParser().parse(url.openStream(),url.toString(),options);
    Feed feed = doc.getRoot();    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    feed.writeTo("activity", out);
    
    // Convert it to an Activity Stream
    String r = new String(out.toByteArray(),"UTF-8");
    Collection<Activity> col = IO.get().readCollection(new StringReader(r));
    
    // Prepare the input data.. here's where the interesting bit starts...
    // this first step indexes the collection of activities into a Iterable 
    // of Pair objects. A Pair object is essentially a tuple with two elements,
    // called first() and second(). The first() is used as the key in the 
    // Map function, while second() is used as the value. In this particular
    // case, we're using a null key on the input...
    PairBuilder<Void,Activity> gen = 
      Pair.<Void,Activity>make()
        .index(MoreFunctions.<Activity>alwaysVoid(), col.getItems());
        
    // The Function ff is asynchronous... we apply it, then call get on
    // the returned Future to wait for the result. The mapreduce operation
    // occurs in a different thread and sets the value of the Future 
    // when it is complete... once it does, we iterate through the collection
    // of Pairs it kicks out.. which in this case, is a listing of actors 
    // in the stream sorted by number of activities each.
    for (Pair<Integer,Iterable<String>> entry : ff.apply(gen).get())
      System.out.println(
        entry.first() + "=" + entry.second());
    
    
  }
  
  static class MyMapper 
    implements Mapper<Void,Activity,String,ASObject> {
    public void map(
      Void key, 
      Activity val, 
      Collector<String,ASObject> context) {
        String ot = val.getActor().getDisplayName();
        context.collect(ot!=null?ot:"", val.getActor());
    }    
  }
}
