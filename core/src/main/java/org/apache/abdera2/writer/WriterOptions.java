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

import org.apache.abdera2.common.io.Compression.CompressionCodec;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

public class WriterOptions {

  public static Builder make() {
    return new Builder();
  }
  
  public static Builder from(WriterOptions options) {
    Builder builder = new Builder();
    builder.charset = options.charset;
    builder.autoclose = options.autoclose;
    builder.codecs.addAll(options.codecs);
    return builder;
  }
  
  public static class Builder implements Supplier<WriterOptions> {
    
    protected String charset = "UTF-8";
    protected ImmutableSet.Builder<CompressionCodec> codecs = 
      ImmutableSet.builder();
    protected boolean autoclose = false;
    
    public Builder charset(String charset) {
      this.charset = charset;
      return this;
    }
    
    public Builder compression(CompressionCodec codec) {
      this.codecs.add(codec);
      return this;
    }
    
    public Builder autoclose() {
      this.autoclose = true;
      return this;
    }
    
    public Builder doNotAutoclose() {
      this.autoclose = false;
      return this;
    }
    
    public WriterOptions get() {
      return new WriterOptions(this);
    }
    
    
  }
  
  private final String charset;
  private final ImmutableSet<CompressionCodec> codecs;
  private final boolean autoclose;
  
  WriterOptions(Builder builder) {
    this.charset = builder.charset;
    this.codecs = builder.codecs.build();
    this.autoclose = builder.autoclose;
  }
  
    /**
     * When writing, use the specified compression codecs
     */
    public Iterable<CompressionCodec> getCompressionCodecs() {
      return codecs;
    }

    /**
     * The character encoding to use for the output
     */
    public String getCharset() {
      return charset;
    }

    /**
     * True if the writer should close the output stream or writer when finished
     */
    public boolean getAutoClose() {
      return autoclose;
    }

}
