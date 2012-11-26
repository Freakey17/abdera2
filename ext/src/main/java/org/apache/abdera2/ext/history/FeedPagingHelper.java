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
package org.apache.abdera2.ext.history;

import javax.xml.namespace.QName;

import static org.apache.abdera2.model.Link.REL_NEXT;
import static org.apache.abdera2.model.Link.REL_PREV;
import static org.apache.abdera2.model.Link.REL_CURRENT;
import static org.apache.abdera2.model.Link.REL_PREVIOUS;
import static org.apache.abdera2.model.Link.REL_FIRST;
import static org.apache.abdera2.model.Link.REL_LAST;
import static org.apache.abdera2.model.Link.REL_NEXT_ARCHIVE;
import static org.apache.abdera2.model.Link.REL_PREV_ARCHIVE;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.protocol.RequestOptions;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ExtensibleElement;
import org.apache.abdera2.model.Link;
import org.apache.abdera2.model.Source;
import org.apache.abdera2.protocol.client.AbderaClient;
import org.apache.abdera2.protocol.client.AbderaClientResponse;
import org.apache.abdera2.protocol.client.AbderaSession;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Initial support for Mark Nottingham's Feed Paging and Archiving draft
 * (http://ietfreport.isoc.org/all-ids/draft-nottingham-atompub-feed-history-11.txt)
 */
@SuppressWarnings("deprecation")
public final class FeedPagingHelper {

    public static final String FH_PREFIX = "fh";
    public static final String FHNS = "http://purl.org/syndication/history/1.0";
    public static final QName COMPLETE = new QName(FHNS, "complete", FH_PREFIX);
    public static final QName ARCHIVE = new QName(FHNS, "archive", FH_PREFIX);

    FeedPagingHelper() {
    }

    /**
     * Returns true if the feed is "complete". According to the Feed Paging and Archiving specification, in a complete
     * feed, "any entry not actually in the feed document SHOULD NOT be considered to be part of that feed."
     * 
     * @param feed The feed to check
     */
    public static boolean isComplete(Source feed) {
        return feed.has(COMPLETE);
    }

    /**
     * Flag the feed as being complete. According to the Feed Paging and Archiving specification, in a complete feed,
     * "any entry not actually in the feed document SHOULD NOT be considered to be part of that feed."
     * 
     * @param feed The Feed to mark as complete
     * @param complete True if the feed is complete
     */
    public static void setComplete(Source feed, boolean complete) {
      toggle(complete,feed,COMPLETE);
    }

    /**
     * Flag the feed as being an archive.
     * 
     * @param feed The Feed to mark as an archive
     * @param archive True if the feed is an archive
     */
    public static void setArchive(Source feed, boolean archive) {
      toggle(archive,feed,ARCHIVE);
    }
    
    private static void toggle(
      boolean val, 
      ExtensibleElement el, 
      QName qname) {
      if (val)
          if (!el.has(qname))
              el.addExtension(qname);
      else
          if (el.has(qname))
              el.getExtension(qname).discard();
  }

    public static Predicate<Source> isArchive() {
      return new Predicate<Source>() {
        public boolean apply(Source input) {
          return isArchive(input);
        }
      };
    }
    
    public static Predicate<Source> isPaged() {
      return new Predicate<Source>() {
        public boolean apply(Source input) {
          return isPaged(input);
        }
      };
    }
    
    public static Predicate<Source> isComplete() {
      return new Predicate<Source>() {
        public boolean apply(Source input) {
          return isComplete(input);
        }
      };
    }
    
    /**
     * Return true if the feed has been marked as an archive
     * 
     * @param feed The feed to check
     */
    public static boolean isArchive(Source feed) {
        return feed.has(ARCHIVE);
    }

    /**
     * Return true if the feed contains any next, previous, first or last paging link relations
     * 
     * @param feed The feed to check
     */
    public static boolean isPaged(Source feed) {
        return feed.getLink(REL_NEXT) != null 
            || feed.getLink(REL_PREVIOUS) != null
            || feed.getLink(REL_PREV) != null
            || feed.getLink(REL_FIRST) != null
            || feed.getLink(REL_LAST) != null;
    }

    private static Link _setLink(Source feed, String rel, String iri) {
      Link link = feed.getLink(rel);
      if (link != null)
          link.setHref(iri);
      else
          link = feed.addLink(iri, rel);
      return link;
  }
    
    /**
     * Adds a next link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the next feed document
     * @return The newly created Link
     */
    public static Link setNext(Source feed, String iri) {
        return _setLink(feed, REL_NEXT, iri);
    }

    /**
     * Adds a previous link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the previous feed document
     * @return The newly created Link
     */
    public static Link setPrevious(Source feed, String iri) {
      return _setLink(feed, REL_PREVIOUS, iri);
    }

    /**
     * Adds a first link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the first feed document
     * @return The newly created Link
     */
    public static Link setFirst(Source feed, String iri) {
      return _setLink(feed, REL_FIRST, iri);
    }

    /**
     * Adds a last link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the last feed document
     * @return The newly created Link
     */
    public static Link setLast(Source feed, String iri) {
      return _setLink(feed, REL_LAST, iri);
    }

    /**
     * Adds a next-archive link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the next archive feed document
     * @return The newly created Link
     */
    public static Link setNextArchive(Source feed, String iri) {
      return _setLink(feed, REL_NEXT_ARCHIVE, iri);
    }

    /**
     * Adds a prev-archive link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the previous archive feed document
     * @return The newly created Link
     */
    public static Link setPreviousArchive(Source feed, String iri) {
      return _setLink(feed, REL_PREV_ARCHIVE, iri);
    }

    /**
     * Adds a current link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the current feed document
     * @return The newly created Link
     */
    public static Link setCurrent(Source feed, String iri) {
      return _setLink(feed, REL_CURRENT, iri);
    }

    private static IRI _getLink(Source feed, String rel) {
      Link link = feed.getLink(rel);
      return (link != null) ? link.getResolvedHref() : null;
    }
    
    /**
     * Returns the IRI of the next link relation
     */
    public static IRI getNext(Source feed) {
        return _getLink(feed, REL_NEXT);
    }

    /**
     * Returns the IRI of the previous link relation
     */
    public static IRI getPrevious(Source feed) {
        IRI iri = _getLink(feed, REL_PREVIOUS);
        if (iri == null)
          iri = _getLink(feed, REL_PREV);
        return iri;
    }

    /**
     * Returns the IRI of the first link relation
     */
    public static IRI getFirst(Source feed) {
        return _getLink(feed, REL_FIRST);
    }

    /**
     * Returns the IRI of the last link relation
     */
    public static IRI getLast(Source feed) {
        return _getLink(feed, REL_LAST);
    }

    /**
     * Returns the IRI of the prev-archive link relation
     */
    public static IRI getPreviousArchive(Source feed) {
        return _getLink(feed,REL_PREV_ARCHIVE);
    }

    /**
     * Returns the IRI of the next-archive link relation
     */
    public static IRI getNextArchive(Source feed) {
        return _getLink(feed,REL_NEXT_ARCHIVE);
    }

    /**
     * Returns the IRI of the current link relation
     */
    public static IRI getCurrent(Source feed) {
        return _getLink(feed,REL_CURRENT);
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchNext(final AbderaSession session) {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchNext(input,session);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchPrevious(final AbderaSession session) {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchPrevious(input,session);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchCurrent(final AbderaSession session) {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchCurrent(input,session);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchLast(final AbderaSession session) {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchLast(input,session);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchNextArchive(final AbderaSession session) {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchNextArchive(input,session);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchPreviousArchive(final AbderaSession session) {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchPreviousArchive(input,session);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchNext() {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchNext(input);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchPrevious() {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchPrevious(input);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchCurrent() {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchCurrent(input);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchLast() {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchLast(input);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchNextArchive() {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchNextArchive(input);
        }
      };
    }
    
    public static <E extends Element>Function<Source,Document<E>> fetchPreviousArchive() {
      return new Function<Source,Document<E>>() {
        public Document<E> apply(Source input) {
          return fetchPreviousArchive(input);
        }
      };
    }
    
    public static <E extends Element>Document<E> fetchNext(Source source) {
      AbderaClient client = new AbderaClient();
      AbderaSession session = client.newSession();
      return fetchNext(source, session);
    }
    
    public static <E extends Element>Document<E> fetchPrevious(Source source) {
      AbderaClient client = new AbderaClient();
      AbderaSession session = client.newSession();
      return fetchPrevious(source, session);
    }
    
    public static <E extends Element>Document<E> fetchCurrent(Source source) {
      AbderaClient client = new AbderaClient();
      AbderaSession session = client.newSession();
      return fetchCurrent(source, session);
    }
    
    public static <E extends Element>Document<E> fetchLast(Source source) {
      AbderaClient client = new AbderaClient();
      AbderaSession session = client.newSession();
      return fetchLast(source, session);
    }
    
    public static <E extends Element>Document<E> fetchNextArchive(Source source) {
      AbderaClient client = new AbderaClient();
      AbderaSession session = client.newSession();
      return fetchNextArchive(source, session);
    }
    
    public static <E extends Element>Document<E> fetchPreviousArchive(Source source) {
      AbderaClient client = new AbderaClient();
      AbderaSession session = client.newSession();
      return fetchPreviousArchive(source, session);
    }
    
    public static <E extends Element>Document<E> fetchNext(Source source, AbderaSession session) {
      return fetch(getNext(source),session);
    }
    
    public static <E extends Element>Document<E> fetchPrevious(Source source, AbderaSession session) {
      return fetch(getPrevious(source),session);
    }
    
    public static <E extends Element>Document<E> fetchCurrent(Source source, AbderaSession session) {
      return fetch(getCurrent(source),session);
    }
    
    public static <E extends Element>Document<E> fetchLast(Source source, AbderaSession session) {
      return fetch(getLast(source),session);
    }
    
    public static <E extends Element>Document<E> fetchNextArchive(Source source, AbderaSession session) {
      return fetch(getNextArchive(source),session);
    }
    
    public static <E extends Element>Document<E> fetchPreviousArchive(Source source, AbderaSession session) {
      return fetch(getPreviousArchive(source),session);
    }
    
    private static <E extends Element>Document<E> fetch(IRI iri, AbderaSession session) {
      if (iri == null) return null;
      PageFetch<E> fetch = new PageFetch<E>(iri.toString(), session);
      return fetch.apply(session.getDefaultRequestOptions().get());
    }
    
    public static class PageFetch<E extends Element>
      implements Function<RequestOptions,Document<E>> {
        private final String uri;
        private final AbderaSession session;
        public PageFetch(String uri, AbderaSession session) {
          this.uri = uri;
          this.session = session;
        }
        public Document<E> apply(RequestOptions options) {
          AbderaClientResponse resp = session.get(uri,options);
          switch(resp.getType()) {
          case SUCCESSFUL:
            return resp.getDocument();
          default:
            org.apache.abdera2.protocol.error.Error.create(
              Abdera.getInstance(), 
              resp.getStatus(), 
              resp.getStatusText())
                .throwException();
            return null;
          }
        }
    }
}
