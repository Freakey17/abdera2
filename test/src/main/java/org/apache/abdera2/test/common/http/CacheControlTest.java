package org.apache.abdera2.test.common.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.abdera2.common.http.CacheControl;
import org.junit.Test;

import com.google.common.collect.Iterables;

public class CacheControlTest {

  public static final String CC = "private=\"A\", public, no-cache=\"A\", no-store, no-transform, only-if-cached, must-revalidate, proxy-revalidate, max-age=10, max-stale=10, min-fresh=10, stale-if-error=10, stale-while-revalidate=10, a=b";
  
  @Test
  public void testCacheControl() {
    CacheControl cc = 
      CacheControl
        .make()
        .extension("a", "b")
        .maxAge(10)
        .maxStale(10)
        .minFresh(10)
        .mustRevalidate(true)
        .noCache(true)
        .noCacheHeaders("A")
        .noStore(true)
        .noTransform(true)
        .onlyIfCached(true)
        .isPrivate(true)
        .privateHeaders("A")
        .proxyRevalidate(true)
        .isPublic(true)
        .staleIfError(10)
        .staleWhileRevalidate(10).get();
    assertEquals("b",cc.getExtension("a"));
    assertEquals(10,cc.getMaxAge());
    assertEquals(10,cc.getMaxStale());
    assertEquals(10,cc.getMinFresh());
    assertTrue(cc.isMustRevalidate());
    assertTrue(cc.isNoCache());
    Iterable<String> hs = cc.getNoCacheHeaders();
    hs = cc.getNoCacheHeaders();
    assertTrue(!Iterables.isEmpty(hs));
    assertTrue(cc.isNoStore());
    assertTrue(cc.isNoTransform());
    assertTrue(cc.isOnlyIfCached());
    assertTrue(cc.isPrivate());
    hs = cc.getPrivateHeaders();
    assertTrue(!Iterables.isEmpty(hs));
    assertTrue(cc.isProxyRevalidate());
    assertTrue(cc.isPublic());
    assertEquals(10,cc.getStaleIfError());
    assertEquals(10,cc.getStaleWhileRevalidate());
    assertEquals(CC,cc.toString());
  }
  
  @Test
  public void testCacheControl2() {
    CacheControl cc = CacheControl.parse(CC);
    assertEquals("b",cc.getExtension("a"));
    assertEquals(10,cc.getMaxAge());
    assertEquals(10,cc.getMaxStale());
    assertEquals(10,cc.getMinFresh());
    assertTrue(cc.isMustRevalidate());
    assertTrue(cc.isNoCache());
    Iterable<String> hs = cc.getNoCacheHeaders();
    assertTrue(!Iterables.isEmpty(hs));
    assertTrue(cc.isNoStore());
    assertTrue(cc.isNoTransform());
    assertTrue(cc.isOnlyIfCached());
    assertTrue(cc.isPrivate());
    hs = cc.getNoCacheHeaders();
    assertTrue(!Iterables.isEmpty(hs));
    assertTrue(cc.isProxyRevalidate());
    assertTrue(cc.isPublic());
    assertEquals(10,cc.getStaleIfError());
    assertEquals(10,cc.getStaleWhileRevalidate());
    assertEquals(CC,cc.toString());
  }
}
