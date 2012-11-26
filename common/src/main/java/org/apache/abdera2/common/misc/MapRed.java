/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera2.common.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Provides lightweight, standalone MapReduce functionality without
 * requiring Hadoop. This is intended to provide *BASIC* support for 
 * *SIMPLE* MapReduce operations on *SMALL* datasets. If you need 
 * more intensive support, then Hadoop MapReduce is obviously the 
 * better option. 
 * 
 * The MapRed class provides an number of methods for creating and 
 * composing MapReduce operations and bridges those with the Guava
 * Libraries interfaces. For instance, using the various compose()
 * methods, it is possible to create a com.google.common.base.Function
 * that wraps a complete MapReduce operation with a single call to 
 * Function.apply().
 * 
 * Heavy use of Generics makes proper set up a bit tricky and complicated,
 * but once created, the MapReduce functions are immutable and threadsafe
 * and can safely be stored as static final variables used throughout 
 * an application.
 * 
 * A number of common basic Mapper and Reducer objects are also provided.
 * 
 * <pre>
 * Function&lt;Iterable&lt;Pair&lt;Void,String>>,Iterable&lt;Pair&lt;String,Iterable&lt;Integer>>>> f1 = ...
 * Iterable&lt;Pair&lt;String,Iterable&lt;Integer>>> res = f1.apply(gen.get());
 * </pre>
 * 
 * By default, the Function, and the wrapped MapReduce operation, executes 
 * within the current thread and blocks until completion. If you need async
 * operation.. first, you should consider whether you need a full MapReduce 
 * implementation like Hadoop, or, you can use wrap the Function in a 
 * MoreFunctions.FutureFunction... e.g. 
 * 
 * <pre>
 * ExecutorService exec = 
 *   MoreExecutors.getExitingExecutorService(
 *     (ThreadPoolExecutor) Executors.newCachedThreadPool());
 *
 * Function&lt;
 *   Iterable&lt;MapRed.Pair&lt;Void, Activity>>, 
 *   Future&lt;Iterable&lt;MapRed.Pair&lt;Integer,Iterable&lt;String>>>>> ff = 
 *   MoreFunctions.&lt;
 *     Iterable&lt;MapRed.Pair&lt;Void, Activity>>, 
 *     Iterable&lt;MapRed.Pair&lt;Integer,Iterable&lt;String>>>>futureFunction(f3,exec);
 * </pre>
 * 
 * Note, again, the tricky use of generics but defining things in this way
 * ensures type-safety throughout the operation. The FutureFunction.apply() 
 * will submit the operation to the provided Executor and return a Future 
 * whose value will be set once the operation completes. For greater control 
 * over the execution, you can call MoreFunctions.functionTask(...) to get
 * a FutureTask instance that you can submit the ExecutorService yourself.
 */
public final class MapRed {
  
  private MapRed() {}
  
  public static interface Reducer<K2,V2,K3,V3> {
    void reduce(
      K2 key, 
      Iterator<V2> vals,
      Collector<K3,V3> context);
  }
  
  public static interface Mapper<K1,V1,K2,V2> {
    void map(K1 key, V1 val, Collector<K2,V2> context);
  }
  
  public static interface Collector<K,V> {
    void collect(K key, V val);
  }
  
  public static interface MapperFunction<K1,V1,K2,V2>
    extends Function<Iterable<Pair<K1,V1>>,Iterable<Pair<K2,Iterable<V2>>>> {}

  public static interface ReducerFunction<K1,V1,K2,V2>
    extends Function<Iterable<Pair<K1,Iterable<V1>>>,Iterable<Pair<K2,Iterable<V2>>>> {}

  public static <K1,V1,K2,V2,K3,V3>Function<Iterable<Pair<K1,V1>>,Iterable<Pair<K3,Iterable<V3>>>> compose(
    Mapper<K1,V1,K2,V2> mapper, 
    Reducer<K2,V2,K3,V3> reducer) {
      return Functions.compose(asFunction(reducer),asFunction(mapper));
  }

  public static <K1,V1,K2,V2,K3,V3>Function<Iterable<Pair<K1,V1>>,Iterable<Pair<K3,Iterable<V3>>>> compose(
    Mapper<K1,V1,K2,V2> mapper, 
    Reducer<K2,V2,K2,V2> combiner,
    Reducer<K2,V2,K3,V3> reducer) {
      return Functions
        .compose(
          asFunction(reducer),
          Functions
            .compose(
              asFunction(combiner),
              asFunction(mapper)));
  }
  
  public static <K1,V1,K2,V2,V3>Function<Iterable<Pair<K1,V1>>,Iterable<Pair<K2,Iterable<V3>>>> compose(
    Mapper<K1,V1,K2,V2> mapper, 
    Reducer<K2,V2,K2,V3> reducer,
    Comparator<K2> comparator) {
      return Functions.compose(asFunction(reducer,comparator),asFunction(mapper,comparator));
  }
  
  public static <K1,V1,K2,V2,K3,V3>Function<Iterable<Pair<K1,V1>>,Iterable<Pair<K3,Iterable<V3>>>> compose(
    Mapper<K1,V1,K2,V2> mapper, 
    Reducer<K2,V2,K3,V3> reducer,
    Comparator<K2> comparator,
    Comparator<K3> comparator2) {
      return Functions.compose(asFunction(reducer,comparator2),asFunction(mapper,comparator));
  }
  
  public static <K1,V1,K2,V2,K3,V3>Function<Iterable<Pair<K1,V1>>,Iterable<Pair<K3,Iterable<V3>>>> compose(
    Mapper<K1,V1,K2,V2> mapper, 
    Reducer<K2,V2,K2,V2> combiner,
    Reducer<K2,V2,K3,V3> reducer,
    Comparator<K2> comparator,
    Comparator<K3> comparator2) {
      return Functions
        .compose(
          asFunction(reducer, comparator2),
          Functions
            .compose(
              asFunction(combiner, comparator),
              asFunction(mapper,comparator)));
  }
  
  public static <K1,V1,K2,V2,V3>Function<Iterable<Pair<K1,V1>>,Iterable<Pair<K2,Iterable<V3>>>> compose(
    Mapper<K1,V1,K2,V2> mapper, 
    Reducer<K2,V2,K2,V2> combiner,
    Reducer<K2,V2,K2,V3> reducer,
    Comparator<K2> comparator) {
      return Functions
        .compose(
          asFunction(reducer, comparator),
          Functions
            .compose(
              asFunction(combiner, comparator),
              asFunction(mapper,comparator)));
  }
  
  public static <K1,V1,K2,V2,K3,V3>Function<Iterable<Pair<K1,V1>>,Iterable<Pair<K3,Iterable<V3>>>> compose(
    MapperFunction<K1,V1,K2,V2> mapper, 
    ReducerFunction<K2,V2,K3,V3> reducer) {
      return Functions.compose(reducer,mapper);
  }

  public static <K1,V1,K2,V2,K3,V3>Function<Iterable<Pair<K1,V1>>,Iterable<Pair<K3,Iterable<V3>>>> compose(
    MapperFunction<K1,V1,K2,V2> mapper, 
    ReducerFunction<K2,V2,K2,V2> combiner,
    ReducerFunction<K2,V2,K3,V3> reducer) {
      return Functions
        .compose(
          reducer,
          Functions
            .compose(
              combiner,
              mapper));
  }
  
  public static <K1,V1,K2,V2>MapperFunction<K1,V1,K2,V2> asFunction(
      final Mapper<K1,V1,K2,V2> mapper) {
    return asFunction(mapper,false);
  }
  
  public static <K1,V1,K2,V2>MapperFunction<K1,V1,K2,V2> asFunction(
    final Mapper<K1,V1,K2,V2> mapper,
    final Comparator<K2> comparator) {
    return asFunction(mapper,false, comparator);
  }
  
  public static <K1,V1,K2,V2>MapperFunction<K1,V1,K2,V2> asFunction(
    final Mapper<K1,V1,K2,V2> mapper,
    final boolean nulls) {
    return new MapperFunction<K1,V1,K2,V2>() {
      public Iterable<Pair<K2,Iterable<V2>>> apply(Iterable<Pair<K1,V1>> input) {
        SimpleCollector<K2,V2> context = new SimpleCollector<K2,V2>(nulls);
        ImmutableList.Builder<Pair<K2, Iterable<V2>>> list = 
          ImmutableList.builder();
        for (Pair<K1, V1> entry : input)
          mapper.map(entry.first(), entry.second(), context);
        for (Map.Entry<K2, Iterable<V2>> entry : context.collected())
          list.add(Pair.of(entry.getKey(), entry.getValue()));
        return list.build();
      }      
    };
  }
  
  public static <K1,V1,K2,V2>MapperFunction<K1,V1,K2,V2> asFunction(
    final Mapper<K1,V1,K2,V2> mapper,
    final boolean nulls,
    final Comparator<K2> comparator) {
    return new MapperFunction<K1,V1,K2,V2>() {
      public Iterable<Pair<K2,Iterable<V2>>> apply(Iterable<Pair<K1,V1>> input) {
        SimpleCollector<K2,V2> context = new SimpleCollector<K2,V2>(nulls,comparator);
        ImmutableList.Builder<Pair<K2, Iterable<V2>>> list = ImmutableList.builder();
        for (Pair<K1, V1> entry : input)
          mapper.map(entry.first(), entry.second(), context);
        for (Map.Entry<K2, Iterable<V2>> entry : context.collected())
          list.add(Pair.of(entry.getKey(), entry.getValue()));
        return list.build();
      }      
    };
  }

  public static <K1,V1,K2,V2>ReducerFunction<K1,V1,K2,V2> asFunction(
      final Reducer<K1,V1,K2,V2> reducer) {
    return asFunction(reducer,false);
  }
  
  public static <K1,V1,K2,V2>ReducerFunction<K1,V1,K2,V2> asFunction(
      final Reducer<K1,V1,K2,V2> reducer,
      final Comparator<K2> comparator) {
    return asFunction(reducer,false, comparator);
  }

  public static <K1,V1,K2,V2>ReducerFunction<K1,V1,K2,V2> asFunction(
    final Reducer<K1,V1,K2,V2> reducer,
    final boolean nulls,
    final Comparator<K2> comparator) {
    return new ReducerFunction<K1,V1,K2,V2>() {
      public Iterable<Pair<K2,Iterable<V2>>> apply(Iterable<Pair<K1,Iterable<V1>>> input) {
        SimpleCollector<K2,V2> context = new SimpleCollector<K2,V2>(nulls, comparator);
        ImmutableList.Builder<Pair<K2, Iterable<V2>>> list = ImmutableList.builder();
        for (Pair<K1, Iterable<V1>> entry : input)
          reducer.reduce(entry.first(), entry.second().iterator(), context);
        for (Map.Entry<K2, Iterable<V2>> entry : context.collected())
          list.add(Pair.of(entry.getKey(), entry.getValue()));
        return list.build();
      }      
    };
  }
  
  public static <K1,V1,K2,V2>ReducerFunction<K1,V1,K2,V2> asFunction(
    final Reducer<K1,V1,K2,V2> reducer,
    final boolean nulls) {
    return new ReducerFunction<K1,V1,K2,V2>() {
      public Iterable<Pair<K2,Iterable<V2>>> apply(Iterable<Pair<K1,Iterable<V1>>> input) {
        SimpleCollector<K2,V2> context = new SimpleCollector<K2,V2>(nulls);
        ImmutableList.Builder<Pair<K2, Iterable<V2>>> list = ImmutableList.builder();
        for (Pair<K1, Iterable<V1>> entry : input)
          reducer.reduce(entry.first(), entry.second().iterator(), context);
        for (Map.Entry<K2, Iterable<V2>> entry : context.collected())
          list.add(Pair.of(entry.getKey(), entry.getValue()));
        return list.build();
      }      
    };
  }
  
  public static <K,V>Mapper<K,V,V,K> invertMapper() {
    return new Mapper<K,V,V,K>() {
      public void map(K key, V val, Collector<V, K> context) {
        context.collect(val, key);
      }      
    };
  }
  
  public static <K,V>Reducer<K,V,V,K> invertReducer() {
    return new Reducer<K,V,V,K>() {
      public void reduce(K key, Iterator<V> vals, Collector<V, K> context) {
        while(vals.hasNext())
          context.collect(vals.next(), key);
      }      
    };
  }
  
  public static <K,V>Mapper<K,V,K,V> identityMapper() {
    return new Mapper<K,V,K,V>() {
      public void map(K key, V val, Collector<K, V> context) {
        context.collect(key, val);
      }
    };
  }
  
  public static <K,V>Reducer<K,V,K,V> identityReducer() {
    return new Reducer<K,V,K,V>() {
      public void reduce(K key, Iterator<V> vals, Collector<K, V> context) {
        while(vals.hasNext())
          context.collect(key,vals.next());
      }      
    };
  }
  
  public static <K,V>Mapper<K,V,K,V> identityMapper(
    final Predicate<V> predicate) {
    return new Mapper<K,V,K,V>() {
      public void map(K key, V val, Collector<K, V> context) {
        if (predicate.apply(val))
          context.collect(key, val);
      }
    };
  }
  
  public static <K,V>Reducer<K,V,K,V> identityReducer(
    final Predicate<V> predicate) {
    return new Reducer<K,V,K,V>() {
      public void reduce(K key, Iterator<V> vals, Collector<K, V> context) {
        while(vals.hasNext()) {
          V val = vals.next();
          if (predicate.apply(val))
            context.collect(key,val);
        }
      }      
    };
  }
  
  public static <K,V1,V2>Mapper<K,V1,K,V2> functionMapper(
    final Function<V1,V2> transform) {
    return new Mapper<K,V1,K,V2>() {
      public void map(K key, V1 val, Collector<K,V2> context) {
        context.collect(key, transform.apply(val));
      }
    };
  }
  
  public static <K,V1,V2>Reducer<K,V1,K,V2> functionReducer(
    final Function<Iterator<V1>,V2> transform) {
      return new Reducer<K,V1,K,V2>() {
        public void reduce(K key, Iterator<V1> vals, Collector<K, V2> context) {
          context.collect(key, transform.apply(vals)); 
        }        
      };
  }
  
  public static <K,V1,V2>Mapper<K,V1,K,V2> functionMapper(
    final Function<V1,V2> transform,
    final Predicate<V1> predicate) {
    return new Mapper<K,V1,K,V2>() {
      public void map(K key, V1 val, Collector<K,V2> context) {
        if (predicate.apply(val))
          context.collect(key, transform.apply(val));
      }
    };
  }
  
  public static <K1,V1,K2,V2>Mapper<K1,V1,K2,V2> functionMapper(
    final Function<K1,K2> keyTransform,
    final Function<V1,V2> valTransform) {
      return new Mapper<K1,V1,K2,V2>() {
        public void map(K1 key, V1 val, Collector<K2, V2> context) {
          context.collect(keyTransform.apply(key),valTransform.apply(val));         
        }
      };
  }
  
  public static <K1,V1,K2,V2>Reducer<K1,V1,K2,V2> functionReducer(
    final Function<K1,K2> keyTransform,
    final Function<Iterator<V1>,V2> valTransform) {
      return new Reducer<K1,V1,K2,V2>() {
        public void reduce(K1 key, Iterator<V1> vals, Collector<K2, V2> context) {
          context.collect(keyTransform.apply(key), valTransform.apply(vals)); 
        }        
      };
  }
  
  public static <K1,V1,K2,V2>Mapper<K1,V1,K2,V2> functionMapper(
    final Function<K1,K2> keyTransform,
    final Function<V1,V2> valTransform,
    final Predicate<V1> predicate) {
      return new Mapper<K1,V1,K2,V2>() {
        public void map(K1 key, V1 val, Collector<K2, V2> context) {
          if (predicate.apply(val))
            context.collect(keyTransform.apply(key),valTransform.apply(val));
        }        
      };
  }
  
  public static <K,V>Reducer<K,V,K,Integer> countingReducer() {
    return new Reducer<K,V,K,Integer>() {
      public void reduce(K key, Iterator<V> vals, Collector<K, Integer> context) {
        context.collect(key, Iterators.size(vals));
      }      
    };
  }
  
  public static <K,V>Reducer<K,V,K,V> noDuplicatesReducer() {
    return new Reducer<K,V,K,V>() {
      final Set<V> set = new LinkedHashSet<V>();
      public void reduce(K key, Iterator<V> vals, Collector<K, V> context) {
        while(vals.hasNext()) {
          V v = vals.next();
          if (!set.contains(v)) {
            context.collect(key, v);
            set.add(v);
          }
        }
      }      
    };
  }

  public static <K,V>Reducer<K,V,K,V> noEquivalentsReducer(
    final Equivalence<V> equivalence) {
    return new Reducer<K,V,K,V>() {
      final Set<V> set = new LinkedHashSet<V>();
      public void reduce(K key, Iterator<V> vals, Collector<K, V> context) {
        while(vals.hasNext()) {
          V v = vals.next();
          Predicate<V> ve = equivalence.equivalentTo(v);
          try {
            Iterables.find(set, ve);
          } catch (NoSuchElementException e) {
            context.collect(key,v);
            set.add(v);
          }
        }
      }      
    };
  }

  /**
   * A PartitionFunction is responsible for taking an input Iterable and
   * splitting it into multiple sub-iterables.
   */
  public static interface PartitionFunction<T> 
    extends Function<Iterable<T>,Iterable<Iterable<T>>> {}
  
  /**
   * A CombinerFunction is responsible for taking multiple Iterables and
   * combining them into a single Iterable. 
   */
  public static interface CombinerFunction<T>
    extends Function<Iterable<Iterable<T>>,Iterable<T>> {}
  
  /**
   * Return the default basic PartitionFunction that splits the 
   * input Iterable into multiple unmodifiable Iterables of the 
   * given size (the final iterable may be smaller)
   */
  public static <T>PartitionFunction<T> partitioner(final int size) {
    return new PartitionFunction<T>() {
      public Iterable<Iterable<T>> apply(Iterable<T> input) {
        Iterable<List<T>> i = Iterables.<T>partition(input, size);
        ImmutableList.Builder<Iterable<T>> l = ImmutableList.builder();
        for (List<T> list : i)
          l.add(list);
        return l.build();
      }
    };
  }
  
  /**
   * Return the default CombinerFunction that concats the 
   * given Iterables into a single Iterable without resorting. 
   * (e.g. and Iterable of [["a","b"],["d","c"]] would be 
   * combined into a single Iterable ["a","b","d","c"]
   */
  public static <T>CombinerFunction<T> combiner() {
    return new CombinerFunction<T>() {
      public Iterable<T> apply(Iterable<Iterable<T>> input) {
        return Iterables.concat(input);
      }      
    };
  }
  
  /**
   * Returns a CombinerFunction that concats the 
   * given Iterables into a single Iterable without resorting,
   * with all duplicates removed.
   * (e.g. and Iterable of [["a","b"],["d","c","b"]] would be 
   * combined into a single Iterable ["a","b","d","c"]
   */
  public static <T>CombinerFunction<T> uniqueCombiner() {
    return new CombinerFunction<T>() {
      public Iterable<T> apply(Iterable<Iterable<T>> input) {
        Iterable<T> c = Iterables.concat(input);
        return ImmutableSet.copyOf(c);
      }      
    };
  }
  
  /**
   * Return the default CombinerFunction that concats the 
   * given Iterables into a single Iterable using the specified
   * Comparator for sorting. If Comparator is null, no sorting
   * will be performed
   * (e.g. and Iterable of [["a","b"],["d","c"]] would be 
   * combined into a single Iterable ["a","b","c","d"]
   */
  public static <T>CombinerFunction<T> sortingCombiner(final Comparator<T> order) {
    return new CombinerFunction<T>() {
      public Iterable<T> apply(Iterable<Iterable<T>> input) {
        Iterable<T> i = Iterables.concat(input);
        List<T> l = Lists.newArrayList(i);
        if (order != null)
          Collections.sort(l,order);
        return ImmutableList.copyOf(l);
      }      
    };
  }
  
  /**
   * Return the default CombinerFunction that concats the 
   * given Iterables into a single Iterable using the natural
   * order of the elements for sorting
   * (e.g. and Iterable of [["a","b"],["d","c"]] would be 
   * combined into a single Iterable ["a","b","c","d"]
   */
  public static <T extends Comparable<T>>CombinerFunction<T> sortingCombiner() {
    return new CombinerFunction<T>() {
      public Iterable<T> apply(Iterable<Iterable<T>> input) {
        Iterable<T> i = Iterables.concat(input);
        List<T> l = Lists.newArrayList(i);
        Collections.<T>sort(l);
        return l;
      }      
    };
  }
  
  
  /**
   * Return the default CombinerFunction that concats the 
   * given Iterables into a single Iterable using the specified
   * Comparator for sorting. If Comparator is null, no sorting
   * will be performed
   * (e.g. and Iterable of [["a","b"],["d","c"]] would be 
   * combined into a single Iterable ["a","b","c","d"]
   */
  public static <T>CombinerFunction<T> uniqueSortingCombiner(final Comparator<T> order) {
    return new CombinerFunction<T>() {
      public Iterable<T> apply(Iterable<Iterable<T>> input) {
        Iterable<T> i = Iterables.concat(input);
        Set<T> l = new ConcurrentSkipListSet<T>(order);
        Iterables.addAll(l, i);
        return l;
      }      
    };
  }
  
  /**
   * Return the default CombinerFunction that concats the 
   * given Iterables into a single Iterable using the natural
   * order of the elements for sorting
   * (e.g. and Iterable of [["a","b"],["d","c"]] would be 
   * combined into a single Iterable ["a","b","c","d"]
   */
  public static <T extends Comparable<T>>CombinerFunction<T> uniqueSortingCombiner() {
    return new CombinerFunction<T>() {
      public Iterable<T> apply(Iterable<Iterable<T>> input) {
        Iterable<T> i = Iterables.concat(input);
        Set<T> l = new ConcurrentSkipListSet<T>();
        Iterables.addAll(l, i);
        return l;
      }      
    };
  }
  
  /**
   * Return the default collector impl
   */
  public static <K,V>Collector<K,V> collector(boolean nulls, Comparator<K> order) {
    return new SimpleCollector<K,V>(nulls,order);
  }
  
  /**
   * Return the default collector impl
   */
  public static <K,V>Collector<K,V> collector(boolean nulls) {
    return new SimpleCollector<K,V>(nulls);
  }
  
  /**
   * Return the default collector impl
   */
  public static <K,V>Collector<K,V> collector(Comparator<K> order) {
    return new SimpleCollector<K,V>(false,order);
  }
  
  /**
   * Return the default collector impl
   */
  public static <K,V>Collector<K,V> collector() {
    return new SimpleCollector<K,V>(false);
  }
  
  public static class SimpleCollector<K,V> 
    implements MapRed.Collector<K, V> {
    
    private final Map<K,Iterable<V>> map;
    private final boolean collectNulls;
    public SimpleCollector() {
      this(false);
    }
    public SimpleCollector(boolean nulls) {
      this.collectNulls = nulls;
      map = new ConcurrentSkipListMap<K,Iterable<V>>();
    }
    public SimpleCollector(Comparator<K> comparator) {
      this(false,comparator);
    }
    public SimpleCollector(boolean nulls, Comparator<K> comparator) {
      this.collectNulls = nulls;
      map = comparator != null ?
        new ConcurrentSkipListMap<K,Iterable<V>>(comparator) :
        new ConcurrentSkipListMap<K,Iterable<V>>();
    }
    public void collect(K key, V val) {
      if (!collectNulls && val == null) return;
      List<V> set = (List<V>) map.get(key);
      if (set == null) {
        set = new ArrayList<V>();
        map.put(key,set);
      }
      set.add(val);
    }
    public Iterable<Map.Entry<K,Iterable<V>>> collected() {  
      ImmutableMap.Builder<K,Iterable<V>> builder = 
        ImmutableMap.builder();
      for (Map.Entry<K,Iterable<V>> entry : map.entrySet())
        builder.put(entry.getKey(), ImmutableList.copyOf(entry.getValue()));
      return builder.build().entrySet();
    }
  }
}
