package org.apache.abdera2.ext.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;

import javax.xml.stream.XMLStreamReader;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.parser.AbstractParser;
import org.apache.abdera2.parser.ParseException;
import org.apache.abdera2.parser.ParserOptions;
import org.apache.abdera2.writer.StreamWriter;
import static com.google.common.base.Preconditions.*;

@Name("activity")
public class ActivityToFeedParser 
  extends AbstractParser {

  private static final ActivityToFeedConverter a2fc = 
    new ActivityToFeedConverter();

  private final IO io;
  
  public ActivityToFeedParser() {
    this(null);
  }

  public ActivityToFeedParser(Abdera abdera) {
    super(abdera);
    io = IO.get();
  }

  protected ParserOptions.Builder initDefaultParserOptions() {
    return ParserOptions.make();
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element> Document<T> parse(
    Reader in, 
    String base,
    ParserOptions options) 
      throws ParseException {
    
    // first, we read it in as an activity stream thingy
    ASBase as = io.read(in);
    checkNotNull(as);
    checkArgument(as instanceof Collection || as instanceof Activity);
    StreamWriter sw = abdera.create(StreamWriter.class);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    sw.setAutoclose(true)
      .setOutputStream(out,"UTF-8");
    if (as instanceof Collection) {
      a2fc.convert((Collection<Activity>)as,sw);
    } else {
      a2fc.convert((Activity)as,sw);
    }
    ByteArrayInputStream input = 
      new ByteArrayInputStream(out.toByteArray());
    return abdera.getParser().parse(input);
  }

  public <T extends Element> Document<T> parse(
    XMLStreamReader reader,
    String base, 
    ParserOptions options) 
      throws ParseException {
    throw new UnsupportedOperationException();
  }

}
