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
package org.apache.abdera2.common.mediatype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParameterList;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.misc.ExceptionHelper;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import static org.apache.abdera2.common.misc.Comparisons.*;

/**
 * Utilities for working with MIME Media Types
 */
public final class MimeTypeHelper {

    private MimeTypeHelper() {}
  
    public static final MimeType WILDCARD = createWildcard();
    public static final MimeType ATOM = unmodifiableMimeType(Constants.ATOM_MEDIA_TYPE);
    public static final MimeType ENTRY = unmodifiableMimeType(Constants.ENTRY_MEDIA_TYPE);
    public static final MimeType FEED = unmodifiableMimeType(Constants.FEED_MEDIA_TYPE);

    public static String getCharset(String mediatype) {
        try {
            MimeType mt = new MimeType(mediatype);
            return mt.getParameter("charset");
        } catch (Exception e) {
            return null;
        }
    }
    
    public static MimeType create(String mimeType) {
      try {
        return new MimeType(mimeType);
      } catch (javax.activation.MimeTypeParseException e) {
        throw MimeTypeParseException.wrap(e);
      }
    }

    private static MimeType createWildcard() {
      return unmodifiableMimeType("*/*");
    }

    public static Predicate<String> isMatch(final String a) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return isMatch(a,input);
        }
      };
    }
    
    public static Predicate<MimeType> isMatch(final MimeType a) {
      return new Predicate<MimeType>() {
        public boolean apply(MimeType input) {
          return isMatch(a,input);
        }
      };
    }
    
    public static Predicate<MimeType> isMatch(final MimeType a, final boolean includeparams) {
      return new Predicate<MimeType>() {
        public boolean apply(MimeType input) {
          return isMatch(a,input,includeparams);
        }
      };
    }
    
    /**
     * Returns true if media type a matches media type b
     */
    public static boolean isMatch(String a, String b) {
        if (bothAreEmpty(a, b)) return true;
        if (onlyOneIsNull(a, b)) return false;
        return isMatch(
          unmodifiableMimeType(a.toLowerCase()), 
          unmodifiableMimeType(b.toLowerCase()));
    }

    public static boolean isMatch(MimeType a, MimeType b) {
        return isMatch(a, b, false);
    }

    /**
     * Returns true if media type a matches media type b
     */
    public static boolean isMatch(MimeType a, MimeType b, boolean includeparams) {
      if (bothAreNull(a,b))
          return true;
      if (a.match(b))
        if (includeparams) {
          MimeTypeParameterList aparams = a.getParameters();
          MimeTypeParameterList bparams = b.getParameters();
          if (bothAreTrue(aparams.isEmpty(),bparams.isEmpty()))
              return true;
          if (onlyOneIsTrue(aparams.isEmpty(),bparams.isEmpty()))
              return false;
          for (Enumeration<?> e = aparams.getNames(); e.hasMoreElements();) {
            String aname = (String)e.nextElement();
            String avalue = aparams.get(aname);
            String bvalue = bparams.get(aname);
            if (!avalue.equals(bvalue))
              return false;
          }
          return true;
        } else
            return true;
      if (a.equals(WILDCARD))
          return true;
      if (a.getPrimaryType().equals("*"))
        return isMatch(unmodifiableMimeType(b.getPrimaryType() + "/" + a.getSubType()), b);
      if (b.getPrimaryType().equals("*"))
        return isMatch(a, unmodifiableMimeType(a.getPrimaryType() + "/" + b.getSubType()));

      return false;
    }

    private static boolean isMatchType(String actual, String expected) {
        return (actual != null && actual.equalsIgnoreCase(expected));
    }

    public static Predicate<String> isApp() {
      return isMatch(Constants.APP_MEDIA_TYPE);
    }
    
    public static Predicate<String> isAtom() {
      return isMatch(Constants.ATOM_MEDIA_TYPE);
    }
    
    public static Predicate<String> isJson() {
      return isMatch(Constants.JSON_MEDIA_TYPE);
    }
    
    public static Predicate<String> isAtomEntry() {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return isEntry(input);
        }
      };
    }
    
    public static Predicate<String> isAtomFeed() {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return isFeed(input);
        }
      };
    }
    
    public static Predicate<String> isXml() {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return isXml(input);
        }
      };
    }
    
    public static Predicate<String> isText() {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return isText(input);
        }
      };
    }
    
    public static Predicate<String> isMimeType() {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return isMimeType(input);
        }
      };
    }
    
    /**
     * Returns true if media type a matches application/atomsrv+xml
     */
    public static boolean isApp(String a) {
        return isMatch(Constants.APP_MEDIA_TYPE, a);
    }

    /**
     * Returns true if media type a matches application/atom+xml
     */
    public static boolean isAtom(String a) {
        if (isEntry(a) || isFeed(a))
            return true;
        return isMatch(Constants.ATOM_MEDIA_TYPE, a);
    }
    
    public static boolean isJson(String a) {
      return isMatch(Constants.JSON_MEDIA_TYPE, a);
    }

    /**
     * Returns true if media type a specifically identifies an Atom entry document
     */
    public static boolean isEntry(String a) {
        try {
            MimeType mta = new MimeType(a.toLowerCase());
            return isMatch(mta, ENTRY,true) || 
                  (isMatch(mta, ATOM) && 
                   isMatchType(mta.getParameter("type"), "entry"));
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Returns true if media type a explicitly identifies an Atom feed document
     */
    public static boolean isFeed(String a) {
        try {
            MimeType mta = new MimeType(a.toLowerCase());
            return isMatch(mta, FEED, true) || 
                  (isMatch(mta, ATOM) && 
                   isMatchType(mta.getParameter("type"), "feed"));
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Returns true if media type a matches application/xml, text/xml or application/*+xml
     */
    public static boolean isXml(String a) {
        boolean answer = isMatch(Constants.XML_MEDIA_TYPE, a) || isMatch("text/xml", a);
        if (!answer) {
            try {
                MimeType mta = new MimeType(a);
                answer =
                    (("application".equalsIgnoreCase(mta.getPrimaryType()) ||
                      "text".equalsIgnoreCase(mta.getPrimaryType())) && 
                      mta.getSubType().equals("xml") || 
                      mta.getSubType().endsWith("+xml"));
            } catch (Exception e) {
            }
        }
        return answer;
    }

    /**
     * Returns true if media type a matches text/*
     */
    public static boolean isText(String a) {
        return isMatch("text/*", a);
    }

    /**
     * Returns true if this is a valid media type
     */
    public static boolean isMimeType(String a) {
        boolean answer = false;
        try {
            new MimeType(a);
            answer = true;
        } catch (javax.activation.MimeTypeParseException e) {
            answer = false;
        }
        return answer;
    }

    public static Function<String[],String[]> condense() {
      return new Function<String[],String[]>() {
        public String[] apply(String[] input) {
          return condense(input);
        }
      };
    }
    
    /**
     * This will take an array of media types and will condense them based on wildcards, etc. For instance,
     * condense("image/png", "image/jpg", "image/*") condenses to [image/*] condense("application/atom",
     * "application/*", "image/png", "image/*") condenses to [application/*, image/*]
     */
    public static String[] condense(String... types) {
      if (types == null) return new String[0];
      if (types.length <= 1)
          return types;
      List<String> res = new ArrayList<String>();
      Arrays.sort(types, getComparator());
      for (String t : types)
        if (!contains(t, res, true))
          res.add(t);
      for (int n = 0; n < res.size(); n++) {
          String t = res.get(n);
          if (contains(t, res, false))
              res.remove(t);
      }
      return res.toArray(new String[res.size()]);
    }

    private static boolean contains(String t1, List<String> t, boolean self) {
        if (self && t.contains(t1))
            return true;
        for (String t2 : t) {
            int c = compare(t1, t2);
            if (c == 1)
                return true;
        }
        return false;
    }

    /**
     * Returns a Comparator that can be used to compare and sort MIME media types according to their level of
     * specificity (e.g. text/* is less specific than text/plain and would appear first in a sorted list)
     */
    public static Comparator<String> getComparator() {
        return new Comparator<String>() {
            public int compare(String o1, String o2) {
                return MimeTypeHelper.compare(o1, o2);
            }
        };
    }

    /**
     * Compare two media types according to their relative level of specificity
     */
    public static int compare(MimeType mt1, MimeType mt2) {
        String st1 = mt1.getSubType();
        String st2 = mt2.getSubType();
        if (MimeTypeHelper.isMatch(mt1, mt2)) {
            if (st1.equals("*"))
                return -1;
            if (st2.equals("*"))
                return 1;
        }
        return 0;
    }

    /**
     * Compare two media types according to their relative level of specificity
     */
    public static int compare(String t1, String t2) {
        try {
            MimeType mt1 = new MimeType(t1);
            MimeType mt2 = new MimeType(t2);
            return compare(mt1, mt2);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Returns true if media type is a multiparted file.
     */
    public static boolean isMultipart(String a) {
        return isMatch(Constants.MULTIPART_RELATED_TYPE, a);
    }

    /**
     * Returns an Equivalence object to compare two MimeTypes
     */
    public Equivalence<MimeType> equivalence() {
      return new Equivalence<MimeType>() {
        protected boolean doEquivalent(MimeType a, MimeType b) {
          return isMatch(a,b);
        }
        protected int doHash(MimeType t) {
          return t.hashCode();
        }        
      };
    }
    
    public static final Function<String,MimeType> parser = 
      new Function<String,MimeType>() {
        public MimeType apply(String input) {
          return input != null ? unmodifiableMimeType(input) : null;
        }
      
    };
    
  public static MimeType unmodifiableMimeType(String mimeType) {
    try {
      return new UnmodifiableMimeType(mimeType);
    } catch (javax.activation.MimeTypeParseException t) {
      throw ExceptionHelper.propogate(t);
    }
  }

  public static MimeType unmodifiableMimeType(MimeType mimeType) {
    try {
      return mimeType instanceof UnmodifiableMimeType ?
        mimeType :
        new UnmodifiableMimeType(mimeType.toString());
    } catch (javax.activation.MimeTypeParseException t) {
      throw ExceptionHelper.propogate(t);
    }
  }
  
  public static class UnmodifiableMimeTypeParameterList 
    extends MimeTypeParameterList {
    private boolean inited = false;
    public UnmodifiableMimeTypeParameterList() {
      super();
      inited = true;
    }
  
    public UnmodifiableMimeTypeParameterList(String arg0)
      throws javax.activation.MimeTypeParseException {
        super(arg0);
        inited = true;
    }
  
    @Override
    public void remove(String arg0) {
      if (inited)
        throw new UnsupportedOperationException();
      else super.remove(arg0);
    }
  
    @Override
    public void set(String arg0, String arg1) {
      if (inited)
        throw new UnsupportedOperationException();
      else super.set(arg0, arg1);
    }
  }
    
  public static class UnmodifiableMimeType extends MimeType {
    private final MimeTypeParameterList list;
    private boolean inited = false;
    public UnmodifiableMimeType() {
      super();
      this.list = getList();
      inited = true;
    }
    public UnmodifiableMimeType(String primary, String sub)
        throws javax.activation.MimeTypeParseException {
      super(primary, sub);
      this.list = getList();
      inited = true;
    }
    public UnmodifiableMimeType(String rawdata)
        throws javax.activation.MimeTypeParseException {
      super(rawdata);
      this.list = getList();
      inited = true;
    }
    private MimeTypeParameterList getList() {
      try {
        return new UnmodifiableMimeTypeParameterList(
          super.getParameters().toString());
      } catch (javax.activation.MimeTypeParseException e) {
        throw new MimeTypeParseException(e);
      }
    }
    public void setPrimaryType(String primary)
        throws javax.activation.MimeTypeParseException {
      if (inited)
        throw new UnsupportedOperationException();
      else super.setPrimaryType(primary);
    }
    public void setSubType(String sub)
        throws javax.activation.MimeTypeParseException {
      if (inited)
        throw new UnsupportedOperationException();
      else super.setSubType(sub);
    }
    public void setParameter(String name, String value) {
      if (inited) 
        throw new UnsupportedOperationException();
      else super.setParameter(name, value);
    }
    public void removeParameter(String name) {
      if (inited)
        throw new UnsupportedOperationException();
      else super.removeParameter(name);
    }
    public MimeTypeParameterList getParameters() {
      return list;
    }
  }
}
