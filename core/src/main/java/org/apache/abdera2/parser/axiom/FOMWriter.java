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
package org.apache.abdera2.parser.axiom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.writer.AbstractWriter;
import org.apache.abdera2.writer.WriterOptions;

@Name("default")
public class FOMWriter extends AbstractWriter {

    public FOMWriter() {
      super(
        Constants.ATOM_MEDIA_TYPE, 
        Constants.APP_MEDIA_TYPE, 
        Constants.CAT_MEDIA_TYPE,
        Constants.XML_MEDIA_TYPE);
    }

    public FOMWriter(Abdera abdera) {
      super(
          Constants.ATOM_MEDIA_TYPE, 
          Constants.APP_MEDIA_TYPE, 
          Constants.CAT_MEDIA_TYPE,
          Constants.XML_MEDIA_TYPE);
    }
    
    public void writeTo(Base base, OutputStream out, WriterOptions options) throws IOException {
        out = getCompressedOutputStream(out, options);
        String charset = options.getCharset();
        if (charset == null) {
            if (base instanceof Document)
                charset = ((Document<?>)base).getCharset();
            else if (base instanceof Element) {
                Document<?> doc = ((Element)base).getDocument();
                if (doc != null)
                    charset = doc.getCharset();
            }
            if (charset == null)
                charset = "UTF-8";
        } else {
            Document<?> doc = null;
            if (base instanceof Document)
                doc = (Document<?>)base;
            else if (base instanceof Element)
                doc = ((Element)base).getDocument();
            if (doc != null)
                doc.setCharset(charset);
        }
        base.writeTo(new OutputStreamWriter(out, charset));
        finishCompressedOutputStream(out, options);
        if (options.getAutoClose())
            out.close();
    }

    public void writeTo(Base base, Writer out, WriterOptions options) throws IOException {
        base.writeTo(out);
        if (options != null && options.getAutoClose())
            out.close();
    }

    public Object write(Base base, WriterOptions options) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeTo(base, out, options);
        return out.toString();
    }

    @Override
    protected WriterOptions.Builder initDefaultWriterOptions() {
        return WriterOptions.make();
    }

}
