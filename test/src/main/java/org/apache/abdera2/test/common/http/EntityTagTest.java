package org.apache.abdera2.test.common.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

import org.apache.abdera2.common.http.EntityTag;
import org.junit.Test;

import com.google.common.collect.Iterables;

public class EntityTagTest {
  @Test
  public void testEntityTag() throws Exception {
      String[] tags = {"hello", "\"hello\"", "W/\"hello\"", "*"};
      EntityTag[] etags = new EntityTag[tags.length];
      for (int n = 0; n < tags.length; n++) {
          etags[n] = new EntityTag(tags[n]);
      }
      assertFalse(etags[0].isWeak());
      assertFalse(etags[0].isWild());
      assertFalse(etags[1].isWeak());
      assertFalse(etags[1].isWild());
      assertTrue(etags[2].isWeak());
      assertFalse(etags[2].isWild());
      assertFalse(etags[3].isWeak());
      assertTrue(etags[3].isWild());
      assertEquals("hello", etags[0].getTag());
      assertEquals("hello", etags[1].getTag());
      assertEquals("hello", etags[2].getTag());
      assertEquals("*", etags[3].getTag());
      assertEquals(tags[1], etags[0].toString());
      assertEquals(tags[1], etags[1].toString());
      assertEquals(tags[2], etags[2].toString());
      assertEquals(tags[3], etags[3].toString());

      assertTrue(EntityTag.matches(etags[3], etags[0]));
      assertTrue(EntityTag.matches(etags[3], etags[1]));
      assertTrue(EntityTag.matches(etags[3], etags[2]));
      assertTrue(EntityTag.matches(etags[3], etags[3]));

      assertTrue(EntityTag.matches(etags[0], etags[1]));
      assertFalse(EntityTag.matches(etags[0], etags[2]));

      assertTrue(EntityTag.matchesAny(etags[3], Arrays.asList(new EntityTag[] {etags[0], etags[1], etags[2]})));
      assertTrue(EntityTag.matchesAny(etags[0], Arrays.asList(new EntityTag[] {etags[3], etags[1], etags[2]})));
      assertTrue(EntityTag.matchesAny(etags[1], Arrays.asList(new EntityTag[] {etags[0], etags[3], etags[2]})));
      assertTrue(EntityTag.matchesAny(etags[2], Arrays.asList(new EntityTag[] {etags[0], etags[1], etags[3]})));

      java.util.Arrays.sort(etags);
      assertEquals(tags[3], etags[0].toString());
      assertEquals(tags[1], etags[1].toString());
      assertEquals(tags[1], etags[2].toString());
      assertEquals(tags[2], etags[3].toString());
      EntityTag etag = EntityTag.generate("a", "b", "c", "d");
      assertEquals("\"e2fc714c4727ee9395f324cd2e7f331f\"", etag.toString().toLowerCase());
  }

  @Test
  public void simpleetagquoted() {
    EntityTag etag = new EntityTag("W/\"\\\"foo\\\\\"");
    assertTrue(etag.isWeak());
    assertEquals("\"foo\\",etag.getTag());
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void multiple() {
    Iterable<EntityTag> tags = EntityTag.parseTags("\"foo\", W/\"bar\", \"\\\\whee\\\"\"");
    assertEquals(3,Iterables.size(tags));
    for (EntityTag etag : tags) {
      assertThat(etag.getTag(), anyOf(is("foo"),is("bar"),is("\\whee\"")));
      if (etag.getTag().equals("bar"))
        assertTrue(etag.isWeak());
    }
  }
}
