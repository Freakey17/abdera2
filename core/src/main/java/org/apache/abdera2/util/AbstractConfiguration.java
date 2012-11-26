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
package org.apache.abdera2.util;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.factory.ExtensionFactory;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.writer.StreamWriter;
import org.apache.abdera2.writer.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractConfiguration implements Constants, Configuration {

    private static final long serialVersionUID = 7460203853824337559L;
    private final static Log log = LogFactory.getLog(AbstractConfiguration.class);

    private static ResourceBundle getBundle(Locale locale) {
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle(
              "abdera", locale, 
              Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            // Do nothing
        }
        return bundle;
    }

    protected final Abdera abdera;
    protected final ResourceBundle bundle;
    protected final Set<ExtensionFactory> factories;
    protected final Map<String, Writer> writers;
    protected final Map<String, Class<? extends StreamWriter>> streamwriters;
    protected final Map<String, Parser> parsers;

    public AbstractConfiguration(Abdera abdera) {
        this(null,abdera);
    }

    protected AbstractConfiguration(ResourceBundle bundle, Abdera abdera) {
        this.abdera = abdera;
        this.bundle = (bundle != null) ? bundle : 
          AbstractConfiguration.getBundle(Locale.getDefault());
        factories = loadExtensionFactories();
        writers = initNamedWriters();
        parsers = initNamedParsers();
        streamwriters = initStreamWriters();
    }

    protected abstract Set<ExtensionFactory> loadExtensionFactories();

    protected ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Retrieve the value of the specified configuration option
     * 
     * @return The configuration option value or null
     */
    public String getConfigurationOption(String id) {
        String option = System.getProperty(id);
        if (option == null) {
            try {
                ResourceBundle bundle = getBundle();
                if (bundle != null)
                    option = bundle.getString(id);
            } catch (Exception e) {
                // Do Nothing
            }
        }
        return option;
    }

    /**
     * Retrieve the value of the specified configuration option or _default if the value is null
     * 
     * @return The configuration option value of _default
     */
    public String getConfigurationOption(String id, String _default) {
        String value = getConfigurationOption(id);
        return (value != null) ? value : _default;
    }

    /**
     * Registers an ExtensionFactory implementation.
     */
    public AbstractConfiguration addExtensionFactory(ExtensionFactory factory) {
        this.factories.add(factory);
        return this;
    }

    /**
     * Returns the listing of registered ExtensionFactory implementations
     */
    public Iterable<ExtensionFactory> getExtensionFactories() {
        return factories;
    }

    /**
     * Registers a NamedWriter implementation
     */
    public AbstractConfiguration addWriter(Writer writer) {
        Map<String, Writer> writers = getWriters();
        String name = AnnoUtil.getName(writer);
        if (!writers.containsKey(name)) {
            writers.put(name, writer);
        } else {
            log.warn("The NamedWriter is already registered: " + name);
        }
        return this;
    }

    /**
     * Registers NamedWriter implementations using the /META-INF/services/org.apache.abdera.writer.NamedWriter file
     */
    protected abstract Map<String, Writer> initNamedWriters();

    /**
     * Registers StreamWriter implementations using the /META-INF/services/org.apache.abdera.writer.StreamWriter file
     */
    protected abstract Map<String, Class<? extends StreamWriter>> initStreamWriters();

    /**
     * Returns the collection of NamedWriters
     */
    public Map<String, Writer> getWriters() {
        return writers;
    }

    /**
     * Returns the collection of NamedWriters
     */
    public Map<String, Class<? extends StreamWriter>> getStreamWriters() {
        return streamwriters;
    }

    /**
     * Registers a NamedParser implementation
     */
    public AbstractConfiguration addParser(Parser parser) {
        Map<String, Parser> parsers = getParsers();
        String name = AnnoUtil.getName(parser);
        if (!parsers.containsKey(name)) {
            parsers.put(name, parser);
        } else {
            log.warn("The NamedParser is already registered: " + name);
        }
        return this;
    }

    /**
     * Registers a StreamWriter implementation
     */
    public AbstractConfiguration addStreamWriter(Class<? extends StreamWriter> sw) {
        Map<String, Class<? extends StreamWriter>> streamWriters = getStreamWriters();
        String swName = AnnoUtil.getName(sw);
        if (!streamWriters.containsKey(swName)) {
            streamWriters.put(swName, sw);
        } else {
            log.warn("The StreamWriter is already registered: " + swName);
        }
        return this;
    }

    /**
     * Registers NamedParser implementations using the /META-INF/services/org.apache.abdera.writer.NamedParser file
     */
    protected abstract Map<String, Parser> initNamedParsers();

    /**
     * Returns the collection of Named Parsers
     */
    public Map<String, Parser> getParsers() {
        return parsers;
    }

    public <T>T newInstance(Abdera abdera, Class<T> _class) {
      try {
        return newInstance(abdera,_class,AnnoUtil.getDefaultImplementation(_class));
      } catch (Throwable t) {
        throw throwex("IMPLEMENTATION.NOT.AVAILABLE",_class.getSimpleName(), t);
      }
    }    
    
    public abstract <T>T newInstance(Abdera abdera, Class<T> _class, String defaultImpl);
    
    protected RuntimeException throwex(String id, String arg, Throwable t) {
        return new RuntimeException(Localizer.sprintf(id, arg), t);
    }
}
