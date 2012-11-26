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
import org.apache.abdera2.common.geo.Coordinate;
import org.apache.abdera2.common.geo.IsoPosition;
import org.apache.abdera2.common.geo.Point;

import com.google.common.base.Supplier;

/**
 * Represents a location... can be logical or physical. At a minimum, the 
 * place can be described in terms of a geological coordinates, an address, 
 * or a name. 
 */
public class PlaceObject 
  extends ASObject {
  public static final String ADDRESS = "address";
  public static final String POSITION = "position";
  
  public PlaceObject(Map<String,Object> map) {
    super(map,PlaceBuilder.class,PlaceObject.class);
  }
  
  public <X extends PlaceObject, M extends Builder<X,M>>PlaceObject(Map<String,Object> map, Class<M> _class,Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public IsoPosition getPosition() {
    return getProperty(POSITION);
  }
  
  public Address getAddress() {
    return getProperty(ADDRESS);
  }
  
  public static PlaceBuilder makePlace() {
    return new PlaceBuilder("place");
  }
  
  public static PlaceObject makePlace(String displayName) {
    return makePlace().displayName(displayName).get();
  }
  
  public static PlaceObject makePlace(String displayName, double latitude, double longitude) {
    return makePlace().displayName(displayName).position(latitude,longitude).get();
  }
  
  public static PlaceObject makePlace(String displayName, Coordinate coord) {
    return makePlace().displayName(displayName).position(coord).get();
  }
  
  public static PlaceObject makePlace(String displayName, Point point) {
    return makePlace().displayName(displayName).position(point).get();
  }
  
  public static PlaceObject makePlace(String displayName, IsoPosition position) {
    return makePlace().displayName(displayName).position(position).get();
  }
  
  public static PlaceObject makePlace(String displayName, Address address) {
    return makePlace().displayName(displayName).address(address).get();
  }
  
  public static PlaceObject makePlace(String displayName, Supplier<? extends Address> address) {
    return makePlace().displayName(displayName).address(address).get();
  }
  
  @Name("place")
  public static final class PlaceBuilder extends Builder<PlaceObject,PlaceBuilder> {

    public PlaceBuilder() {
      super(PlaceObject.class,PlaceBuilder.class);
    }

    public PlaceBuilder(Map<String, Object> map) {
      super(map, PlaceObject.class,PlaceBuilder.class);
    }

    public PlaceBuilder(String objectType) {
      super(objectType, PlaceObject.class,PlaceBuilder.class);
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends PlaceObject, M extends Builder<X,M>>
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
    public M position(IsoPosition position) {
      set(POSITION,position);
      return (M)this;
    }
    public M position(double latitude, double longitude) {
      return position(IsoPosition.at(latitude, longitude));
    }
    public M position(Coordinate coord) {
      return position(IsoPosition.at(coord));
    }
    public M position(Point point) {
      return position(IsoPosition.at(point));
    }
    public M address(Supplier<? extends Address> address) {
      return address(address.get());
    }
    public M address(Address address) {
      set(ADDRESS,address);
      return (M)this;
    }
  }
}
