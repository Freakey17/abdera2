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

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.extra.Extra.ExtensionObject;
import org.apache.abdera2.activities.extra.Extra.ExtensionBuilder;

import com.google.common.base.Supplier;
/**
 * Additional extension properties for the EventObject...
 * attach this interface to an EventObject using the 
 * extend method... e.g.
 * 
 * AdditionalEventProperties avp = event.extend(AdditionaEventProperties.class);
 * avp.setHost(...);
 * avp.setOffers(...);
 * ...
 */
public interface AdditionalEventProperties
  extends ExtensionObject {
  <T extends ASObject>T getHost();
  <T extends ASObject>T getOffers();
  <T extends ASObject>T getSubEvents();
  <T extends ASObject>T getSuperEvent();
  <T extends ASObject>T getPerformers();
  
  public static interface Builder
    extends ExtensionBuilder {
    Builder host(ASObject host);
    Builder offers(ASObject offers);
    Builder subEvents(ASObject subEvents);
    Builder superEvent(ASObject superEvent);
    Builder performers(ASObject performers);
    
    Builder host(Supplier<? extends ASObject> host);
    Builder offers(Supplier<? extends ASObject> offers);
    Builder subEvents(Supplier<? extends ASObject> subEvents);
    Builder superEvent(Supplier<? extends ASObject> superEvent);
    Builder performers(Supplier<? extends ASObject> performers);
  }
  
}
