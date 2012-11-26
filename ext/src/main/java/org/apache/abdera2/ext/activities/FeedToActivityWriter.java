package org.apache.abdera2.ext.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.activities.model.CollectionWriter;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.io.Compression;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.writer.AbstractWriter;
import org.apache.abdera2.writer.Writer;
import org.apache.abdera2.writer.WriterOptions;

import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.*;

@Name("activity")
public class FeedToActivityWriter 
  extends AbstractWriter 
  implements Writer {

  private static final FeedToActivityConverter f2ac = 
    new FeedToActivityConverter();

  public FeedToActivityWriter(Abdera abdera) {
    super(
      "application/json", 
      "application/javascript", 
      "application/ecmascript", 
      "text/javascript", 
      "text/ecmascript");
  }
  
  public void writeTo(
    Base base, 
    OutputStream out, 
    WriterOptions options)
      throws IOException {
    checkNotNull(base);
    checkNotNull(out);
    checkNotNull(options);
    if (base instanceof Document) {
      base = ((Document<?>)base).getRoot();
      checkNotNull(base);
    }
    checkArgument(base instanceof Feed || base instanceof Entry);
    if (!Iterables.isEmpty(options.getCompressionCodecs())) 
      out = Compression.wrap(out, options.getCompressionCodecs());
    if (base instanceof Entry) {
       f2ac.convert((Entry)base).writeTo(out,options.getCharset());
    } else if (base instanceof Feed) {
      CollectionWriter cw = 
        IO.get().getCollectionWriter(
          out,options.getCharset());
      f2ac.convert((Feed)base, cw);
    }
    if (options.getAutoClose())
      out.close();
  }

  public void writeTo(
    Base base, 
    java.io.Writer out, 
    WriterOptions options)
      throws IOException {
    checkNotNull(base);
    checkNotNull(out);
    checkNotNull(options);
    checkArgument(base instanceof Feed || base instanceof Entry);
    if (base instanceof Entry) {
       f2ac.convert((Entry)base).writeTo(out);
    } else if (base instanceof Feed) {
      CollectionWriter cw = 
        IO.get().getCollectionWriter(out);
      f2ac.convert((Feed)base, cw);
    }
    if (options.getAutoClose())
      out.close();
  }

  public Object write(Base base, WriterOptions options) throws IOException {
    try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeTo(base, out, options);
        return new String(out.toByteArray(), options.getCharset());
    } catch (IOException i) {
        throw i;
    } catch (Exception e) {
        throw new IOException(e.getMessage());
    }
  }

  protected WriterOptions.Builder initDefaultWriterOptions() {
    return WriterOptions.make();
  }

}
