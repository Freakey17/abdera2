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

public class Line extends Multiple {
  
  private static final long serialVersionUID = 2852975001208906285L;

  public static Line with(Iterable<Coordinate> coordinates) {
    Builder builder = make();
    for (Coordinate c : coordinates)
      builder.add(c);
    return builder.get();    
  }
  
  public static Line with(Coordinate... coordinates) {
    Builder builder = make();
    for (Coordinate c : coordinates)
      builder.add(c);
    return builder.get();
  }
  
  public static Line with(String... positions) {
    Builder builder = make();
    for (String p : positions)
      builder.add(p);
    return builder.get();
  }
  
  public static Line with(IsoPosition... positions) {
    Builder builder = make();
    for (IsoPosition p : positions)
      builder.add(p);
    return builder.get();
  }
  
  
  public static Builder make(Iterable<Coordinate> coordinates) {
    Builder builder = make();
    for (Coordinate c : coordinates)
      builder.add(c);
    return builder;    
  }
  
  public static Builder make(Coordinate... coordinates) {
    Builder builder = make();
    for (Coordinate c : coordinates)
      builder.add(c);
    return builder;
  }
  
  public static Builder make(String... positions) {
    Builder builder = make();
    for (String p : positions)
      builder.add(p);
    return builder;
  }
  
  public static Builder make(IsoPosition... positions) {
    Builder builder = make();
    for (IsoPosition p : positions)
      builder.add(p);
    return builder;
  }
  
    public static Builder make() {
      return new Builder();
    }
  
    public static class Builder extends Multiple.Builder<Line> {
      
      public Builder() {}
      
      public Line get() {
        return new Line(this);
      }
    }
 
    public Line(Builder builder) {
      super(builder);
    }

}
