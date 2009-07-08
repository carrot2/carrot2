package org.carrot2.workbench.core.helpers;

import java.io.*;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.simpleframework.xml.*;

import com.google.common.collect.Maps;

/**
 * Test utilities converting simple XML beans to {@link IMemento}s.
 */
public class SimpleXmlMementoTest extends TestCase
{
    @Root(name = "root1")
    public static class MementoClazz1
    {
        @Element(required = false)
        public int value = 5;
    }

    @Root()
    public static class MementoClazz2
    {
        @Element
        public String value;

        @Element
        public MementoClazz1 nested;
        
        @ElementMap
        public Map<String, Boolean> map = Maps.newHashMap();
    }

    public void testSerializeDeserialize() throws IOException
    {
        MementoClazz1 o1 = new MementoClazz1();
        IMemento memento = SimpleXmlMemento.toMemento(o1);
        MementoClazz1 o2 = SimpleXmlMemento.fromMemento(MementoClazz1.class, memento);
        assertEquals(o1.value, o2.value);
    }

    public void testSerializeDeserializeComplex() throws IOException
    {
        MementoClazz1 o1 = new MementoClazz1();
        o1.value = 10;

        MementoClazz2 o2 = new MementoClazz2();
        o2.nested = o1;
        o2.value = "Buchacha";
        o2.map.put("key", true);
        IMemento memento = SimpleXmlMemento.toMemento(o2);

        MementoClazz2 o3 = SimpleXmlMemento.fromMemento(MementoClazz2.class, memento);
        assertEquals(o3.value, o2.value);
        assertEquals(o3.nested.value, o2.nested.value);
        assertTrue(o3.map.get("key"));
    }
    
    public void testAddGet() throws Exception
    {
        XMLMemento parent = XMLMemento.createWriteRoot("parent");

        MementoClazz1 o1 = new MementoClazz1();
        o1.value = 10;

        MementoClazz2 o2 = new MementoClazz2();
        o2.nested = o1;
        o2.value = "Buchacha";
        o2.map.put("key", true);
        SimpleXmlMemento.addChild(parent, o2);

        System.out.println(SimpleXmlMemento.toString(parent));

        StringWriter sw = new StringWriter();
        parent.save(sw);
        parent = XMLMemento.createReadRoot(new StringReader(sw.toString()));

        MementoClazz2 o3 = SimpleXmlMemento.getChild(MementoClazz2.class, parent);
        assertEquals(o3.value, o2.value);
        assertEquals(o3.nested.value, o2.nested.value);
        assertTrue(o3.map.get("key"));
    }
}
