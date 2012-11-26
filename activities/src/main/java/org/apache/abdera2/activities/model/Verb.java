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
package org.apache.abdera2.activities.model;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.abdera2.common.misc.MoreFunctions;

/**
 * Activity Verbs... contains the core set of verbs and allows the creation
 * of new verbs on the fly.
 */
public abstract class Verb implements Serializable {

 private static final long serialVersionUID = 32575425326522967L;

 private static final Map<String,Verb> VERBS = 
   new ConcurrentHashMap<String,Verb>();
  
 public static synchronized final Verb get(String name) {
   return VERBS.containsKey(name) ? 
       VERBS.get(name) : 
       new Verb(name) {};
 }
 
 // Core Verbs //
 
 public static final Verb ADD = new Verb("add") {};
 public static final Verb CANCEL = new Verb("cancel") {};
 public static final Verb CHECKIN = new Verb("checkin") {};
 public static final Verb DELETE = new Verb("delete") {};
 public static final Verb FAVORITE = new Verb("favorite") {};
 public static final Verb FOLLOW = new Verb("follow") {};
 public static final Verb GIVE = new Verb("give") {};
 public static final Verb IGNORE = new Verb("ignore") {};
 public static final Verb INVITE = new Verb("invite") {};
 public static final Verb JOIN = new Verb("join") {};
 public static final Verb LEAVE = new Verb("leave") {};
 public static final Verb LIKE = new Verb("like") {};
 public static final Verb MAKE_FRIEND = new Verb("make-friend") {};
 public static final Verb POST = new Verb("post") {};
 public static final Verb PLAY = new Verb("play") {};
 public static final Verb RECEIVE = new Verb("receive") {};
 public static final Verb REMOVE = new Verb("remove") {};
 public static final Verb REMOVE_FRIEND = new Verb("remove-friend") {};
 public static final Verb REQUEST_FRIEND = new Verb("request-friend") {};
 public static final Verb RSVP_MAYBE = new Verb("rsvp-maybe") {};
 public static final Verb RSVP_NO = new Verb("rsvp-no") {};
 public static final Verb RSVP_YES = new Verb("rsvp-yes") {};
 public static final Verb SAVE = new Verb("save") {};
 public static final Verb SHARE = new Verb("share") {};
 public static final Verb STOP_FOLLOWING = new Verb("stop-following") {};
 public static final Verb TAG = new Verb("tag") {};
 public static final Verb UNFAVORITE = new Verb("unfavorite") {};
 public static final Verb UNLIKE = new Verb("unlike") {};
 public static final Verb UNSAVE = new Verb("unsave") {};
 public static final Verb UPDATE = new Verb("update") {};
 
 public static final Verb COMMENT = new Verb("comment") {};
 public static final Verb PURCHASE = new Verb("purchase") {};  
 public static final Verb CONSUME = new Verb("consume") {};
 public static final Verb HOST = new Verb("host") {};
 public static final Verb READ = new Verb("read") {};
 public static final Verb APPROVE = new Verb("approve") {};
 public static final Verb REJECT = new Verb("reject") {};
 public static final Verb ARCHIVE = new Verb("archive") {};
 public static final Verb INSTALL = new Verb("install") {};
 public static final Verb CLOSE = new Verb("close") {};
 public static final Verb OPEN = new Verb("open") {};
 public static final Verb RESOLVE = new Verb("resolve") {};

 
  private final String name;
  
  protected Verb(String name) {
    this.name = name;
    VERBS.put(name,this);
  }
  
  public String getName() {
    return name;
  }
  
  public String toString() {
    return name;
  }

  public int hashCode() {
    return MoreFunctions.genHashCode(1,name.toLowerCase());
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Verb other = (Verb) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equalsIgnoreCase(other.name))
      return false;
    return true;
  }
  
}
