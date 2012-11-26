package org.apache.abdera2.test.common.text;

import static org.junit.Assert.assertEquals;

import org.apache.abdera2.common.text.NormalizationForm;
import org.apache.abdera2.common.text.Slug;
import org.junit.Test;

public class SlugTest {

  @Test
  public void slugTest() {
    assertEquals("a_slug",Slug.create("A Slug").toString());
    assertEquals("a__slug",Slug.create("\u212B Slug","_").toString());
    assertEquals("A%CC%8A_Slug",Slug.create("\u212B Slug", null, false, NormalizationForm.D).toString());
  }
  
}
