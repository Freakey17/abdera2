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
package org.apache.abdera2.common.geo;

import java.util.Iterator;

import static com.google.common.base.Preconditions.*;
public class Box extends Multiple {

    private static final String TWO_COORDINATES = "A box must have two coordinates";
    private static final long serialVersionUID = 3994252648307511152L;

    public static Box at(Iterable<Coordinate> coordinates) {
      Iterator<Coordinate> i = coordinates.iterator();
      return make().add(i.next()).add(i.next()).get();
    }
    
    public static Box at(Coordinate lowerCorner, Coordinate upperCorner) {
      return make().add(lowerCorner).add(upperCorner).get();
    }
    
    public static Box at(String lowerCorner, String upperCorner) {
      return make().add(lowerCorner).add(upperCorner).get();
    }
    
    public static Box at(IsoPosition lowerCorner, IsoPosition upperCorner) {
      return make().add(lowerCorner).add(upperCorner).get();
    }
    
    public static Box at(double lowerCornerLat, double lowerCornerLong, double upperCornerLat, double upperCornerLong) {
      return make().add(lowerCornerLat,lowerCornerLong).add(upperCornerLat,upperCornerLong).get();
    }
    
    
    public static Builder make(Iterable<Coordinate> coordinates) {
      Iterator<Coordinate> i = coordinates.iterator();
      return make().add(i.next()).add(i.next());
    }
    
    public static Builder make(Coordinate lowerCorner, Coordinate upperCorner) {
      return make().add(lowerCorner).add(upperCorner);
    }
    
    public static Builder make(String lowerCorner, String upperCorner) {
      return make().add(lowerCorner).add(upperCorner);
    }
    
    public static Builder make(IsoPosition lowerCorner, IsoPosition upperCorner) {
      return make().add(lowerCorner).add(upperCorner);
    }
    
    public static Builder make(double lowerCornerLat, double lowerCornerLong, double upperCornerLat, double upperCornerLong) {
      return make().add(lowerCornerLat,lowerCornerLong).add(upperCornerLat,upperCornerLong);
    }
    
    
    public static Builder make() {
      return new Builder();
    }
    
    public static class Builder 
      extends Multiple.Builder<Box> {
      
      public Builder() {
        noDuplicates()
        .maximumCoordinates(2);
      }
      
      public Box get() {
        return new Box(this);
      }
    }
 
    private final transient Coordinate lower, upper;
    
    public Box(Builder builder) {
      super(builder);
      checkArgument(size() == 2, TWO_COORDINATES);
      Iterator<Coordinate> i = iterator();
      this.lower = i.next();
      this.upper = i.next();
    }

    public Coordinate getUpperCorner() {
        return upper;
    }

    public Coordinate getLowerCorner() {
        return lower;
    }
}
