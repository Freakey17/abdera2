package org.apache.abdera2.test.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;

import javax.activation.MimeType;

import org.apache.abdera2.activities.extra.Difference;
import org.apache.abdera2.activities.extra.Extra;
import org.apache.abdera2.activities.io.gson.AdaptedType;
import org.apache.abdera2.activities.io.gson.SimpleAdapter;
import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.CollectionWriter;
import org.apache.abdera2.activities.model.Verb;
import org.apache.abdera2.activities.model.ASBase.ASBuilder;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.activities.model.MediaLink;
import org.apache.abdera2.activities.model.objects.BinaryObject;
import org.apache.abdera2.activities.model.objects.Mood;
import org.apache.abdera2.activities.model.objects.NoteObject;
import org.apache.abdera2.activities.model.objects.Objects;
import org.apache.abdera2.activities.model.objects.PersonObject;
import org.apache.abdera2.activities.model.objects.ServiceObject;
import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.common.geo.IsoPosition;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.io.Compression.CompressionCodec;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.common.misc.MoreExecutors2;
import org.apache.abdera2.common.misc.Pair;
import org.apache.abdera2.common.security.HashHelper;
import org.apache.abdera2.common.templates.Template;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class ActivitiesTest {

  @Test
  public void testIo() {
    
    // Test Default IO
    IO io = IO.get();
    assertNotNull(io);
    
    // Test IO with Custom Type Adapter
    io = IO.get(new TestTypeAdapter());
    assertNotNull(io);
    
    // Test IO Builder
    io = IO.make()
      .autoClose()
      .charset("UTF-8")
      .prettyPrint()
      .adapter(new TestTypeAdapter())
      .get();
    assertNotNull(io);
    
    // Test getCollectionWriter
    assertNotNull(io.getCollectionWriter(System.out));
    
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testASBase() {
    
    // Test Creation
    ASBase base = ASBase
      .make()
      .base("http://example.org")
      .lang("en-US")
      .set("a", "b")
      .set(Pair.of("b", "d"))
      .set(Pair.of("c", "e"), Pair.of("d", "f"))
      .extend(ExtBuilder.class)
      .e("g")
      .unwrap()
      .get();
    assertNotNull(base);
    assertEquals("b",base.getProperty("a"));
    assertEquals("d",base.getProperty("b"));
    assertEquals("e",base.getProperty("c"));
    assertEquals("f",base.getProperty("d"));
    assertEquals("g",base.getProperty("e"));
    
    assertEquals(Lang.parse("en-US"), base.getLang());
    assertEquals(new IRI("http://example.org"), base.getBase());
    
    assertEquals(
      "B",
      base.getProperty(
        "a", 
        new Function<String,String>() {
          public String apply(String input) {
            return input.toUpperCase(Locale.US);
          }
        }));
    
    assertEquals("g", base.extend(ExtObject.class).getE());
    assertEquals(ASBase.class, base.extend(ExtObject.class).unwrap().getClass());
    
    assertThat(base, hasItems("a","b","c","d","e","@base","@language"));
   
    // Test as
    Mood mood = base.as(Mood.class);
    assertNotNull(mood);
    assertThat(mood, hasItems("a","b","c","d","e","@base","@language"));
    
    // Test merging objects
    ASBase base2 = ASBase.make().set("f","g").get();
    base2 = base2.as(ASBase.class,base);
    assertThat(base2, hasItems("a","b","c","d","e","f","@base","@language"));
    
    // Test filtering out fields
    assertThat(base2, hasItem("f"));
    base2 = base2.as(ASBase.class, ASBase.withoutFields("f"));
    assertThat(base2, not(hasItem("f")));
    
    // Test limiting fields
    base2 = base.as(ASBase.class, ASBase.withFields("a","b"));
    assertThat(base2, hasItems("a","b"));
    assertThat(base2, not(hasItems("c","d","e","f","@base","@language")));
    
    // Test Template
    ASBuilder builder = base.template();
    assertNotNull(builder);
    base2 = builder.get();
    assertEquals(base,base2);
    
    builder = base.template(ASBase.withFields("a"));
    base2 = builder.set("e","z").set("z","y").get();
    assertThat(base2, hasItems("a","e","z"));
    assertThat(base2, not(hasItems("b","c","d")));
    
    Difference diff = base.diff(base2);
    assertThat(diff.added(), hasItem(Pair.<String,Object>of("z","y")));
    assertThat(diff.changed(), hasItem(Pair.<String,Pair<Object,Object>>of("e", Pair.<Object,Object>of("g","z"))));
    assertThat(diff.removed(),
      hasItems(
        Pair.<String,Object>of("d","f"),
        Pair.<String,Object>of("b","d"),
        Pair.<String,Object>of("c","e"),
        Pair.<String,Object>of("@language",Lang.parse("en-US")),
        Pair.<String,Object>of("@base",new IRI("http://example.org"))));
    
    // test read/parse
    IO io = IO.get();
    StringWriter sw = new StringWriter();
    io.write(base,sw);
    assertEquals("{\"@base\":\"http://example.org\",\"@language\":\"en-US\",\"a\":\"b\",\"b\":\"d\",\"c\":\"e\",\"d\":\"f\",\"e\":\"g\"}",sw.toString());
    
    base = io.read(new StringReader(sw.toString()));
    assertThat(base, hasItems("a","b","c","d","e","@base","@language"));
    
    // test non-blocking serialization and read
    try {
      String s = io.write(base, MoreExecutors2.getExitingExecutor()).get();
      assertEquals("{\"@base\":\"http://example.org\",\"@language\":\"en-US\",\"a\":\"b\",\"b\":\"d\",\"c\":\"e\",\"d\":\"f\",\"e\":\"g\"}",s);
      base = io.read(new StringReader(s), MoreExecutors2.getExitingExecutor()).get();
      assertNotNull(base);
      assertThat(base, hasItems("a","b","c","d","e","@base","@language"));
    } catch (Throwable t) {}
  }
  
  @Test
  public void testCustomType() {
    IO io = IO.make()
      .adapter(new TestTypeAdapter())
      .property("a", TestType.class)
      .get();
    ASBase base = ASBase.make().set("a",new TestType("b")).get();
    String str = io.write(base);
    assertEquals("{\"a\":\"b\"}",str);
    base = io.read(new StringReader(str));
    assertTrue(base.getProperty("a") instanceof TestType);
  }
  
  @Test
  public void testASObject() {
    
    ASObject obj = 
      ASObject.makeObject("foo")
      .attachment(ASObject.makeObject("bar").get())
      .author(ASObject.makeObject("person").get())
      .content("FooBar")
      .displayName("My Object")
      .downstreamDuplicate("urn:bob")
      .id("http://example.org/foo")
      .image(MediaLink.makeMediaLink().get())
      .publishedNow()
      .summary("Summary")
      .tag(ASObject.makeObject("baz").get())
      .updatedNow()
      .url("http://example.org/baz")
      .get();
    
    assertEquals("foo", obj.getObjectType());
    assertNotNull(obj.getAuthor());
    assertEquals("person",obj.getAuthor().getObjectType());
    assertEquals("FooBar",obj.getContent());
    assertEquals("My Object",obj.getDisplayName());
    assertThat(obj.getDownstreamDuplicates(), hasItem("urn:bob"));
    assertEquals("http://example.org/foo",obj.getId());
    assertNotNull(obj.getImage());
    assertNotNull(obj.getPublished());
    assertEquals("Summary",obj.getSummary());
    assertThat(obj.getTags(),hasItem(ASObject.makeObject("baz").get()));
    assertNotNull(obj.getUpdated());
    assertEquals(new IRI("http://example.org/baz"),obj.getUrl());
    assertThat(obj.getKnownIds(), hasItems("http://example.org/foo","urn:bob"));

  }
  
  @Test
  public void testActivity() {
    Activity activity = 
      Activity.makeActivity()
      .actor(PersonObject.makePerson("James"))
      .verb(Verb.POST)
      .object(NoteObject.makeNote().content("Test").get())
      .target(ServiceObject.makeService().id("urn:my:wall").get())
      .to(PersonObject.makePerson("Joe"))
      .to(Objects.ME)
      .cc(PersonObject.makePerson("Jane"))
      .bcc(Objects.ME)
      .bto(Objects.NETWORK)   
      .get();
    assertNotNull(activity.getActor());
    assertNotNull(activity.getVerb());
    assertNotNull(activity.getObject());
    assertNotNull(activity.getTarget());
    
    assertTrue(Extra.actorIs(PersonObject.makePerson("James").get()).select(activity));
    assertFalse(Extra.actorIs(Objects.SELF).select(activity));
    assertTrue(Extra.isToMeOr(PersonObject.makePerson("Joe").get()).select(activity));
    assertTrue(Extra.isTo(PersonObject.makePerson("Joe").get()).select(activity));
    assertTrue(Extra.isCc(PersonObject.makePerson("Jane").get()).select(activity));
    assertTrue(Extra.isBccMe().select(activity));
    assertTrue(Extra.isBtoNetwork().select(activity));
    assertFalse(Extra.isTo(PersonObject.makePerson("Joe").id("urn:foo").get()).select(activity));
    
  }
  
  @Test
  public void testCollectionWriter() {
    IO io = IO.get();
    StringWriter sw = new StringWriter();
    CollectionWriter cw = io.getCollectionWriter(sw);
    cw.writeHeader(
      ASBase.make()
      .set("a","b")
      .get());
    cw.writeObject(
      Activity.makeActivity().verb(Verb.POST).get());
    cw.writeObjects(
      Activity.makeActivity().verb(Verb.POST).get(),
      Activity.makeActivity().verb(Verb.POST).get());
    cw.complete();
    Collection<Activity> col = io.read(sw.toString());
    assertEquals("b",col.getProperty("a"));
    assertEquals(3, Iterables.size(col.getItems()));
  }
  
  @Test
  public void testMediaLink() {
    MediaLink ml = 
      MediaLink.makeMediaLink()
      .url("http://example.org/foo")
      .height(10)
      .width(10)
      .duration(10)
      .get();
    
    assertEquals(new IRI("http://example.org/foo"), ml.getUrl());
    assertEquals(10, ml.getHeight());
    assertEquals(10, ml.getWidth());
    assertEquals(10, ml.getDuration());
  }
  
  @Test
  public void testCollection() {
    Collection<Activity> col = 
      Collection.<Activity>makeCollection()
      .item(Activity.makeActivity().get())
      .get();
    assertThat(col.getItems(), hasItem(Activity.makeActivity().get()));
    assertEquals(1, col.getTotalItems());
  }
  
  @Test
  public void testVerb() {
    Verb verb = Verb.get("some-new-verb");
    assertNotNull(verb);
    assertEquals("some-new-verb",verb.getName());
  }
  
  @Test
  public void testDefaultAdaptedTypes() {
    
    IO io = IO.make()
      .property("a",Date.class)
      .property("b",DateTime.class)
      .property("c",Duration.class)
      .property("d",EntityTag.class)
      .property("e",Interval.class)
      .property("f",IRI.class)
      .property("g",Lang.class)
      .property("h",MimeType.class)
      .property("i",Multimap.class)
      .property("j",IsoPosition.class)
      .property("k",Template.class)
      .property("l",Verb.class)
      .get();
    ASBase base = 
      ASBase.make()
      .set("a", new Date())
      .set("b", DateTimes.now())
      .set("c", Duration.millis(1))
      .set("d", EntityTag.parse("W/\"foo\""))
      .set("e", new Interval(DateTimes.now(),DateTimes.now().plusHours(1)))
      .set("f", new IRI("http://example.org"))
      .set("g", Lang.ENGLISH)
      .set("h", MimeTypeHelper.unmodifiableMimeType("text/plain"))
      .set("i", HashMultimap.<String,String>create())
      .set("j", IsoPosition.at(1, 2))
      .set("k", new Template("{foo}"))
      .set("l", Verb.POST)
      .get();
    String str = io.write(base);
    base = io.read(str);
    assertTrue(base.getProperty("a") instanceof Date);
    assertTrue(base.getProperty("b") instanceof DateTime);
    assertTrue(base.getProperty("c") instanceof Duration);
    assertTrue(base.getProperty("d") instanceof EntityTag);
    assertTrue(base.getProperty("e") instanceof Interval);
    assertTrue(base.getProperty("f") instanceof IRI);
    assertTrue(base.getProperty("g") instanceof Lang);
    assertTrue(base.getProperty("h") instanceof MimeType);
    assertTrue(base.getProperty("i") instanceof Multimap);
    assertTrue(base.getProperty("j") instanceof IsoPosition);
    assertTrue(base.getProperty("k") instanceof Template);
    assertTrue(base.getProperty("l") instanceof Verb);
  }
  
  @Test
  public void testBinaryObject() throws IOException {
    BinaryObject b = 
      BinaryObject.makeBinary() 
      .data(new byte[] {1,2,3,4,5}, 
      new HashHelper.Md5(),
      CompressionCodec.GZIP)
      .get();
    IO io = IO.get();
    String str = io.write(b);
    b = io.read(str);
    assertEquals("H4sIAAAAAAAAAGNkYmZhBQD0mQtHBQAAAA\u003d\u003d",b.getProperty("data"));
    assertEquals("7cfdd07889b3295d6a550914ab35e068",b.getProperty("md5"));
    assertEquals("application/octet-stream",b.getProperty("mimeType").toString());
    assertEquals(5,b.getProperty("length"));
    assertEquals("gzip",b.getProperty("compression"));
    InputStream in = b.getInputStream();
    assertEquals((byte)1,in.read());
    assertEquals((byte)2,in.read());
    assertEquals((byte)3,in.read());
    assertEquals((byte)4,in.read());
    assertEquals((byte)5,in.read());
    assertEquals(-1,in.read());
  }
  
  public static interface ExtBuilder extends Extra.ExtensionBuilder {
    ExtBuilder e(String v);
  }
  public static interface ExtObject extends Extra.ExtensionObject {
    String getE();
  }
  
  public static class TestType {
    private final String val;
    public TestType(String v) {
      this.val = v;
    }
    public String toString() {
      return val;
    }
    public String getVal() {
      return val;
    }
  }
  
  @AdaptedType(TestType.class)
  public static class TestTypeAdapter
    extends SimpleAdapter<TestType> {
    protected TestType deserialize(String v) {
      return new TestType(v);
    }    
  }
}
