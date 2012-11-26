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

import java.util.Map;

import javax.activation.MimeType;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;

public class FileObject 
  extends ASObject {

  public static final String FILEURL = "fileUrl";
  
  public FileObject(Map<String,Object> map) {
    super(map,FileBuilder.class,FileObject.class);
  }
  
  public <X extends FileObject, M extends Builder<X,M>>FileObject(Map<String,Object> map, Class<M> _class, Class<X> _obj) {
    super(map,_class,_obj);
  }
  
  public IRI getFileUrl() {
    return getProperty(FILEURL);
  }
  
  public MimeType getMimeType() {
    return getProperty("mimeType");
  }
  
  public static FileBuilder makeFile() {
    return new FileBuilder("file");
  }
  
  public static FileObject makeFile(
    String displayName, 
    String fileUrl, 
    String mimeType) {
    return makeFile()
      .displayName(displayName)
      .fileUrl(fileUrl)
      .mimeType(mimeType)
      .get();
  }
  
  public static FileObject makeFile(
    String displayName, 
    IRI fileUrl, 
    String mimeType) {
    return makeFile()
      .displayName(displayName)
      .fileUrl(fileUrl)
      .mimeType(mimeType)
      .get();
  }
  
  public static FileObject makeFile(
    String displayName, 
    String fileUrl, 
    MimeType mimeType) {
    return makeFile()
      .displayName(displayName)
      .fileUrl(fileUrl)
      .mimeType(mimeType)
      .get();
  }
  
  public static FileObject makeFile(
    String displayName, 
    IRI fileUrl, 
    MimeType mimeType) {
    return makeFile()
      .displayName(displayName)
      .fileUrl(fileUrl)
      .mimeType(mimeType)
      .get();
  }
  
  @Name("file")
  public static final class FileBuilder extends Builder<FileObject,FileBuilder> {
    public FileBuilder() {
      super(FileObject.class,FileBuilder.class);
    }
    public FileBuilder(Map<String, Object> map) {
      super(map, FileObject.class,FileBuilder.class);
    }
    public FileBuilder(String objectType) {
      super(objectType, FileObject.class,FileBuilder.class);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends FileObject, M extends Builder<X,M>>
    extends ASObject.Builder<X,M> {
    public Builder(Class<X> _class, Class<M> _builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X> _class, Class<M> _builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X> _class, Class<M> _builder) {
      super(map,_class,_builder);
    }
    public M fileUrl(IRI iri) {
      set(FILEURL,iri);
      try {
        if (isExperimentalEnabled())
          link("enclosure",iri);
      } catch (IllegalStateException e) {}
      return (M)this;
    }
    public M fileUrl(String uri) {
      return fileUrl(new IRI(uri));
    }
    public M mimeType(MimeType mimeType) {
      set("mimeType",MimeTypeHelper.unmodifiableMimeType(mimeType));
      return (M)this;
    }
    public M mimeType(String mimeType) {
      set("mimeType",MimeTypeHelper.unmodifiableMimeType(mimeType));
      return (M)this;
    }
  }
}
