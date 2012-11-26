/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. The ASF licenses this file to You under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. For additional
 * information regarding copyright in this work, please see the NOTICE file in
 * the top level directory of this distribution.
 */
package org.apache.abdera2.common.http;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.text.CharUtils;
import org.apache.abdera2.common.text.UrlEncoding;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.*;
import static org.apache.abdera2.common.text.CharUtils.*;

/**
 * Implements an EntityTag.
 */
public class EntityTag 
    implements Cloneable, Serializable, Comparable<EntityTag> {

    private static final long serialVersionUID = 1559972888659121461L;

    public static final EntityTag WILD = new EntityTag("*");

    public static EntityTag create(String tag) {
      return new EntityTag(tag);
    }
    
    public static EntityTag weak(String tag) {
      return new EntityTag(tag,true);
    }
    
    public static EntityTag parse(String entity_tag) {
      checkNotNull(entity_tag);
      checkArgument(entity_tag.length() > 0, "Invalid");
      int l = entity_tag.length()-1;
      boolean wild = 
        entity_tag.charAt(0) == '*' && l == 0;
      if (wild) return EntityTag.WILD;
      boolean weak = Character.toUpperCase(entity_tag.charAt(0)) == 'W';
      checkArgument(!weak || entity_tag.charAt(1) =='/',"Invalid");
      int pos = weak?2:0;
      checkArgument(
        entity_tag.charAt(pos) == '"' && 
        entity_tag.charAt(l) == '"',"Invalid");
      String tag = entity_tag.substring(pos+1,l);
      tag = CharUtils.unescape(CharUtils.unquote(tag));
      return new EntityTag(tag, weak, false);
    }

    public static Iterable<EntityTag> parseTags(String entity_tags) {
        if (entity_tags == null || 
            entity_tags.length() == 0)
          return ImmutableSet.<EntityTag>of();
        String[] tags = 
          entity_tags.split("((?<=\")\\s*,\\s*(?=([wW]/)?\"|\\*))");
        List<EntityTag> etags = 
          new ArrayList<EntityTag>();
        for (String tag : tags)
            etags.add(EntityTag.parse(tag.trim()));
        return ImmutableSet.<EntityTag>copyOf(etags);
    }

    public static Predicate<String> matchesAny(final EntityTag tag) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return matchesAny(tag,input);
        }
      };
    }
    
    public static Predicate<String> matchesAnyWeak(final EntityTag tag) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return matchesAny(tag,input,true);
        }
      };
    }
    
    public static Predicate<Iterable<EntityTag>> matchesAnyInList(final EntityTag tag) {
      return new Predicate<Iterable<EntityTag>>() {
        public boolean apply(Iterable<EntityTag> input) {
          return matchesAny(tag,input,false);
        }
      };
    }
    
    public static Predicate<Iterable<EntityTag>> matchesAnyWeakInList(final EntityTag tag) {
      return new Predicate<Iterable<EntityTag>>() {
        public boolean apply(Iterable<EntityTag> input) {
          return matchesAny(tag,input,true);
        }
      };
    }
    
    public static Predicate<String> matchesAny(final String tag) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return matchesAny(tag,input);
        }
      };
    }
    
    public static Predicate<String> matchesAnyWeak(final String tag) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return matchesAny(tag,input,true);
        }
      };
    }
    
    public static boolean matchesAny(EntityTag tag1, String tags) {
        return matchesAny(tag1, parseTags(tags), false);
    }

    public static boolean matchesAny(EntityTag tag1, String tags, boolean weak) {
        return matchesAny(tag1, parseTags(tags), weak);
    }

    public static boolean matchesAny(String tag1, String tags) {
        return matchesAny(parse(tag1), parseTags(tags), false);
    }

    public static boolean matchesAny(String tag1, String tags, boolean weak) {
        return matchesAny(parse(tag1), parseTags(tags), weak);
    }

    public static boolean matchesAny(EntityTag tag1, Iterable<EntityTag> tags) {
        return matchesAny(tag1, tags, false);
    }

    private static boolean empty(Iterable<EntityTag> tags) {
      return Iterables.isEmpty(tags);
    }
    
    public static boolean matchesAny(EntityTag tag1, Iterable<EntityTag> tags, boolean weak) {
        if (tags == null)
            return (tag1 == null) ? true : false;
        if (tag1.isWild() && !empty(tags))
            return true;
        for (EntityTag tag : tags)
          if (tag1.equals(tag,weak) || tag.isWild())
            return true;
        return false;
    }

    public static Predicate<EntityTag> matches(final EntityTag tag1) {
      return new Predicate<EntityTag>() {
        public boolean apply(EntityTag tag) {
          return matches(tag1,tag);
        }
      };
    }
    
    public static Predicate<EntityTag> matchesWeak(final EntityTag tag1) {
      return new Predicate<EntityTag>() {
        public boolean apply(EntityTag tag) {
          return matches(tag1,tag,true);
        }
      };
    }
    
    public static Predicate<String> matches(final String tag1) {
      return new Predicate<String>() {
        public boolean apply(String tag) {
          return matches(tag1,tag);
        }
      };
    }
    
    public static boolean matches(EntityTag tag1, EntityTag tag2) {
        return tag1.equals(tag2);
    }
    
    public static boolean matches(EntityTag tag1, EntityTag tag2, boolean weak) {
      return tag1.equals(tag2,weak);
    }

    public static boolean matches(String tag1, String tag2) {
        EntityTag etag1 = parse(tag1);
        EntityTag etag2 = parse(tag2);
        return etag1.equals(etag2);
    }

    public static boolean matches(EntityTag tag1, String tag2) {
        return tag1.equals(parse(tag2));
    }

    public static Function<String[],EntityTag> variation(final EntityTag tag) {
      return new Function<String[],EntityTag>() {
        public EntityTag apply(String[] labels) {
          if (labels.length > 1) {
            String label = labels[0];
            String[] other = new String[labels.length-1];
            System.arraycopy(labels, 1, other, 0, other.length);
            return variation(tag,label,other);
          } else {
            return variation(tag,labels[0]);
          }
        }
      };
    }
    
    /**
     * Produces a variation of the entity tag by appending one or 
     * more labels to the tag. This is helpful when generating 
     * alternative entity tags for different content-encoding
     * variations of a resource.
     */
    public static EntityTag variation(
        EntityTag tag, 
        String label, 
        String... labels) {
        if (label == null || tag == null || tag.isWild()) 
          throw new IllegalArgumentException();
        StringBuilder buf = 
          new StringBuilder(tag.getTag());
        buf.append('-')
           .append(label);
        for (String l : labels)
          buf.append('-')
             .append(l);
        return new EntityTag(buf.toString(),tag.isWeak());
    }
    
    private final String tag;

    private final boolean weak;
    private final boolean wild;

    public EntityTag(String tag) {
        this(tag, false);
    }

    public EntityTag(String tag, boolean weak) {
        EntityTag etag = attemptParse(tag);
        if (etag == null) {
            checkArgument(tag.indexOf('"') == -1, "Invalid");
            this.tag = tag;
            this.weak = weak;
            this.wild = tag.equals("*");
        } else {
            this.tag = etag.tag;
            this.weak = etag.weak;
            this.wild = etag.wild;
        }
    }

    private EntityTag(String tag, boolean weak, boolean wild) {
        this.tag = tag;
        this.weak = weak;
        this.wild = wild;
    }

    public Function<String[],EntityTag> variation() {
      return variation(this);
    }
    
    public EntityTag variation(String label, String... labels) {
      return variation(this,label,labels);
    }
    
    private EntityTag attemptParse(String tag) {
        try {
            return parse(tag);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isWild() {
        return wild;
    }

    public String getTag() {
        return tag;
    }

    public boolean isWeak() {
        return weak;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (wild)
            buf.append("*");
        else {
          appendif(weak,buf,"W/");
          buf.append(quoted(tag,true));
        }
        return buf.toString();
    }

    @Override
    public int hashCode() {
      return MoreFunctions.genHashCode(
        1,tag,weak,wild);
    }

    @Override
    public boolean equals(Object obj) {
      return equals(obj,false);
    }
    
    public boolean equals(Object obj, boolean weakmatch) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final EntityTag other = (EntityTag)obj;
        if (isWild() || other.isWild())
            return true;
        if (tag == null) {
            if (other.tag != null)
                return false;
        } else if (!tag.equals(other.tag))
            return false;
        if (!weakmatch && (weak != other.weak))
            return false;
        if (wild != other.wild)
            return false;
        return true;
    }

    @Override
    protected EntityTag clone() {
        try {
            return (EntityTag)super.clone();
        } catch (CloneNotSupportedException e) {
            return new EntityTag(tag, weak, wild); // not going to happen
        }
    }

    public static Function<String[],EntityTag> generate() {
      return new Function<String[],EntityTag>() {
        public EntityTag apply(String[] material) {
          return generate(material);
        }
      };
    }
    
    public static Function<Object,EntityTag> generateFromObject() {
      return new Function<Object,EntityTag>() {
        public EntityTag apply(Object obj) {
          return generate(obj);
        }
      };
    }
    
    public final static Function<String, EntityTag> parser = 
      new Function<String,EntityTag>() {
        public EntityTag apply(String input) {
          return input != null ? parse(input) : null;
        }
    };
    
    public final static Function<String, Iterable<EntityTag>> parseMultiple = 
      new Function<String,Iterable<EntityTag>>() {
        public Iterable<EntityTag> apply(String input) {
          return input != null ? 
            parseTags(input) : 
            Collections.<EntityTag>emptySet();
        }
    };
    
    /**
     * Utility method for generating ETags. Works by concatenating the UTF-8 bytes of the provided strings then
     * generating an MD5 hash of the result.
     */
    public static EntityTag generate(String... material) {
        String etag = null;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            for (String s : material) {
                if (s != null)
                    md.update(s.getBytes("utf-8"));
            }
            byte[] digest = md.digest();
            StringBuilder buf = new StringBuilder();
            UrlEncoding.hex(buf,0,digest.length,digest);
            etag = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Hashing algorithm unavailable");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 unsupported", e);
        }
        return new EntityTag(etag);
    }
    
    public static Predicate<EntityTag> matches(final String... material) {
      return new Predicate<EntityTag>() {
        public boolean apply(EntityTag input) {
          return matches(input,material);
        }
      };
    }
    
    /**
     * Checks that the passed in ETag matches the ETag generated by the generate method
     */
    public static boolean matches(EntityTag etag, String... material) {
        EntityTag etag2 = generate(material);
        return EntityTag.matches(etag, etag2);
    }
    
    public static String toString(EntityTag tag, EntityTag... tags) {
        if (tag == null) return null;
        StringBuilder buf = new StringBuilder();
        buf.append(tag.toString());
        for (EntityTag t : tags)
            buf.append(", ")
               .append(t.toString());
        return buf.toString();
    }

    public static String toString(String tag, String... tags) {
        if (tag == null) return null;
        StringBuilder buf = new StringBuilder();
        buf.append(new EntityTag(tag).toString());
        for (String t : tags)
            buf.append(", ")
               .append(new EntityTag(t).toString());
        return buf.toString();
    }
    
    public static String toString(Iterable<EntityTag> tags) {
      StringBuilder buf = new StringBuilder();
      boolean first = true;
      for (EntityTag tag : tags) {
        first = appendcomma(first,buf);
        buf.append(tag.toString());
      }
      return buf.toString();
    }
    
    public int compareTo(EntityTag o) {
      return o.wild && !wild || weak && !o.weak ?
        1 : wild && !o.wild || o.weak && !weak ? 
       -1 : tag.compareTo(o.tag);
    }
    
    public static interface EntityTagGenerator<T> extends Function<T,EntityTag> {}
    
    @Retention(RUNTIME)
    @Target( {TYPE})
    public @interface ETagGenerator {
      Class<? extends EntityTagGenerator<?>> value();
    }
    
    @SuppressWarnings("unchecked")
    public static <T>EntityTag generate(T t) {
      EntityTag etag = null;
      try {
        Class<?> _class = checkNotNull(t).getClass();
        if (_class.isAnnotationPresent(ETagGenerator.class)) {
          ETagGenerator g = _class.getAnnotation(ETagGenerator.class);
          Class<? extends EntityTagGenerator<T>> gen = 
            (Class<? extends EntityTagGenerator<T>>) g.value();
          EntityTagGenerator<T> etg = 
            gen.newInstance();
          etag = etg.apply(t);
        } else etag = generate(new String[] {t.toString()});
      } catch (Throwable e) {
        throw ExceptionHelper.propogate(e);
      }
      return etag;
    }
    
    public static <T>EntityTag generate(T t, EntityTagGenerator<T> gen) {
      return gen.apply(checkNotNull(t));
    }
    
}
