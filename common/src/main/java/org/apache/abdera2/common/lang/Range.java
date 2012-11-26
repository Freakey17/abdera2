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
package org.apache.abdera2.common.lang;

import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.lang.Subtag.Type;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import static org.apache.abdera2.common.text.CharUtils.*;
import static com.google.common.base.Preconditions.*;

/**
 * A language range used for matching language tags
 */
public final class Range 
  extends SubtagSet {

    private static final long serialVersionUID = -6397227794306856431L;
    private final boolean extended;

    public Range(String range, boolean extended) {
        super(parse(checkNotNull(range), extended).root);
        this.extended = extended;
    }

    public Range(String range) {
        this(parse(checkNotNull(range)).root);
    }

    public Range(Lang lang) {
        this(checkNotNull(lang).toString());
    }

    public Range(Lang lang, boolean extended) {
        this(checkNotNull(lang).toString(), extended);
    }

    Range(Subtag primary) {
        super(checkNotNull(primary));
        this.extended = !checkBasic();
    }

    public Range append(Subtag subtag) {
      Subtag last = null, first = null;
      for (Subtag tag : this) {
        last = new Subtag(tag,last);
        if (first == null) first = last;
      }
      if (last == null)
        last = subtag;
      last.setNext(subtag);
      return new Range(first);
    }

    public Range appendWildcard() {
      return append(Subtag.newWildcard());
    }

    public Range toBasicRange() {
        if (root.type() == Subtag.Type.WILDCARD) {
            return new Range("*");
        } else {
            ImmutableList.Builder<Subtag> list = ImmutableList.builder();
            for (Subtag tag : this)
              if (tag.type() != Subtag.Type.WILDCARD)
                list.add(new Subtag(tag.type(),tag.name(),null));
            Subtag primary = null, current = null;
            for (Subtag tag : list.build()) {
              tag.setNext(null);
              tag.setPrevious(null);
              if (primary == null) {
                primary = tag;
                current = primary;
              } else {
                checkNotNull(current).setNext(tag);
                current = tag;
              }
            }
            return new Range(primary);
        }
    }

    public boolean isBasic() {
        return !extended;
    }

    private boolean checkBasic() {
      Subtag current = root.next();
      while (current != null) {
        if (current.type() == Subtag.Type.WILDCARD)
          return false;
        current = current.next();
      }
      return true;
    }

    public Predicate<Lang> matches() {
      final Range thisRange = this;
      return new Predicate<Lang>() {
        public boolean apply(Lang input) {
          return thisRange.matches(input);
        }
      };
    }
    
    public Predicate<String> matchesString() {
      final Range thisRange = this;
      return new Predicate<String>() {
        public boolean apply(String input) {
          return thisRange.matches(input);
        }
      };
    }
    
    public Predicate<Lang> matchesExtended() {
      final Range thisRange = this;
      return new Predicate<Lang>() {
        public boolean apply(Lang input) {
          return thisRange.matches(input,true);
        }
      };
    }
    
    public Predicate<String> matchesStringExtended() {
      final Range thisRange = this;
      return new Predicate<String>() {
        public boolean apply(String input) {
          return thisRange.matches(input,true);
        }
      };
    }
    
    public boolean matches(String lang) {
        return matches(new Lang(lang), extended);
    }

    public boolean matches(String lang, boolean extended) {
        return matches(new Lang(lang), extended);
    }

    public boolean matches(Lang lang) {
        return matches(lang, extended);
    }

    public boolean matches(Lang lang, boolean extended) {
        Iterator<Subtag> i = iterator();
        Iterator<Subtag> e = lang.iterator();
        if (isBasic() && !extended) {
            if (root.type() == Subtag.Type.WILDCARD)
                return true;
            for (; i.hasNext() && e.hasNext();) {
                Subtag in = i.next();
                Subtag en = e.next();
                if (!in.equals(en))
                    return false;
            }
            return true;
        } else {
            Subtag icurrent = i.next();
            Subtag ecurrent = e.next();
            if (!icurrent.equals(ecurrent))
                return false;

            while (i.hasNext()) {
                icurrent = i.next();
                while (icurrent.type() == Subtag.Type.WILDCARD && i.hasNext())
                    icurrent = i.next();
                // the range ends in a wildcard so it will match everything beyond this point
                if (icurrent.type() == Subtag.Type.WILDCARD)
                    return true;
                boolean matched = false;
                while (e.hasNext()) {
                    ecurrent = e.next();
                    if (extended && (ecurrent.type().ordinal() < icurrent.type().ordinal()))
                        continue;
                    if (!ecurrent.equals(icurrent))
                        break;
                    else {
                        matched = true;
                        break;
                    }
                }
                if (!matched)
                    return false;
            }
            return true;
        }
    }

    public Function<Lang[],Iterable<Lang>> filter() {
      final Range thisRange = this;
      return new Function<Lang[],Iterable<Lang>>() {
        public Iterable<Lang> apply(Lang[] input) {
          return thisRange.filter(input);
        }
      };
    }
    
    public Function<String[],Iterable<String>> filterString() {
      final Range thisRange = this;
      return new Function<String[],Iterable<String>>() {
        public Iterable<String> apply(String[] input) {
          return thisRange.filter(input);
        }
      };
    }
    
    public Iterable<Lang> filter(Lang... lang) {
      ImmutableList.Builder<Lang> langs = 
        ImmutableList.builder();
      for (Lang l : lang)
        if (matches(l))
          langs.add(l);
      return langs.build();
    }

    public Iterable<String> filter(String... lang) {
      ImmutableList.Builder<String> langs = 
        ImmutableList.builder();
        for (String l : lang)
          if (matches(l))
            langs.add(l);
        return langs.build();
    }

    public static Iterable<Lang> filter(String range, Lang... lang) {
        return new Range(range).filter(lang);
    }

    public static Iterable<String> filter(String range, String... lang) {
        return new Range(range).filter(lang);
    }
    
    public static boolean matches(String range, Lang lang, boolean extended) {
        return new Range(range, extended).matches(lang);
    }

    public static boolean matches(String range, Lang lang) {
        return new Range(range).matches(lang);
    }

    public static boolean matches(String range, String lang, boolean extended) {
        return new Range(range, extended).matches(lang);
    }

    public static boolean matches(String range, String lang) {
        return new Range(range).matches(lang);
    }

    public static Predicate<String> matchesString(final String range) {
      return new Range(range).matchesString();
    }
    
    public static Predicate<String> matchesStringExtended(final String range) {
      return new Range(range).matchesStringExtended();
    }
    
    public static Predicate<Lang> matchesLang(final String range) {
      return new Range(range).matches();
    }
    
    public static Predicate<Lang> matchesLangExtended(final String range) {
      return new Range(range).matchesExtended();
    }
    
    // Parsing logic //

    private static final String SEP = "\\s*[-_]\\s*";
    private static final String range = "((?:[a-zA-Z]{1,8}|\\*))((?:[-_](?:[a-zA-Z0-9]{1,8}|\\*))*)";
    private static final String range_component = "[-_]((?:[a-zA-Z0-9]{1,8}|\\*))";
    private static final Pattern p_range = Pattern.compile(range);
    private static final Pattern p_range_component = Pattern.compile(range_component);

    private static final String language =
        "((?:[a-zA-Z]{2,3}(?:[-_](?:[a-zA-Z]{3}|\\*)){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8}|\\*)";
    private static final String script = "((?:[-_](?:[a-zA-Z]{4}|\\*))?)";
    private static final String region = "((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})|\\*))?)";
    private static final String variant = "((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})|\\*))*)";
    private static final String extension = "((?:[-_](?:(?:[a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)|\\*))*)";
    private static final String privateuse = "[xX](?:[-_][a-zA-Z0-9]{2,8})+";
    private static final String _privateuse = "((?:[-_](?:" + privateuse + ")+|\\*)?)";
    private static final String langtag = "^" + language + script + region + variant + extension + _privateuse + "$";
    private static final String grandfathered =
        "^(?:art[-_]lojban|cel[-_]gaulish|en[-_]GB[-_]oed|i[-_]ami|i[-_]bnn|i[-_]default|i[-_]enochian|i[-_]hak|i[-_]klingon|i[-_]lux|i[-_]mingo|i[-_]navajo|i[-_]pwn|i[-_]tao||i[-_]tay|i[-_]tsu|no[-_]bok|no[-_]nyn|sgn[-_]BE[-_]fr|sgn[-_]BE[-_]nl|sgn[-_]CH[-_]de|zh[-_]cmn|zh[-_]cmn[-_]Hans|zh[-_]cmn[-_]Hant|zh[-_]gan|zh[-_]guoyu|zh[-_]hakka|zh[-_]min|zh[-_]min[-_]nan|zh[-_]wuu|zh[-_]xiang|zh[-_]yue)$";
    private static final Pattern p_privateuse = Pattern.compile("^" + privateuse + "$");
    private static final Pattern p_grandfathered = Pattern.compile(grandfathered);
    private static final Pattern p_extended_range = Pattern.compile(langtag);

    /**
     * Parse the language-range
     */
    public static Range parse(String range) {
        return parse(range, false);
    }

    /**
     * Parse the language-range
     * 
     * @param range The language-range
     * @param extended true to use extended language rules
     */
    public static Range parse(String range, boolean extended) {
        if (!extended) {
          Subtag primary = null, current = null;
          Matcher m = p_range.matcher(range);
          if (m.find()) {
            String first = m.group(1);
            String therest = m.group(2);
            current = primary =
              Subtag.simple(first.toLowerCase(Locale.US));
            Matcher n = p_range_component.matcher(therest);
            while (n.find()) {
              String name = n.group(1).toLowerCase(Locale.US);
              current = Subtag.simple(name, current);
            }
          }
          return new Range(checkNotNull(primary));
        } else {
            Subtag primary = null;
            Matcher m = p_grandfathered.matcher(range);
            if (m.find()) {
                String[] tags = range.split(SEP);
                Subtag current = null;
                for (String tag : tags)
                  current = current == null ?
                    primary = Subtag.grandfathered(tag) :
                    Subtag.grandfathered(tag,current);
                return new Range(primary);
            }
            m = p_privateuse.matcher(range);
            if (m.find()) {
                String[] tags = range.split(SEP);
                Subtag current = null;
                for (String tag : tags)
                    current = current == null ?
                        primary = new Subtag(tag.equals("*") ? Type.WILDCARD : Type.SINGLETON, tag) :
                        new Subtag(tag.equals("*") ? Type.WILDCARD : Type.PRIVATEUSE, tag, current);
                return new Range(primary);
            }
            m = p_extended_range.matcher(range);
            checkArgument(m.find());
            String langtag = m.group(1);
            String script = m.group(2);
            String region = m.group(3);
            String variant = m.group(4);
            String extension = m.group(5);
            String privateuse = m.group(6);
            Subtag current = null;
            String[] tags = langtag.split(SEP);
            for (String tag : tags)
              current = current == null ?
                primary = Subtag.language(tag) :
                Subtag.extlang(tag, current);
            if (not_empty(script))
              current =
                Subtag.script(
                  script.substring(1),
                  current);
            if (not_empty(region))
              current =
                Subtag.region(
                  region.substring(1),
                  current);
            if (not_empty(variant)) {
              for (String tag : variant.substring(1).split(SEP))
                current = Subtag.variant(tag, current);
            }
            if (not_empty(extension)) {
              tags = extension.substring(1).split(SEP);
              current = Subtag.singleton(tags[0], current);
              for (int i = 1; i < tags.length; i++) {
                String tag = tags[i];
                current =
                  tag.length() == 1 ?
                    Subtag.singleton(tag, current) :
                    Subtag.extension(tag, current);
              }
            }
            if (not_empty(privateuse)) {
              tags = privateuse.substring(1).split(SEP);
              current = Subtag.singleton(tags[0], current);
              for (int i = 1; i < tags.length; i++)
                current = Subtag.privateuse(tags[i], current);
            }
            return new Range(primary);
        }

    }

}
