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

import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkNotNull;

public class Pair<K,V> {

  private final K k;
  private final V v;
  
  public Pair(K k, V v) {
    this.k = k;
    this.v = v;
  }
  
  public K first() {
    return k;
  }
  
  public V second() {
    return v;
  }
  
  public static <K,V>Pair<K,V> of(K k, V v) {
    return new Pair<K,V>(k,v);
  }
  
  public static Pair<String,String> from(String pair) {
    String[] split = pair.split("\\s*=\\s*",2);
    return new Pair<String,String>(split[0],split.length>1?split[1]:null);
  }
  
  public static <K,V>Iterable<Pair<K,V>> from(Map<K,V> map) {
    PairBuilder<K,V> pb = make();
    for (Map.Entry<K,V> entry : map.entrySet())
      pb.pair(entry);
    return pb.get();
  }
  
  public static Iterable<Pair<String,String>> from(String[] pairs) {
    PairBuilder<String,String> pb = make();
    for (String pair : pairs)
      pb.pair(from(pair));
    return pb.get();
  }
  
  public static Iterable<Pair<String,String>> from(String pairs, String delim) {
    return from(pairs.split(String.format("\\s*%s\\s*",delim)));
  }
  
  public Object[] toArray() {
    return new Object[] {first(),second()};
  }
  
  @Override
  public int hashCode() {
    return MoreFunctions.genHashCode(1, k,v);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pair<K,V> other = (Pair) obj;
    if (k == null) {
      if (other.k != null)
        return false;
    } else if (!k.equals(other.k))
      return false;
    if (v == null) {
      if (other.v != null)
        return false;
    } else if (!v.equals(other.v))
      return false;
    return true;
  }
  
  public String toString() {
    return 
    new StringBuilder()
      .append('[')
      .append(first())
      .append(',')
      .append(second())
      .append(']')
      .toString();
  }
  
  public static <K,V>PairBuilder<K,V> make() {
    return new PairBuilder<K,V>();
  }
  
  public static class PairBuilder<K,V> 
    implements Supplier<Iterable<Pair<K,V>>>, PairReader<K,V> {
    private final ImmutableSet.Builder<Pair<K,V>> builder =
      ImmutableSet.builder();
    public PairBuilder<K,V> index(
      Function<V,K> keyFunction,
      V... vals) {
      checkNotNull(keyFunction);
      for (V v : vals)
        pair(keyFunction.apply(v),v);
      return this;      
    }
    public PairBuilder<K,V> index(
      Function<V,K> keyFunction, 
      Iterable<V> vals) {
      checkNotNull(keyFunction);
      for (V v : checkNotNull(vals))
        pair(keyFunction.apply(v),v);
      return this;
    }
    public PairBuilder<K,V> pair(K k, V v) {
      builder.add(Pair.<K,V>of(k,v));
      return this;
    }
    public PairBuilder<K,V> pair(Pair<K,V> pair) {
      builder.add(pair);
      return this;
    }
    public PairBuilder<K,V> pair(Map.Entry<K,V> entry) {
      return pair(entry.getKey(),entry.getValue());
    }
    public Iterable<Pair<K, V>> get() {
      return builder.build();
    }
    public Iterator<Pair<K, V>> iterator() {
      return get().iterator();
    }
  }
  
  public static interface PairReader<K,V> extends Iterable<Pair<K,V>> {}
}
