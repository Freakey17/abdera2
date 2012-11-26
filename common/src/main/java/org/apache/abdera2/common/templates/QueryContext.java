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
package org.apache.abdera2.common.templates;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.misc.Pair;

import com.google.common.collect.ImmutableMap;

import static java.util.Collections.addAll;
import static org.apache.abdera2.common.text.CharUtils.*;
import static com.google.common.collect.Lists.newArrayList;

import static com.google.common.base.Preconditions.*;

/**
 * Constructs a mutable Template Context based on an existing IRI/URI 
 * Query String. This can be used to construct new IRI's based on an
 * existing IRI -- for instance, when needing to construct an IRI that 
 * contains all of the same querystring parameters as the original 
 * IRI or when modifying querystring parameter values.
 */
public class QueryContext extends MapContext  {

  private static final long serialVersionUID = -3083469437683051678L;

  public QueryContext(IRI iri) {
    super(parse(iri));
  }
  
  public QueryContext(String iri) {
    super(parse(new IRI(iri)));
  }
  
  public Template getTemplate(boolean fragment, Context additionalParams) {
    Context context = this;
    if (additionalParams != null)
      context = new DefaultingContext(context,additionalParams);
    return QueryContext.templateFromContext(context, fragment);
  }
  
  public Template getTemplate(boolean fragment) {
    return QueryContext.templateFromContext(this, fragment);
  }
  
  public Template getTemplate() {
    return QueryContext.templateFromContext(this, false);
  }
  
  public String expand(boolean fragment) {
    return getTemplate(fragment).expand(this);
  }
  
  public String expand() {
    return getTemplate(false).expand(this);
  }
  
  public String expand(boolean fragment, Context additionalParams) {
    Context context = this;
    if (additionalParams != null)
      context = new DefaultingContext(context,additionalParams);
    return getTemplate(fragment,additionalParams).expand(context);
  }
  
  public String expand(Context additionalParams) {
    return expand(false,additionalParams);
  }
  
  private static Map<String,Object> parse(IRI iri) {
    Map<String,Object> map = new HashMap<String,Object>();
    String query = checkNotNull(iri).getQuery();
    if (query != null)
      for (Pair<String,String> pair : Pair.from(query,"&"))
        setval(map,pair.first(),pair.second());
    return ImmutableMap.copyOf(map);
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static void setval(Map<String,Object> map, String key, String val) {
    if (checkNotNull(map).containsKey(key)) {
      Object value = map.get(key);
      if (value instanceof Collection)
        addAll((Collection)value, val);
      else
        map.put(key, newArrayList(value,val));
    } else map.put(key,val);
  }
  
  public static Template templateFromContext(Context context, boolean fragment) {
    checkNotNull(context);
    StringBuilder buf = new StringBuilder();
    buf.append('{').append(fragment?'&':'?');
    boolean first = true;
    for (String name : context) {
      first = appendcomma(first,buf);
      buf.append(name);
      Object val = context.resolve(name);
      appendif(val == null, buf, "^");
      appendif(val instanceof List, buf, "*");
    }
    buf.append('}');
    return new Template(buf.toString());
  }
  
  public static Template templateFromIri(IRI iri) {
    return templateFromQuery(checkNotNull(iri).toString(), false, null);
  }
  
  public static Template templateFromIri(IRI iri, Context additionalParams) {
    return templateFromQuery(checkNotNull(iri).toString(), false, additionalParams);
  }
  
  public static Template templateFromQuery(String query, boolean fragment, Context additionalParams) {
    Context context = new QueryContext(checkNotNull(query));
    if (additionalParams != null)
      context = new DefaultingContext(context,additionalParams);
    StringBuilder buf = new StringBuilder(baseFromQuery(query));
    buf.append(templateFromContext(context,fragment));
    return new Template(buf.toString());
  }
  
  public static String baseFromQuery(String query) {
    IRI iri = new IRI(checkNotNull(query)).normalize();
    String s = iri.resolve(iri.getPath()).toString();
    return s;
  }
  
  public static String expandQuery(String query, Context context) {
    return expandQuery(
      checkNotNull(query),
      checkNotNull(context),
      (Template)null);
  }
  
  public static String expandQuery(String query, Context context, String extender) {
    return expandQuery(
      checkNotNull(query),
      checkNotNull(context),
      new Template(checkNotNull(extender)));
  }
  
  public static String expandQuery(String query, Context context, Template extender) {
    QueryContext qc = new QueryContext(checkNotNull(query));
    DefaultingContext dc = new DefaultingContext(checkNotNull(context),qc);
    Template temp = QueryContext.templateFromQuery(query, false, qc);
    return extender != null ?
      temp.extend(extender).expand(dc) :
      temp.expand(dc);
  }
}
