package org.apache.abdera2.test.parser.axiom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Text;
import org.junit.Test;

public class FOMDivTest {
    
    @Test
    public void getInternalValueWithUtf8Characters (){
        Abdera abdera = Abdera.getInstance();
        InputStream in = FOMTest.class.getResourceAsStream("/utf8characters.xml");
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();

        assertEquals("Item", entry.getTitle());
        assertEquals(Text.Type.TEXT, entry.getTitleType());
        String value = entry.getContentElement().getValue();
        assertTrue(value.contains("\u0200\u0201"));
    }
}
