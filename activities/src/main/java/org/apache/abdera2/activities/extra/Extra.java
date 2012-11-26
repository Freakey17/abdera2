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
package org.apache.abdera2.activities.extra;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Activity.Audience;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.activities.model.MediaLink;
import org.apache.abdera2.activities.model.Verb;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.misc.Pair;
import org.apache.abdera2.common.selector.AbstractSelector;
import org.apache.abdera2.common.selector.PropertySelector;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.common.selector.Selectors;
import org.joda.time.DateTime;

import com.google.common.base.CaseFormat;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps.EntryTransformer;

import static com.google.common.base.Predicates.*;
import static org.apache.abdera2.activities.model.objects.Objects.*;
import static org.apache.abdera2.common.misc.MorePredicates.*;
import static com.google.common.base.Preconditions.*;

@SuppressWarnings({"unchecked","rawtypes"})
public final class Extra {

  private Extra() {}
  
  /**
   * Returns a Selector that tests whether the provided 
   * activity uses the given Verb.
   */
  public static Selector<Activity> usesVerb(Verb verb) {
    return PropertySelector.<Activity>create(
      Activity.class, 
      "getVerb", 
      equalTo(verb));
  }
  
  /**
   * Returns a Selector that tests whether the provided
   * activity uses the given ObjectType
   */
  public static Selector<ASObject> isObjectType(String name) {
    return PropertySelector.<ASObject>create(
      ASObject.class,
      "getObjectType",
      equalsIgnoreCase(name));
  }
  
  /**
   * Returns a selector that tests the given Objects "published" property.
   * This can typically be used with the various Range predicates provided
   * by the (@see org.apache.abdera2.common.date.DateTimes) class
   */
  public static <A extends ASObject>Selector<A> published(
    Class<A> _class, 
    Predicate<?> predicate) {
    return PropertySelector.<A>create(
      _class,
      "getPublished",
      predicate);
  }
  
  /**
   * Returns a selector that tests the given Activities "published" property.
   */
  public static Selector<Activity> activityPublished(Predicate<DateTime> predicate) {
    return published(Activity.class,predicate);
  }
  
  /**
   * Returns a selector that tests the given Objects "published" property
   */
  public static Selector<ASObject> objectPublished(Predicate<DateTime> predicate) {
    return published(ASObject.class,predicate);
  }
  
  /**
   * Returns a Selector that tests the given Objects "updated" property.
   * This can typically be used with the various Range predicates provided
   * by the (@see org.apache.abder2.common.date.DateTimes) class
   */
  public static <A extends ASObject>Selector<A> updated(Class<A> _class, Predicate<DateTime> predicate) {
    return PropertySelector.<A>create(
      _class,
      "getUpdated",
      predicate);
  }
  
  /**
   * Returns a Selector that tests the given Activities "updated" property.
   */
  public static Selector<Activity> activityUpdated(Predicate<DateTime> predicate) {
    return updated(Activity.class,predicate);
  }
  
  /**
   * Returns a Selector that tests the given Objects "updated" property
   */
  public static Selector<ASObject> objectUpdated(Predicate<DateTime> predicate) {
    return updated(ASObject.class,predicate);
  }
       
  public static Selector<Activity> isTo(ASObject obj) {
    return Extra.audienceHas(Audience.TO, sameIdentity(obj));
  }
  
  public static Selector<Activity> isBcc(ASObject obj) {
    return Extra.audienceHas(Audience.BCC, sameIdentity(obj));
  }
  
  public static Selector<Activity> isCc(ASObject obj) {
    return Extra.audienceHas(Audience.CC, sameIdentity(obj));
  }
  
  public static Selector<Activity> isBTo(ASObject obj) {
    return Extra.audienceHas(Audience.BTO, sameIdentity(obj));
  }
  
  public static Selector<Activity> isBccMe() {
    return audienceHasMe(Audience.BCC);
  }
  
  public static Selector<Activity> isBccMeOr(ASObject obj) {
    return audienceHasMeOr(Audience.BCC, obj);
  }
  
  public static Selector<Activity> isBccSelf() {
    return audienceHasSelf(Audience.BCC);
  }
  
  public static Selector<Activity> isBccSelfOr(ASObject obj) {
    return audienceHasSelfOr(Audience.BCC, obj);
  }
  
  public static Selector<Activity> isBccFriends() {
    return audienceHasFriends(Audience.BCC);
  }
  
  public static Selector<Activity> isBccFriendsOr( ASObject obj) {
    return audienceHasFriendsOr(Audience.BCC, obj);
  }
  
  public static Selector<Activity> isBccFriends( String id) {
    return audienceHasFriends(Audience.BCC, id);
  }
  
  public static Selector<Activity> isBccFriendsOr( String id, ASObject obj) {
    return audienceHasFriendsOr(Audience.BCC, id, obj);
  }
  
  public static Selector<Activity> isBccNetwork() {
    return audienceHasNetwork(Audience.BCC);
  }
  
  public static Selector<Activity> isBccNetworkOr( ASObject obj) {
    return audienceHasNetworkOr(Audience.BCC, obj);
  }
  
  public static Selector<Activity> isBccAll() {
    return audienceHasAll(Audience.BCC);
  }
  
  public static Selector<Activity> isBccAllOr( ASObject obj) {
    return audienceHasAllOr(Audience.BCC, obj);
  }
  
  public static Selector<Activity> isBccPublic() {
    return audienceHasPublic(Audience.BCC);
  }
  
  public static Selector<Activity> isBccPublicOr( ASObject obj) {
    return audienceHasPublicOr(Audience.BCC, obj);
  }
  
  public static Selector<Activity> isCcMe() {
    return audienceHasMe(Audience.CC);
  }
  
  public static Selector<Activity> isCcMeOr( ASObject obj) {
    return audienceHasMeOr(Audience.CC, obj);
  }
  
  public static Selector<Activity> isCcSelf() {
    return audienceHasSelf(Audience.CC);
  }
  
  public static Selector<Activity> isCcSelfOr( ASObject obj) {
    return audienceHasSelfOr(Audience.CC, obj);
  }
  
  public static Selector<Activity> isCcFriends() {
    return audienceHasFriends(Audience.CC);
  }
  
  public static Selector<Activity> isCcFriendsOr( ASObject obj) {
    return audienceHasFriendsOr(Audience.CC, obj);
  }
  
  public static Selector<Activity> isCcFriends( String id) {
    return audienceHasFriends(Audience.CC, id);
  }
  
  public static Selector<Activity> isCcFriendsOr( String id, ASObject obj) {
    return audienceHasFriendsOr(Audience.CC, id, obj);
  }
  
  public static Selector<Activity> isCcNetwork() {
    return audienceHasNetwork(Audience.CC);
  }
  
  public static Selector<Activity> isCcNetworkOr( ASObject obj) {
    return audienceHasNetworkOr(Audience.CC, obj);
  }
  
  public static Selector<Activity> isCcAll() {
    return audienceHasAll(Audience.CC);
  }
  
  public static Selector<Activity> isCcAllOr( ASObject obj) {
    return audienceHasAllOr(Audience.CC, obj);
  }
  
  public static Selector<Activity> isCcPublic() {
    return audienceHasPublic(Audience.CC);
  }
  
  public static Selector<Activity> isCcPublicOr( ASObject obj) {
    return audienceHasPublicOr(Audience.CC, obj);
  }
  
  public static Selector<Activity> isBtoMe() {
    return audienceHasMe(Audience.BTO);
  }
  
  public static Selector<Activity> isBtoMeOr( ASObject obj) {
    return audienceHasMeOr(Audience.BTO, obj);
  }
  
  public static Selector<Activity> isBtoSelf() {
    return audienceHasSelf(Audience.BTO);
  }
  
  public static Selector<Activity> isBtoSelfOr( ASObject obj) {
    return audienceHasSelfOr(Audience.BTO, obj);
  }
  
  public static Selector<Activity> isBtoFriends() {
    return audienceHasFriends(Audience.BTO);
  }
  
  public static Selector<Activity> isBtoFriendsOr( ASObject obj) {
    return audienceHasFriendsOr(Audience.BTO, obj);
  }
  
  public static Selector<Activity> isBtoFriends( String id) {
    return audienceHasFriends(Audience.BTO, id);
  }
  
  public static Selector<Activity> isBtoFriendsOr( String id, ASObject obj) {
    return audienceHasFriendsOr(Audience.BTO, id, obj);
  }
  
  public static Selector<Activity> isBtoNetwork() {
    return audienceHasNetwork(Audience.BTO);
  }
  
  public static Selector<Activity> isBtoNetworkOr( ASObject obj) {
    return audienceHasNetworkOr(Audience.BTO, obj);
  }
  
  public static Selector<Activity> isBtoAll() {
    return audienceHasAll(Audience.BTO);
  }
  
  public static Selector<Activity> isBtoAllOr( ASObject obj) {
    return audienceHasAllOr(Audience.BTO, obj);
  }
  
  public static Selector<Activity> isBtoPublic() {
    return audienceHasPublic(Audience.BTO);
  }
  
  public static Selector<Activity> isBtoPublicOr( ASObject obj) {
    return audienceHasPublicOr(Audience.BTO, obj);
  }
  
  public static Selector<Activity> isToOwner() {
    return audienceHasOwner(Audience.TO);
  }
  
  public static Selector<Activity> isToViewer() {
    return audienceHasViewer(Audience.TO);
  }
  
  public static Selector<Activity> isToOwnerOr(ASObject obj) {
    return audienceHasOwnerOr(Audience.TO,obj);
  }
  
  public static Selector<Activity> isToViewerOr(ASObject obj) {
    return audienceHasViewerOr(Audience.TO,obj);
  }
  
  public static Selector<Activity> isBtoOwner() {
    return audienceHasOwner(Audience.BTO);
  }
  
  public static Selector<Activity> isBtoViewer() {
    return audienceHasViewer(Audience.BTO);
  }
  
  public static Selector<Activity> isBtoOwnerOr(ASObject obj) {
    return audienceHasOwnerOr(Audience.BTO,obj);
  }
  
  public static Selector<Activity> isBtoViewerOr(ASObject obj) {
    return audienceHasViewerOr(Audience.BTO,obj);
  }
  
  public static Selector<Activity> isCcOwner() {
    return audienceHasOwner(Audience.CC);
  }
  
  public static Selector<Activity> isCcViewer() {
    return audienceHasViewer(Audience.CC);
  }
  
  public static Selector<Activity> isCcOwnerOr(ASObject obj) {
    return audienceHasOwnerOr(Audience.CC,obj);
  }
  
  public static Selector<Activity> isCcViewerOr(ASObject obj) {
    return audienceHasViewerOr(Audience.CC,obj);
  }

  public static Selector<Activity> isBccOwner() {
    return audienceHasOwner(Audience.BCC);
  }
  
  public static Selector<Activity> isBccViewer() {
    return audienceHasViewer(Audience.BCC);
  }
  
  public static Selector<Activity> isBccOwnerOr(ASObject obj) {
    return audienceHasOwnerOr(Audience.BCC,obj);
  }
  
  public static Selector<Activity> isBccViewerOr(ASObject obj) {
    return audienceHasViewerOr(Audience.BCC,obj);
  }
  
  public static Selector<Activity> isToMe() {
    return audienceHasMe(Audience.TO);
  }
  
  public static Selector<Activity> isToMeOr( ASObject obj) {
    return audienceHasMeOr(Audience.TO, obj);
  }
  
  public static Selector<Activity> isToSelf() {
    return audienceHasSelf(Audience.TO);
  }
  
  public static Selector<Activity> isToSelfOr( ASObject obj) {
    return audienceHasSelfOr(Audience.TO, obj);
  }
  
  public static Selector<Activity> isToFriends() {
    return audienceHasFriends(Audience.TO);
  }
  
  public static Selector<Activity> isToFriendsOr( ASObject obj) {
    return audienceHasFriendsOr(Audience.TO, obj);
  }
  
  public static Selector<Activity> isToFriends( String id) {
    return audienceHasFriends(Audience.TO, id);
  }
  
  public static Selector<Activity> isToFriendsOr( String id, ASObject obj) {
    return audienceHasFriendsOr(Audience.TO, id, obj);
  }
  
  public static Selector<Activity> isToNetwork() {
    return audienceHasNetwork(Audience.TO);
  }
  
  public static Selector<Activity> isToNetworkOr( ASObject obj) {
    return audienceHasNetworkOr(Audience.TO, obj);
  }
  
  public static Selector<Activity> isToAll() {
    return audienceHasAll(Audience.TO);
  }
  
  public static Selector<Activity> isToAllOr( ASObject obj) {
    return audienceHasAllOr(Audience.TO, obj);
  }
  
  public static Selector<Activity> isToPublic() {
    return audienceHasPublic(Audience.TO);
  }
  
  public static Selector<Activity> isToPublicOr( ASObject obj) {
    return audienceHasPublicOr(Audience.TO, obj);
  }
  
  public static Selector<Activity> actorIsViewer() {
    return actorIs(Extra.<Activity>isViewer());
  }
  
  public static Selector<Activity> actorIsViewerOr(ASObject object) {
    return actorIs(Extra.<Activity>isViewerOr(object));
  }
  
  public static Selector<Activity> actorIsOwner() {
    return actorIs(Extra.<Activity>isOwner());
  }
  
  public static Selector<Activity> actorIsOwnerOr(ASObject object) {
    return actorIs(Extra.<Activity>isOwnerOr(object));
  }
  
  public static Selector<Activity> actorIsMe() {
    return actorIs(Extra.<Activity>isMe());
  }
  
  public static Selector<Activity> actorIsMeOr(ASObject object) {
    return actorIs(Extra.<Activity>isMeOr(object));
  }
  
  public static Selector<Activity> actorIsSelf() {
    return actorIs(Extra.<Activity>isSelf());
  }
  
  public static Selector<Activity> actorIsSelfOr(ASObject object) {
    return actorIs(Extra.<Activity>isSelfOr(object));
  }
  
  public static Selector<Activity> actorIsFriends() {
    return actorIs(Extra.<Activity>isFriends());
  }
  
  public static Selector<Activity> actorIsFriendsOr(ASObject object) {
    return actorIs(Extra.<Activity>isFriendsOr(object));
  }
  
  public static Selector<Activity> actorIsFriends(String id) {
    return actorIs(Extra.<Activity>isFriends(id));
  }
  
  public static Selector<Activity> actorIsFriendsOr(String id, ASObject object) {
    return actorIs(Extra.<Activity>isFriendsOr(id, object));
  }
  
  public static Selector<Activity> actorIsNetwork() {
    return actorIs(Extra.<Activity>isNetwork());
  }
  
  public static Selector<Activity> actorIsNetworkOr(ASObject object) {
    return actorIs(Extra.<Activity>isNetworkOr(object));
  }
  
  public static Selector<Activity> actorIsAll() {
    return actorIs(Extra.<Activity>isAll());
  }
  
  public static Selector<Activity> actorIsAllOr(ASObject object) {
    return actorIs(Extra.<Activity>isAllOr(object));
  }
  
  public static Selector<Activity> actorIsPublic() {
    return actorIs(Extra.<Activity>isPublic());
  }
  
  public static Selector<Activity> actorIsPublicOr(ASObject object) {
    return actorIs(Extra.<Activity>isPublicOr(object));
  }
  
  private static Selector<Activity> actorIs(Predicate<Activity> pred) {
    return 
        PropertySelector
          .<Activity>create(
            Activity.class, 
            "getActor", 
            pred);
  }
  
  public static Selector<Activity> actorIs(ASObject obj) {
    return actorIs(Extra.<Activity>sameIdentity(obj));
  }
   
  public static Selector<Activity> audienceHasViewer(Audience audience) {
    return audienceHas(audience,isViewer());
  }
  
  public static Selector<Activity> audienceHasOwner(Audience audience) {
    return audienceHas(audience,isOwner());
  }
  
  public static Selector<Activity> audienceHasViewerOr(Audience audience, ASObject obj) {
    return audienceHas(audience,isViewerOr(obj));
  }
  
  public static Selector<Activity> audienceHasOwnerOr(Audience audience, ASObject obj) {
    return audienceHas(audience,isOwnerOr(obj));
  }
  
  public static Selector<Activity> audienceHasMe(Audience audience) {
    return audienceHas(audience,isMe());
  }
  
  public static Selector<Activity> audienceHasMeOr(Audience audience, ASObject obj) {
    return audienceHas(audience,isMeOr(obj));
  }
  
  public static Selector<Activity> audienceHasSelf(Audience audience) {
    return audienceHas(audience,isSelf());
  }
  
  public static Selector<Activity> audienceHasSelfOr(Audience audience, ASObject obj) {
    return audienceHas(audience,isSelfOr(obj));
  }
  
  public static Selector<Activity> audienceHasFriends(Audience audience) {
    return audienceHas(audience,isFriends());
  }
  
  public static Selector<Activity> audienceHasFriendsOr(Audience audience, ASObject obj) {
    return audienceHas(audience,isFriendsOr(obj));
  }
  
  public static Selector<Activity> audienceHasFriends(Audience audience, String id) {
    return audienceHas(audience,isFriends(id));
  }
  
  public static Selector<Activity> audienceHasFriendsOr(Audience audience, String id, ASObject obj) {
    return audienceHas(audience,isFriendsOr(id,obj));
  }
  
  public static Selector<Activity> audienceHasNetwork(Audience audience) {
    return audienceHas(audience,isNetwork());
  }
  
  public static Selector<Activity> audienceHasNetworkOr(Audience audience, ASObject obj) {
    return audienceHas(audience,isNetworkOr(obj));
  }
  
  public static Selector<Activity> audienceHasAll(Audience audience) {
    return audienceHas(audience,isMe());
  }
  
  public static Selector<Activity> audienceHasAllOr(Audience audience, ASObject obj) {
    return audienceHas(audience,isAllOr(obj));
  }
  
  public static Selector<Activity> audienceHasPublic(Audience audience) {
    return audienceHas(audience,isPublic());
  }
  
  public static Selector<Activity> audienceHasPublicOr(Audience audience, ASObject obj) {
    return audienceHas(audience,isPublicOr(obj));
  }
  
  private static Selector<Activity> audienceHas(
    final Audience audience, 
    final Selector<ASObject> pred) {
      return new AbstractSelector<Activity>() {
        public boolean select(Object item) {
          checkArgument(item instanceof Activity);
          Activity activity = (Activity) item;
          Iterable<ASObject> aud = 
            activity.getAudience(audience, pred);
          return !Iterables.isEmpty(aud);
        }
      };
  }
  
  
  public static <X extends ASObject>Selector<X> isOwner() {
    return (Selector<X>)sameIdentity(OWNER);
  }
  
  public static <X extends ASObject>Selector<X> isViewer() {
    return (Selector<X>)sameIdentity(VIEWER);
  }
  
  public static <X extends ASObject>Selector<X> isMe() {
    return (Selector<X>)sameIdentity(ME);
  }
  
  public static <X extends ASObject>Selector<X> isSelf() {
    return (Selector<X>)sameIdentity(SELF);
  }
  
  public static <X extends ASObject>Selector<X> isFriends() {
    return (Selector<X>)sameIdentity(FRIENDS);
  }
  
  public static <X extends ASObject>Selector<X> isFriends(String id) {
    return (Selector<X>)sameIdentity(FRIENDS(id).get());
  }
  
  public static <X extends ASObject>Selector<X> isNetwork() {
    return (Selector<X>)sameIdentity(NETWORK);
  }
  
  public static <X extends ASObject>Selector<X> isAll() {
    return (Selector<X>)sameIdentity(ALL);
  }
  
  public static <X extends ASObject>Selector<X> isPublic() {
    return (Selector<X>)sameIdentity(PUBLIC);
  }
  
  public static <X extends ASObject>Selector<X> isOwnerOr(ASObject object) {
    Selector<X> s1 = sameIdentity(object);
    Selector<X> s2 = sameIdentity(OWNER);
    return Selectors.<X>or(s1,s2);
  }
  
  public static <X extends ASObject>Selector<X> isViewerOr(ASObject object) {
    Selector<X> s1 = sameIdentity(object);
    Selector<X> s2 = sameIdentity(VIEWER);
    return Selectors.<X>or(s1,s2);
  }
  
  public static <X extends ASObject>Selector<X> isMeOr(ASObject object) {
    Selector<X> s1 = sameIdentity(object);
    Selector<X> s2 = sameIdentity(ME);
    return Selectors.<X>or(s1,s2);
  }
  
  public static <X extends ASObject>Selector<X> isSelfOr(ASObject object) {
    Selector<X> s1 = sameIdentity(object);
    Selector<X> s2 = sameIdentity(SELF);
    return Selectors.<X>or(s1,s2);
  }
  
  public static <X extends ASObject>Selector<X> isFriendsOr(ASObject object) {
    Selector<X> s1 = sameIdentity(object);
    Selector<X> s2 = sameIdentity(FRIENDS);
    return Selectors.<X>or(s1,s2);
  }
  
  public static <X extends ASObject>Selector<X> isFriendsOr(String id, ASObject object) {
    Selector<X> s1 = sameIdentity(object);
    Selector<X> s2 = sameIdentity(FRIENDS(id).get());
    return Selectors.<X>or(s1,s2);
  }
  
  public static <X extends ASObject>Selector<X> isNetworkOr(ASObject object) {
    Selector<X> s1 = sameIdentity(object);
    Selector<X> s2 = sameIdentity(NETWORK);
    return Selectors.<X>or(s1,s2);
  }
  
  public static <X extends ASObject>Selector<X> isAllOr(ASObject object) {
    Selector<X> s1 = sameIdentity(object);
    Selector<X> s2 = sameIdentity(ALL);
    return Selectors.<X>or(s1,s2);
  }
  
  public static <X extends ASObject>Selector<X> isPublicOr(ASObject object) {
    Selector<X> s1 = sameIdentity(object);
    Selector<X> s2 = sameIdentity(PUBLIC);
    return Selectors.<X>or(s1,s2);
  }
  
  /**
   * Returns a Selector that tests if two objects are identity equivalent.
   * ASObjets are identity equivalent if they have the same objectType
   * and id property values.
   */
  public static <X extends ASObject>Selector<X> sameIdentity(final ASObject obj) {
    return new AbstractSelector<X>() {
      public boolean select(Object item) {
        checkArgument(item instanceof ASObject);
        ASObject other = (ASObject) item;
        return IDENTITY_EQUIVALENCE.equivalent(obj, other);
      }     
    };
  }
  
  /**
   * Equivalence instance that can be used to check the equivalence of two
   * ASObjects
   */
  public static final Equivalence<ASObject> IDENTITY_EQUIVALENCE = identity();
  
  /**
   * Equivalence instance that can be used to check the equivalence of two
   * ASObjects. This version also checks the downstreamDuplicates and 
   * upstreamDuplicates values to determine identity equivalence.
   */
  public static final Equivalence<ASObject> IDENTITY_WITH_DUPLICATES_EQUIVALENCE = identityWithDuplicates();
  
  static final EntryTransformer<String,Object,Object> lower_val = 
    caseTransform("alias","objectType");
  
  public static EntryTransformer<String,Object,Object> caseTransform(
    final String... lowerFields) {
    final ImmutableSet<String> fields = ImmutableSet.copyOf(lowerFields);
    return new EntryTransformer<String,Object,Object>() {
      public Object transformEntry(String key, Object value) {
        return fields.contains(key) ?
          value.toString().toLowerCase() :
          value;
      }
    };
  }
  
  /**
   * Two ASObject's are considered equivalent in identity if 
   * they share the same objectType and id property
   * values. Note: This implementation does not yet take 
   * the downstreamDuplicates and upstreamDuplicates properties
   * into account when determining equivalence.
   */
  public static <X extends ASObject>Equivalence<X> identity() {
    return new Equivalence<X>() {
      protected boolean doEquivalent(X a, X b) {
        Selector<Map.Entry<String,Object>> filter = 
          ASBase.withFields("id","alias","objectType","displayName");
        Map<String,Object> map1 = Maps.transformEntries(a.toMap(filter),lower_val);
        Map<String,Object> map2 = Maps.transformEntries(b.toMap(filter),lower_val);
        MapDifference<String,Object> diff = 
          Maps.difference(map1, map2);
        return ((diff.entriesInCommon().containsKey("alias") ||
            diff.entriesInCommon().containsKey("id") || 
            diff.entriesInCommon().containsKey("displayName")) && 
            !diff.entriesDiffering().containsKey("objectType") && 
            !diff.entriesDiffering().containsKey("id") && 
            !diff.entriesOnlyOnLeft().containsKey("id") && 
            !diff.entriesOnlyOnRight().containsKey("id"));
      }
      protected int doHash(ASObject t) {
        return MoreFunctions.genHashCode(
          1, t.getId(), 
          t.getProperty("alias"), 
          t.getObjectType());
      }
    };
  }
  
  public static <X extends ASObject>Equivalence<X> identityWithDuplicates() {
    return new Equivalence<X>() {
      protected boolean doEquivalent(X a, X b) {
        if (IDENTITY_EQUIVALENCE.equivalent(a, b)) 
          return true;
        Iterable<String> aids = a.getKnownIds();
        Iterable<String> bids = b.getKnownIds();
        // if is empty, it's not a duplicate, so return false
        // if isn't empty, they are likely duplicates, return true
        return !Iterables.isEmpty(
          Iterables.filter(
            aids, in((Set<String>)bids)));
      }
      protected int doHash(ASObject t) {
        return MoreFunctions.genHashCode(
            1, t.getId(), 
            t.getProperty("alias"), 
            t.getObjectType());
      }
    };
  }
  
  public static final Comparator<ASObject> UPDATED_COMPARATOR = 
    new UpdatedComparator<ASObject>();
  public static final Comparator<ASObject> PUBLISHED_COMPARATOR = 
    new PublishedComparator<ASObject>();
  
  public static <X extends ASObject>Comparator<X> reversedUpdatedComparator() {
    return Collections.<X>reverseOrder(Extra.<X>updatedComparator());
  }
  
  public static <X extends ASObject>Comparator<X> reversedPublishedComparator() {
    return Collections.<X>reverseOrder(Extra.<X>publishedComparator());
  }
  
  public static <X extends ASObject>Comparator<X> updatedComparator() {
    return new UpdatedComparator<X>();
  }
  
  public static <X extends ASObject>Comparator<X> publishedComparator() {
    return new PublishedComparator<X>();
  }
  
  static class UpdatedComparator<X extends ASObject>
    extends DateTimes.DateTimeComparator<X>
    implements Serializable {
      private static final long serialVersionUID = 6821667653694869403L;

      public int compare(X a1, X a2) {
        DateTime d1 = a1.getUpdated();
        DateTime d2 = a2.getUpdated();
        return innerCompare(d1,d2);
      }
  }
  
  static class PublishedComparator<X extends ASObject>
    extends DateTimes.DateTimeComparator<X>
    implements Serializable {
      private static final long serialVersionUID = -8381760915617994979L;

      public int compare(X a1, X a2) {
        return innerCompare(
          a1.getPublished(), 
          a2.getPublished());
      }
  }
  
  
  private static Class<?>[] addin(Class<?>[] types, Class<?> type) {
    Class<?>[] ntypes = new Class<?>[types.length+1];
    System.arraycopy(types, 0, ntypes, 0, types.length);
    ntypes[ntypes.length-1] = type;
    return ntypes;
  }
  
  /**
   * Uses cglib to create an extension of the base ASObject type
   * that implements the given interface. All setter/getter methods
   * on the supplied interface will be mapped to properties on the
   * underlying ASObject.. for instance, getFoo() and setFoo(..) will
   * be mapped to a "foo" property
   */
  public static <X extends ASBase,M>M extend(
    X object,
    Class<?> type) {
    checkNotNull(type);
    checkNotNull(object);
    Enhancer e = new Enhancer();
    if (type.isInterface()) {
      e.setSuperclass(type);
      e.setInterfaces(addin(object.getClass().getInterfaces(),type));
    } else if (ASObject.class.isAssignableFrom(type)) {
      e.setSuperclass(type);
    }
    e.setCallback(new ExtensionWrapper(type,object));
    return (M)e.create();
  }
  
  /**
   * Uses cglib to create an extension of the base ASObject type
   * that implements the given interface. All setter/getter methods
   * on the supplied interface will be mapped to properties on the
   * underlying ASObject.. for instance, getFoo() and setFoo(..) will
   * be mapped to a "foo" property
   */
  public static <X extends ASBase.Builder,M>M extendBuilder(
    X object,
    Class<?> type) {
    checkNotNull(type);
    checkNotNull(object);
    Enhancer e = new Enhancer();
    if (type.isInterface()) {
      e.setSuperclass(type);
      e.setInterfaces(addin(object.getClass().getInterfaces(),type));
    } else if (ASObject.class.isAssignableFrom(type)) {
      e.setSuperclass(type);
    }
    e.setCallback(new BuilderWrapper(type,object));
    return (M)e.create();
  }
  
  private static class BuilderWrapper 
  implements MethodInterceptor {
  private final Class<?> type;
  private final ASBase.Builder builder;
  BuilderWrapper(
    Class<?> type, 
    ASBase.Builder builder) {
    this.type = type;
    this.builder = builder;
  }
  public Object intercept(
    Object obj, 
    Method method, 
    Object[] args,
    MethodProxy proxy) 
      throws Throwable {
      if (method.getDeclaringClass().equals(type)) {
        boolean setter = 
          method.getName().matches("[Ss]et.+") || 
          ((void.class.isAssignableFrom(method.getReturnType()) ||
           method.getReturnType().isAssignableFrom(type)) && 
          method.getParameterTypes().length == 1);
        String name = get_name(method);
        if (setter) {
          if (args.length != 1)
            throw new UnsupportedOperationException();
          if (args[0] instanceof Supplier) 
            builder.set(name,((Supplier)args[0]).get());
          else 
            builder.set(name,args[0]);
          return method.getReturnType().isAssignableFrom(type) ?
            obj : null; 
        } else {
          throw new UnsupportedOperationException();
        }
      } else if (method.getDeclaringClass().equals(ExtensionBuilder.class) && 
          method.getName().equals("unwrap")) {
        return builder;
      } else return proxy.invokeSuper(builder, args);
    }    
  }
  
  public static interface ExtensionBuilder {
    <T extends ASBase.Builder>T unwrap();
  }
  
  public static interface ExtensionObject {
    <T extends ASBase>T unwrap();
  }
  
  private static class ExtensionWrapper 
    implements MethodInterceptor {
    private final Class<?> type;
    private final ASBase base;
    ExtensionWrapper(
      Class<?> type, 
      ASBase base) {
      this.type = type;
      this.base = base;
    }
    public Object intercept(
      Object obj, 
      Method method, 
      Object[] args,
      MethodProxy proxy) 
        throws Throwable {
      if (method.getDeclaringClass().equals(type)) {
        String name = get_name(method);
        if (method.getParameterTypes().length == 0 && 
            method.getReturnType() != Void.class) {
          Object ret = base.getProperty(name);
          Class<?> retType = method.getReturnType();
          if (ret instanceof ASBase && 
              ASBase.class.isAssignableFrom(retType) && 
              !ret.getClass().equals(ASBase.class) &&
              !ret.getClass().equals(type)) {
            ASBase bret = (ASBase) ret;
            return bret.as((Class<? extends ASBase>)retType);
          } else {
            return retType.cast(base.getProperty(name));
          }
        } else {
          throw new UnsupportedOperationException();
        }
      } else if (method.getDeclaringClass().equals(ExtensionObject.class) && 
          method.getName().equals("unwrap")) {
        return base;
      } else return proxy.invokeSuper(base, args);
    }    
  }
  
  static String get_name(Method obj) {   
    String name = null;
    if (obj.isAnnotationPresent(Name.class))
      name = obj.getAnnotation(Name.class).value();
    else {
      name = obj.getName();
      if (name.startsWith("get") || 
        name.startsWith("set"))
      name = name.substring(3);
      name = CaseFormat.UPPER_CAMEL.to(
          CaseFormat.LOWER_CAMEL, name);
    }
    return name;
  }
  
  public static <T extends ASBase>Function<InputStream,T> readFromInputStream(
    final String charset) {
    return readFromInputStream(IO.get(),charset);
  }
  
  public static <T extends ASBase>Function<InputStream,T> readFromInputStream(
    final IO io, 
    final String charset) {
    return new Function<InputStream,T>() {
      public T apply(InputStream input) {
        return (T) io.read(input,charset);
      }
    };
  }
  
  public static <T extends ASBase>Function<Reader,T> readFromReader() {
    return readFromReader(IO.get());
  }
  
  public static <T extends ASBase>Function<Reader,T> readFromReader(
    final IO io) {
    return new Function<Reader,T>() {
      public T apply(Reader input) {
        return (T) io.read(input);
      }
    };
  }
  
  public static <T extends ASBase>Function<String,T> readFromString() {
    return readFromString(IO.get());
  }
  
  public static <T extends ASBase>Function<String,T> readFromString(
    final IO io) {
    return new Function<String,T>() {
      public T apply(String input) {
        return (T) io.read(input);
      }
    };
  }
 
  public static <T extends ASBase>Function<InputStream,T> readFromInputStreamAs(
      final String charset, 
      final Class<T> _class) {
    return readFromInputStreamAs(IO.get(),charset,_class);
  }
  
  public static <T extends ASBase>Function<InputStream,T> readFromInputStreamAs(
    final IO io, 
    final String charset, 
    final Class<T> _class) {
    return new Function<InputStream,T>() {
      public T apply(InputStream input) {
        return io.readAs(input,charset,_class);
      }
    };
  }
  
  public static <T extends ASBase>Function<Reader,T> readFromReaderAs(
      final Class<T> _class) {
    return readFromReaderAs(IO.get(),_class);
  }
  
  public static <T extends ASBase>Function<Reader,T> readFromReaderAs(
    final IO io,
    final Class<T> _class) {
    return new Function<Reader,T>() {
      public T apply(Reader input) {
        return io.readAs(input,_class);
      }
    };
  }
  
  public static <T extends ASBase>Function<String,T> readFromStringAs(
      final Class<T> _class) {
    return readFromStringAs(IO.get(),_class);
  }
  
  public static <T extends ASBase>Function<String,T> readFromStringAs(
    final IO io,
    final Class<T> _class) {
    return new Function<String,T>() {
      public T apply(String input) {
        return io.readAs(input,_class);
      }
    };
  }
  
  public static <T extends ASObject>Function<InputStream,T> readObjectFromInputStream(
      final String charset) {
    return readObjectFromInputStream(IO.get(),charset);
  }
  
  public static <T extends ASObject>Function<InputStream,T> readObjectFromInputStream(
    final IO io, 
    final String charset) {
    return new Function<InputStream,T>() {
      public T apply(InputStream input) {
        return (T) io.readObject(input,charset);
      }
    };
  }
  
  public static <T extends ASObject>Function<Reader,T> readObjectFromReader() {
    return readObjectFromReader(IO.get());
  }
      
  public static <T extends ASObject>Function<Reader,T> readObjectFromReader(
    final IO io) {
    return new Function<Reader,T>() {
      public T apply(Reader input) {
        return (T) io.readObject(input);
      }
    };
  }
  
  public static <T extends ASObject>Function<String,T> readObjectFromString() {
    return readObjectFromString(IO.get());
  }
  
  public static <T extends ASObject>Function<String,T> readObjectFromString(
    final IO io) {
    return new Function<String,T>() {
      public T apply(String input) {
        return (T) io.readObject(input);
      }
    };
  }
  
  public static <T extends ASObject>Function<InputStream,T> readObjectFromInputStreamAs(
      final String charset, 
      final Class<T> _class) {
    return readObjectFromInputStreamAs(IO.get(),charset,_class);
  }
  
  public static <T extends ASObject>Function<InputStream,T> readObjectFromInputStreamAs(
    final IO io, 
    final String charset, 
    final Class<T> _class) {
    return new Function<InputStream,T>() {
      public T apply(InputStream input) {
        return io.readObjectAs(input,charset,_class);
      }
    };
  }
  
  public static <T extends ASObject>Function<Reader,T> readObjectFromReaderAs(
      final Class<T> _class) {
    return readObjectFromReaderAs(IO.get(),_class);
  }
  
  public static <T extends ASObject>Function<Reader,T> readObjectFromReaderAs(
    final IO io,
    final Class<T> _class) {
    return new Function<Reader,T>() {
      public T apply(Reader input) {
        return io.readObjectAs(input,_class);
      }
    };
  }
  
  public static <T extends ASObject>Function<String,T> readObjectFromStringAs(final Class<T> _class) {
    return readObjectFromStringAs(IO.get(),_class);
  }
  
  public static <T extends ASObject>Function<String,T> readObjectFromStringAs(
    final IO io,
    final Class<T> _class) {
    return new Function<String,T>() {
      public T apply(String input) {
        return io.readObjectAs(input,_class);
      }
    };
  }
  
  public static Function<InputStream,Activity> readActivityFromInputStream(
    final String charset) {
    return readActivityFromInputStream(IO.get(),charset);
  }
  
  public static Function<InputStream,Activity> readActivityFromInputStream(
    final IO io, 
    final String charset) {
    return new Function<InputStream,Activity>() {
      public Activity apply(InputStream input) {
        return io.readActivity(input,charset);
      }
    };
  }
  
  public static Function<Reader,Activity> readActivityFromReader() {
    return readActivityFromReader(IO.get());
  }
  
  public static Function<Reader,Activity> readActivityFromReader(
    final IO io) {
    return new Function<Reader,Activity>() {
      public Activity apply(Reader input) {
        return io.readActivity(input);
      }
    };
  }
  
  public static Function<String,Activity> readActivityFromString() {
    return readActivityFromString(IO.get());
  }
  
  public static Function<String,Activity> readActivityFromString(
    final IO io) {
    return new Function<String,Activity>() {
      public Activity apply(String input) {
        return io.readActivity(input);
      }
    };
  }
  
  public static Function<InputStream,MediaLink> readMediaLinkFromInputStream(
    final String charset) {
    return readMediaLinkFromInputStream(IO.get(),charset);
  }
  
  public static Function<InputStream,MediaLink> readMediaLinkFromInputStream(
    final IO io, 
    final String charset) {
    return new Function<InputStream,MediaLink>() {
      public MediaLink apply(InputStream input) {
        return io.readMediaLink(input,charset);
      }
    };
  }
  
  public static Function<Reader,MediaLink> readMediaLinkFromReader() {
    return readMediaLinkFromReader(IO.get());
  }
  
  public static Function<Reader,MediaLink> readMediaLinkFromReader(
    final IO io) {
    return new Function<Reader,MediaLink>() {
      public MediaLink apply(Reader input) {
        return io.readMediaLink(input);
      }
    };
  }
  
  public static Function<String,MediaLink> readMediaLinkFromString() {
    return readMediaLinkFromString(IO.get());
  }
  
  public static Function<String,MediaLink> readMediaLinkFromString(
    final IO io) {
    return new Function<String,MediaLink>() {
      public MediaLink apply(String input) {
        return io.readMediaLink(input);
      }
    };
  }
  
  public static <T extends ASObject>Function<InputStream,Collection<T>> readCollectionFromInputStream(
    final String charset) {
    return readCollectionFromInputStream(IO.get(),charset);
  }
  
  public static <T extends ASObject>Function<InputStream,Collection<T>> readCollectionFromInputStream(
    final IO io, 
    final String charset) {
    return new Function<InputStream,Collection<T>>() {
      public Collection<T> apply(InputStream input) {
        return io.readCollection(input,charset);
      }
    };
  }
  
  public static <T extends ASObject>Function<Reader,Collection<T>> readCollectionFromReader() {
    return readCollectionFromReader(IO.get());
  }
  
  public static <T extends ASObject>Function<Reader,Collection<T>> readCollectionFromReader(
    final IO io) {
    return new Function<Reader,Collection<T>>() {
      public Collection<T> apply(Reader input) {
        return io.readCollection(input);
      }
    };
  }
  
  public static <T extends ASObject>Function<String,Collection<T>> readCollectionFromString() {
    return readCollectionFromString(IO.get());
  }
  
  public static <T extends ASObject>Function<String,Collection<T>> readCollectionFromString(
    final IO io) {
    return new Function<String,Collection<T>>() {
      public Collection<T> apply(String input) {
        return io.readCollection(input);
      }
    };
  }
  
  public static <T extends ASBase>Function<T,String> write(final IO io) {
    return new Function<T,String>() {
      public String apply(T input) {
        return io.write(input);
      }
    };
  }
  
  public static <T extends ASBase>Function<T,String> write() {
    return write(IO.get());
  }
  
  public static <T extends ASBase>Function<T,Void> writeTo(
    final IO io, 
    final Appendable out) {
    return new Function<T,Void>() {
      public Void apply(T input) {
        io.write(input,out);
        return null;
      }
    };
  }
  
  public static <T extends ASBase>Function<T,Void> writeTo(
    final Appendable out) {
    return writeTo(IO.get(),out);
  }
  
  public static <T extends ASBase>Function<T,Void> writeTo(
    final IO io, 
    final OutputStream out) {
    return new Function<T,Void>() {
      public Void apply(T input) {
        io.write(input,out);
        return null;
      }
    };
  }
  
  public static <T extends ASBase>Function<T,Void> writeTo(
    final OutputStream out) {
    return writeTo(IO.get(),out);
  }
  
  public static <T extends ASBase>Function<T,Void> writeTo(
    final IO io, 
    final OutputStream out,
    final String charset) {
    return new Function<T,Void>() {
      public Void apply(T input) {
        io.write(input,out,charset);
        return null;
      }
    };
  }
  
  public static <T extends ASBase>Function<T,Void> writeTo(
    final OutputStream out,
    final String charset) {
    return writeTo(IO.get(),out,charset);
  }
  
  public static <T extends ASBase,M extends ASBase>Function<T,M> as(
    final Class<M> _class) {
      return new Function<T,M>() {
        public M apply(T input) {
          return input.as(_class);
        }
      };
  }
  
  public static <T extends ASBase,M extends ASBase>Function<T,M> as(
    final Class<M> _class,
    final ASBase other) {
      return new Function<T,M>() {
        public M apply(T input) {
          return input.as(_class,other);
        }
      };
  }
  
  public static <T extends ASBase,M extends ASBase>Function<T,M> as(
    final Class<M> _class,
    final Map<String,Object> other) {
      final ImmutableMap<String,Object> map = 
        ImmutableMap.copyOf(other);
      return new Function<T,M>() {
        public M apply(T input) {
          return input.as(_class,map);
        }
      };
  }
  
  public static <T extends ASBase,M extends ASBase>Function<T,M> as(
    final Class<M> _class,
    final Selector<Map.Entry<String,Object>> filter) {
      return new Function<T,M>() {
        public M apply(T input) {
          return input.as(_class,filter);
        }
      };
  }
  
  
  
  
  public static <T extends ASObject>Iterable<T> filterUpdatedIdentityEquivalent(Iterable<T> items) {
    return filterEquivalent(items, Extra.<T>identity(),Extra.<T>reversedUpdatedComparator());
  }
  
  public static <T extends ASObject>Iterable<T> filterPublishedIdentityEquivalent(Iterable<T> items) {
    return filterEquivalent(items, Extra.<T>identity(),Extra.<T>reversedPublishedComparator());
  }
  
  public static <T extends ASObject>Iterable<T> filterUpdatedIdentityWithDuplicatesEquivalent(Iterable<T> items) {
    return filterEquivalent(items, Extra.<T>identityWithDuplicates(),Extra.<T>reversedUpdatedComparator());
  }
  
  public static <T extends ASObject>Iterable<T> filterPublishedIdentityWithDuplicatesEquivalent(Iterable<T> items) {
    return filterEquivalent(items, Extra.<T>identityWithDuplicates(),Extra.<T>reversedPublishedComparator());
  }
  
  public static <T extends ASBase>Iterable<T> filterEquivalent(
    Iterable<T> items, 
    Equivalence<? super T> equiv, 
    Comparator<T> comp) {
    EquivalenceSet<T> a = 
      new EquivalenceSet<T>(equiv);
    Iterables.addAll(
      a,sorted(items,comp));
    return Iterables.unmodifiableIterable(a);
  }
  
  public static <T extends ASBase>Iterable<T> filterEquivalent(
    Iterable<T> items, 
    Equivalence<? super T> equiv) {
    EquivalenceSet<T> a = 
      new EquivalenceSet<T>(equiv);
    Iterables.addAll(
      a,items);
    return Iterables.unmodifiableIterable(a);
  }
  
  public static <T extends Comparable<T>>Iterable<T> sorted(Iterable<? extends T> items) {
    return ImmutableSortedSet.<T>naturalOrder().addAll(items).build();
  }
  
  public static <T>Iterable<T> sorted(Iterable<? extends T> items, Comparator<T> comp) {
    return ImmutableSortedSet.<T>orderedBy(comp).addAll(items).build();
  }
  
  public static class EquivalenceSet<T> 
    extends AbstractSet<T> {

    private final Set<T> set;
    private final Equivalence<? super T> equiv;
    
    public EquivalenceSet(Comparator<? super T> order, Equivalence<? super T> equiv) {
      this.set = Sets.newTreeSet(order);
      this.equiv = equiv;
    }
    
    public EquivalenceSet(Equivalence<? super T> equiv) {
      this.set = Sets.newLinkedHashSet();
      this.equiv = equiv;
    }
    
    @Override
    public boolean add(T o) {
      if (set.isEmpty()) return set.add(o);
      else {
        Iterable<T> filtered = 
          Iterables.filter(
            set, equiv.equivalentTo(o));
        if (Iterables.isEmpty(filtered))
          return set.add(o);
        else return false;
      }
    }

    @Override
    public Iterator<T> iterator() {
      return set.iterator();
    }

    @Override
    public int size() {
      return set.size();
    }
    
  }
  
  private static final Joiner types_joiner = Joiner.on(' ');
  
  public static MimeType getMediaType(ASBase base) {
    MimeType mt = null;
    try {
      mt = new MimeType("application/json");
      if (base instanceof ASObject) {
        ASObject obj = (ASObject) base;
        String otype = obj.getObjectType();
        if (otype != null)
          mt.setParameter("type", otype);
      }
      if (base instanceof Collection) {
        Collection<?> col = (Collection<?>) base;
        Iterable<String> otypes = col.getObjectTypes();
        if (!Iterables.isEmpty(otypes))
          mt.setParameter("items", types_joiner.join(otypes));
        else if (!Iterables.isEmpty(col.getItems())) {
          ImmutableSet.Builder<String> set = ImmutableSet.builder();
          for (ASObject obj : col.getItems())
            if (obj.has("objectType"))
              set.add(obj.getObjectType());
          mt.setParameter("items", types_joiner.join(set.build()));
        }
      }
    } catch (Throwable t) {
      throw ExceptionHelper.propogate(t);
    }
    return mt;
  }
  
  public static <X extends ASObject>Function<Collection<X>,Iterable<Pair<Void, X>>> pairIndexer() { 
    return new Function<Collection<X>,Iterable<Pair<Void, X>>>() {
      public Iterable<Pair<Void, X>> apply(Collection<X> input) {
        return 
          Pair.<Void,X>make()
            .index(MoreFunctions.<X>alwaysVoid(), input.getItems());
      }
    };
  }
  
}
