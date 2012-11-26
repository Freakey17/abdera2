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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.Discover;
import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.factory.ExtensionFactory;
import org.apache.abdera2.factory.StreamBuilder;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.parser.axiom.PrettyWriter;
import org.apache.abdera2.protocol.error.ErrorExtensionFactory;
import org.apache.abdera2.writer.StreamWriter;
import org.apache.abdera2.writer.Writer;


/**
 * Provides the basic configuration for the Abdera default implementation. This class should not be accessed by
 * applications directly without very good reason.
 */
public final class AbderaConfiguration 
  extends AbstractConfiguration
  implements Constants, Configuration {

    private static final long serialVersionUID = 7460203853824337559L;

    public AbderaConfiguration(Abdera abdera) {
        super(null,abdera);
    }

    protected AbderaConfiguration(ResourceBundle bundle, Abdera abdera) {
      super(bundle,abdera);
    }

    protected Set<ExtensionFactory> loadExtensionFactories() {
        Set<ExtensionFactory> list = new HashSet<ExtensionFactory>();
        list.add(new ErrorExtensionFactory());
        Iterable<ExtensionFactory> factories = 
          Discover.locate(ExtensionFactory.class);
        for (ExtensionFactory factory : factories)
            list.add(factory);
        return list;
    }

    protected Map<String, Writer> initNamedWriters() {
        Map<String, Writer> writers = null;
        Iterable<Writer> _writers = Discover.locate(Writer.class,abdera);
        writers = Collections.synchronizedMap(new HashMap<String, Writer>());
        for (Writer writer : _writers) {
            writers.put(AnnoUtil.getName(writer), writer);
        }
        writers.put(AnnoUtil.getName(PrettyWriter.class).toLowerCase(), new PrettyWriter());
        return writers;
    }

    protected Map<String, Class<? extends StreamWriter>> initStreamWriters() {
        Map<String, Class<? extends StreamWriter>> writers = null;
        Iterable<Class<? extends StreamWriter>> _writers = 
          Discover.locate("org.apache.abdera2.writer.StreamWriter", true, abdera);
        writers = Collections.synchronizedMap(new HashMap<String, Class<? extends StreamWriter>>());
        for (Class<? extends StreamWriter> writer : _writers) {
            String name = AnnoUtil.getName(writer);
            if (name != null)
                writers.put(name.toLowerCase(), writer);
        }
        writers.put("fom", StreamBuilder.class);
        return writers;
    }

    protected Map<String, Parser> initNamedParsers() {
        Map<String, Parser> parsers = null;
        Iterable<Parser> _parsers = Discover.locate(Parser.class, abdera);
        parsers = Collections.synchronizedMap(new HashMap<String, Parser>());
        for (Parser parser : _parsers)
            parsers.put(AnnoUtil.getName(parser), parser);
        return parsers;
    }

    public <T>T newInstance(Abdera abdera, Class<T> _class, String defaultImpl) {
      return Discover.locate(
          _class, 
          abdera.getConfiguration().getConfigurationOption(
              _class.getName(), defaultImpl), abdera);      
    }
    
}
