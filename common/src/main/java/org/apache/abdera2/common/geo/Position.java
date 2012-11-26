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

import java.io.Serializable;

import org.apache.abdera2.common.misc.MoreFunctions;

import com.google.common.base.Supplier;

public abstract class Position 
  implements Serializable, 
             Cloneable, 
             Comparable<Position> {
  
    @SuppressWarnings("unchecked")
    public static abstract class Builder<P extends Position> implements Supplier<P> {

      protected String featureTypeTag;
      protected String relationshipTag;
      protected Double elevation;
      protected Double floor;
      protected Double radius;
      
      public <X extends Builder<P>>X featureType(String tag) {
        this.featureTypeTag = tag;
        return (X)this;
      }
      
      public <X extends Builder<P>>X relationship(String tag) {
        this.relationshipTag = tag;
        return (X)this;
      }
      
      public <X extends Builder<P>>X elevation(double elevation) {
        this.elevation = elevation;
        return (X)this;
      }
      
      public <X extends Builder<P>>X floor(double floor) {
        this.floor = floor;
        return (X)this;
      }
      
      public <X extends Builder<P>>X radius(double radius) {
        this.radius = radius;
        return (X)this;
      }
      
    }
  
    private static final long serialVersionUID = 2024463162259330581L;
    public static final String DEFAULT_FEATURE_TYPE_TAG = "location";
    public static final String DEFAULT_RELATIONSHIP_TAG = "is-located-at";

    protected final String featureTypeTag;
    protected final String relationshipTag;
    protected final Double elevation;
    protected final Double floor;
    protected final Double radius;

    protected Position(Builder<?> builder) {
      this.featureTypeTag = builder.featureTypeTag;
      this.relationshipTag = builder.relationshipTag;
      this.elevation = builder.elevation;
      this.floor = builder.floor;
      this.radius = builder.radius;
    }
    
    public Double getElevation() {
        return elevation;
    }

    public String getFeatureTypeTag() {
        return featureTypeTag;
    }

    public Double getFloor() {
        return floor;
    }

    public Double getRadius() {
        return radius;
    }

    public String getRelationshipTag() {
        return relationshipTag;
    }

    @Override
    public int hashCode() {
      return MoreFunctions.genHashCode(
        getClass().hashCode(),
        elevation,
        featureTypeTag == null ? DEFAULT_FEATURE_TYPE_TAG : featureTypeTag,
        floor, radius, 
        relationshipTag == null ? DEFAULT_RELATIONSHIP_TAG : relationshipTag
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) 
          return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final Position other = (Position)obj;
        if (elevation == null) {
            if (other.elevation != null)
                return false;
        } else if (!elevation.equals(other.elevation))
            return false;
        if (featureTypeTag == null) {
            if (other.featureTypeTag != null && !other.featureTypeTag.equalsIgnoreCase(DEFAULT_FEATURE_TYPE_TAG))
                return false;
        } else {
            String s = other.featureTypeTag != null ? other.featureTypeTag : DEFAULT_FEATURE_TYPE_TAG;
            if (!featureTypeTag.equalsIgnoreCase(s))
                return false;
        }
        if (floor == null) {
            if (other.floor != null)
                return false;
        } else if (!floor.equals(other.floor))
            return false;
        if (radius == null) {
            if (other.radius != null)
                return false;
        } else if (!radius.equals(other.radius))
            return false;
        if (relationshipTag == null) {
            if (other.relationshipTag != null && !other.relationshipTag.equalsIgnoreCase(DEFAULT_RELATIONSHIP_TAG))
                return false;
        } else {
            String s = other.relationshipTag != null ? other.relationshipTag : DEFAULT_RELATIONSHIP_TAG;
            if (!relationshipTag.equalsIgnoreCase(s))
                return false;
        }
        return true;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null; // should never happen
        }
    }
}
