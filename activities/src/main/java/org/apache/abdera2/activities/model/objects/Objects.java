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
import org.apache.abdera2.activities.model.ASObject.ASObjectBuilder;

public final class Objects {

  private Objects() {}
  
  /**
   * Special AS Object that represents the authenticated user
   */
  
  public static final ASObject SELF = SELF().get();
  public static final ASObject ME = ME().get();
  public static final ASObject FRIENDS = FRIENDS().get();
  public static final ASObject NETWORK = NETWORK().get();
  public static final ASObject ALL = ALL().get();
  public static final ASObject PUBLIC = PUBLIC().get();
  public static final ASObject VIEWER = VIEWER().get();
  public static final ASObject OWNER = OWNER().get();
  public static final ASObject FOLLOWERS = FOLLOWERS().get();
  
  public static ASObjectBuilder ALIASED(String alias) {
    return ASObject.makeObject().set("alias",alias);
  }
  
  public static ASObjectBuilder FOLLOWERS() {
    return ALIASED("@followers");
  }
  
  public static ASObjectBuilder VIEWER() {
    return ALIASED("@viewer");
  }
  
  public static ASObjectBuilder OWNER() {
    return ALIASED("@owner");
  }
  
  public static ASObjectBuilder SELF() {
    return ALIASED("@self");
  }
  
  /**
   * Special AS Object that represents the authenticated user.
   * synonymous with @self
   */
  public static ASObjectBuilder ME() {
    return ALIASED("@me");
  }
  
  /**
   * Special AS Object that represents the authenticated users 
   * collection of direct contacts
   */
  public static ASObjectBuilder FRIENDS() {
    return ALIASED("@friends");
  }
  
  /**
   * Special AS Object that represents a subset of the authenticated users
   * collection of direct contacts
   */
  public static ASObjectBuilder FRIENDS(String id) {
    return ALIASED("@friends").id(id);
  }
  
  /**
   * Special AS Object that represents the authenticated users collection
   * of extended contacts (e.g. friends of friends)
   */
  public static ASObjectBuilder NETWORK() {
    return ALIASED("@network");
  }
  
  /**
   * Special AS Object that represents everyone. synonymous with @public
   */
  public static ASObjectBuilder ALL() {
    return ALIASED("@all");
  }
  
  /**
   * Special AS Object that represents everyone
   */
  public static ASObjectBuilder PUBLIC() {
    return ALIASED("@public");
  }
  
  /**
   * Create an anonymous AS Object (no objectType property)
   */
  public static ASObjectBuilder untypedObject(String id) {
    return ASObject.makeObject().id(id);
  }
  
  public static ASObjectBuilder untypedObject(String id, String displayName) {
    return ASObject.makeObject().id(id).displayName(displayName);
  }
  
  public static final ASObject DISCONTINUED = DISCONTINUED().get();
  public static final ASObject INSTOCK = INSTOCK().get();
  public static final ASObject INSTOREONLY = INSTOREONLY().get();
  public static final ASObject ONLINEONLY = ONLINEONLY().get();
  public static final ASObject OUTOFSTOCK = OUTOFSTOCK().get();
  public static final ASObject PREORDER = PREORDER().get();
  public static final ASObject EBOOK = EBOOK().get();
  public static final ASObject HARDCOVER = HARDCOVER().get();
  public static final ASObject PAPERBACK = PAPERBACK().get();
  public static final ASObject DAMAGED = DAMAGED().get();
  public static final ASObject NEW = NEW().get();
  public static final ASObject REFURBISHED = REFURBISHED().get();
  public static final ASObject USED = USED().get();
  
  public static ASObjectBuilder DISCONTINUED() {
    return untypedObject("discontinued");
  }
  
  public static ASObjectBuilder INSTOCK() {
    return untypedObject("in-stock");
  }
  
  public static ASObjectBuilder INSTOREONLY() {
    return untypedObject("in-store-only");
  }
  
  public static ASObjectBuilder ONLINEONLY() {
    return untypedObject("online-only");
  }
  
  public static ASObjectBuilder OUTOFSTOCK() {
    return untypedObject("out-of-stock");
  }
  
  public static ASObjectBuilder PREORDER() {
    return untypedObject("pre-order");
  }
  
  public static ASObjectBuilder EBOOK() {
    return untypedObject("ebook");
  }
  
  public static ASObjectBuilder HARDCOVER() {
    return untypedObject("hardcover");
  }
  
  public static ASObjectBuilder PAPERBACK() {
    return untypedObject("paperback");
  }
  
  public static ASObjectBuilder DAMAGED() {
    return untypedObject("damaged");
  }
  
  public static ASObjectBuilder NEW() {
    return untypedObject("new");
  }
  
  public static ASObjectBuilder REFURBISHED() {
    return untypedObject("refurbished");
  }
  
  public static ASObjectBuilder USED() {
    return untypedObject("used");
  }
  
}
