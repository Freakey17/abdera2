package org.apache.abdera2.common.misc;
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

import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.*;

public final class Chain<T,R> 
  implements Function<T,R> {

    private final Iterator<Task<T,R>> tasks;
    private final Function<T,R> to;
    private boolean started = false;

    public Chain(Function<T,R> to, Task<T,R>... tasks) {
      this.to = to;
      this.tasks = ImmutableList.copyOf(tasks).iterator();
    }
    
    public Chain(Function<T,R> to, Iterable<Task<T,R>> tasks) {
      this.tasks = tasks.iterator();
      this.to = to;
    }
   
    public R next(T input) {
      return tasks.hasNext() ? 
        tasks.next().apply(input, this) : 
        to.apply(input);
    }

    public R apply(T input) {
      checkState(!started,"Chain has already been started");
      started = true;
      return next(input);
    }

    @SuppressWarnings("synthetic-access")
    public static <T,R>Builder<T,R> make() {
      return new Builder<T,R>();
    }
    
    public static class Builder<T,R> {
      private final ImmutableList.Builder<Task<T,R>> tasks = 
        ImmutableList.builder();
      private Function<T,R> finalTask;
      private Builder() {}
      public Builder<T,R> to(Function<T,R> finalTask) {
        this.finalTask = finalTask;
        return this;
      }
      public Builder<T,R> via(Task<T,R> task) {
        this.tasks.add(task);
        return this;
      }
      public Builder<T,R> via(Task<T,R>... tasks) {
        for (Task<T,R> task : tasks)
          via(task);
        return this;
      }
      /**
       * Creates a Task that applies specified functions to the input and output.
       */
      public Builder<T,R> via(Function<T,T> in, Function<R,R> out) {
        return via(new FunctionTask<T,R>(in,out));
      }
      public Builder<T,R> via(Iterable<? extends Task<T,R>> tasks) {
        for (Task<T,R> task : tasks)
          via(task);
        return this;
      }
      public Builder<T,R> via(Supplier<? extends Task<T,R>> task) {
        via(task.get());
        return this;
      }
      public Chain<T,R> get() {
        checkNotNull(finalTask);
        return new Chain<T,R>(finalTask,tasks.build());
      }
    }
    
    private static class FunctionTask<T,R> 
      implements Task<T, R> {
      private final Function<T,T> in;
      private final Function<R,R> out;
      FunctionTask(
        Function<T,T> in, 
        Function<R,R> out) {
        this.in = in;
        this.out = out;
      }
      public R apply(T input, Chain<T, R> flow) {
        input = in != null ? in.apply(input) : input;
        R output = flow.next(input);
        return out != null ? out.apply(output) : output;
      }
    }

}
