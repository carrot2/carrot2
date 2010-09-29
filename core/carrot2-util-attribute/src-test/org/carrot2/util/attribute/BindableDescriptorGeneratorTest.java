package org.carrot2.util.attribute;

import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

public class BindableDescriptorGeneratorTest
{
    @Bindable
    public static class D
    {
        @Attribute(key = "d")
        public int attrD;
    }

    @Bindable
    public static class A
    {
        @Attribute(key = "a")
        public int attrA;
        
        @Attribute(key = "c")
        public int attrC;        
    }
    
    public static class B extends A
    {
    }

    @Bindable
    public static class C extends B
    {
        @Attribute(key = "c")
        public int attrC;

        /* nested bindable type. */
        public final D attrD = new D();
    }

    @Test
    public void testAttributesC()
    {
        IBindableDescriptor descriptor = BindableDescriptorUtils.getDescriptor(C.class);
        Map<String, AttributeInfo> attributesByKey = descriptor.getAttributesByKey();

        // static attributes
        assertTrue(attributesByKey.containsKey("a"));
        assertTrue(attributesByKey.containsKey("c"));

        // 'd' is a nested bindable, its attributes are collected dynamically at runtime.
        assertFalse(attributesByKey.containsKey("d"));
    }

    @Test
    public void testOwnAttributesC()
    {
        IBindableDescriptor descriptor = BindableDescriptorUtils.getDescriptor(C.class);
        assertEquals(descriptor.getOwnAttributes().size(), 1);
    }

    @Test
    public void testKeysC() throws Exception
    {
        Class<? extends IBindableDescriptor> descriptorClass = 
            BindableDescriptorUtils.getDescriptorClass(C.class);
        
        Class<?> keysClass = getNestedClass(descriptorClass, "Keys");
        keysClass.getField("ATTR_A");
        keysClass.getField("ATTR_C");
    }

    @Test
    public void testAttributeDescriptorsC() throws Exception
    {
        Class<? extends IBindableDescriptor> descriptorClass = 
            BindableDescriptorUtils.getDescriptorClass(C.class);

        Class<?> keysClass = getNestedClass(descriptorClass, "Attributes");
        keysClass.getField("attrC");
    }

    @Test
    public void testAttributeBuilderC() throws Exception
    {
        Class<? extends IBindableDescriptor> descriptorClass = 
            BindableDescriptorUtils.getDescriptorClass(C.class);

        Class<?> keysClass = getNestedClass(descriptorClass, "AttributeBuilder");
        keysClass.getMethod("attrA", int.class);
        keysClass.getMethod("attrC", int.class);
        // nested builder
        keysClass.getMethod("attrD");
    }

    private Class<?> getNestedClass(Class<?> clazz, String nested)
    {
        for (Class<?> nestedClass : clazz.getClasses())
        {
            if (nestedClass.getSimpleName().equals(nested))
                return nestedClass;
        }

        throw new AssertionError("No such nested class: " + nested);
    }
    
    @Bindable
    public static class KeyCheck
    {
        @Attribute
        public int field;
    }

    @Test
    public void testKeyInKeyCheck()
    {
        IBindableDescriptor descriptor = BindableDescriptorUtils.getDescriptor(KeyCheck.class);
        Map<String, AttributeInfo> attributesByKey = descriptor.getAttributesByKey();
        assertTrue(attributesByKey.containsKey(AttributeUtils.getKey(KeyCheck.class, "field")));
    }    
}
