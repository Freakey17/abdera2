package org.apache.abdera2.ext.activities;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.ASObject.ASObjectBuilder;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Activity.ActivityBuilder;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.Collection.CollectionBuilder;
import org.apache.abdera2.activities.model.CollectionWriter;
import org.apache.abdera2.activities.model.MediaLink;
import org.apache.abdera2.activities.model.Verb;
import org.apache.abdera2.activities.model.objects.PersonObject;
import org.apache.abdera2.activities.model.objects.ServiceObject;
import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.ext.history.FeedPagingHelper;
import org.apache.abdera2.model.Category;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.ExtensibleElement;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.model.Link;
import org.apache.abdera2.model.Person;
import org.apache.abdera2.model.Generator;
import org.apache.abdera2.model.Text;
import static org.apache.abdera2.model.selector.Selectors.withRel;

/**
 * A default Atom Feed to Activity Stream Conversion implementation. 
 * Applications can customize the conversion process by subclassing
 * this class and overriding the various conversion methods.
 */
public class FeedToActivityConverter {

  public static final String NS = "http://activitystrea.ms/spec/1.0/";
  
  public static final QName VERB = new QName(NS,"verb");
  public static final QName TARGET = new QName(NS,"target");
  public static final QName OBJECT = new QName(NS,"object");
  public static final QName OBJECTTYPE = new QName(NS,"object-type");
  
  public void convert(Feed feed, CollectionWriter writer) {
    convert(feed,writer,null);
  }
  
  public Activity convert(Entry entry) {
    return item(entry);
  }
  
  @SuppressWarnings("rawtypes")
  public void convert(
    Feed feed, 
    CollectionWriter writer, 
    Selector selector) {
    ASBase header = header(feed);
    if (header != null)
      writer.writeHeader(header);
    Iterable<Entry> entries = 
      selector != null ? 
        feed.getEntries(selector) : 
        feed.getEntries();
    for (Entry entry : entries) {
      Activity activity = item(entry);
      if (activity != null)
        writer.writeObject(activity);
    }
    writer.complete();
  }

  protected ASBase header(Feed feed) {
    CollectionBuilder<Activity> builder = 
      Collection.<Activity>makeCollection()
        .id(feed.getId().toString())
        .author(authors(feed.getAuthors()))
        .set("contributors", authors(feed.getContributors()));
    categories(feed.getCategories(), builder);
    builder.set("generator", generator(feed.getGenerator()))
      .image(image(feed.getIcon()))
      .url(feed.getAlternateLinkResolvedHref())
      .displayName(feed.getTitle())
      .summary(feed.getSubtitle())
      .updated(feed.getUpdated())
      .set("nextLink", FeedPagingHelper.getNext(feed))
      .set("previousLink", FeedPagingHelper.getPrevious(feed))
      .set("firstLink", FeedPagingHelper.getFirst(feed))
      .set("lastLink", FeedPagingHelper.getLast(feed))
      .set("selfLink", feed.getSelfLinkResolvedHref());
    return builder.get();
  }
  
  protected Verb verb(Entry entry) {
    Verb verb = Verb.POST;
    if (entry.has(VERB)) {
      String val = entry.getSimpleExtension(VERB);
      // might be a url, just get the last component
      int n = val.indexOf('/');
      if (n > -1) val = val.substring(n+1);
      verb = Verb.get(val);
    }
    return verb;
  }
  
  protected Activity item(Entry entry) {
    ActivityBuilder builder = Activity.makeActivity();
    if (entry.getId() != null)
      builder.id(entry.getId().toString());
    if (entry.getUpdated() != null)
      builder.updated(entry.getUpdated());
    if (entry.getPublished() != null)
      builder.published(entry.getPublished());
    builder.url(entry.getAlternateLinkResolvedHref())
      .title(entry.getTitle())
      .summary(entry.getSummary());
    categories(entry.getCategories(), builder);
    builder.lang(entry.getLanguageTag())
      .verb(verb(entry));
    List<Person> authors = entry.getAuthorsInherited();
    builder.actor(authors(authors))
      .set("contributors", authors(entry.getContributors()))
      .object(object(entry))
      .target(target(entry));
    List<Link> enclosures = entry.getLinks("enclosure");
    for (Link link : enclosures)
      builder.attachment(
        attachment(link));
    return builder.get();
  }
  
  protected String objectType(ExtensibleElement ext) {
    String objtype = null;
    if (ext.has(OBJECTTYPE)) {
      String val = ext.getSimpleExtension(OBJECTTYPE);
      int n = val.lastIndexOf('/');
      if (n > -1) val = val.substring(n+1);
      objtype = val;
    }
    return objtype;
  }
  
  protected ASObject object(ExtensibleElement ext) {
    ASObjectBuilder builder = ASObject.makeObject(objectType(ext));
    if (ext.has(Constants.ID))
      builder.id(ext.getSimpleExtension(Constants.ID));
    if (ext.has(Constants.TITLE)) {
      Text text = ext.getExtension(Constants.TITLE);
      builder.displayName(text.getValue());
    }
    if (ext.has(Constants.SUMMARY)) {
      Text text = ext.getExtension(Constants.SUMMARY);
      builder.summary(text.getValue());
    }
    List<Link> links = ext.getExtensions(
      Constants.LINK, withRel("preview"));
    for (Link link : links) {
      String rel = link.getCanonicalRel();
      if (Link.REL_ALTERNATE.equalsIgnoreCase(rel)) {
        builder.url(link.getResolvedHref());
      } else if ("preview".equalsIgnoreCase(rel)) {
        builder.image(image(link.getResolvedBaseUri()));
      }
    }
    return builder.get();
  }
  
  protected ASObject target(Entry entry) {
    return entry.has(TARGET) ?
        object((ExtensibleElement)entry.getExtension(TARGET)) :
        null;
  }
  
  protected ASObject object(Entry entry) {    
    return entry.has(OBJECT) ?
      object((ExtensibleElement)entry.getExtension(OBJECT)) :
      object((ExtensibleElement)entry);
  }
  
  protected ASObject attachment(Link link) {
    return ASObject.makeObject("link")
      .url(link.getResolvedHref())
      .set("mimeType", link.getMimeType())
      .displayName(link.getTitle())
      .set("hreflang", link.getHrefLang())
      .get();
  }
  
  protected MediaLink image(IRI iri) {
    return iri == null ? null :
      MediaLink.makeMediaLink()
        .url(iri).get();
  }
  
  protected ASObject generator(Generator generator) {
    return generator == null ? null :
      ServiceObject
        .makeService()
        .displayName(generator.getText())
        .set("version",generator.getVersion())
        .url(generator.getUri())
        .get();
  }
  
  protected void categories(List<Category> categories, ASObject.Builder<?,?> builder) {
    for (Category category : categories)
      builder.tag(category(category));
  }
  
  protected ASObject category(Category category) {
    return ASObject.makeObject("category")
      .displayName(category.getTerm())
      .set("scheme",category.getScheme()).get();
  }
  
  protected ASObject authors(List<Person> authors) {
    if (authors.size() == 1) {
      return person(authors.get(0));
    } else if (authors.size() > 1) {
      CollectionBuilder<PersonObject> _authors = 
        Collection.<PersonObject>makeCollection();
      for (Person person : authors) {
        PersonObject _person = person(person);
        if (_person != null) 
          _authors.item(_person);
      }
      return _authors.get();
    } else return null;
  }
  
  protected PersonObject person(Person person) {
    return 
      PersonObject.makePerson()
       .displayName(person.getName())
       .url(person.getUri())
       .email(person.getEmail()).get();
  }
}
