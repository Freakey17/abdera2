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
package org.apache.abdera2.common.pusher;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public abstract class AbstractPusher<T> 
  implements Pusher<T> {

  public void pushAll(Iterable<T> t) {
    for (T i : Iterables.unmodifiableIterable(t))
      push(i);
  }

  public void push(Supplier<? extends T> t) {
    if (t == null) return;
    T i = t.get();
    if (i != null) 
      push(i);
  }
  
  public void pushAll(T... t) {
    if (t == null) return;
    pushAll(ImmutableList.copyOf(t));
  }
  
  public void pushAll(Supplier<? extends T>... t) {
    ImmutableList.Builder<T> list = ImmutableList.builder();
    for (Supplier<? extends T> s : t) {
      T i = s.get();
      if (i != null)
        list.add(i);
    }
    pushAll(list.build());
  }
}
