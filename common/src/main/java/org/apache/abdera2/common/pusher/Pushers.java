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

import java.util.concurrent.Future;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.AbstractFuture;

public final class Pushers {

  public static <T>Function<T,Void> pushAsFunction(
    final Pusher<T> pusher) {
    return new Function<T,Void>() {
      public Void apply(T input) {
        pusher.push(input);
        return null;
      }
    };
  }
  
  public static <T>Function<Iterable<T>,Void> pushAllAsFunction(
    final Pusher<T> pusher) {
    return new Function<Iterable<T>,Void>() {
      public Void apply(Iterable<T> input) {
        pusher.pushAll(input);
        return null;
      }
    };
  }
  
  /**
   * Wraps a receiver with a Future that will listen for a single 
   * item and then unregister the listener.
   */
  public static <T>Future<T> receiveAsFuture(Receiver<T> receiver) {
    return new ReceiverFuture<T>(receiver,null);
  }
  
  /**
   * Wraps a receiver with a Future that will listen for a single 
   * item that matches the predicate and then unregister the listener.
   */
  public static <T>Future<T> receiveAsFuture(Receiver<T> receiver, Predicate<T> check) {
    return new ReceiverFuture<T>(receiver,check);
  }
  
  private static class ReceiverFuture<T> 
    extends AbstractFuture<T>
    implements Listener<T> {
    private final Receiver<T> receiver;
    private final Predicate<T> check;
    ReceiverFuture(Receiver<T> receiver, Predicate<T> check) {
      this.receiver = receiver;
      this.check = check;
      receiver.startListening(this);
    }
    public boolean cancel(boolean mayInterruptIfRunning) {
      receiver.stopListening(this);
      return super.cancel(mayInterruptIfRunning);
    }
    public void beforeItems() {}
    public void onItem(T t) {
      if (check == null || check.apply(t))
        this.set(t);
    }
    public void afterItems() {
      receiver.stopListening(this);
      if (!isDone()) this.cancel(true);
    }
  }
}
