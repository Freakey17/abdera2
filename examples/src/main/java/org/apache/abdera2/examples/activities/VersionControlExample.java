package org.apache.abdera2.examples.activities;

import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Activity.ActivityBuilder;
import org.apache.abdera2.activities.model.Collection.CollectionBuilder;
import org.apache.abdera2.activities.model.objects.FileObject;
import org.apache.abdera2.activities.model.objects.VersionObject;

import static org.apache.abdera2.activities.model.Verb.POST;
import static org.apache.abdera2.activities.model.Verb.REJECT;
import static org.apache.abdera2.activities.model.Verb.APPROVE;
import static org.apache.abdera2.activities.model.Collection.makeCollection;
import static org.apache.abdera2.activities.model.Activity.makeActivity;
import static org.apache.abdera2.activities.model.objects.PersonObject.makePerson;
import static org.apache.abdera2.activities.model.objects.VersionObject.makeVersion;
import static org.apache.abdera2.activities.model.objects.FileObject.makeFile;

public class VersionControlExample {

  public static void main(String... args) throws Exception {
        
    ActivityBuilder me = 
      makeActivity()
      .actor(
        makePerson()
          .displayName("James")
          .get());
    
    ActivityBuilder boss = 
      makeActivity()
      .actor(
        makePerson()
          .displayName("The Boss")
          .get());
    
    CollectionBuilder<Activity> builder = 
      makeCollection();
    
    // first, indicate that we created a document
    FileObject file = 
      makeFile()
        .id("urn:example:file/presentation")
        .fileUrl("http://example.org/presentation.ppt")
        .displayName("presentation.ppt")
        .get();
    
    builder.item(
      me.template()
        .set("object", file)
        .set("verb", POST)
        .get());
    
    // second, indicate that a new version was created
    VersionObject version = 
      makeVersion()
        .of(file)
        .major(2)
        .get();
    
    builder.item(
      me.template() 
         .set("object", version)
         .set("verb", POST)
         .get());
    
    // whoops, the boss rejected the new version
    builder.item(
      boss.template()
         .set("object", version)
         .set("verb", REJECT)
         .set("summary", "This version is missing something")
         .get());
    
    // create a new version to deal with the bosses concerns
    VersionObject old = version;
    version = makeVersion()
      .of(file)
      .major(3)
      .previous(old)
      .get();
    
    builder.item(
      me.template() 
         .set("object", version)
         .set("verb", POST)
         .get());
    
    // the boss approves the new version
    builder.item(
      boss.template()
       .set("object", version)
       .set("verb", APPROVE)
       .get());
    
    builder.get().writeTo(System.out);
  }
  
}


/** Produces:
{
  "items":[
    {
      "verb":"post",
      "object": {
        "id":"urn:example:file/presentation",
        "fileUrl":"http://example.org/presentation.ppt",
        "displayName":"presentation.ppt",
        "objectType":"file"},
      "actor":{

        "displayName":"James",
        "objectType":"person" },
      "objectType":"activity"
    },
    {
      "verb":"post",
      "object":{
        "of":{
          "id":"urn:example:file/presentation",
          "fileUrl":"http://example.org/presentation.ppt",
          "displayName":"presentation.ppt",
          "objectType":"file"},
        "objectType":"version",
        "major":"2"},
      "actor":{
        "displayName":"James",
        "objectType":"person"},
      "objectType":"activity"},
    {
      "summary":"This version is missing something",
      "verb":"reject",
      "object":{
        "of":{
          "id":"urn:example:file/presentation",
          "fileUrl":"http://example.org/presentation.ppt",
          "displayName":"presentation.ppt",
          "objectType":"file"},
        "objectType":"version",
        "major":"2"},
      "actor":{
        "displayName":"The Boss",
        "objectType":"person"},
      "objectType":"activity"},
    {
      "verb":"post",
      "object":{
        "of":{
          "id":"urn:example:file/presentation",
          "fileUrl":"http://example.org/presentation.ppt",
          "displayName":"presentation.ppt",
          "objectType":"file"},
        "previousVersion":{
          "of":{
            "id":"urn:example:file/presentation",
            "fileUrl":"http://example.org/presentation.ppt",
            "displayName":"presentation.ppt",
            "objectType":"file"},
          "objectType":"version",
          "major":"2"},
        "objectType":"version",
        "major":"3"},
      "actor":{
        "displayName":"James",
        "objectType":"person"},
      "objectType":"activity"},
    {
      "verb":"approve",
      "object":{
        "of":{
          "id":"urn:example:file/presentation",
          "fileUrl":"http://example.org/presentation.ppt",
          "displayName":"presentation.ppt",
          "objectType":"file"},
        "previousVersion":{
          "of":{
            "id":"urn:example:file/presentation",
            "fileUrl":"http://example.org/presentation.ppt", 
            "displayName":"presentation.ppt",
            "objectType":"file"},
          "objectType":"version",
          "major":"2"},
        "objectType":"version",
        "major":"3"},
      "actor":{
        "displayName":"The Boss",
        "objectType":"person"},
      "objectType":"activity"}
  ],
  "totalItems":5,
  "objectType":"collection"
}

*/