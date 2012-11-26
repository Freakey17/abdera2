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

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class QuestionObject 
  extends ASObject {

  public static final String OPTIONS = "options";
  
  public QuestionObject(Map<String,Object> map) {
    super(map,QuestionBuilder.class,QuestionObject.class);
  }
  
  public <X extends QuestionObject, M extends Builder<X,M>>QuestionObject(Map<String,Object> map, Class<M> _class,Class<X>_obj) {
    super(map,_class,_obj);
  }
  
  public Iterable<ASObject> getOptions() {
    return getProperty(OPTIONS);
  }
  
  public static QuestionBuilder makeQuestion() {
    return new QuestionBuilder("question");
  }
  
  public static QuestionObject makeQuestion(
    String displayName,
    String summary, 
    ASObject author,
    ASObject... options) {
    return makeQuestion()
      .displayName(displayName)
      .summary(summary)
      .author(author)
      .option(options).get();
  }
  
  public static QuestionObject makeQuestion(
    String displayName,
    String summary, 
    Supplier<? extends ASObject> author,
    Supplier<? extends ASObject>... options) {
    return makeQuestion()
      .displayName(displayName)
      .summary(summary)
      .author(author)
      .option(options).get();
  }
  
  public static QuestionObject makeQuestion(
    String displayName,
    String summary, 
    ASObject author,
    Iterable<ASObject> options) {
    return makeQuestion()
      .displayName(displayName)
      .summary(summary)
      .author(author)
      .option(options).get();
  }

  @Name("question")
  public static final class QuestionBuilder extends Builder<QuestionObject,QuestionBuilder> {

    public QuestionBuilder() {
      super(QuestionObject.class,QuestionBuilder.class);
    }

    public QuestionBuilder(Map<String, Object> map) {
      super(map,QuestionObject.class,QuestionBuilder.class);
    }

    public QuestionBuilder(String objectType) {
      super(objectType,QuestionObject.class,QuestionBuilder.class);
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder <X extends QuestionObject, M extends Builder<X,M>>
    extends ASObject.Builder<X,M> {
    
    private ImmutableSet.Builder<ASObject> options = 
      ImmutableSet.builder();
    boolean a;
    
    protected Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    protected Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    protected Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M option(Iterable<? extends ASObject> options) {
      if (Iterables.isEmpty(options)) return (M)this;
      for (ASObject option : options)
        option(option);
      return (M)this;
    }
    public M option(Supplier<? extends ASObject>... options) {
      if (options == null) return (M)this;
      for (Supplier<? extends ASObject> option : options)
        option(option.get());
      return (M)this;
    }
    public M option(ASObject... options) {
      if (options == null) return (M)this;
      for (ASObject option : options)
        option(option);
      return (M)this;
    }
    public M option(Supplier<? extends ASObject> object) {
      return option(object.get());
    }
    public M option(ASObject object) {
      if (object == null) return (M)this;
      a = true;
      options.add(object);
      return (M)this;
    }
    public void preGet() {
      super.preGet();
      if (a) set(OPTIONS, options.build());
    }
  }
}

