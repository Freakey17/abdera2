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
package org.apache.abdera2.common.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.text.CharUtils.Profile;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * A reader implementation that profiles out unwanted characters By default, unwanted characters are simply removed from
 * the stream. Alternatively, a replacement character can be provided so long as it is acceptable to the specified
 * profile
 */
public class FilteredCharReader extends FilterReader {

    /**
     * The XMLVersion determines which set of restrictions to apply depending on the XML version being parsed
     */
    private final Profile profile;
    private final char replacement;

    public FilteredCharReader(
      InputStream in, 
      Profile profile) {
        this(reader(in), profile);
    }

    public FilteredCharReader(
      InputStream in, 
      String charset, 
      Profile profile) {
        this(reader(in, charset), profile);
    }

    public FilteredCharReader(
      InputStream in, 
      Profile profile, 
      char replacement) {
        this(reader(in), profile, replacement);
    }

    public FilteredCharReader(
      InputStream in, 
      String charset, 
      Profile profile, 
      char replacement) {
        this(reader(in, charset), profile, replacement);
    }

    private static Reader reader(InputStream in) {
      return new InputStreamReader(in);
    }
    
    private static Reader reader(InputStream in, String charset) {
      try {
        return new InputStreamReader(in,charset);
      } catch (Throwable t) {
        throw ExceptionHelper.propogate(t);
      }
    }
    
    public FilteredCharReader(Reader in) {
      this(in, Profile.NONOP, (char)0);
    }

    public FilteredCharReader(Reader in, Profile profile) {
      this(in, profile, (char)0);
    }

    public FilteredCharReader(Reader in, char replacement) {
      this(in, Profile.NONOP, replacement);
    }

    public FilteredCharReader(Reader in, Profile profile, char replacement) {
      super(in);
      this.profile = profile;
      this.replacement = replacement;
      checkArgument(
        replacement == 0 ||
        Character.isValidCodePoint(replacement) ||
        !profile.apply(replacement)
      );
    }

    @Override
    public int read() throws IOException {
      int c = -1;
      if (replacement == 0)
        while (((c = super.read()) != -1 && profile.apply(c))) {}
      else {
        c = super.read();
        if (c != -1 && profile.apply(c))
          c = replacement;
      }
      return c;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
      int n = off;
      for (; n < Math.min(len, cbuf.length - off); n++) {
        int r = read();
        if (r != -1)
            cbuf[n] = (char)r;
        else
            break;
      }
      n -= off;
      return n <= 0 ? -1 : n;
    }

}
