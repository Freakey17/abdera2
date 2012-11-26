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
package org.apache.abdera2.activities.model.objects;

import java.util.Map;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.common.anno.Name;
import org.joda.time.DateTime;

import com.google.common.base.Supplier;

public class EventObject 
  extends ASObject {

  public static final String ATTENDING = "attending";
  public static final String ENDTIME = "endTime";
  public static final String MAYBEATTENDING = "maybeAttending";
  public static final String NOTATTENDING = "notAttending";
  public static final String STARTTIME = "startTime";
  
  public EventObject(Map<String,Object> map) {
    super(map,EventBuilder.class,EventObject.class);
  }
  
  public <X extends EventObject, M extends Builder<X,M>>EventObject(Map<String,Object> map, Class<M> _class, Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public Collection<ASObject> getAttending() {
    return getProperty(ATTENDING);
  }
  
  public DateTime getEndTime() {
    return getProperty(ENDTIME);
  }
  
  public Collection<ASObject> getMaybeAttending() {
    return getProperty(MAYBEATTENDING);
  }
  
  public Collection<ASObject> getNotAttending() {
    return getProperty(NOTATTENDING);
  }
  
  public DateTime getStartTime() {
    return getProperty(STARTTIME);
  }
  
  public static EventBuilder makeEvent() {
    return new EventBuilder("event");
  }
  
  public static EventBuilder makeEvent(String objectType) {
    return new EventBuilder(objectType);
  }
  
  @Name("event")
  public static final class EventBuilder extends Builder<EventObject,EventBuilder> {

    public EventBuilder() {
      super(EventObject.class,EventBuilder.class);
    }

    public EventBuilder(Map<String, Object> map) {
      super(map, EventObject.class,EventBuilder.class);
    }

    public EventBuilder(String objectType) {
      super(objectType, EventObject.class,EventBuilder.class);
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends EventObject,M extends Builder<X,M>>
    extends ASObject.Builder<X,M> {

    public Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    
    public Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    
    public Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    
    public <T extends ASObject>M attending(Supplier<? extends Collection<T>> col) {
      return attending(col.get());
    }

    public <T extends ASObject>M attending(Collection<T> col) {
      set(ATTENDING, col);
      return (M)this;
    }
    
    public M endTime(DateTime dt) {
      set(ENDTIME, dt);
      return (M)this;
    }
    
    public <T extends ASObject>M maybeAttending(Supplier<? extends Collection<T>> col) {
      return maybeAttending(col.get());
    }
    
    public <T extends ASObject>M maybeAttending(Collection<T> col) {
      set(MAYBEATTENDING, col);
      return (M)this;
    }
    
    public <T extends ASObject>M notAttending(Supplier<? extends Collection<T>> col) {
      return notAttending(col.get());
    }
    
    public <T extends ASObject>M notAttending(Collection<T> col) {
      set(NOTATTENDING, col);
      return (M)this;
    }
    
    public M startTime(DateTime t) {
      set(STARTTIME, t);
      return (M)this;
    }    
  }
}
