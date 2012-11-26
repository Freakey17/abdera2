/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera2.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;

import org.apache.abdera2.common.io.Compression;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;

import com.google.common.collect.Iterables;

public abstract class AbstractWriter implements Writer {

    protected WriterOptions options;
    
    protected final Set<String> formats = 
      new HashSet<String>();
    
    protected AbstractWriter() {}
    
    protected AbstractWriter(String... formats) {
      for (String format : formats)
        this.formats.add(format);
    }

    public synchronized WriterOptions getDefaultWriterOptions() {
      if (options == null)
          options = initDefaultWriterOptions().get();
      return options;
    }
    
    public WriterOptions.Builder makeDefaultWriterOptions() {
      return initDefaultWriterOptions();
    }

    protected abstract WriterOptions.Builder initDefaultWriterOptions();

    public synchronized Writer setDefaultWriterOptions(WriterOptions options) {
        this.options = options != null ? options : initDefaultWriterOptions().get();
        return this;
    }

    public Object write(Base base) throws IOException {
        return write(base, getDefaultWriterOptions());
    }

    public void writeTo(Base base, OutputStream out) throws IOException {
        writeTo(base, out, getDefaultWriterOptions());
    }

    public void writeTo(Base base, java.io.Writer out) throws IOException {
        writeTo(base, out, getDefaultWriterOptions());
    }

    protected OutputStream getCompressedOutputStream(OutputStream out, WriterOptions options) throws IOException {
      if (options.getCompressionCodecs() != null)
        out = Compression.wrap(out, options.getCompressionCodecs());
      return out;
    }

    protected void finishCompressedOutputStream(OutputStream out, WriterOptions options) throws IOException {
      if (!Iterables.isEmpty(options.getCompressionCodecs()))
        ((DeflaterOutputStream)out).finish();
    }

    public void writeTo(Base base, WritableByteChannel out, WriterOptions options) throws IOException {
        String charset = options.getCharset();
        if (charset == null) {
            Document<?> doc = null;
            if (base instanceof Document)
                doc = (Document<?>)base;
            else if (base instanceof Element) {
                doc = ((Element)base).getDocument();
            }
            charset = doc != null ? doc.getCharset() : null;
        }
        writeTo(base, Channels.newWriter(out, charset != null ? charset : "utf-8"), options);
    }

    public void writeTo(Base base, WritableByteChannel out) throws IOException {
        writeTo(base, out, getDefaultWriterOptions());
    }

    public Iterable<String> getOutputFormats() {
      return formats;
  }

  public boolean outputsFormat(String mediatype) {
      for (String format : formats) {
          if (MimeTypeHelper.isMatch(format, mediatype))
              return true;
      }
      return false;
  }
}
