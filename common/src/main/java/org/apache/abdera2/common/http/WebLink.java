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
package org.apache.abdera2.common.http;

import java.io.Serializable;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.abdera2.common.text.Codec;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import static org.apache.abdera2.common.text.CharUtils.*;
import static com.google.common.base.Preconditions.*;
/**
 * Implements the HTTP Link Header
 * (http://tools.ietf.org/html/rfc5988)
 */
public class WebLink implements Serializable {

  private static final long serialVersionUID = 3875558439575297581L;
  public static final String MEDIA_SCREEN = "screen";
  public static final String MEDIA_TTY = "tty";
  public static final String MEDIA_TV = "tv";
  public static final String MEDIA_PROJECTION = "projection";
  public static final String MEDIA_HANDHELD = "handheld";
  public static final String MEDIA_PRINT = "print";
  public static final String MEDIA_BRAILLE = "braille";
  public static final String MEDIA_AURAL = "aural";
  public static final String MEDIA_ALL = "all";
  
  public static Builder make() {
    return new Builder();
  }
  
  public static Builder make(String rel) {
    return make().rel(rel);
  }
  
  public static Builder make(WebLink from) {
    return new Builder(from);
  }
  
  public static class Builder implements Supplier<WebLink> {

    IRI iri;
    final ImmutableSet.Builder<String> rel = 
      ImmutableSet.builder();
    IRI anchor;
    final ImmutableSet.Builder<String> rev =
     ImmutableSet.builder();
    Lang lang;
    final ImmutableSet.Builder<String> media = 
      ImmutableSet.builder();
    String title;
    MimeType mediaType;
    final ImmutableMap.Builder<String,String> params = 
      ImmutableMap.builder();

    public Builder () {}
    
    public Builder(WebLink from) {
      from(from);
    }
    
    public WebLink get() {
      return new WebLink(this);
    }
    
    public Builder iri(IRI iri) {
      this.iri = iri;
      return this;
    }
    
    Builder from(WebLink link) {
      this.iri = link.iri;
      this.rel.addAll(link.rel);
      this.anchor = link.anchor;
      this.rev.addAll(link.rev);
      this.lang = link.lang;
      this.media.addAll(link.media);
      this.title = link.title;
      this.mediaType = link.mediaType;
      this.params.putAll(link.params);
      return this;
    }
    
    public Builder iri(String iri) {
      return iri(new IRI(iri));
    }
    
    public Builder rel(String rel) {
      this.rel.add(rel);
      return this;
    }
    
    public Builder anchor(IRI iri) {
      this.anchor = iri;
      return this;
    }
    
    public Builder anchor(String iri) {
      this.anchor = new IRI(iri);
      return this;
    }
    
    public Builder rev(String rev) {
      this.rev.add(rev);
      return this;
    }
    
    public Builder lang(String lang) {
      this.lang = new Lang(lang);
      return this;
    }
    
    public Builder lang(Lang lang) {
      this.lang = lang;
      return this;
    }
    
    public Builder lang(Locale locale) {
      this.lang = new Lang(locale);
      return this;
    }
    
    public Builder media(String media) {
      this.media.add(media);
      return this;
    }
    
    public Builder param(String name, String value) {
      checkNotNull(name);
      checkArgument(!reserved(name));
      this.params.put(name,value);
      return this;
    }
    
    public Builder title(String title) {
      this.title = title;
      return this;
    }
    
    public Builder mediaType(String type) {
      this.mediaType = MimeTypeHelper.unmodifiableMimeType(type);
      return this;
    }
    
    public Builder mediaType(MimeType type) {
      this.mediaType = MimeTypeHelper.unmodifiableMimeType(type);
      return this;
    }
  }
  
  final IRI iri;
  final Set<String> rel;
  final IRI anchor;
  final Set<String> rev;
  final Lang lang;
  final Set<String> media;
  final String title;
  final MimeType mediaType;
  final ImmutableMap<String,String> params;
  
  WebLink(Builder builder) {
    this.iri = builder.iri;
    this.rel = builder.rel.build();
    this.rev = builder.rev.build();
    this.media = builder.media.build();
    this.anchor = builder.anchor;
    this.lang = builder.lang;
    this.title = builder.title;
    this.mediaType = builder.mediaType;
    this.params = builder.params.build();
  }
  
  public WebLink(String iri) {
    this(new IRI(iri));
  }
  
  public WebLink(String iri, String rel) {
    this(new IRI(iri),rel);
  }
  
  public WebLink(IRI iri, String rel) {
    checkNotNull(iri);
    this.iri = iri.normalize();
    this.rel = rel != null ?
      ImmutableSet.of(rel) :
      ImmutableSet.<String>of();
    this.rev = ImmutableSet.<String>of();
    this.media = ImmutableSet.<String>of();
    this.anchor = null;
    this.lang = null;
    this.title = null;
    this.mediaType = null;
    this.params = ImmutableMap.<String,String>of();
  }
  
  public WebLink(IRI iri) {
    checkNotNull(iri);
    this.iri = iri;
    this.anchor = null;
    this.lang = null;
    this.title = null;
    this.mediaType = null;
    this.rel = ImmutableSet.<String>of();
    this.rev = ImmutableSet.<String>of();
    this.media = ImmutableSet.<String>of();
    this.params = ImmutableMap.<String,String>of();
  }
  
  public IRI getResolvedIri(IRI base) {
    checkNotNull(base);
    IRI context = getContextIri(base);
    return context != null ? context.resolve(iri) : iri;
  }
  
  public IRI getContextIri(IRI base) {
    checkNotNull(base);
    if (anchor == null) return base;
    return base != null ? base.resolve(anchor) : anchor;
  }
  
  public IRI getIri() {
    return iri;
  }
  
  public Iterable<String> getRel() {
    return this.rel;
  }
  
  public IRI getAnchor() {
    return anchor;
  }
  
  public Iterable<String> getRev() {
    return this.rev;
  }
  
  public Lang getHrefLang() {
    return lang;
  }
  
  public Iterable<String> getMedia() {
    return this.media;
  }
  
  public String getTitle() {
    return title;
  }
  
  public MimeType getMediaType() {
    return mediaType;
  }
  
  @Override
  public int hashCode() {
    return MoreFunctions.genHashCode(
      1, anchor,iri,lang,media,
      mediaType,
      params,rel,rev,title);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    WebLink other = (WebLink) obj;
    if (anchor == null) {
      if (other.anchor != null)
        return false;
    } else if (!anchor.equals(other.anchor))
      return false;
    if (iri == null) {
      if (other.iri != null)
        return false;
    } else if (!iri.equals(other.iri))
      return false;
    if (lang == null) {
      if (other.lang != null)
        return false;
    } else if (!lang.equals(other.lang))
      return false;
    if (media == null) {
      if (other.media != null)
        return false;
    } else if (!media.equals(other.media))
      return false;
    if (mediaType == null) {
      if (other.mediaType != null)
        return false;
    } else if (!mediaType.equals(other.mediaType))
      return false;
    if (params == null) {
      if (other.params != null)
        return false;
    } else if (!params.equals(other.params))
      return false;
    if (rel == null) {
      if (other.rel != null)
        return false;
    } else if (!rel.equals(other.rel))
      return false;
    if (rev == null) {
      if (other.rev != null)
        return false;
    } else if (!rev.equals(other.rev))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    return true;
  }



  private static final Set<String> reserved = 
    ImmutableSet.of(
      "rel","anchor","rev","hreflang",
      "media","title","type");
  static boolean reserved(String name) {
    return reserved.contains(name);
  }
  
  public String getParam(String name) {
    checkNotNull(name);
    checkArgument(!reserved(name));
    return params.get(name);
  }
  
  private void append(Set<String> set, String name, StringBuilder buf) {
    if (set.size() > 0) {
      buf.append(String.format(";%s=\"",name));
      boolean first = true;
      for (String r : set) {
        first = appendcomma(first,buf);
        buf.append(quotedIfNotToken(r,false));
      }
      buf.append('"');
    }
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('<')
       .append(iri.toASCIIString())
       .append('>');
    appendif(anchor != null,buf,";anchor=<%s>",anchor!=null?anchor.toASCIIString():"");
    appendif(lang != null,buf,";hreflang=%s",lang!=null?lang.toString():"");
    appendif(mediaType != null,buf,";type=%s",mediaType!=null?quotedIfNotToken(mediaType.toString()):"");
    append(rel,"rel",buf);
    append(rev,"rev",buf);
    append(media,"media",buf);
    if (title != null) {
      String enctitle = Codec.encode(title,Codec.STAR);
      boolean test = title.equals(enctitle);
      buf.append(";title");
      appendif(!test,buf,"*=%s",enctitle);
      appendif(test,buf,"=%s",quotedIfNotToken(title));
    }
    for (Map.Entry<String, String> entry : params.entrySet()) {
      String val = entry.getValue();
      String encval = Codec.encode(val,Codec.STAR);
      boolean test = val.equals(encval);
      buf.append(';')
         .append(entry.getKey());
      appendif(!test,buf,"*=%s", encval);
      appendif(test,buf,"=%s", quotedIfNotToken(entry.getValue()));
    }
    return buf.toString();
  }

  public static final Function<String,Iterable<WebLink>> parser = 
    new Function<String,Iterable<WebLink>>() {
      public Iterable<WebLink> apply(String input) {
        return
          input != null ?
          parse(input) :
          Collections.<WebLink>emptySet();
      }
  };
  
  public static Iterable<WebLink> parse(String text) {
    ImmutableList.Builder<WebLink> links = ImmutableList.builder();
    if (text == null) return ImmutableList.<WebLink>of();
    int z = scanFor('<', text, 0, true);
    while(z != -1) {
      int s = z;
      int e = scanFor('>', text, s, false);
      checkArgument(e != -1);
      String uri = text.substring(s+1,e).trim();
      WebLink.Builder maker = WebLink.make().iri(uri);
      s = scanFor(';', text,e+1,false);
      while(s != -1 && text.charAt(s) != ',') {
        e = scanFor('=', text,s+1,false);
        String name = text.substring(s+1,text.charAt(e-1)=='*'?e-1:e).trim();
        s = scanFor(';', text,e+1,false);
        String val = s!=-1?text.substring(e+1,s).trim():text.substring(e+1).trim();
        val = unescape(unquote(Codec.decode(val)));
        if (name.equals("rel"))
          for (String v : val.toLowerCase(Locale.US).split("\\s+"))
            maker.rel(v);
        else if (name.equals("anchor"))
          maker.anchor(unwrap(val, '<', '>'));
        else if (name.equals("rev"))
          for (String v : val.toLowerCase(Locale.US).split("\\s+"))
            maker.rev(v);
        else if (name.equals("hreflang"))
          maker.lang(val.toLowerCase(Locale.US));
        else if (name.equals("media"))
          for (String v : val.toLowerCase(Locale.US).split("\\s+"))
            maker.media(v);
        else if (name.equals("title"))
          maker.title(val);
        else if (name.equals("type"))
          maker.mediaType(val.toLowerCase(Locale.US));
        else
          maker.param(name,val);
      }
      links.add(maker.get());
      if (s == -1) break;
      z = scanFor('<', text, s+1, false);
    }
    return links.build();
  }
    
  public static String toString(WebLink link, WebLink... links) {
    if (link == null) return null;
    StringBuilder buf = new StringBuilder();
    buf.append(link.toString());
    for (WebLink l : links)
      buf.append(", ").append(l.toString());
    return buf.toString();
  }
  
  public static String toString(Iterable<WebLink> links ) {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for (WebLink link : links) {
      first = appendcomma(first,buf);
      buf.append(link.toString());
    }
    return buf.toString();
  }
}
