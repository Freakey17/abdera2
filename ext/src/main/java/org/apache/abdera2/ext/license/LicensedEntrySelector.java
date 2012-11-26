package org.apache.abdera2.ext.license;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.abdera2.common.selector.AbstractSelector;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.model.Entry;

/**
 * Used to only select entries that have the specified licenses or, if no
 * licenses are specified, select entries that specify any license
 */
public class LicensedEntrySelector 
extends AbstractSelector<Entry>
implements Selector<Entry> {

  private Set<String> set = new LinkedHashSet<String>();
  
  public LicensedEntrySelector(String... licenses) {
    for (String license : licenses)
      this.set.add(license);
  }
  
  public boolean select(Object item) {
    if (!(item instanceof Entry)) return false;
    Entry entry = (Entry)item;
    if (set.size() > 0) {
      for (String license : set) 
        if (LicenseHelper.hasLicense(entry, license, true))
          return true;
      return false;
    } else 
      return LicenseHelper.hasLicense(entry, true);
  }

}
