package org.carrot2.core.parameters;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * 
 */
public class AttributeBinderTest
{
    @Bindable
    public static class TestImpl
    {
        @Attribute(key="inField", direction = Direction.IN)
        private int inField = 5;

        @Attribute(key="inoutField", direction = Direction.INOUT)
        private int inoutField = 5;

        @Attribute(key="outField", direction = Direction.OUT)
        private int outField = 5;
        
        @Attribute(direction = Direction.INOUT)
        private int defaultKeyField = 5;
    }

    @Bindable(prefix = "prefix")
    public static class TestImpl2
    {
        @Attribute(direction = Direction.IN)
        private int inField = 5;

        private TestImpl referenced = new TestImpl();
    }

    @Test
    public void testInAttributes() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("inField", 10);
        params.put("outField", 10);

        TestImpl instance = new TestImpl();
        AttributeBinder.bind(instance, params, Direction.IN);
        assertEquals(10, instance.inField);
        assertEquals(5, instance.outField);
        assertEquals(5, instance.inoutField);
    }

    @Test
    public void testOutAttributes() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("inField", 10);
        params.put("outField", 10);

        TestImpl instance = new TestImpl();
        AttributeBinder.bind(instance, params, Direction.OUT);
        assertEquals(new Integer(5), params.get("outField"));
        assertEquals(new Integer(10), params.get("inField"));
        assertEquals(new Integer(5), params.get("inoutField"));
    }
    
    @Test
    public void testDefaultKey() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        final String fieldName = TestImpl.class.getName() + ".defaultKeyField";
        final TestImpl instance = new TestImpl();

        AttributeBinder.bind(instance, params, Direction.INOUT);
        assertEquals(new Integer(5), params.get(fieldName));

        params.put(fieldName, 10);
        AttributeBinder.bind(instance, params, Direction.INOUT);
        assertEquals(new Integer(10), params.get(fieldName));
    }
    
    @Test
    public void testClassPrefix() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("prefix.inField", 10);

        TestImpl2 instance = new TestImpl2();
        AttributeBinder.bind(instance, params, Direction.IN);
        assertEquals(10, instance.inField);
    }
    
    @Test
    public void testReferenced() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();

        TestImpl2 instance = new TestImpl2();
        AttributeBinder.bind(instance, params, Direction.INOUT);

        assertEquals(new Integer(5), params.get("prefix.inField"));
        assertEquals(new Integer(5), params.get("inField"));

        params.put("prefix.inField", 10);
        params.put("inField", 10);        
        AttributeBinder.bind(instance, params, Direction.INOUT);
        assertEquals(10, instance.inField);
        assertEquals(10, instance.referenced.inField);
    }        
}
