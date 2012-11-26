package org.apache.abdera2.test.common.anno;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.common.anno.DefaultImplementation;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.anno.Namespace;
import org.apache.abdera2.common.anno.QName;
import org.apache.abdera2.common.anno.Version;
import org.apache.abdera2.common.protocol.BasicClient;
import org.junit.Test;

@DefaultImplementation("FooBarBaz")
@Version(value="A",name="B",uri="C")
@Name("AnnoUtilTest")
@Namespace("Foo")
@QName(value="A",ns="B",pfx="C")
public class AnnoUtilTest {

  @Test
  public void getDefaultImplementationTest() {
    String di = AnnoUtil.getDefaultImplementation(AnnoUtilTest.class);
    assertEquals("FooBarBaz", di);
  }
  
  @Test
  public void getDefaultUserAgentTest() {
    String ua = BasicClient.getDefaultUserAgent();
    assertEquals("Abdera/v2.0-SNAPSHOT",ua);
  }
  
  @Test
  public void getVersionTest() {
    Version version = AnnoUtil.getVersion(AnnoUtilTest.class);
    assertEquals("A",version.value());
    assertEquals("B",version.name());
    assertEquals("C",version.uri());
  }
  
  @Test
  public void getNameTest() {
    String name = AnnoUtil.getName(AnnoUtilTest.class);
    assertEquals("AnnoUtilTest",name);
  }
  
  @Test
  public void getNamespacesTest() {
    Set<String> set = AnnoUtil.getNamespaces(AnnoUtilTest.class);
    assertTrue(set.contains("Foo"));
  }
  
  @Test
  public void handlesNamespaceTest() {
    assertTrue(AnnoUtil.handlesNamespace("Foo", AnnoUtilTest.class));
  }
  
  @Test
  public void getQNameTest() {
    javax.xml.namespace.QName qname = AnnoUtil.getQName(AnnoUtilTest.class);
    assertEquals("A",qname.getLocalPart());
    assertEquals("B",qname.getNamespaceURI());
    assertEquals("C",qname.getPrefix());
  }
  
  @Test
  public void qNameFromAnnoTest() {
    Class<?> _class = AnnoUtilTest.class;
    QName qname = _class.getAnnotation(QName.class);
    javax.xml.namespace.QName q = AnnoUtil.qNameFromAnno(qname);
    assertEquals("A",q.getLocalPart());
    assertEquals("B",q.getNamespaceURI());
    assertEquals("C",q.getPrefix());
  }
}
