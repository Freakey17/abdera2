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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.abdera2.activities.extra.Extra;
import org.apache.abdera2.activities.model.objects.EmbeddedExperience;
import org.apache.abdera2.activities.model.objects.Mood;
import org.apache.abdera2.activities.model.objects.PlaceObject;
import org.apache.abdera2.activities.model.objects.TaskObject;
import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.selector.Selector;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.*;

/**
 * Base class for all Activity Streams Objects.
 */
@SuppressWarnings("unchecked")
public class ASObject extends ASBase {

  public static final String ATTACHMENTS = "attachments";
  public static final String AUTHOR = "author";
  public static final String CONTENT = "content";
  public static final String DISPLAYNAME = "displayName";
  public static final String DOWNSTREAMDUPLICATES = "downstreamDuplicates";
  public static final String ID = "id";
  public static final String IMAGE = "image";
  public static final String OBJECTTYPE = "objectType";
  public static final String PUBLISHED = "published";
  public static final String SUMMARY = "summary";
  public static final String UPDATED = "updated";
  public static final String UPSTREAMDUPLICATES = "upstreamDuplicates";
  public static final String URL = "url";
  public static final String REACTIONS = "reactions";
  
  public static final String INREPLYTO = "inReplyTo";
  public static final String LOCATION = "location";
  public static final String SOURCE = "source";
  public static final String MOOD = "mood";
  public static final String TAGS = "tags";
  public static final String RATING = "rating";
  
  public static final String EMBED = "embed";
  
  public static ASObjectBuilder makeObject() {
    return new ASObjectBuilder();
  }
  
  public static ASObjectBuilder makeObject(String objectType) {
    return new ASObjectBuilder(objectType);
  }
  
  public static class ASObjectBuilder extends Builder<ASObject,ASObjectBuilder> {
    public ASObjectBuilder() {
      super(ASObject.class, ASObjectBuilder.class);
    }
    public ASObjectBuilder(Map<String, Object> map) {
      super(map, ASObject.class, ASObjectBuilder.class);
    }
    public ASObjectBuilder(String objectType) {
      super(objectType, ASObject.class, ASObjectBuilder.class);
    }
  }
  
  public static abstract class Builder<X extends ASObject, M extends Builder<X,M>>
    extends ASBase.Builder<X,M> {

    private ImmutableSet.Builder<ASObject> attachments = ImmutableSet.builder();
    private ImmutableSet.Builder<ASObject> tags = ImmutableSet.builder();
    private ImmutableSet.Builder<ASObject> replies = ImmutableSet.builder();
    private ImmutableSet.Builder<String> downdups = ImmutableSet.builder();
    private ImmutableSet.Builder<String> updups = ImmutableSet.builder();
    private ImmutableSet.Builder<TaskObject> tasks = ImmutableSet.builder();
    private boolean a,t,r,d,u,z;
    
    public Builder(String objectType, Class<X> _class, Class<M> _builder) {
      super(_class,_builder);
      set(OBJECTTYPE,objectType);
    }
    public Builder(Class<X> _class, Class<M> _builder) {
      super(_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X> _class, Class<M> _builder) {
      super(map,_class,_builder);
    }
    
    /**
     * Add a new reaction to the object. You must call experimental() before
     * calling this method.
     */
    public M reaction(Supplier<? extends TaskObject> object) {
      checkState(isExperimentalEnabled(),"Experimental features not yet enabled. Call experimental() first.");
      return reaction(object.get());
    }
    
    /**
     * Add a new reaction to the object. You must call experimental() before
     * calling this method.
     */
    public M reaction(TaskObject object) {
      checkState(isExperimentalEnabled(),"Experimental features not yet enabled. Call experimental() first.");
      if (object == null) return (M)this;
      z = true;
      tasks.add(object);
      return (M)this;
    }
    
    /**
     * Add a new reaction to the object. You must call experimental() before
     * calling this method.
     */
    public M reaction(Supplier<? extends TaskObject>... objects) {
      checkState(isExperimentalEnabled(),"Experimental features not yet enabled. Call experimental() first.");
      if (objects == null) return (M)this;
      for (Supplier<? extends TaskObject> object : objects)
        reaction(object.get());
      return (M)this;
    }
    
    /**
     * Add a new reaction to the object. You must call experimental() before
     * calling this method.
     */
    public M reaction(TaskObject... objects) {
      checkState(isExperimentalEnabled(),"Experimental features not yet enabled. Call experimental() first.");
      if (objects == null) return (M)this;
      for (TaskObject obj : objects)
        reaction(obj);
      return (M)this;
    }
    
    /**
     * Add a new reaction to the object. You must call experimental() before
     * calling this method.
     */
    public M reaction(Iterable<? extends TaskObject> objects) {
      checkState(isExperimentalEnabled(),"Experimental features not yet enabled. Call experimental() first.");
      if (objects == null) return (M)this;
      for (TaskObject obj : objects)
        reaction(obj);
      return (M)this;
    }
    
    public M attachment(Supplier<? extends ASObject>... objects) {
      if (objects == null) return (M)this;
      for (Supplier<? extends ASObject> object : objects)
        attachment(object.get());
      return (M)this;
    }
    
    public M attachment(ASObject... objects) {
      if (objects == null) return (M)this;
      for (ASObject obj : objects)
        attachment(obj);
      return (M)this;
    }
    
    public M attachment(Iterable<? extends ASObject> objects) {
      if (objects == null) return (M)this;
      for (ASObject obj : objects)
        attachment(obj);
      return (M)this;
    }
    
    public M attachment(Supplier<? extends ASObject> object) {
      return attachment(object.get());
    }
    
    public M attachment(ASObject object) {
      if (object == null) return (M)this;
      a = true;
      attachments.add(object);
      return (M)this;
    }
    
    public M downstreamDuplicate(String... objects) {
      if (objects == null) return (M)this;
      for (String obj : objects)
        downstreamDuplicate(obj);
      return (M)this;
    }
    
    public M downstreamDuplicate(Iterable<String> objects) {
      if (objects == null) return (M)this;
      for (String obj : objects)
        downstreamDuplicate(obj);
      return (M)this;
    }
    
    public M downstreamDuplicate(String id) {
      if (id == null) return (M)this;
      d = true;
      downdups.add(id);
      return (M)this;
    }
    
    public M inReplyTo(Supplier<? extends ASObject> object) {
      return inReplyTo(object.get());
    }
    
    public M inReplyTo(ASObject object) {
      if (object == null) return (M)this;
      r = true;
      replies.add(object);
      return (M)this;
    }
    
    public M inReplyTo(Supplier<? extends ASObject>... objects) {
      if (objects == null) return (M)this;
      for (Supplier<? extends ASObject> object : objects)
        inReplyTo(object.get());
      return (M)this;
    }
    
    public M inReplyTo(ASObject... objects) {
      if (objects == null) return (M)this;
      for (ASObject object : objects)
        inReplyTo(object);
      return (M)this;
    }
    
    public M inReplyTo(Iterable<ASObject> objects) {
      if (objects == null) return (M)this;
      for (ASObject object : objects)
        inReplyTo(object);
      return (M)this;
    }
    
    public M tag(Supplier<? extends ASObject>... objects) {
      if (objects == null) return (M)this;
      for (Supplier<? extends ASObject>object : objects )
        tag(object.get());
      return (M)this;
    }
    
    public M tag(ASObject... objects) {
      if (objects == null) return (M)this;
      for (ASObject obj : objects)
        tag(obj);
      return (M)this;
    }
    
    public M tag(Iterable<? extends ASObject> objects) {
      if (objects == null) return (M)this;
      for (ASObject obj : objects)
        tag(obj);
      return (M)this;
    }
    
    public M tag(Supplier<? extends ASObject> object) {
      return tag(object.get());
    }
    
    public M tag(ASObject object) {
      if (object == null) return (M)this;
      t = true;
      tags.add(object);
      return (M)this;
    }
    
    public M upstreamDuplicate(String... objects) {
      if (objects == null) return (M)this;
      for (String obj : objects)
        upstreamDuplicate(obj);
      return (M)this;
    }
    
    public M upstreamDuplicate(Iterable<String> objects) {
      if (objects == null) return (M)this;
      for (String obj : objects)
        upstreamDuplicate(obj);
      return (M)this;
    }
    
    public M upstreamDuplicate(String id) {
      if (id == null) return (M)this;
      u = true;
      updups.add(id);
      return (M)this;
    }
    
    public M author(Supplier<? extends ASObject> object) {
      return author(object.get());
    }
    
    public M author(ASObject object) {
      set(AUTHOR,object);
      return (M)this;
    }
    
    public M content(String content) {
      set(CONTENT,content);
      return (M)this;
    }
    
    public M displayName(String displayName) {
      set(DISPLAYNAME,displayName);
      return (M)this;
    }
    
    public M embed(Supplier<? extends ASObject> object) {
      return embed(object.get());
    }
    
    public M embed(ASObject object) {
      set(EMBED,object);
      return (M)this;
    }
    
    public M embeddedExperience(Supplier<? extends EmbeddedExperience> object) {
      return embeddedExperience(object.get());
    }
    
    public M embeddedExperience(EmbeddedExperience ee) {
      set(
        "openSocial",
        ASBase
          .make()
            .set("embed",ee)
          .get()
      );
      return (M)this;
    }
    
    public M id(String id) {
      set(ID,id);
      return (M)this;
    }
    
    public M id(IRI id) {
      set(ID,checkNotNull(id).toString());
      return (M)this;
    }
    
    public M image(Supplier<MediaLink> object) {
      return image(object.get());
    }
    
    public M image(MediaLink link) {
      set(IMAGE,link);
      return (M)this;
    }
    
    public M location(Supplier<? extends PlaceObject> object) {
      return location(object.get());
    }
    
    public M location(PlaceObject object) {
      set(LOCATION,object);
      return (M)this;
    }
    
    public M mood(Supplier<Mood> object) {
      return mood(object.get());
    }
    
    public M mood(Mood mood) {
      set(MOOD,mood);
      return (M)this;
    }
    
    public M objectType(String type) {
      set(OBJECTTYPE,type);
      return (M)this;
    }
    
    public M published(DateTime dt) {
      set(PUBLISHED,dt);
      return (M)this;
    }
    
    public M publishedNow() {
      return published(DateTimes.now());
    }
    
    public M rating(double rating) {
      set(RATING,rating);
      return (M)this;
    }
    
    public M source(Supplier<? extends ASObject> object) {
      return source(object.get());
    }
    
    public M source(ASObject object) {
      set(SOURCE,object);
      return (M)this;
    }
    
    public M summary(String summary) {
      set(SUMMARY,summary);
      return (M)this;
    }
    
    public M updated(DateTime dt) {
      set(UPDATED,dt);
      return (M)this;
    }
    
    public M updatedNow() {
      return updated(DateTimes.now());
    }
    
    public M url(IRI url) {
      set(URL,url);
      try {
        if (isExperimentalEnabled())
          link("alternate",url);
      } catch (IllegalStateException t) {}
      return (M)this;
    }
    
    public M url(String url) {
      return url(url != null ? new IRI(url) : null);
    }
    
    public void preGet() {
      super.preGet();
      if (a) set(ATTACHMENTS, attachments.build());
      if (t) set(TAGS, tags.build());
      if (r) set(INREPLYTO, replies.build());
      if (d) set(DOWNSTREAMDUPLICATES, downdups.build());
      if (u) set(UPSTREAMDUPLICATES, updups.build());
      if (z) set(REACTIONS, tasks.build());
    }
    
  }
    
  public ASObject(Map<String,Object> map) {
    super(map,ASObjectBuilder.class,ASObject.class);
  }
  
  public <X extends ASObject, M extends Builder<X,M>>ASObject(Map<String,Object> map, Class<M> _class, Class<X> _obj) {
    super(map,_class,_obj);
  }
  
  /**
   * Returns the value of the "attachments" property
   */
  public Iterable<ASObject> getAttachments() {
    return checkEmpty((Iterable<ASObject>)getProperty(ATTACHMENTS));
  }
  
  /**
   * Return the author of this object
   */
  public <E extends ASObject>E getAuthor() {
    return (E)getProperty(AUTHOR);
  }
  
  /**
   * Get the content of the object
   */
  public String getContent() {
    return getProperty(CONTENT);
  }
  
  /**
   * Get the displayName of the object
   */
  public String getDisplayName() {
    return getProperty(DISPLAYNAME);
  }
  
  /**
   * Return the list of downstream duplicate ids for this object. 
   * When an object is redistributed by third parties, the value of the "id"
   * property may change. When such changes do occur, it becomes difficult
   * to track duplicate versions of the same object. The "downstreamDuplicates"
   * and "upstreamDuplicates" properties on the object can be used to track 
   * modifications that occur in the "id" of the object in order to make
   * duplication detection easier
   */
  public Iterable<String> getDownstreamDuplicates() {
    return checkEmpty(this.<Iterable<String>>getProperty(DOWNSTREAMDUPLICATES));
  }
  
  public Iterable<TaskObject> getReactions() {
    return checkEmpty(this.<Iterable<TaskObject>>getProperty(REACTIONS));
  }
  
  /**
   * Get the id of this object
   */
  public String getId() {
    return getProperty(ID);
  }
  
  /**
   * Get the "image" property
   */
  public MediaLink getImage() {
    return getProperty(IMAGE);
  }
  
  /**
   * Get the objectType
   */
  public String getObjectType() {
    return getProperty(OBJECTTYPE);
  }
    
  /**
   * Get the "published" datetime
   */
  public DateTime getPublished() {
    return getProperty(PUBLISHED);
  }
  
  /**
   * Get the "summary" property
   */
  public String getSummary() {
    return getProperty(SUMMARY);
  }
  
  /**
   * Get the "updated" property
   */
  public DateTime getUpdated() {
    return getProperty(UPDATED);
  }
  
  /**
   * Return the list of upstream duplicate ids for this object. 
   * When an object is redistributed by third parties, the value of the "id"
   * property may change. When such changes do occur, it becomes difficult
   * to track duplicate versions of the same object. The "downstreamDuplicates"
   * and "upstreamDuplicates" properties on the object can be used to track 
   * modifications that occur in the "id" of the object in order to make
   * duplication detection easier
   */
  public Iterable<String> getUpstreamDuplicates() {
    return checkEmpty((Iterable<String>)getProperty(UPSTREAMDUPLICATES));
  }
  
  /**
   * Get the url of this object
   */
  public IRI getUrl() {
    return getProperty(URL);
  }
  
  /**
   * Get the collection of objects this object is considered a response to
   */
  public Iterable<ASObject> getInReplyTo() {
    return checkEmpty((Iterable<ASObject>)getProperty(INREPLYTO));
  }
  
  /**
   * Get the collection of objects this object is considered a response to
   * using the specified selector to filter the results
   */
  public Iterable<ASObject> getInReplyTo(Selector<ASObject> selector) {
    List<ASObject> list= new ArrayList<ASObject>();
    for (ASObject obj : getInReplyTo())
      if (selector.apply(obj))
        list.add(obj);
    return list;
  }
    
  /**
   * Get the "location" property
   */
  public PlaceObject getLocation() {
    return getProperty(LOCATION);
  }
  
  /**
   * Get the "mood" property
   */
  public Mood getMood() {
    return getProperty(MOOD);
  }
  
  /**
   * Get the "source" property
   */
  public <E extends ASObject>E getSource() {
    return (E)getProperty(SOURCE);
  }
  
  /**
   * Get the collection of tags for this object
   */
  public Iterable<ASObject> getTags() {
    return checkEmpty((Iterable<ASObject>)getProperty(TAGS));
  }
  
  /**
   * @see org.apache.abdera2.activities.model.ASObject.getEmbeddedExperience()
   * {"embed":{...}}
   */
  public <T extends ASObject>T getEmbed() {
    return (T)getProperty(EMBED);
  }
  
  /**
   * Get the "rating" property
   */
  public double getRating() {
    return (Double)getProperty(RATING);
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String objectType = getObjectType();
    String displayName = getDisplayName();
    if (displayName != null)
      sb.append(displayName);
    else if (objectType != null) {
      char s = objectType.charAt(0);
      if ("aeiou".indexOf(s) > -1) 
        sb.append("an ");
      else sb.append("a ");
      sb.append(objectType);
    } else {
      sb.append("an object");
    }
    return sb.toString();
  }

  /**
   * "Embedded Experiences" were introduced to Activity Streams 
   * by the OpenSocial 2.0 specification, while functionally not 
   * specific to OpenSocial, the spec defines that the "Embedded 
   * Experience" document has to be wrapped within an "openSocial"
   * extension property. Other applications, such as Google+, however,
   * use the "embed" property directly within an object without the
   * "openSocial" wrapper. To use the "embed" property without 
   * the "openSocial" wrapper, use the setEmbed/getEmbed properties
   * on ASObject. To use OpenSocial style Embedded Experiences, 
   * use the setEmbeddedExperience/getEmbeddedExperience/hasEmbeddedExperience
   * methods. The OpenSocial style Embedded Experience should be used
   * primarily to associate OpenSocial Gadgets with an activity 
   * object while the alternative "embed" can be used to reference
   * any kind of embedded content.
   * 
   * {"openSocial":{"embed":{...}}}
   */
  public EmbeddedExperience getEmbeddedExperience() {
    if (!has("openSocial")) return null;
    ASBase os = getProperty("openSocial");
    if (!os.has("embed")) return null;
    ASBase e = os.getProperty("embed");
    if (!(e instanceof EmbeddedExperience))
      e = e.as(EmbeddedExperience.class);
    return (EmbeddedExperience) e;
  }
  
  /**
   * Checks to see if the "openSocial":{"embed":{...}} property exists
   */
  public boolean hasEmbeddedExperience() {
    if (!has("openSocial")) return false;
    ASBase os = getProperty("openSocial");
    return os.has("embed");
  }
  
  /**
   * Performs an equivalence check. Two ASObjects are equivalent if they
   * share the same objectType and id property values. All other properties
   * can be different (e.g. they are different versions of the same object)
   */
  public boolean is(ASObject obj) {
    return Extra.sameIdentity(this).apply(obj);
  }
  
  /**
   * Returns a union of all known IDs for this object.. specifically,
   * this is a union of the "id", "downstreamDuplicates" and "upstreamDuplicates"
   * properties
   */
  public Iterable<String> getKnownIds() {
    ImmutableSet.Builder<String> list = ImmutableSet.builder();
    if (has("id")) list.add(getId());
    list.addAll(checkEmpty(getDownstreamDuplicates()));
    list.addAll(checkEmpty(getUpstreamDuplicates()));
    return list.build();
  }
  
  public <T extends ASObject,M extends Builder<T,M>>T as(
    Class<T> type, 
    String newObjectType) {
    return (T)as(type,withoutFields("objectType"))
      .<T,M>template()
        .objectType(newObjectType).get();
  }
  
}
