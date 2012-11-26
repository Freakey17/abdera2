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
import org.apache.abdera2.common.iri.IRI;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class PersonObject 
  extends ASObject {
  
  public PersonObject(Map<String,Object> map) { 
    super(map,PersonBuilder.class,PersonObject.class);
  }
  
  public <X extends PersonObject, M extends Builder<X,M>>PersonObject(Map<String,Object> map, Class<M> _class,Class<X>_obj) { 
    super(map,_class,_obj);
  }
  
  public String getAboutMe() {
    return getProperty("aboutMe");
  }
  
  public String getContactPreference() {
    return getProperty("contactPreference");
  }
  
  public String getDn() {
    return getProperty("dn");
  }
  
  public String getPreferredUsername() {
    return getProperty("preferredUsername");
  }
  
  public IRI getProfileUrl() {
    return getProperty("profileUrl");
  }
  
  public String getStatus() {
    return getProperty("status");
  }
  
  public IRI getThumbnailUrl() {
    return getProperty("thumbnailUrl");
  }
  
  public String getUtcOffset() {
    return getProperty("utcOffset");
  }
  
  public NameObject getName() {
    return getProperty("name");
  }
  
  public NameObject getNativeName() {
    return getProperty("nativeName");
  }
  
  public NameObject getPreferredName() {
    return getProperty("preferredName");
  }
  
  public Iterable<NameObject> getAlternateNames() {
    return checkEmpty(this.<Iterable<NameObject>>getProperty("alternateNames"));
  }
  
  public Iterable<String> getEmails() {
    return checkEmpty(this.<Iterable<String>>getProperty("emails"));
  }
  
  public Iterable<String> getIms() {
    return checkEmpty(this.<Iterable<String>>getProperty("ims"));
  }
  
  public Iterable<String> getPhoneNumbers() {
    return checkEmpty(this.<Iterable<String>>getProperty("phoneNumbers"));
  }
  
  public Iterable<IRI> getUrls() {
    return checkEmpty(this.<Iterable<IRI>>getProperty("urls"));
  }
  
  public Iterable<Address> getAddresses() {
    return checkEmpty(this.<Iterable<Address>>getProperty("addresses"));
  }
  
  public Iterable<AccountObject> getAccounts() {
    return checkEmpty(this.<Iterable<AccountObject>>getProperty("accounts"));
  }
  
  public Iterable<OrganizationObject> getOrganizations() {
    return checkEmpty(this.<Iterable<OrganizationObject>>getProperty("organizations"));
  }
  
  public static PersonBuilder makePerson() {
    return new PersonBuilder("person");
  }
  
  public static PersonBuilder makePerson(String displayName) {
    return makePerson().displayName(displayName);
  }
  
  @Name("person")
  @Properties({
    @Property(name="profileUrl",to=IRI.class),
    @Property(name="thumbnailUrl",to=IRI.class),
    @Property(name="urls",to=IRI.class),
    @Property(name="name",to=NameObject.class),
    @Property(name="preferredName",to=NameObject.class),
    @Property(name="nativeName",to=NameObject.class),
    @Property(name="alternateNames",to=NameObject.class),
    @Property(name="accounts",to=AccountObject.class),
    @Property(name="addresses",to=Address.class),
    @Property(name="organizations",to=OrganizationObject.class)
  })
  public static final class PersonBuilder extends Builder<PersonObject,PersonBuilder> {

    public PersonBuilder() {
      super(PersonObject.class, PersonBuilder.class);
    }

    public PersonBuilder(Map<String, Object> map) {
      super(map, PersonObject.class, PersonBuilder.class);
    }

    public PersonBuilder(String objectType) {
      super(objectType, PersonObject.class, PersonBuilder.class);
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public static class Builder<X extends PersonObject, M extends Builder<X,M>>
    extends ASObject.Builder<X,M> {
    private final ImmutableSet.Builder<AccountObject> accounts = ImmutableSet.builder();
    private final ImmutableSet.Builder<Address> addresses = ImmutableSet.builder();
    private final ImmutableSet.Builder<NameObject> altnames = ImmutableSet.builder();
    private final ImmutableSet.Builder<String> emails = ImmutableSet.builder();
    private final ImmutableSet.Builder<String> ims = ImmutableSet.builder();
    private final ImmutableSet.Builder<OrganizationObject> orgs = ImmutableSet.builder();
    private final ImmutableSet.Builder<String> phones = ImmutableSet.builder();
    private final ImmutableSet.Builder<IRI> urls = ImmutableSet.builder();
    boolean a,b,c,d,e,f,g,h;
    public Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M account(Supplier<? extends AccountObject> object) {
      return account(object.get());
    }
    public M account(AccountObject object) {
      if (object == null) return (M)this;
      a = true;
      accounts.add(object);
      return (M)this;
    }
    public M account(Supplier<? extends AccountObject>... objects) {
      if (objects == null) return (M)this;
      for (Supplier<? extends AccountObject> object : objects)
        account(object.get());
      return (M)this;
    }
    public M account(AccountObject... objects) {
      if (objects.length == 0) return (M)this;
      for (AccountObject object : objects)
        account(object);
      return (M)this;
    }
    public M account(Iterable<? extends AccountObject> objects) {
      if (Iterables.isEmpty(objects)) return (M)this;
      for (AccountObject object : objects)
        account(object);
      return (M)this;
    }
    public M address(Supplier<? extends Address> object) {
      return address(object.get());
    }
    public M address(Address object) {
      if (object == null) return (M)this;
      b = true;
      addresses.add(object);
      return (M)this;
    }
    public M address(Supplier<? extends Address>... objects) {
      if (objects == null) return (M)this;
      for (Supplier<? extends Address> object : objects)
        address(object.get());
      return (M)this;
    }
    public M address(Address... objects) {
      if (objects.length == 0) return (M)this;
      for (Address object : objects)
        address(object);
      return (M)this;
    }
    public M address(Iterable<? extends Address> objects) {
      if (Iterables.isEmpty(objects)) return (M)this;
      for (Address object : objects)
        address(object);
      return (M)this;
    }
    public M alternateName(Supplier<? extends NameObject> name) {
      return alternateName(name.get());
    }
    public M alternateName(NameObject name) {
      if (name == null) return (M)this;
      c = true;
      altnames.add(name);
      return (M)this;
    }
    public M alternateName(Supplier<? extends NameObject>... objects) {
      if (objects == null) return (M)this;
      for (Supplier<? extends NameObject> object : objects)
        alternateName(object.get());
      return (M)this;
    }
    public M alternateName(NameObject... objects) {
      if (objects.length == 0) return (M)this;
      for (NameObject object : objects)
        alternateName(object);
      return (M)this;
    }
    public M alternateName(Iterable<? extends NameObject> objects) {
      if (Iterables.isEmpty(objects)) return (M)this;
      for (NameObject object : objects)
        alternateName(object);
      return (M)this;
    }
    public M email(String email) {
      if (email == null) return (M)this;
      d = true;
      emails.add(email);
      return (M)this;
    }
    public M email(String... objects) {
      if (objects.length == 0) return (M)this;
      for (String object : objects)
        email(object);
      return (M)this;
    }
    public M email(Iterable<String> objects) {
      if (Iterables.isEmpty(objects)) return (M)this;
      for (String object : objects)
        email(object);
      return (M)this;
    }
    public M im(String im) {
      if (im == null) return (M)this;
      e = true;
      ims.add(im);
      return (M)this;
    }
    public M im(String... objects) {
      if (objects.length == 0) return (M)this;
      for (String object : objects)
        im(object);
      return (M)this;
    }
    public M im(Iterable<String> objects) {
      if (Iterables.isEmpty(objects)) return (M)this;
      for (String object : objects)
        im(object);
      return (M)this;
    }
    public M organization(Supplier<? extends OrganizationObject> object) {
      return organization(object.get());
    }
    public M organization(OrganizationObject object) {
      if (object == null) return (M)this;
      f = true;
      orgs.add(object);
      return (M)this;
    }
    public M organization(Supplier<? extends OrganizationObject>... objects) {
      if (objects == null) return (M)this;
      for (Supplier<? extends OrganizationObject> object : objects)
        organization(object.get());
      return (M)this;
    }
    public M organization(OrganizationObject... objects) {
      if (objects.length == 0) return (M)this;
      for (OrganizationObject object : objects)
        organization(object);
      return (M)this;
    }
    public M organization(Iterable<? extends OrganizationObject> objects) {
      if (Iterables.isEmpty(objects)) return (M)this;
      for (OrganizationObject object : objects)
        organization(object);
      return (M)this;
    }
    public M phoneNumber(String pn) {
      if (pn == null) return (M)this;
      g = true;
      phones.add(pn);
      return (M)this;
    }
    public M phoneNumber(String... objects) {
      if (objects.length == 0) return (M)this;
      for (String object : objects)
        phoneNumber(object);
      return (M)this;
    }
    public M phoneNumber(Iterable<String> objects) {
      if (Iterables.isEmpty(objects)) return (M)this;
      for (String object : objects)
        phoneNumber(object);
      return (M)this;
    }
    public M urls(String url) {
      if (url == null) return (M)this;
      return urls(new IRI(url));
    }
    public M urls(String... objects) {
      if (objects.length == 0) return (M)this;
      for (String object : objects)
        urls(object);
      return (M)this;
    }
    public M urls(Iterable<Object> objects) {
      if (Iterables.isEmpty(objects)) return (M)this;
      for (Object object : objects)
        urls(object.toString());
      return (M)this;
    }
    public M urls(IRI url) {
      if (url == null) return (M)this;
      h = true;
      urls.add(url);
      return (M)this;
    }
    public M urls(IRI... objects) {
      if (objects.length == 0) return (M)this;
      for (IRI object : objects)
        urls(object);
      return (M)this;
    }
    public M aboutMe(String val) {
      set("aboutMe",val);
      return (M)this;
    }
    public M contactPreference(String val) {
      set("contactPreference",val);
      return (M)this;
    }
    public M dn(String val) {
      set("dn",val);
      return (M)this;
    }
    public M name(Supplier<? extends NameObject> val) {
      return name(val.get());
    }
    public M name(NameObject val) {
      set("name",val);
      return (M)this;
    }
    public M nativeName(Supplier<? extends NameObject> object) {
      return nativeName(object.get());
    }
    public M nativeName(NameObject val) {
      set("nativeName",val);
      return (M)this;
    }
    public M preferredName(Supplier<? extends NameObject> object) {
      return preferredName(object.get());
    }
    public M preferredName(NameObject val) {
      set("preferredName",val);
      return (M)this;
    }
    public M profileUrl(String val) {
      return profileUrl(new IRI(val));
    }
    public M profileUrl(IRI val) {
      set("profileUrl",val);
      return (M)this;
    }
    public M status(String val) {
      set("status",val);
      return (M)this;
    }
    public M thumbnailUrl(String val) {
      return thumbnailUrl(new IRI(val));
    }
    public M thumbnailUrl(IRI val) {
      set("thumbnailUrl",val);
      return (M)this;
    }
    public M utcOffset(String val) {
      set("utcOffset",val);
      return (M)this;
    }
    public void preGet() {
      super.preGet();
      if (a) set("accounts",accounts.build());
      if (b) set("addresses",addresses.build());
      if (c) set("alternateNames",altnames.build());
      if (d) set("emails",emails.build());
      if (e) set("ims",ims.build());
      if (f) set("organizations",orgs.build());
      if (g) set("phoneNumbers",phones.build());
      if (h) set("urls",urls.build());
    }
    
  }
}
