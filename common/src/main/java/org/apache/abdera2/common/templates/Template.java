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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.anno.URITemplate;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.templates.Context;
import org.apache.abdera2.common.templates.Expression;
import org.apache.abdera2.common.templates.MapContext;
import org.apache.abdera2.common.templates.ObjectContext;
import org.apache.abdera2.common.templates.Template;
import org.apache.abdera2.common.templates.Expression.VarSpec;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import static com.google.common.base.Preconditions.*;

/**
 * A URI Template as defined by (http://datatracker.ietf.org/doc/draft-gregorio-uritemplate/) 
 * 
 * URI Templates are special strings that can be expanded into either 
 * URI's or IRI's given an input context. For example, 
 * 
 *   http://{user}.example.org{/path}{?a,b}{#c}
 * 
 * Given the values:
 * 
 *   user = john
 *   path = [x,y,z]
 *   a    = 1
 *   b    = 2
 *   c    = abc
 *   
 * This URI Template expands to:
 * 
 *   http://john.example.org/x/y/z?a=1&b=2#abc
 * 
 * Template objects are threadsafe and immutable once created, and can be 
 * safely initialized as final static variables and used throughout an 
 * application
 */
@SuppressWarnings("unchecked")
public final class Template 
  implements Iterable<Expression>, 
             Serializable {

    private static final long serialVersionUID = -613907262632631896L;

    private static final Pattern EXPRESSION = Pattern.compile("\\{[^{}]+\\}");
    private static final String EXP_START = "\\{";
    private static final String EXP_STOP = "\\}";

    private final String pattern;
    private final Iterable<Expression> expressions;
    private final Iterable<String> variables;

    /**
     * @param pattern A URI Template
     */
    public Template(String pattern) {
      checkNotNull(pattern, "Template pattern must not be null");
      this.pattern = pattern;
      this.expressions = initExpressions();
      this.variables = initVariables(expressions);
    }
    
    private static Iterable<String> initVariables(Iterable<Expression> expressions) {
      ImmutableSet.Builder<String> builder = 
        ImmutableSet.builder();
      for (Expression exp : expressions)
        for (VarSpec spec : exp)
          builder.add(spec.getName());
      return builder.build();
    }
    
    public Template(Object object) {
      this(extractPattern(object));
    }

    private static String extractPattern(Object object) {
      if (object == null)
        return null;
      if (object instanceof String)
        return (String)object;
      else if (object instanceof Template)
        return ((Template)object).pattern;
      Class<?> _class = object instanceof Class ? (Class<?>)object : object.getClass();
      URITemplate uriTemplate = (URITemplate)_class.getAnnotation(URITemplate.class);
      String pattern = 
        uriTemplate != null ?
           uriTemplate.value() :
           object instanceof TemplateProvider ? 
             ((TemplateProvider)object).get() : 
             null;
      checkNotNull(pattern);
      return pattern;
    }

    /**
     * Iterate the template expressions
     */
    public Iterator<Expression> iterator() {
        return expressions.iterator();
    }

    /**
     * Return the array of template variables
     */
    private Iterable<Expression> initExpressions() {
      ImmutableList.Builder<Expression> expressions = 
        ImmutableList.builder();
      Matcher matcher = EXPRESSION.matcher(pattern);
      while (matcher.find()) {
        String token = matcher.group();
        token = token.substring(1, token.length() - 1);
        Expression exp = new Expression(token);
        expressions.add(exp);
      }
      return expressions.build();
    }

    /**
     * Return the array of template variables
     */
    public Iterable<String> getVariables() {
      return variables;
    }

    /**
     * Expand the URI Template using the specified Context.
     * 
     * @param context The Context impl used to resolve variable values
     * @return An expanded URI
     */
    public String expand(Context context) {
      String pattern = this.pattern;
      for (Expression exp : this)
          pattern = 
            replace(
              pattern, 
              exp, 
              exp.evaluate(context));
      return pattern;
    }
    
    public String expand(Supplier<Context> context) {
      if (context == null) return null;
      return expand(context.get());
    }

    /**
     * Expand the URI Template using the non-private fields and methods of the specified object to resolve the template
     * tokens
     */
    public String expand(Object object) {
      if (object == null) return null;
      if (object instanceof Supplier)
        object = ((Supplier<?>)object).get();
        return expand(object, false);
    }

    /**
     * Expand the template using the non-private fields and methods of the specified object to resolve the template
     * tokens. If isiri is true, IRI escaping rules will be used.
     */
    public String expand(Object object, boolean isiri) {
      if (object == null) return null;
      if (object instanceof Supplier)
        object = ((Supplier<?>)object).get();
      return expand(asContext(object,isiri));
    }

    private String replace(String pattern, Expression exp, String value) {
        return pattern.replaceAll(EXP_START + Pattern.quote(exp.toString()) + EXP_STOP, value);
    }

    @Override
    public int hashCode() {
      return MoreFunctions.genHashCode(1, pattern);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Template other = (Template)obj;
        if (pattern == null) {
            if (other.pattern != null)
                return false;
        } else if (!pattern.equals(other.pattern))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return pattern;
    }

    public static String expand(String pattern, Supplier<Context> context) {
      checkNotNull(pattern);
      checkNotNull(context);
      return expand(pattern,context.get());
    }
    
    public static String expand(String pattern, Context context) {
        checkNotNull(context);
        checkNotNull(pattern);
        return new Template(pattern).expand(context);
    }

    public static String expand(String pattern, Object object) {
        return expand(pattern, object, false);
    }

    public static String expand(String pattern, Object object, boolean isiri) {
        checkNotNull(pattern);
        checkNotNull(object);
        return new Template(pattern).expand(object, isiri);
    }

    public static String expandAnnotated(Object object) {
      return expandAnnotated(object,null);
    }
    
    @SuppressWarnings("rawtypes")
    private static Context asContext(Object obj, boolean isiri) {
      checkNotNull(obj);
      return 
        obj instanceof Context ? 
          (Context)obj : 
         obj instanceof Map ? 
           new MapContext((Map)obj, isiri) : 
           obj instanceof Multimap ?
             new MapContext(((Multimap)obj).asMap()) :
             new ObjectContext(obj, isiri);
    }
    
    /**
     * Use an Object annotated with the URITemplate annotation to expand a template
     */
    public static String expandAnnotated(Object object, Object additional) {
        checkNotNull(object);
        Object contextObject = null;
        Class<?> _class = null;
        if (object instanceof Class<?>) {
          _class = (Class<?>)object;
          contextObject = new AnnotationContext(_class);
        } else {
          _class = object.getClass();
          contextObject = object;
          if (_class.isAnnotationPresent(org.apache.abdera2.common.anno.Context.class)) {
            additional = new AnnotationContext(_class);
          }
        }
        URITemplate uritemplate = (URITemplate)_class.getAnnotation(URITemplate.class);
        checkNotNull(uritemplate, "No URI Template Provided");
        if (additional != null) {
          Context add = asContext(additional, uritemplate.isiri());
          Context main = asContext(contextObject, uritemplate.isiri());
          contextObject = new DefaultingContext(add,main);
        }
        return expand(
          uritemplate.value(), 
          contextObject, 
          uritemplate.isiri());
    }
    
    public static Context getAnnotatedContext(Object object) {
      return new AnnotationContext(object);
    }

    /**
     * Create a new Template by appending the given template to this
     */
    public Template extend(Template template) {
      StringBuilder buf = new StringBuilder(pattern);
      if (template != null)
        buf.append(template.pattern);
      return new Template(buf.toString());
    }
    
    public Template extend(String template) {
      StringBuilder buf = new StringBuilder(pattern);
      if (template != null)
        buf.append(template);
      return new Template(buf.toString());
    }
    
    public Supplier<String> supplierFor(Object context) {
      return new TSupplier(this,context);
    }
    
    public Function<Object,String> asFunction() {
      return new TFunction(this);
    }
    
    private static class TSupplier implements Supplier<String> {
      private final Template template;
      private final Object context;
      TSupplier(Template template, Object context) {
        this.template = template;
        this.context = context;
      }
      public String get() {
        return template.expand(context);
      }
    }
    
    private static class TFunction implements Function<Object,String> {
      private final Template template;
      TFunction(Template template) {
        this.template = template;
      }
      public String apply(Object object) {
        return template.expand(object);
      }
    }
}
