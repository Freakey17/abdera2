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

import java.util.Map;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.misc.Pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps.EntryTransformer;

import static com.google.common.collect.Maps.difference;
import static com.google.common.collect.Maps.transformEntries;
import static org.apache.abdera2.activities.extra.Extra.caseTransform;

public final class Difference {

  public static Difference diff(ASBase base1, ASBase base2) {
    return new Difference(base1,base2);
  }
  
  private static final EntryTransformer<String,Object,Object> transform =
    caseTransform("objectType","alias");
  
  private final Iterable<Pair<String,Pair<Object,Object>>> changed;
  private final Iterable<Pair<String,Object>> added;
  private final Iterable<Pair<String,Object>> removed;
    
  Difference(ASBase base1, ASBase base2) {
    MapDifference<String,Object> difference = 
      difference(
        transformEntries(base1.toMap(),transform), 
        transformEntries(base2.toMap(),transform));
    this.changed = _fieldsChanged(difference);
    this.added = _fieldsAdded(difference);
    this.removed = _fieldsRemoved(difference);
  }
  
  public Iterable<Pair<String,Pair<Object,Object>>> changed() {
    return changed;
  }
  
  public Iterable<Pair<String,Object>> added() {
    return added;
  }
  
  public Iterable<Pair<String,Object>> removed() {
    return removed;
  }
  
  public String toString() {
    return new StringBuilder()
      .append("Changes: ").append(changed).append('\n')
      .append("Added:   ").append(added).append('\n')
      .append("Removed: ").append(removed).append('\n')
      .toString();
  }
  
  public int hashCode() {
    return MoreFunctions.genHashCode(1,changed,added,removed);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Difference other = (Difference) obj;
    if (added == null) {
      if (other.added != null)
        return false;
    } else if (!Iterables.elementsEqual(added, other.added))
      return false;
    if (changed == null) {
      if (other.changed != null)
        return false;
    } else if (!Iterables.elementsEqual(changed, other.changed))
      return false;
    if (removed == null) {
      if (other.removed != null)
        return false;
    } else if (!Iterables.elementsEqual(removed, other.removed))
      return false;
    return true;
  }

  private Iterable<Pair<String,Pair<Object,Object>>> _fieldsChanged(MapDifference<String,Object> difference) {
    Map<String,ValueDifference<Object>> changes = 
      difference.entriesDiffering();
    ImmutableSet.Builder<Pair<String,Pair<Object,Object>>> set =
      ImmutableSet.builder();
    for (Map.Entry<String, ValueDifference<Object>> entry : changes.entrySet()) {
      ValueDifference<Object> vd = entry.getValue();
      set.add(Pair.of(entry.getKey(),Pair.of(vd.leftValue(), vd.rightValue())));
    }
    return set.build();
  }
  
  private Iterable<Pair<String,Object>> _fieldsAdded(MapDifference<String,Object> difference) {
    return Pair.from(difference.entriesOnlyOnRight());
  }
  
  private Iterable<Pair<String,Object>> _fieldsRemoved(MapDifference<String,Object> difference) {
    return Pair.from(difference.entriesOnlyOnLeft());
  }
}
