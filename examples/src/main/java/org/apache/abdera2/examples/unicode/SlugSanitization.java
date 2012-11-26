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
package org.apache.abdera2.examples.unicode;

import org.apache.abdera2.common.text.NormalizationForm;
import org.apache.abdera2.common.text.Slug;


/**
 * In the Atom Publishing Protocol, the Slug HTTP header is used to allow a client to provide text that can be used to
 * create a human-friendly URL for an entry. For instance, in many weblog software packages, it is not uncommon to find
 * entries whose permalink URL's look like /my_trip_to_the_zoo. Slug text provided by the client, however, may not be
 * directly suitable for use within a URL. It may, for instance, contain non-ascii characters, whitespace, or characters
 * not allowed within a URL. The Slug class will take input text and output a modified result suitable for use in
 * URL's
 */
public class SlugSanitization {

    public static void main(String... args) throws Exception {

        // french for "My trip to the beach".. note the accented character and the added superfluous whitespace characters
        String input = "Mon\tvoyage à la\tplage";

        // The default rules will replace whitespace with underscore characters
        // and convert non-ascii characters to pct-encoded utf-8
        Slug slug = Slug.create(input);
        String output = slug.toString();
        System.out.println(output);
        // Output = Mon_voyage_%C3%A0_la_plage

        // As an alternative to pct-encoding, a replacement string can be provided
        output = Slug.create(input, "").toString();
        System.out.println(output);
        // Output = Mon_voyage__la_plage

        // In certain cases, applying Unicode normalization form D to the
        // input can produce a good ascii equivalent to the input text.
        output = Slug.create(input, "", true, NormalizationForm.D).toString();
        System.out.println(output);
        // Output = mon_voyage_a_la_plage

    }

}
