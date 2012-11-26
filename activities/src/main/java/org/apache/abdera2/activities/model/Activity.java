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
package org.apache.abdera2.activities.model;

import java.util.Map;
import java.lang.Iterable;

import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.selector.Selector;
import org.joda.time.DateTime;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * An Activity. Represents some action that has been taken. At it's core,
 * every Activity consists of an Actor (who performed the action), a Verb
 * (what action was performed) and an Object (what was acted upon), and a 
 * Target (to which the action is directed). For instance, if I said
 * "John posted a photo to his album", the Actor is "John", the Verb is 
 * "post", the Object is "a photo" and the Target is "his album". 
 */
@SuppressWarnings("unchecked")
public class Activity extends ASObject {

  public static final String ACTOR = "actor";
  public static final String CONTENT = "content";
  public static final String GENERATOR = "generator";
  public static final String ICON = "icon";
  public static final String ID = "id";
  public static final String OBJECT = "object";
  public static final String PUBLISHED = "published";
  public static final String PROVIDER = "provider";
  public static final String TARGET = "target";
  public static final String TITLE = "title";
  public static final String UPDATED = "updated";
  public static final String URL = "url";
  public static final String VERB = "verb";
  
  /**
   * Used to specify the target audience for an Activity.
   */
  public enum Audience { 
    /** Identifies the public primary target audience of the Activity **/
    TO,
    /** Identifies the private primary target audience of the Activity **/
    BTO, 
    /** Identifies the public secondary target audience of the Activity **/
    CC, 
    /** Identifies the private secondary target audience of the Activity **/
    BCC; 
    String label() {
      return name().toLowerCase();
    }
  };
  
  /**
   * Begin creating a new Activity object using the fluent factory API
   */
  public static ActivityBuilder makeActivity() {
    return new ActivityBuilder("activity");
  }
  
  @Name("activity")
  public static class ActivityBuilder extends Builder<Activity,ActivityBuilder> {
    public ActivityBuilder() {
      super(Activity.class,ActivityBuilder.class);
    }
    public ActivityBuilder(Map<String, Object> map) {
      super(map, Activity.class,ActivityBuilder.class);
    }
    public ActivityBuilder(String objectType) {
      super(objectType,Activity.class,ActivityBuilder.class);
    }
  }
  
  public static abstract class Builder<X extends Activity, M extends Builder<X,M>> 
    extends ASObject.Builder<X,M> {

    private final ImmutableSet.Builder<ASObject> to = 
      ImmutableSet.builder();
    private final ImmutableSet.Builder<ASObject> bto = 
      ImmutableSet.builder();
    private final ImmutableSet.Builder<ASObject> cc = 
      ImmutableSet.builder();
    private final ImmutableSet.Builder<ASObject> bcc = 
      ImmutableSet.builder();
    boolean a,b,c,d;
    
    protected Builder(Class<X> _class, Class<M> _builder) {
      super(_class,_builder);
    }
    
    protected Builder(String objectType,Class<X> _class, Class<M> _builder) {
      super(objectType,_class,_builder);
    }
    
    protected Builder(Map<String,Object> map,Class<X> _class, Class<M> _builder) {
      super(map,_class,_builder);
    }
    
    public M to(Supplier<? extends ASObject> object) {
      return to(object.get());
    }
    public M to(ASObject object) {
      a = true;
      to.add(object);
      return (M)this;
    }
    public M cc(Supplier<? extends ASObject> object) {
      return cc(object.get());
    }
    public M cc(ASObject object) {
      b = true;
      cc.add(object);
      return (M)this;
    }
    public M bcc(Supplier<? extends ASObject> object) {
      return bcc(object.get());
    }
    public M bcc(ASObject object) {
      c = true;
      bcc.add(object);
      return (M)this;
    }
    public M bto(Supplier<? extends ASObject> object) {
      return bto(object.get());
    }
    public M bto(ASObject object) {
      d = true;
      bto.add(object);
      return (M)this;
    }
    public M actor(Supplier<? extends ASObject> object) {
      return actor(object.get());
    }
    public M actor(ASObject object) {
      set(ACTOR,object);
      return (M)this;
    }
    public M generator(Supplier<? extends ASObject> object) {
      return generator(object.get());
    }
    public M generator(ASObject object) {
      set(GENERATOR,object);
      return (M)this;
    }
    public M icon(Supplier<MediaLink> object) {
      return icon(object.get());
    }
    public M icon(MediaLink link) {
      set(ICON,link);
      return (M)this;
    }
    public M object(Supplier<? extends ASObject> object) {
      return object(object.get());
    }
    public M object(ASObject object) {
      set(OBJECT,object);
      return (M)this;
    }
    public M provider(Supplier<? extends ASObject> object) {
      return provider(object.get());
    }
    public M provider(ASObject object) {
      set(PROVIDER,object);
      return (M)this;
    }
    public M target(Supplier<? extends ASObject> object) {
      return target(object.get());
    }
    public M target(ASObject object) {
      set(TARGET,object);
      return (M)this;
    }
    public M title(String title) {
      set(TITLE,title);
      return (M)this;
    }
    public M verb(Verb verb) {
      set(VERB,verb);
      return (M)this;
    }
    public M displayName(String displayName) {
      title(displayName);
      return (M)this;
    }
    public M image(Supplier<MediaLink> object) {
      return image(object.get());
    }
    public M image(MediaLink link) {
      icon(link);
      return (M)this;
    }
    public M author(Supplier<? extends ASObject> object) {
      return author(object.get());
    }
    public M author(ASObject author) {
      actor(author);
      return (M)this;
    }
    public void preGet() {
      super.preGet();
      if (a) set(Audience.TO.label(),to.build());
      if (b) set(Audience.CC.label(),cc.build());
      if (c) set(Audience.BCC.label(),bcc.build());
      if (d) set(Audience.BTO.label(),bto.build());
    }
  }
  
  public Activity(Map<String,Object> map) {
    super(map,ActivityBuilder.class,Activity.class);
  }
  
  public <X extends Activity, M extends Builder<X,M>>Activity(Map<String,Object> map, Class<M> _class, Class<X> _obj) {
    super(map,_class, _obj);
  }
  
  public ASObject getActor() {
    return getProperty(ACTOR);
  }
  
  /**
   * Return the value of the "content" property for this activity
   */
  public String getContent() {
    return getProperty(CONTENT);
  }
  
  /**
   * Return the ASObject value of the "generator" property for this
   * activity.
   */
  public <E extends ASObject>E getGenerator() {
    return (E)getProperty(GENERATOR);
  }
  
  /**
   * Return the value of the "icon" property 
   */
  public MediaLink getIcon() {
    return getProperty(ICON);
  }
  
  /**
   * Get the value of the "id" property
   */
  public String getId() {
    return getProperty(ID);
  }
  
  /**
   * Get the Activities Object property
   */
  public <E extends ASObject>E getObject() {
    return (E)getProperty(OBJECT);
  }
  
  /**
   * Get the value of the Activities "published" property
   */
  public DateTime getPublished() {
    return getProperty(PUBLISHED);
  }
  
  /**
   * Get the value of the Activities "provider" property
   */
  public <E extends ASObject>E getProvider() {
    return (E)getProperty(PROVIDER);
  }
  
  /**
   * Get the value of Activities "target" property
   */
  public <E extends ASObject>E getTarget() {
    return (E)getProperty(TARGET);
  }
  
  /**
   * Get the value of the "title" property
   */
  public String getTitle() {
    return getProperty(TITLE);
  }
  
  /**
   * Get the value of the "updated" property
   */
  public DateTime getUpdated() {
    return getProperty(UPDATED);
  }
  
  /**
   * Get the value of the "url" property
   */
  public IRI getUrl() {
    return getProperty(URL);
  }
  
  /**
   * Get the value of the "verb" property
   */
  public Verb getVerb() {
    return getProperty(VERB);
  }
  
  /**
   * Get the value of the "author" property.. for Activity 
   * objects, the "author" property is mapped to the "actor"
   * property in order to avoid duplication of content. 
   * If you want to get the actual "author" property, use
   * getProperty("author")...
   */
  public <E extends ASObject>E getAuthor() {
    return (E)getActor();
  }
  
  /**
   * Get the "displayName" property. For Activity objects, the
   * "displayName" property is mapped to the "title" property.
   * If you want to get the actual "displayName" property,
   * us getProperty("displayName")
   */
  public String getDisplayName() {
    return getTitle();
  }

  /**
   * Get the "image" property. For Activity objects, the
   * "image" property is mapped to the "icon" property.
   * If you want to get the actual "image" property,
   * us getProperty("image")
   */
  public MediaLink getImage() {
    return getIcon();
  }

  /**
   * Get the specified target audience for the activity
   */
  public Iterable<ASObject> getAudience(Audience audience) {
    return checkEmpty((Iterable<ASObject>)getProperty(audience.label()));
  }
  
  /**
   * Get the specified target audience for the activity using the 
   * specified selector as a filter to limit the results. This 
   * can be used, for instance, to quickly determine if a particular 
   * entity is included in the audience of the activity
   */
  public Iterable<ASObject> getAudience(
    Audience audience, 
    Selector<ASObject> selector) {
    return Iterables.filter(getAudience(audience), selector);
  }  
  
}
