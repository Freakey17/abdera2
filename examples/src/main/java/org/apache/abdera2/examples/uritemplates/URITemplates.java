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
package org.apache.abdera2.examples.uritemplates;

import java.util.Iterator;
import java.util.List;

import org.apache.abdera2.common.templates.CachingContext;
import org.apache.abdera2.common.templates.MapContext;
import org.apache.abdera2.common.templates.Template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;


@SuppressWarnings("unchecked")
public final class URITemplates {

    private static final Template template =
        new Template("http://example.org/~{user}{/categories}{?foo,bar}");

    public static void main(String... args) throws Exception {

        exampleWithObject();
        exampleIRIWithObject();
        exampleWithMap();
        exampleWithHashMapContext();
        exampleWithCustomContext();

    }

    // Using a Java object
    private static void exampleWithObject() {
      System.out.println(template.expand(new MyObject()));
    }

    // Using a Java object
    private static void exampleIRIWithObject() {
      System.out.println(template.expand(new MyObject(), true));
    }

    // Using a Map
    private static void exampleWithMap() {
      Multimap<String, Object> map = 
        LinkedHashMultimap.create();
      map.put("user", "james");
      map.put("categories", "a");
      map.put("categories", "b");
      map.put("categories", "c");
      map.put("foo", "abc");
      map.put("bar", "xyz");
      System.out.println(template.expand(map));
    }

    // Using a HashMap based context
    private static void exampleWithHashMapContext() {
      MapContext context = new MapContext();
      context.put("user", "james");
      context.put("categories", new String[] {"a", "b", "c"});
      context.put("foo", "abc");
      context.put("bar", "xyz");
      System.out.println(template.expand(context));
    }

    // Using a custom context implementation
    private static void exampleWithCustomContext() {
      CachingContext context = new CachingContext(false) {
        private static final long serialVersionUID = 4896250661828139020L;
        protected <T> T resolveActual(String var) {
          if (var.equals("user"))
            return (T)"james";
          else if (var.equals("categories"))
            return (T)new String[] {"a", "b", "c"};
          else if (var.equals("foo"))
            return (T)"abc";
          else if (var.equals("bar"))
            return (T)"xyz";
          else
            return null;
        }
        public Iterator<String> iterator() {
          return Iterators.forArray("user","categories","foo","bar");
        }
        public boolean contains(String var) {
          return resolveActual(var) != null;
        }
      };
      System.out.println(template.expand(context));
    }

    public static class MyObject {
      public String user = "james";
      public List<String> getCategories() {
        return ImmutableList.of("a","b","c");
      }
      public Foo[] getFoo() {
        return new Foo[] {new Foo(), new Foo()};
      }
      public String getBar() {
        return "xyz";
      }
    }
    private static class Foo {
      public String toString() {
        return "abcæ";
      }
    }
}
