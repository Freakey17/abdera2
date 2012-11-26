package org.apache.abdera2.ext.activities;

import javax.xml.namespace.QName;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.common.xml.XMLVersion;
import org.apache.abdera2.model.Text;
import org.apache.abdera2.writer.StreamWriter;
import org.joda.time.DateTime;

public class ActivityToFeedConverter {

  protected void id(ASObject obj, StreamWriter writer) {
    String id = obj.getId();
    if (id != null) writer.writeId(id);
    else writer.writeId();
  }
  
  protected void title(ASObject obj, StreamWriter writer) {
    writer.writeTitle(
      titleType(obj),
      obj.has("title") ? 
        (String)obj.getProperty("title") :
        obj.has("displayName") ? 
          obj.getDisplayName() :
          "");
  }
  
  protected Text.Type titleType(ASObject obj) {
    return Text.Type.HTML;
  }
  
  protected void summary(ASObject obj, StreamWriter writer) {
    writer.writeSummary(
      summaryType(obj),
      obj.has("summary") ? 
        (String)obj.getSummary() :
        obj.has("displayName") ? 
          obj.getDisplayName() :
          "");
  }
  
  @SuppressWarnings("unchecked")
  protected void actor(Activity activity, StreamWriter writer) {
    ASObject obj = activity.getActor();
    if (obj instanceof Collection) {
      Collection<ASObject> col = 
        (Collection<ASObject>)obj;
      for (ASObject item : col.getItems())
        actor(item,writer);
    } else {
      actor((ASObject)obj,writer);
    }
  }
  
  @SuppressWarnings("unchecked")
  protected void actor(ASObject obj, StreamWriter writer) {
    writer.startAuthor();
    if (obj.getDisplayName() != null)
      writer.writePersonName(obj.getDisplayName());
    if (obj.getUrl() != null)
      writer.writePersonUri(obj.getUrl().toString());
    if (obj.has("emails")) {
      Object emails = obj.getProperty("emails");
      if (emails instanceof Iterable) {
        Iterable<String> i = (Iterable<String>) emails;
        for (String email : i)
          writer.writePersonEmail(email);
      } else {
        writer.writePersonEmail(emails.toString()); // take a chance
      }
    }
    writer.endAuthor();
  }

  protected void object(QName qname, ASObject object, StreamWriter writer) {
    if (object != null) {
      writer.startElement(qname);
      if (object.getObjectType() != null)
        writer.startElement(FeedToActivityConverter.OBJECTTYPE)
              .writeElementText(object.getObjectType())
              .endElement();
      if (object.getId() != null)
        writer.writeId(object.getId());
      if (object.getDisplayName() != null)
        writer.writeTitle(object.getDisplayName());
      if (object.getSummary() != null)
        writer.writeSummary(object.getSummary());
      if (object.getUrl() != null)
        writer.writeLink(object.getUrl().toString());
      if (object.getImage() != null) {
        writer.writeLink(object.getImage().getUrl().toString(), "preview");
      }
      writer.endElement();
    }
  }
  
  protected void object(Activity activity, StreamWriter writer) {
    ASObject object = activity.getObject();
    object(FeedToActivityConverter.OBJECT,object,writer);
  }
  
  protected void target(Activity activity, StreamWriter writer) {
    ASObject object = activity.getTarget();
    object(FeedToActivityConverter.TARGET,object,writer);
  }
  
  protected Text.Type summaryType(ASObject obj) {
    return Text.Type.HTML;
  }
  
  protected void writeFeedHeader(
    Collection<Activity> stream, 
    StreamWriter writer) {
    id(stream,writer);
    title(stream,writer);
    DateTime updated = stream.getUpdated();
    if (updated != null) 
      writer.writeUpdated(updated);    
  }
  
  private void writeEntry(
    Activity activity,
    StreamWriter writer) {
      writer.startEntry();
      writeEntryDetail(activity,writer);
      writer.endEntry();
  }
  
  protected void writeEntryDetail(
    Activity activity, 
    StreamWriter writer) {
      id(activity,writer);
      title(activity,writer);
      summary(activity,writer);
      if (activity.getUpdated() != null)
        writer.writeUpdated(activity.getUpdated());
      else writer.writeUpdatedNow();
      if (activity.getPublished() != null)
        writer.writePublished(activity.getPublished());
      else writer.writePublishedNow();
      if (activity.getUrl() != null)
        writer.writeLink(activity.getUrl().toString());
      actor(activity,writer);
      object(activity,writer);
      target(activity,writer);
  }
  
  public void convert(
    Collection<Activity> stream, 
    StreamWriter writer) {
      writer.startDocument(XMLVersion.XML11, "UTF-8");
      writer.startFeed();
      writeFeedHeader(stream,writer);
      for (Activity activity : stream.getItems())
        writeEntry(activity,writer);
      writer.endFeed();
      writer.endDocument();
  }
  
  public void convert(
    Activity activity, 
    StreamWriter writer) {
      writer.startDocument(XMLVersion.XML11, "UTF-8");
      writeEntry(activity,writer);
      writer.endDocument();
  }
  
}
