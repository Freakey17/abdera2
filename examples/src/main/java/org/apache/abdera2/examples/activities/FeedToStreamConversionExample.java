package org.apache.abdera2.examples.activities;

import java.net.URL;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.parser.ParserOptions;

public class FeedToStreamConversionExample {

  public static void main(String... args) throws Exception {
    
    // this example converts an Atom Feed to a
    // JSON Activity Stream. the call to 
    // feed.writeTo("activity", ...) engages
    // the named writer instance that handles
    // the transformation.
    Abdera abdera = Abdera.getInstance();
    URL url = new URL("http://intertwingly.net/blog/index.atom");
    Parser parser = abdera.getParser();
    ParserOptions options = 
      parser.makeDefaultParserOptions().charset("UTF-8").get();
    Document<Feed> doc = abdera.getParser().parse(url.openStream(),url.toString(),options);
    Feed feed = doc.getRoot();    
    feed.writeTo("activity", System.out);
    
  }
  
}
