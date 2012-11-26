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

import java.io.Serializable;
import java.util.Map;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.anno.DefaultImplementation;
import org.apache.abdera2.factory.ExtensionFactory;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.writer.StreamWriter;
import org.apache.abdera2.writer.Writer;

@DefaultImplementation("org.apache.abdera2.util.AbderaConfiguration")
public interface Configuration extends Serializable {

    /**
     * Retrieve the value of the specified configuration option
     * 
     * @return The configuration option value or null
     */
    public abstract String getConfigurationOption(String id);

    /**
     * Retrieve the value of the specified configuration option or _default if the value is null
     * 
     * @return The configuration option value of _default
     */
    public abstract String getConfigurationOption(String id, String _default);

    public <T>T newInstance(Abdera abdera, Class<T> _class);
        
    /**
     * Get the collection of Parsers;
     */
    public Map<String, Parser> getParsers();

    /**
     * Get the collection of Writers
     */
    public Map<String, Writer> getWriters();

    /**
     * Get the collection of StreamWriters
     */
    public Map<String, Class<? extends StreamWriter>> getStreamWriters();

    /**
     * Get the collection of ExtensionFactory impls
     */
    public Iterable<ExtensionFactory> getExtensionFactories();

    /**
     * Registers a new Parser, this method doesn't override a parser if already exists.
     * 
     * @param parser is the new Parser to add
     * @return the instance of the configuration class
     */
    public Configuration addParser(Parser parser);

    /**
     * Registers a new Writer, this method doesn't override a writer if already exists.
     * 
     * @param writer is the new Writer to add
     * @return the instance of the configuration class
     */
    public Configuration addWriter(Writer writer);

    /**
     * Registers a new ExtensionFactory, this method doesn't override an extensionFactory if already exists.
     * 
     * @param factory is the new ExtensionFactory to add
     * @return the instance of the configuration class
     */
    public Configuration addExtensionFactory(ExtensionFactory factory);

    /**
     * Registers a new StreamWriter, this method doesn't override a streamWriter if already exists.
     * 
     * @param sw is the new StreamWriter to add
     * @return the instance of the configuration class
     */
    public Configuration addStreamWriter(Class<? extends StreamWriter> sw);
}
