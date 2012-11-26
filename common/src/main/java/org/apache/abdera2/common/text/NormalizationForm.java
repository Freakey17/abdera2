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
package org.apache.abdera2.common.text;

import java.util.Iterator;

import org.apache.abdera2.common.misc.MoreFunctions;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalences;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.Normalizer2.Mode;

/**
 * Utility that wraps Unicode Normalization functions
 */
public enum NormalizationForm
  implements Function<CharSequence,CharSequence> {
  
    D(Mode.DECOMPOSE,  "nfc"), 
    C(Mode.COMPOSE,    "nfc"), 
    KD(Mode.DECOMPOSE, "nfkc"), 
    KC(Mode.COMPOSE,   "nfkc");
    
    private final Normalizer2.Mode mode;
    private final String name;
    private final NormalizedEquivalence ne;
    
    NormalizationForm(
        Normalizer2.Mode mode, 
        String name) {
      this.mode = mode;
      this.name = name;
      this.ne = new NormalizedEquivalence(this);
    }
    
    public String normalize(CharSequence s) {
      return Normalizer2.getInstance(null, name, mode).normalize(s);
    }
    
    /**
     * Returns true if the normalized form of both input sequences
     * are equivalent to one another
     */
    public boolean equivalent(CharSequence s1, CharSequence s2) {
      return ne.equivalent(s1, s2);
    }
    
    public Equivalence<CharSequence> equivalence() {
      return ne;
    }
    
    public Predicate<CharSequence> equivalentTo(CharSequence seq) {
      return equivalence().equivalentTo(seq);
    }
    
    public static class NormalizedEquivalence
      extends Equivalence<CharSequence> {
      private final NormalizationForm form;
      public NormalizedEquivalence(NormalizationForm form) {
        this.form = form;
      }
      protected boolean doEquivalent(
        CharSequence a, 
        CharSequence b) {
          if (a == null && b != null) return false;
          if (a != null && b == null) return false;
          String s1 = form.normalize(a);
          String s2 = form.normalize(b);
          return Equivalences.equals().equivalent(s1, s2);
      }

      protected int doHash(CharSequence t) {
        if (t == null)
          throw new IllegalArgumentException();
        String s1 = form.normalize(t);
        return s1.hashCode();
      }
      
    }
    
  public CharSequence apply(CharSequence input) {
    return normalize(input);
  }
  
  /**
   * Returns an Iterator over the normalized codepoints in the input string
   */
  public Iterator<Integer> iterator(CharSequence input) {
    return CodepointIterator.getInstance(normalize(input));
  }
  
  public Iterable<Integer> iterable(CharSequence input) {
    return MoreFunctions.iterableOver(iterator(input));
  }
}