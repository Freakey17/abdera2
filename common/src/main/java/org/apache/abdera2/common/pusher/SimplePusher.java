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


import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import org.apache.abdera2.common.misc.MoreExecutors2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimplePusher<T> 
  extends AbstractPusher<T> implements Pusher<T>, Receiver<T> {

  final static Log log = LogFactory.getLog(SimplePusher.class);
  
  final Queue<T> queue = new ConcurrentLinkedQueue<T>();
  final ExecutorService exec = MoreExecutors2.getExitingExecutor();
  final Set<Listener<T>> listeners = 
    new HashSet<Listener<T>>();
  
  public void startListening(Listener<T> listener) {
    listener.beforeItems();
    listeners.add(listener);
  }
  
  public void stopListening(Listener<T> listener) {
    listener.afterItems();
    listeners.remove(listener);
  }
  
  public void push(T entry) {
    queue.add(entry);    
  }
 
  public boolean isRunning() {
    return !exec.isShutdown() && 
           !exec.isTerminated();
  }
  
  public SimplePusher() {
    exec.execute(
      new Runnable() {
        public void run() {
          while(isRunning()) {
            if (!queue.isEmpty()) {
              final T el = queue.poll();
              if (el != null) {
                log.info("Processing item...");
                try {
                  exec.execute(new Runnable() {
                    public void run() {
                      for (final Listener<T> l : listeners) {
                        try {
                          exec.execute(new Runnable() {
                            public void run() { 
                              try {
                                l.onItem(el);
                              } catch (Throwable t) {
                                log.error(t);
                              }
                            }
                          });
                        } catch (Throwable t) {
                          log.error(t);
                        }
                      }
                    }
                  });
                } catch (Throwable t) {
                  log.error(t);
                }
              }
            }
          }
        }
      }
    );
  }
  
  public void shutdown() {
    for (Listener<?> listener : listeners)
      listener.afterItems();
    listeners.clear(); // remove all the listeners
  }
}
