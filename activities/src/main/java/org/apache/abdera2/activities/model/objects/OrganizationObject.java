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

import java.util.Date;
import java.util.Map;

import org.apache.abdera2.activities.io.gson.Properties;
import org.apache.abdera2.activities.io.gson.Property;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.iri.IRI;
import org.joda.time.DateTime;

import com.google.common.base.Supplier;

public class OrganizationObject extends ASObject {

  public OrganizationObject(Map<String,Object> map) {
    super(map,OrganizationBuilder.class,OrganizationObject.class);
  }
  
  public <X extends OrganizationObject, M extends Builder<X,M>>OrganizationObject(Map<String,Object> map,Class<M>_class,Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public Address getAddress() {
    return getProperty("address");
  }
  
  public String getDepartment() {
    return getProperty("department");
  }
  
  public String getDescription() {
    return getProperty("description");
  }
  
  public DateTime getEndDate() {
    return getProperty("endDate");
  }
  
  public String getField() {
    return getProperty("field");
  }
  
  public String getName() {
    return getProperty("name");
  }
  
  public String getSalary() {
    return getProperty("salary");
  }
  
  public DateTime getStartDate() {
    return getProperty("startDate");
  }
  
  public String getSubfield() {
    return getProperty("subfield");
  }
  
  public String getTitle() {
    return getProperty("title");
  }
  
  public String getType() {
    return getProperty("type");
  }
  
  public IRI getWebpage() {
    return getProperty("webpage");
  }
  
  public static OrganizationBuilder makeOrganization() {
    return new OrganizationBuilder("organization");
  }
  
  @Name("organization")
  @Properties({
    @Property(name="webpage",to=IRI.class),
    @Property(name="endDate",to=Date.class),
    @Property(name="startDate",to=Date.class)
  })
  public static final class OrganizationBuilder extends Builder<OrganizationObject,OrganizationBuilder> {

    public OrganizationBuilder() {
      super(OrganizationObject.class,OrganizationBuilder.class);
    }

    public OrganizationBuilder(Map<String, Object> map) {
      super(map, OrganizationObject.class,OrganizationBuilder.class);
    }

    public OrganizationBuilder(String objectType) {
      super(objectType, OrganizationObject.class,OrganizationBuilder.class);
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends OrganizationObject, M extends Builder<X,M>>
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
    public M address(Supplier<? extends Address> obj) {
      return address(obj.get());
    }
    public M address(Address address) {
      set("address",address);
      return (M)this;
    }
    public M department(String val) {
      set("department",val);
      return (M)this;
    }
    public M description(String val) {
      set("description",val);
      return (M)this;
    }
    public M endDate(DateTime dt) {
      set("endDate",dt);
      return (M)this;
    }
    public M field(String field) {
      set("field",field);
      return (M)this;
    }
    public M name(String name) {
      set("name",name);
      return (M)this;
    }
    public M salary(String salary) {
      set("salary",salary);
      return (M)this;
    }
    public M startDate(DateTime dt) {
      set("startDate",dt);
      return (M)this;
    }
    public M subfield(String val) {
      set("subfield",val);
      return (M)this;
    }
    public M title(String val) {
      set("title",val);
      return (M)this;
    }
    public M type(String val) {
      set("type",val);
      return (M)this;
    }
    public M webpage(String uri) {
      return webpage(new IRI(uri));
    }
    public M webpage(IRI uri) {
      set("webpage",uri);
      return (M)this;
    }
  }
}
