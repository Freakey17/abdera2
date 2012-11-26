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
import org.apache.abdera2.common.anno.Name;

/**
 * Represents a mailing address. 
 */
public class Address extends ASObject {

  public static final String FORMATTED = "formatted";
  public static final String STREETADDRESS = "streetAddress";
  public static final String LOCALITY = "locality";
  public static final String REGION = "region";
  public static final String POSTALCODE = "postalCode";
  public static final String COUNTRY = "country";
  public static final String BUILDING = "building";
  public static final String FLOOR = "floor";
  
  public Address(Map<String,Object> map) {
    super(map,AddressBuilder.class,Address.class);
  }
  
  public <X extends Address, M extends Builder<X,M>>Address(Map<String,Object> map, Class<M> _class,Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public String getFormatted() {
    return getProperty(FORMATTED);
  }
  
  public String getStreetAddress() {
    return getProperty(STREETADDRESS);
  }
  
  public String getLocality() {
    return getProperty(LOCALITY);
  }
  
  public String getRegion() {
    return getProperty(REGION);
  }
  
  public String getPostalCode() {
    return getProperty(POSTALCODE);
  }
  
  public String getCountry() {
    return getProperty(COUNTRY);
  }
  
  public String getBuilding() {
    return getProperty(BUILDING);
  }
  
  public String getFloor() {
    return getProperty(FLOOR);
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    if (getFormatted() != null) {
      buf.append(getFormatted());
    } else {
      buf.append("an address");
    }
    return buf.toString();
  }
  
  public static AddressBuilder makeAddress() {
    return new AddressBuilder("address");
  }
  
  @Name("address")
  public static final class AddressBuilder extends Builder<Address,AddressBuilder> {
    public AddressBuilder() {
      super(Address.class,AddressBuilder.class);
    }
    public AddressBuilder(Map<String, Object> map) {
      super(map, Address.class,AddressBuilder.class);
    }
    public AddressBuilder(String objectType) {
      super(objectType, Address.class,AddressBuilder.class);
    }
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends Address, M extends Builder<X,M>> 
  extends ASObject.Builder<X,M> {
    public Builder(Class<X> _class, Class<M> _builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X> _class, Class<M> _builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X> _class, Class<M> _builder) {
      super(map,_class,_builder);
    }
    public M country(String country) {
      set(COUNTRY,country);
      return (M)this;
    }
    public M formatted(String formatted) {
      set(FORMATTED,formatted);
      return (M)this;
    }
    public M locality(String locality) {
      set(LOCALITY,locality);
      return (M)this;
    }
    public M postalCode(String postalCode) {
      set(POSTALCODE,postalCode);
      return (M)this;
    }
    public M region(String region) {
      set(REGION, region);
      return (M)this;
    }
    public M building(String building) {
      set(BUILDING,building);
      return (M)this;
    }
    public M floor(String floor) {
      set(FLOOR,floor);
      return (M)this;
    }
    public M streetAddress(String address) {
      set(STREETADDRESS,address);
      return (M)this;
    }
  }
}
