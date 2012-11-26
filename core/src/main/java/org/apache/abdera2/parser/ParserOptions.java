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
package org.apache.abdera2.parser;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera2.common.io.Compression;
import org.apache.abdera2.common.io.Compression.CompressionCodec;
import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.parser.axiom.FOMFactory;
import org.apache.abdera2.parser.filter.ParseFilter;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Parser options are used to modify the behavior of the parser.
 */
public class ParserOptions {

  public static Builder make() {
    return new Builder();
  }
  
  public static Builder from(ParserOptions options) {
    Builder builder = new Builder(true);
    builder.factory = options.factory;
    builder.charset = options.charset;
    builder.parseFilter = options.parseFilter;
    builder.detect = options.detect;
    builder.preserve = options.preserve;
    builder.filterreserved = options.filterreserved;
    builder.replacement = options.replacement;
    builder.resolveentities = options.resolveentities;
    builder.qnamealiasing = options.qnamealiasing;
    builder.codecs.addAll(options.codecs);
    builder.aliases.putAll(options.aliases);
    builder.entities.putAll(options.entities);
    builder.fragment = options.fragment;
    return builder;
  }
  
  public static class Builder 
    implements Supplier<ParserOptions> {

    protected Factory factory = null;
    protected String charset;
    protected ParseFilter parseFilter = null;
    protected boolean detect = true;
    protected boolean preserve = true;
    protected boolean filterreserved = false;
    protected char replacement = 0;
    protected boolean resolveentities = true;
    protected boolean fragment = false;
    
    protected ImmutableSet.Builder<Compression.CompressionCodec> codecs = 
      ImmutableSet.builder();
    protected ImmutableMap.Builder<String,String> entities = 
      ImmutableMap.builder();

    protected boolean qnamealiasing = false;
    protected ImmutableMap.Builder<QName, QName> aliases = 
      ImmutableMap.builder();
    
    public Builder() {
      initDefaultEntities();
    }
    
    Builder(boolean se) {
      // skip the default entities
    }
    
    public Builder factory(Factory factory) {
      this.factory = factory;
      return this;
    }
    
    public Builder charset(String charset) {
      this.charset = charset;
      return this;
    }
    
    public Builder filter(ParseFilter filter) {
      this.parseFilter = filter;
      return this;
    }
    
    public Builder autodetectCharset() {
      this.detect = true;
      return this;
    }
    
    public Builder doNotAutodetectCharset() {
      this.detect = false;
      return this;
    }
    
    public Builder preserveWhitespace() {
      this.preserve = true;
      return this;
    }
    
    public Builder doNotPreserveWhitespace() {
      this.preserve = false;
      return this;
    }
    
    public Builder filterRestrictedCharacters() {
      this.filterreserved = true;
      return this;
    }
    
    public Builder doNotFilterRestrictedCharacters() {
      this.filterreserved = false;
      return this;
    }
    
    public Builder filterRestrictedCharacterReplacement(char c) {
      this.replacement = c;
      return this;
    }
    
    public Builder compression(CompressionCodec codec) {
      this.codecs.add(codec);
      return this;
    }
    
    public Builder resolveEntities() {
      this.resolveentities = true;
      return this;
    }
    
    public Builder doNotResolveEntities() {
      this.resolveentities = false;
      return this;
    }
    
    public Builder entity(String name, String value) {
      this.entities.put(name,value);
      return this;
    }
    
    public Builder entities(Map<String,String> map) {
      this.entities = ImmutableMap.<String,String>builder().putAll(map);
      return this;
    }
    
    public Builder qNameAliasMappingEnabled() {
      this.qnamealiasing = true;
      return this;
    }
    
    public Builder qNameAliasMappingNotEnabled() {
      this.qnamealiasing = false;
      return this;
    }
    
    public Builder alias(QName qname1, QName qname2) {
      this.aliases.put(qname1,qname2);
      return this;
    }
    
    public Builder fragment() {
      this.fragment = true;
      return this;
    }
    
    public ParserOptions get() {
      return new ParserOptions(this);
    }
    
    private void initDefaultEntities() {
      entity("quot", "\"");
      entity("amp", "\u0026");
      entity("lt", "\u003C");
      entity("gt", "\u003E");
      entity("nbsp", " ");
      entity("iexcl", "\u00A1");
      entity("cent", "\u00A2");
      entity("pound", "\u00A3");
      entity("curren", "\u00A4");
      entity("yen", "\u00A5");
      entity("brvbar", "\u00A6");
      entity("sect", "\u00A7");
      entity("uml", "\u00A8");
      entity("copy", "\u00A9");
      entity("ordf", "\u00AA");
      entity("laquo", "\u00AB");
      entity("not", "\u00AC");
      entity("shy", "\u00AD");
      entity("reg", "\u00AE");
      entity("macr", "\u00AF");
      entity("deg", "\u00B0");
      entity("plusmn", "\u00B1");
      entity("sup2", "\u00B2");
      entity("sup3", "\u00B3");
      entity("acute", "\u00B4");
      entity("micro", "\u00B5");
      entity("para", "\u00B6");
      entity("middot", "\u00B7");
      entity("cedil", "\u00B8");
      entity("sup1", "\u00B9");
      entity("ordm", "\u00BA");
      entity("raquo", "\u00BB");
      entity("frac14", "\u00BC");
      entity("frac12", "\u00BD");
      entity("frac34", "\u00BE");
      entity("iquest", "\u00BF");
      entity("Agrave", "\u00C0");
      entity("Aacute", "\u00C1");
      entity("Acirc", "\u00C2");
      entity("Atilde", "\u00C3");
      entity("Auml", "\u00C4");
      entity("Aring", "\u00C5");
      entity("AElig", "\u00C6");
      entity("Ccedil", "\u00C7");
      entity("Egrave", "\u00C8");
      entity("Eacute", "\u00C9");
      entity("Ecirc", "\u00CA");
      entity("Euml", "\u00CB");
      entity("Igrave", "\u00CC");
      entity("Iacute", "\u00CD");
      entity("Icirc", "\u00CE");
      entity("Iuml", "\u00CF");
      entity("ETH", "\u00D0");
      entity("Ntilde", "\u00D1");
      entity("Ograve", "\u00D2");
      entity("Oacute", "\u00D3");
      entity("Ocirc", "\u00D4");
      entity("Otilde", "\u00D5");
      entity("Ouml", "\u00D6");
      entity("times", "\u00D7");
      entity("Oslash", "\u00D8");
      entity("Ugrave", "\u00D9");
      entity("Uacute", "\u00DA");
      entity("Ucirc", "\u00DB");
      entity("Uuml", "\u00DC");
      entity("Yacute", "\u00DD");
      entity("THORN", "\u00DE");
      entity("szlig", "\u00DF");
      entity("agrave", "\u00E0");
      entity("aacute", "\u00E1");
      entity("acirc", "\u00E2");
      entity("atilde", "\u00E3");
      entity("auml", "\u00E4");
      entity("aring", "\u00E5");
      entity("aelig", "\u00E6");
      entity("ccedil", "\u00E7");
      entity("egrave", "\u00E8");
      entity("eacute", "\u00E9");
      entity("ecirc", "\u00EA");
      entity("euml", "\u00EB");
      entity("igrave", "\u00EC");
      entity("iacute", "\u00ED");
      entity("icirc", "\u00EE");
      entity("iuml", "\u00EF");
      entity("eth", "\u00F0");
      entity("ntilde", "\u00F1");
      entity("ograve", "\u00F2");
      entity("oacute", "\u00F3");
      entity("ocirc", "\u00F4");
      entity("otilde", "\u00F5");
      entity("ouml", "\u00F6");
      entity("divide", "\u00F7");
      entity("oslash", "\u00F8");
      entity("ugrave", "\u00F9");
      entity("uacute", "\u00FA");
      entity("ucirc", "\u00FB");
      entity("uuml", "\u00FC");
      entity("yacute", "\u00FD");
      entity("thorn", "\u00FE");
      entity("yuml", "\u00FF");
      entity("OElig", "\u0152");
      entity("oelig", "\u0153");
      entity("Scaron", "\u0160");
      entity("scaron", "\u0161");
      entity("Yuml", "\u0178");
      entity("fnof", "\u0192");
      entity("circ", "\u02C6");
      entity("tilde", "\u02DC");
      entity("Alpha", "\u0391");
      entity("Beta", "\u0392");
      entity("Gamma", "\u0393");
      entity("Delta", "\u0394");
      entity("Epsilon", "\u0395");
      entity("Zeta", "\u0396");
      entity("Eta", "\u0397");
      entity("Theta", "\u0398");
      entity("Iota", "\u0399");
      entity("Kappa", "\u039A");
      entity("Lambda", "\u039B");
      entity("Mu", "\u039C");
      entity("Nu", "\u039D");
      entity("Xi", "\u039E");
      entity("Omicron", "\u039F");
      entity("Pi", "\u03A0");
      entity("Rho", "\u03A1");
      entity("Sigma", "\u03A3");
      entity("Tau", "\u03A4");
      entity("Upsilon", "\u03A5");
      entity("Phi", "\u03A6");
      entity("Chi", "\u03A7");
      entity("Psi", "\u03A8");
      entity("Omega", "\u03A9");
      entity("alpha", "\u03B1");
      entity("beta", "\u03B2");
      entity("gamma", "\u03B3");
      entity("delta", "\u03B4");
      entity("epsilon", "\u03B5");
      entity("zeta", "\u03B6");
      entity("eta", "\u03B7");
      entity("theta", "\u03B8");
      entity("iota", "\u03B9");
      entity("kappa", "\u03BA");
      entity("lambda", "\u03BB");
      entity("mu", "\u03BC");
      entity("nu", "\u03BD");
      entity("xi", "\u03BE");
      entity("omicron", "\u03BF");
      entity("pi", "\u03C0");
      entity("rho", "\u03C1");
      entity("sigmaf", "\u03C2");
      entity("sigma", "\u03C3");
      entity("tau", "\u03C4");
      entity("upsilon", "\u03C5");
      entity("phi", "\u03C6");
      entity("chi", "\u03C7");
      entity("psi", "\u03C8");
      entity("omega", "\u03C9");
      entity("thetasym", "\u03D1");
      entity("upsih", "\u03D2");
      entity("piv", "\u03D6");
      entity("ensp", "\u2002");
      entity("emsp", "\u2003");
      entity("thinsp", "\u2009");
      entity("zwnj", "\u200C");
      entity("zwj", "\u200D");
      entity("lrm", "\u200E");
      entity("rlm", "\u200F");
      entity("ndash", "\u2013");
      entity("mdash", "\u2014");
      entity("lsquo", "\u2018");
      entity("rsquo", "\u2019");
      entity("sbquo", "\u201A");
      entity("ldquo", "\u201C");
      entity("rdquo", "\u201D");
      entity("bdquo", "\u201E");
      entity("dagger", "\u2020");
      entity("Dagger", "\u2021");
      entity("bull", "\u2022");
      entity("hellip", "\u2026");
      entity("permil", "\u2030");
      entity("prime", "\u2032");
      entity("Prime", "\u2033");
      entity("lsaquo", "\u2039");
      entity("rsaquo", "\u203A");
      entity("oline", "\u203E");
      entity("frasl", "\u2044");
      entity("euro", "\u20AC");
      entity("image", "\u2111");
      entity("weierp", "\u2118");
      entity("real", "\u211C");
      entity("trade", "\u2122");
      entity("alefsym", "\u2135");
      entity("larr", "\u2190");
      entity("uarr", "\u2191");
      entity("rarr", "\u2192");
      entity("darr", "\u2193");
      entity("harr", "\u2194");
      entity("crarr", "\u21B5");
      entity("lArr", "\u21D0");
      entity("uArr", "\u21D1");
      entity("rArr", "\u21D2");
      entity("dArr", "\u21D3");
      entity("hArr", "\u21D4");
      entity("forall", "\u2200");
      entity("part", "\u2202");
      entity("exist", "\u2203");
      entity("empty", "\u2205");
      entity("nabla", "\u2207");
      entity("isin", "\u2208");
      entity("notin", "\u2209");
      entity("ni", "\u220B");
      entity("prod", "\u220F");
      entity("sum", "\u2211");
      entity("minus", "\u2212");
      entity("lowast", "\u2217");
      entity("radic", "\u221A");
      entity("prop", "\u221D");
      entity("infin", "\u221E");
      entity("ang", "\u2220");
      entity("and", "\u2227");
      entity("or", "\u2228");
      entity("cap", "\u2229");
      entity("cup", "\u222A");
      entity("int", "\u222B");
      entity("there4", "\u2234");
      entity("sim", "\u223C");
      entity("cong", "\u2245");
      entity("asymp", "\u2248");
      entity("ne", "\u2260");
      entity("equiv", "\u2261");
      entity("le", "\u2264");
      entity("ge", "\u2265");
      entity("sub", "\u2282");
      entity("sup", "\u2283");
      entity("nsub", "\u2284");
      entity("sube", "\u2286");
      entity("supe", "\u2287");
      entity("oplus", "\u2295");
      entity("otimes", "\u2297");
      entity("perp", "\u22A5");
      entity("sdot", "\u22C5");
      entity("lceil", "\u2308");
      entity("rceil", "\u2309");
      entity("lfloor", "\u230A");
      entity("rfloor", "\u230B");
      entity("lang", "\u2329");
      entity("rang", "\u232A");
      entity("loz", "\u25CA");
      entity("spades", "\u2660");
      entity("clubs", "\u2663");
      entity("hearts", "\u2665");
      entity("diams", "\u2666");
    }
  }
  
  private final Factory factory;
  private final String charset;
  private final ParseFilter parseFilter;
  private final boolean detect;
  private final boolean preserve;
  private final boolean filterreserved;
  private final char replacement;
  private final boolean resolveentities;
  private final ImmutableSet<Compression.CompressionCodec> codecs;
  private final ImmutableMap<String,String> entities;
  private final boolean qnamealiasing;
  private final ImmutableMap<QName, QName> aliases;
  private final boolean fragment;

  ParserOptions(Builder builder) {
    this.factory = builder.factory != null ?
      builder.factory : new FOMFactory();
    this.charset = builder.charset;
    this.parseFilter = builder.parseFilter;
    this.detect = builder.detect;
    this.preserve = builder.preserve;
    this.filterreserved = builder.filterreserved;
    this.replacement = builder.replacement;
    this.resolveentities = builder.resolveentities;
    this.codecs = builder.codecs.build();
    this.entities = builder.entities.build();
    this.qnamealiasing = builder.qnamealiasing;
    this.aliases = builder.aliases.build();
    this.fragment = builder.fragment;
  }
  
  ParserOptions(ParserOptions options, String charset) {
    this.factory = options.factory;
    this.charset = charset;
    this.parseFilter = options.parseFilter;
    this.detect = options.detect;
    this.preserve = options.preserve;
    this.filterreserved = options.filterreserved;
    this.replacement = options.replacement;
    this.resolveentities = options.resolveentities;
    this.codecs = options.codecs;
    this.entities = options.entities;
    this.qnamealiasing = options.qnamealiasing;
    this.aliases = options.aliases; 
    this.fragment = options.fragment;
  }
  
   public ParserOptions usingCharset(String charset) {
     return new ParserOptions(this,charset);
   }
  
    /**
     * Returns the factory the parser should use
     */
    public Factory getFactory() {
      return factory;
    }

    /**
     * Returns the default character set to use for the parsed document
     */
    public String getCharset() {
      return charset;
    }
    
    /**
     * Returns the Parse Filter. The parse filter is a set of XML QNames that the parse should watch out for. If the
     * filter is null, the parser will parse all elements in the document. I the filter is not null, the parser will
     * only pay attention to elements whose QName's appear in the filter list.
     */
    public ParseFilter getParseFilter() {
      return parseFilter;
    }

    /**
     * Returns true if the parser should attempt to automatically detect the character encoding from the stream
     */
    public boolean getAutodetectCharset() {
      return detect;
    }

    /**
     * If false, the parser will trim leading and trailing whitespace in element and attribute values unless there is an
     * in-scope xml:space="preserve".
     */
    public boolean getMustPreserveWhitespace() {
      return preserve;
    }

    /**
     * If true, the parser will attempt to silently filter out invalid XML characters appearing within the XML document.
     */
    public boolean getFilterRestrictedCharacters() {
      return this.filterreserved;
    }

    /**
     * If getFilterRestrictedCharacters is true, restricted characters will be replaced with the specified character
     */
    public char getFilterRestrictedCharacterReplacement() {
      return this.replacement;
    }

    /**
     * When parsing an InputStream that contains compressed data, use these codecs to decompress the stream. Only used
     * when parsing an InputStream. Ignored when parsing a Reader
     */
    public Iterable<Compression.CompressionCodec> getCompressionCodecs() {
      return codecs;
    }

    /**
     * Resolves a value for a named entity. This provides an escape clause for when feeds use entities that are not
     * supported in XML without a DTD decl. By default, all of the (X)HTML entities are preregistered
     */
    public String resolveEntity(String name) {
      return entities.get(name);
    }

    /**
     * True if undeclared named entities should be resolved.
     */
    public boolean getResolveEntities() {
      return this.resolveentities;
    }

    /**
     * True if QName-Alias mapping is enabled (default is false)
     */
    public boolean isQNameAliasMappingEnabled() {
      return this.qnamealiasing;
    }
    
    public QName getAlias(QName qname) {
      return aliases.containsKey(qname) ? 
        aliases.get(qname) : qname;
    }

    public boolean isFragment() {
      return fragment;
    }
}
