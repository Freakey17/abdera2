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

import org.apache.abdera2.common.misc.MoreFunctions;

public class Point extends Position {

    public static Point at(Coordinate coordinate) {
      return make().at(coordinate).get();
    }
    
    public static Point at(IsoPosition position) {
      return make().at(position).get();
    }
    
    public static Point at(String position) {
      return make().at(position).get();
    }
    
    public static Point at(double latitude, double longitude) {
      return make().at(latitude,longitude).get();
    }
    
    public static Builder make(Coordinate coordinate) {
      return make().at(coordinate);
    }
    
    public static Builder make(IsoPosition position) {
      return make().at(position);
    }
    
    public static Builder make(String position) {
      return make().at(position);
    }
    
    public static Builder make(double latitude, double longitude) {
      return make().at(latitude,longitude);
    }
  
    public static Builder make() {
      return new Builder();
    }
  
    public static class Builder extends Position.Builder<Point> {

      Coordinate coordinate;
      
      public Builder at(Coordinate coordinate) {
        this.coordinate = coordinate;
        return this;
      }
      
      public Builder at(double latitude, double longitude) {
        this.coordinate = Coordinate.at(latitude, longitude);
        return this;
      }
      
      public Builder at(String position) {
        this.coordinate = Coordinate.at(position);
        return this;
      }
      
      public Builder at(IsoPosition position) {
        this.coordinate = Coordinate.at(position);
        return this;
      }
      
      public Point get() {
        return new Point(this);
      }
      
    }
  
    private static final long serialVersionUID = 7540202474168797239L;

    private final Coordinate coordinate;

    Point(Builder builder) {
      super(builder);
      this.coordinate = builder.coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
    
    public IsoPosition asIsoPosition() {
      return new IsoPosition(
        coordinate.getLatitude(),
        coordinate.getLongitude(),
        elevation);
    }
    
    @Override
    public int hashCode() {
      return MoreFunctions.genHashCode(super.hashCode(),coordinate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Point other = (Point)obj;
        if (coordinate == null) {
            if (other.coordinate != null)
                return false;
        } else if (!coordinate.equals(other.coordinate))
            return false;
        return true;
    }

    public int compareTo(Position o) {
        if (o == null || !(o instanceof Point) || equals(o))
            return 0;
        return coordinate.compareTo(((Point)o).coordinate);
    }

}
