package org.apache.abdera2.test.common.misc;

import static org.apache.abdera2.common.misc.MapRed.asFunction;
import static org.apache.abdera2.common.misc.MapRed.compose;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.abdera2.activities.extra.Extra;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.objects.PersonObject;
import org.apache.abdera2.common.misc.ArrayBuilder;
import org.apache.abdera2.common.misc.Chain;
import org.apache.abdera2.common.misc.Comparison;
import org.apache.abdera2.common.misc.Comparisons;
import org.apache.abdera2.common.misc.MapRed;
import org.apache.abdera2.common.misc.MoreExecutors2;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.misc.Pair;
import org.apache.abdera2.common.misc.Task;
import org.apache.abdera2.common.misc.MapRed.Collector;
import org.apache.abdera2.common.misc.MapRed.Mapper;
import org.apache.abdera2.common.misc.MapRed.ReducerFunction;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class MiscTest {

  @Test
  public void multiIteratorTest() {
    Set<String> a1 = new LinkedHashSet<String>();
    a1.add("a");
    a1.add("b");
    a1.add("c");
    Set<String> a2 = new LinkedHashSet<String>();
    a2.add("d");
    a2.add("e");
    a2.add("f");
    Iterator<String> mi = 
      Iterators.concat(a1.iterator(),a2.iterator());
    assertEquals("a",mi.next());
    assertEquals("b",mi.next());
    assertEquals("c",mi.next());
    assertTrue(mi.hasNext());
    assertEquals("d",mi.next());
    assertEquals("e",mi.next());
    assertEquals("f",mi.next());
    assertFalse(mi.hasNext());
  }
  
  @Test
  public void arrayBuilderTest() {
    //Set array builder (no duplicates)
	ArrayBuilder<String> ab = ArrayBuilder.set(String.class);
    String[] array = 
    	ab      
        .add("a")
        .add("a")
        .add("b")
        .add("c")
        .build();
    assertEquals(3, array.length);
    assertEquals("a", array[0]);
    assertEquals("b", array[1]);
    assertEquals("c", array[2]);
    
    //List array (duplicates)
    ab = ArrayBuilder.list(String.class);
    array = 
    	ab
        .add("a")
        .add("a")
        .add("b")
        .add("c")
        .build();
    assertEquals(4, array.length);
    assertEquals("a", array[0]);
    assertEquals("a", array[1]);
    assertEquals("b", array[2]);
    assertEquals("c", array[3]);
    
  }
  
  @Test
  public void chainTest() {
    final AtomicInteger i = new AtomicInteger(0);
    ArrayBuilder<Task<String,String>> tasks = 
      ArrayBuilder.list(Task.class);
    for (int n = 0; n < 10; n++) 
      tasks.add(
        new Task<String,String>() {
          public String apply(String input, Chain<String,String> flow) {
            i.getAndIncrement();
            return flow.next(input);
          }
        });
    Chain<String,String> chain = 
      Chain.<String,String>make()
        .via(tasks.build())
        .to(new Function<String,String>() {
          public String apply(String input) {
            i.getAndIncrement();
            return input.toUpperCase(Locale.US);
          }
        })
        .get();
    String v = chain.apply("a");
    assertEquals("A",v);
    assertEquals(11, i.get());
  }
  
  @Test
  public void comparisonTest() {
    assertTrue(Comparisons.<String>bothApply(Predicates.<String>notNull()).apply("A", "B"));
    assertFalse(Comparisons.<String>bothApply(Predicates.<String>notNull()).apply("A", null));
    assertFalse(Comparisons.<String>bothApply(Predicates.<String>notNull()).apply(null, "B"));
    
    assertTrue(Comparisons.<String>bothAreNull().apply(null, null));
    assertFalse(Comparisons.<String>bothAreNull().apply("A", null));
    assertFalse(Comparisons.<String>bothAreNull().apply(null, "B"));
    
    assertTrue(Comparisons.<String>onlyFirstIsNull().apply(null, "A"));
    assertFalse(Comparisons.<String>onlyFirstIsNull().apply(null, null));
    assertFalse(Comparisons.<String>onlyFirstIsNull().apply("A", null));
    assertFalse(Comparisons.<String>onlyFirstIsNull().apply("A", "B"));
    
    assertTrue(Comparisons.<String>onlySecondIsNull().apply("A", null));
    assertFalse(Comparisons.<String>onlySecondIsNull().apply(null, null));
    assertFalse(Comparisons.<String>onlySecondIsNull().apply(null, "B"));
    assertFalse(Comparisons.<String>onlySecondIsNull().apply("A", "B"));
    
    assertTrue(Comparisons.<String>onlyOneIsNull().apply("A", null));
    assertFalse(Comparisons.<String>onlyOneIsNull().apply(null, null));
    assertTrue(Comparisons.<String>onlyOneIsNull().apply(null, "B"));
    assertFalse(Comparisons.<String>onlyOneIsNull().apply("A", "B"));
    
    assertFalse(Comparisons.<String>neitherIsNull().apply("A", null));
    assertFalse(Comparisons.<String>neitherIsNull().apply(null, null));
    assertFalse(Comparisons.<String>neitherIsNull().apply(null, "B"));
    assertTrue(Comparisons.<String>neitherIsNull().apply("A", "B"));
    
    assertFalse(Comparisons.eitherApply(Predicates.isNull()).apply("A", "B"));
    assertTrue(Comparisons.eitherApply(Predicates.isNull()).apply(null, "B"));
    assertTrue(Comparisons.eitherApply(Predicates.isNull()).apply("A", null));
    assertTrue(Comparisons.eitherApply(Predicates.isNull()).apply(null, null));
    
    Comparison<String> equalsIgnoreCase = 
      new Comparison<String>() {
        public boolean apply(String r1, String r2) {
          return r1.equalsIgnoreCase(r2);
        }
    };
    assertTrue(equalsIgnoreCase.apply("A", "a"));
    
    Comparison<String> c = 
      Comparisons.<String>neitherIsNull().and(equalsIgnoreCase);
    assertTrue(c.apply("A", "a"));
    assertFalse(c.apply(null, "a"));
    assertFalse(c.apply("a", null));
    
  }
  
  @Test
  public void testMoreFunctions() {
    // Up first... Choice
    Function<String,Integer> choice = 
      MoreFunctions.<String,Integer>choice()
      .of(Comparisons.equalsIgnoreCase().predicateFor("A"),1)
      .of(Comparisons.equalsIgnoreCase().predicateFor("B"),2)
      .otherwise(3)
      .get();
    assertEquals(Integer.valueOf(1),choice.apply("a"));
    assertEquals(Integer.valueOf(2),choice.apply("b"));
    assertEquals(Integer.valueOf(3),choice.apply("c"));
    
    // Test createInstance
    Foo foo = MoreFunctions.createInstance(Foo.class).apply(null);
    assertNotNull(foo);
    
    foo = MoreFunctions.createInstance(Foo.class,String.class).apply(MoreFunctions.array("A"));
    assertNotNull(foo);
    assertEquals("A",foo.a());
    
    // Test each
    String[] array = MoreFunctions.array("a","b","c","d");
    array = MoreFunctions
      .eachArray(new Function<String,String>() {
        public String apply(String input) {
          return input.toUpperCase();
        }
      },String.class).apply(array);
    assertEquals("A",array[0]);
    assertEquals("B",array[1]);
    assertEquals("C",array[2]);
    assertEquals("D",array[3]);
    
    // Test firstNonNull...
    assertEquals("A",MoreFunctions.firstNonNull(null,null,null,"A"));
    
    // Test futureFunction... executes the function in a separate 
    // thread using the passed in ExecutorService
    Future<String> future = 
      MoreFunctions.<String,String>futureFunction(
        new Function<String,String>() {
          public String apply(String input) {
            System.out.println("Thread sleeping....");
            try {
              Thread.sleep(10*1000);
            } catch (Throwable t) {}
            return input;
          }
        }, 
        MoreExecutors2.getExitingExecutor()).apply("A");
    try {
      System.out.println("Waiting for return...");
      assertEquals("A",future.get());
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
    
  }
  
  @Test 
  public void testPair() {
    Pair<String,String> pair = Pair.of("A", "B");
    assertEquals("A",pair.first());
    assertEquals("B",pair.second());
    
    Map<String,String> map = new HashMap<String,String>();
    map.put("A","B");
    map.put("B", "C");
    Iterable<Pair<String,String>> pairs = Pair.from(map);
    assertEquals(2, Iterables.size(pairs));
    assertEquals("A", Iterables.get(pairs,0).first());
    assertEquals("B", Iterables.get(pairs,0).second());
    assertEquals("B", Iterables.get(pairs,1).first());
    assertEquals("C", Iterables.get(pairs,1).second());
  }
  
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
    MoreExecutors2.getExitingExecutor();
  
  private final static Function<
    Collection<Activity>, 
    Future<Iterable<Pair<Integer,Iterable<String>>>>> ff = 
    Functions.compose(
    MoreFunctions.<
      Iterable<Pair<Void, Activity>>, 
      Iterable<Pair<Integer,Iterable<String>>>>futureFunction(f3,exec),
    Extra.<Activity>pairIndexer());

  private Activity getActivity(String name,int n) {
    return Activity.makeActivity()
      .actor(PersonObject.makePerson(name))
      .id(String.format("urn:%s:%s",name,n))
      .get();
  }
  
  @Test
  public void testMapRed() throws Exception {
    Collection<Activity> col = 
      Collection.<Activity>makeCollection()
       .item(getActivity("Joe",1))
       .item(getActivity("Joe",2))
       .item(getActivity("Mark",3))
       .item(getActivity("Mark",4))
       .item(getActivity("Sally",5))
       .get();
    
    // This is basically MapReduce contained within a Function,
    // Runs asynch using exiting executorservice... call to 
    // ff.apply(gen).get() hides all the magic...
    // this particular function looks at the activity stream
    // and counts the number of activities per actor
    
    Iterable<Pair<Integer,Iterable<String>>> ret = ff.apply(col).get();
    
    Pair<Integer,Iterable<String>> first = Iterables.get(ret,0);
    Pair<Integer,Iterable<String>> second = Iterables.get(ret,1);
    assertEquals(Integer.valueOf(2),first.first());
    assertThat(first.second(),hasItems("Joe","Mark"));
    assertEquals(Integer.valueOf(1),second.first());
    assertThat(second.second(),hasItems("Sally"));
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
  
  public static class Foo {
    private final String a;
    public Foo() {
      this.a = null;
    }
    public Foo(String a) {
      this.a = a;
    }
    public String a() {
      return a;
    }
  }
}
