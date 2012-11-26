package org.apache.abdera2.test.factory;

import static org.junit.Assert.assertNotNull;

import javax.xml.namespace.QName;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Content;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.ExtensibleElementWrapper;
import org.apache.abdera2.model.Text;
import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.anno.Namespace;
import org.apache.abdera2.ext.thread.Total;
import org.apache.abdera2.factory.AbstractExtensionFactory;
import org.apache.abdera2.factory.AbstractExtensionFactory.Impls;
import org.apache.abdera2.factory.AbstractExtensionFactory.Impl;
import org.apache.abdera2.factory.Factory;
import org.junit.Test;

public class FactoryTest {

  @Test
  public void factoryTest() {
    Abdera abdera = Abdera.getInstance();
    Factory factory = abdera.getFactory();
    assertNotNull(factory.newEntry());
    assertNotNull(factory.newAuthor());
    assertNotNull(factory.newCategories());
    assertNotNull(factory.newCategory());
    assertNotNull(factory.newCollection());
    assertNotNull(factory.newContent());
    assertNotNull(factory.newContent(Content.Type.TEXT));
    assertNotNull(factory.newContributor());
    assertNotNull(factory.newControl());
    assertNotNull(factory.newDefaultGenerator());
    assertNotNull(factory.newDiv());
    assertNotNull(factory.newDocument());
    assertNotNull(factory.newEdited());
    assertNotNull(factory.newElement(Constants.ENTRY));
    assertNotNull(factory.newElement(Entry.class));
    assertNotNull(factory.newEmail());
    assertNotNull(factory.newExtensionElement(Total.class));
    assertNotNull(factory.newExtensionElement(new QName("a","b")));
    assertNotNull(factory.newFeed());
    assertNotNull(factory.newGenerator());
    assertNotNull(factory.newIcon());
    assertNotNull(factory.newID());
    assertNotNull(factory.newLink());
    assertNotNull(factory.newLogo());
    assertNotNull(factory.newName());
    assertNotNull(factory.newParser());
    assertNotNull(factory.newPublished());
    assertNotNull(factory.newRights());
    assertNotNull(factory.newRights(Text.Type.TEXT));
    assertNotNull(factory.newService());
    assertNotNull(factory.newSource());
    assertNotNull(factory.newSubtitle());
    assertNotNull(factory.newSummary());
    assertNotNull(factory.newTitle());
    assertNotNull(factory.newUpdated());
    assertNotNull(factory.newUri());
    assertNotNull(factory.newUuidUri());
    assertNotNull(factory.newWorkspace());
    assertNotNull(factory.listExtensionFactories());
    
  }
  
  @Test
  public void extensionFactoryTest() {
    Abdera abdera = Abdera.getInstance();
    Factory factory = abdera.getFactory();
    TestExtensionFactory tef = new TestExtensionFactory();
    factory.registerExtension(tef);
    TestExtension te = factory.newExtensionElement(TestExtension.class);
    assertNotNull(te);
    
    Entry entry = abdera.newEntry();
    te = entry.addExtension(TestExtension.class);
    assertNotNull(te);
  }
  
  @Namespace({"foo","b"})
  @Impls(@Impl(TestExtension.class))
  public static class TestExtensionFactory extends AbstractExtensionFactory {}

  @org.apache.abdera2.common.anno.QName(value="a",ns="b")  
  public static class TestExtension extends ExtensibleElementWrapper{
    public TestExtension(Element internal) {
      super(internal);
    }
    public TestExtension(Factory factory, QName qname) {
      super(factory, qname);
    }
  }
}
