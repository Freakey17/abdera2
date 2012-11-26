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
package org.apache.abdera2.activities.model.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.MimeType;

import org.apache.abdera2.activities.io.gson.Properties;
import org.apache.abdera2.activities.io.gson.Property;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.io.Compression;
import org.apache.abdera2.common.io.Compression.CompressionCodec;
import org.apache.abdera2.common.security.HashHelper.Hasher;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

/**
 * The BinaryObject is an extension object type that allows 
 * for an Activity Streams object representing arbitrary 
 * Base64 encoded data. It is an extension of the FileObject 
 * type so it inherits the fileUrl and mimeType properties
 * to communicate the permanent url of the data and the 
 * MIME media type. The content property is used to contain
 * the Base64 encoded data. This object is particularly 
 * useful in conjunction with the "attachments" property 
 * to attach arbitrary binary resources to another ASObject.
 * For instance, attaching a VCALENDAR file to an Event Object.
 * 
 * By default, the mime type will be set to application/octet-stream.
 * Applications need to be careful about which content types they 
 * will allow. 
 */
public class BinaryObject extends FileObject {
  
  public BinaryObject(Map<String,Object> map) {
    super(map,BinaryBuilder.class,BinaryObject.class);
  }
  
  public <X extends BinaryObject, M extends Builder<X,M>>BinaryObject(Map<String,Object> map, Class<M> _class, Class<X>_obj) {
    super(map,_class,_obj);
  }
    
  /**
   * Returns an InputStream that will decode the Base64 encoded 
   * content on the fly. The data is stored encoded in memory and
   * only decoded as the InputStream is consumed.
   */
  public InputStream getInputStream() throws IOException {
    String content = super.getProperty("data");
    if (content == null) return null;
    ByteArrayInputStream in = 
      new ByteArrayInputStream(
        content.getBytes("UTF-8"));
    InputStream bin = 
      new Base64InputStream(in);
    if (has("compression")) {
      String comp = getProperty("compression");
      bin = Compression.wrap(bin, comp);
    }
    return bin;
  }
 
  public static BinaryBuilder makeBinary() {
    return new BinaryBuilder("binary");
  }
  
  public static BinaryObject makeBinary(DataHandler data, CompressionCodec... comps) throws IOException {
    return makeBinary().data(data,comps).get();
  }
  
  public static BinaryObject makeBinary(byte[] data, Hasher hash, CompressionCodec... comps) throws IOException {
    return makeBinary().data(data,hash,comps).get();
  }
  
  public static BinaryObject makeBinary(DataHandler data,Hasher hash, CompressionCodec... comps) throws IOException {
    return makeBinary().data(data,hash,comps).get();
  }
  
  public static BinaryObject makeBinary(InputStream data) throws IOException {
    return makeBinary().data(data).get();
  }
  
  public static BinaryObject makeBinary(InputStream data, Hasher hash, CompressionCodec... comps) throws IOException {
    return makeBinary().data(data,hash,comps).get();
  }

  public static BinaryObject makeBinary(byte[] data, CompressionCodec... comps) throws IOException {
    return makeBinary().data(data,comps).get();
  }
  
  public static BinaryObject makeBinary(byte[] data, int s, int e, CompressionCodec... comps) throws IOException {
    return makeBinary().data(data,s,e,comps).get();
  }
  
  public static BinaryObject makeBinary(byte[] data, int s, int e, Hasher hash, CompressionCodec... comps) throws IOException {
    return makeBinary().data(data,s,e,hash,comps).get();
  }
  
  
  @Name("binary")
  @Properties({
    @Property(name="mimeType",to=MimeType.class)
  })
  public static final class BinaryBuilder extends Builder<BinaryObject,BinaryBuilder> {
    public BinaryBuilder() {
      super(BinaryObject.class,BinaryBuilder.class);
    }
    public BinaryBuilder(Map<String, Object> map) {
      super(map, BinaryObject.class,BinaryBuilder.class);
    }
    public BinaryBuilder(String objectType) {
      super(objectType, BinaryObject.class,BinaryBuilder.class);
    }
  }
  
  @SuppressWarnings("unchecked")
  public static class Builder<X extends BinaryObject,M extends Builder<X,M>> 
    extends FileObject.Builder<X,M> {
    
    boolean a;
    
    public Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M mimeType(MimeType mimeType) {
      a = true;
      return super.mimeType(mimeType);
    }
    
    @Override
    public M set(String name, Object val) {
      if ("mimeType".equals(name)) {
        if (a) return (M)this;
        else a = true;
      }
      return super.set(name, val);
    }
    
    public M data(DataHandler data, CompressionCodec... comps) throws IOException {
      return data(data,(Hasher)null,comps);
    }
    
    public M data(byte[] data, Hasher hash, CompressionCodec... comps) throws IOException {
      return data(new ByteArrayInputStream(data),hash,comps);
    }
    
    /**
     * Set the Content and the MimeType from the DatHandler. 
     * This method defers to the setContent(InputStream) method
     * by passing it the InputStream retrieved from the DataHandler.
     * That means it currently blocks while reading, consuming, and
     * encoding the InputStream. TODO: The better approach would be
     * to simple store the DataHandler in the exts table directly
     * and use a custom TypeAdapter for the BinaryObject to read and
     * consume the DataHandler during the actual Serialization.
     */
    public M data(DataHandler data,Hasher hash, CompressionCodec... comps) throws IOException {
      data(data.getInputStream(),hash,comps);
      if (!a) mimeType(data.getContentType());
      return (M)this;
    }
    
    public M data(InputStream data) throws IOException {
      return data(data,null);
    }
    
    /**
     * Set the Content as a Base64 Encoded string. Calling this 
     * method will perform a blocking read that will consume the 
     * InputStream and generate a Base64 Encoded String. 
     * 
     * If a Hasher class is provided, a hash digest for the 
     * input data prior to encoding will be generated and 
     * stored in a property value whose name is the value
     * returned by Hasher.name(); e.g., HashHelper.Md5 will 
     * add "md5":"{hex digest}" to the JSON object. 
     * 
     * The content may be optionally compressed prior to base64 
     * encoding by passing in one or more CompressionCodecs. If 
     * compression is used, a "compression" property will be added
     * to the object whose value is a comma separated list of 
     * the applied compression codecs in the order of application. 
     * The getInputStream method will automatically search for the 
     * "compression" property and attempt to automatically decompress
     * the stream when reading.
     * 
     * This will also automatically set the "length" property equal 
     * to the total number of uncompressed, unencoded octets.
     */
    public M data(InputStream data, Hasher hash, CompressionCodec... comps) throws IOException {
      ByteArrayOutputStream out = 
        new ByteArrayOutputStream();
      OutputStream bout = 
        new Base64OutputStream(out,true,0,null);
      if (comps.length > 0) {
        bout = Compression.wrap(bout,comps);
        String comp = Compression.describe(null, comps);
        set("compression",comp.substring(1,comp.length()-1));
      }
      byte[] d = new byte[1024];
      int r = -1, len = 0;
      while((r = data.read(d)) > -1) { 
        len += r;
        if (hash != null)
          hash.update(d, 0, r);
        bout.write(d, 0, r);
        bout.flush();
      }
      bout.close();
      set("length",len);
      String c = new String(out.toByteArray(),"UTF-8");
      set("data",c);
      if (hash != null)
        set(
          hash.name(),
          hash.get());
      return (M)this;
    }

    public M data(byte[] data, CompressionCodec... comps) throws IOException {
      return data(new ByteArrayInputStream(data),null,comps);
    }
    
    public M data(byte[] data, int s, int e, CompressionCodec... comps) throws IOException {
      return data(new ByteArrayInputStream(data,s,e),null,comps);
    }
    
    public M data(byte[] data, int s, int e, Hasher hash, CompressionCodec... comps) throws IOException {
      return data(new ByteArrayInputStream(data,s,e),hash,comps);
    }
    
    public void preGet() {
      super.preGet();
      if (!a)  mimeType("application/octet-stream");
    }
  }
}
