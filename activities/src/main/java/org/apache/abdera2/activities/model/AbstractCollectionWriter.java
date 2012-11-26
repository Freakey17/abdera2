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
package org.apache.abdera2.activities.model;

/**
 * Base implementation of the CollectionWriter interface.. handles
 * basic flow and state management
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCollectionWriter 
  implements CollectionWriter {

  protected boolean _items = false;
  protected boolean _header = false;
  
  protected void flush() {}
  
  protected abstract void write(String name, Object val);
  
  protected abstract void startItems();
  
  protected abstract void writeItem(ASObject object);
  
  public abstract void complete();
  
  public <X extends CollectionWriter>X writeHeader(ASBase.Builder<?,?> base) {
    return (X) writeHeader(base.get());
  }
  
  public <X extends CollectionWriter>X writeHeader(ASBase base) {
    if (_items || _header)
    throw new IllegalStateException();
    if (base != null) {
      for (String name : base) {
        if (!"items".equals(name)) {
          Object val = base.getProperty(name);
          write(name,val);
        }
      }
    }
    _header = true;
    flush();
    return (X)this;
  }
  
  public <X extends CollectionWriter>X writeObject(ASObject.Builder<?, ?> object) {
    return (X) writeObject(object.get());
  }
  
  public <X extends CollectionWriter>X writeObject(ASObject object) {
    if (!_items) {
      startItems();
      _items = true;
    }
    writeItem(object);
    flush();
    return (X)this;
  }
  
  public <X extends CollectionWriter>X writeObjects(ASObject... objects) {
    for (ASObject object : objects)
      writeObject(object);
    return (X)this;
  }
  
  public <X extends CollectionWriter>X writeObjects(Iterable<ASObject> objects) {
    for (ASObject object : objects)
      writeObject(object);
    return (X)this;
  }
 
}
