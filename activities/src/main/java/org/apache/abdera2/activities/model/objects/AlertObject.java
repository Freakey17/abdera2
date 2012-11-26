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

import org.apache.abdera2.activities.io.gson.Properties;
import org.apache.abdera2.activities.io.gson.Property;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.date.DateTimes;
import org.joda.time.DateTime;

import static java.lang.Math.*;

public class AlertObject
  extends ASObject {


  public AlertObject(Map<String,Object> map) {
    super(map,AlertBuilder.class,AlertObject.class);
  }
  
  public <X extends AlertObject, M extends Builder<X,M>>AlertObject(Map<String,Object> map, Class<M> _class, Class<X> _obj) {
    super(map,_class,_obj);
  }
  
  public static final String CERTAINTY = "certainty";
  public static final String SEVERITY = "severity";
  public static final String URGENCY = "urgency";
  public static final String EFFECTIVE = "effective";
  public static final String ONSET = "onset";
  public static final String EXPIRES = "expires";
  
  /**
   * Certainty is established using a range between 0.0 and 1.0
   * where 0.0 indicates 0% likelihood of occurrence, and 1.0
   * indicates 100% likelihood.
   */
  public double getCertainty() {
    double c = (Double)getProperty(CERTAINTY);
    return min(1.0,max(0.00, c));
  }
  
  /**
   * Severity is established using a range between 0 and 100, 
   * where 1 indicates the highest possible severity and 
   * 100 indicates the lowest, and 0 indicates a system-defined
   * default severity
   */
  public int getSeverity() {
    int i = (Integer)getProperty(SEVERITY);
    return min(100,max(0,i));
  }
  
  /**
   * Urgency is established using a range between 0 and 100, 
   * where 1 indicates the highest possible urgency and 
   * 100 indicates the lowest, and 0 indicates a system-defined
   * default urgency
   */
  public int getUrgency() {
    int i = (Integer)getProperty(SEVERITY);
    return min(100,max(0,i));
  }
    
  public DateTime getEffective() {
    return getProperty(EFFECTIVE);
  }
    
  public DateTime getOnset() {
    return getProperty(ONSET);
  }

  public DateTime getExpires() {
    return getProperty(EXPIRES);
  }

  public static AlertBuilder makeAlert() {
    return new AlertBuilder("alert");
  }
  
  public static AlertBuilder makeAlert(
    double certainty, 
    int severity, 
    int urgency) {
      return makeAlert()
        .certainty(certainty)
        .severity(severity)
        .urgency(urgency);
  }
  
  @Name("alert")
  @Properties({
    @Property(name="expires",to=DateTime.class),
    @Property(name="onset",to=DateTime.class),
    @Property(name="effective",to=DateTime.class)
  })
  public static final class AlertBuilder extends Builder<AlertObject,AlertBuilder> {
    public AlertBuilder() {
      super(AlertObject.class,AlertBuilder.class);
    }
    public AlertBuilder(Map<String, Object> map) {
      super(map, AlertObject.class,AlertBuilder.class);
    }
    public AlertBuilder(String objectType) {
      super(objectType, AlertObject.class,AlertBuilder.class);
    }
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends AlertObject,M extends Builder<X,M>> 
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
    public M expires(DateTime expires) {
      set(EXPIRES, expires);
      return (M)this;
    }
    public M expiresNow() {
      return expires(DateTimes.now());
    }
    public M onset(DateTime onset) {
      set(ONSET,onset);
      return (M)this;
    }
    public M onsetNow() {
      return onset(DateTimes.now());
    }
    public M effective(DateTime effective) {
      set(EFFECTIVE,effective);
      return (M)this;
    }
    public M effectiveNow() {
      return effective(DateTimes.now());
    }
    public M certainty(double c) {
      set(CERTAINTY,min(1.0,max(0.00,c)));
      return (M)this;
    }
    public M severity(int c) {
      set(SEVERITY,min(100,max(0,c)));
      return (M)this;
    }
    public M urgency(int c) {
      set(URGENCY,min(100,max(0,c)));
      return (M)this;
    }
  }
}
