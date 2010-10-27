
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

public class BindableDescriptorGeneratorTest
{
    @Bindable
    public static class D
    {
        @Attribute(key = "d")
        @Input
        public int attrD;
    }

    @Bindable
    public static class A
    {
        @Attribute(key = "a")
        @Input
        public int attrA;
        
        @Attribute(key = "c")
        @Input
        public int attrC;        
    }
    
    public static class B extends A
    {
    }

    @Bindable
    public static class C extends B
    {
        @Attribute(key = "c")
        @Input
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
    
    /*
     * http://issues.carrot2.org/browse/CARROT-734 
     */
    @Bindable
    public static class OutputAttributeCheck
    {
        @Attribute
        @Input
        @Output
        public int inout;

        @Attribute
        @Input
        public int in;

        @Attribute
        @Output
        public int out;
        
        @Attribute
        @Input
        @Output
        public A inoutRef;

        @Attribute
        @Input
        public A inRef;

        @Attribute
        @Output
        public A outRef;
    }

    /*
     * http://issues.carrot2.org/browse/CARROT-734
     */
    @Test
    public void testInputOutputGettersSetters()
    {
        IBindableDescriptor descriptor = 
            BindableDescriptorUtils.getDescriptor(OutputAttributeCheck.class);

        Class<?> builder = getNestedClass(descriptor.getClass(), "AttributeBuilder");

        assertNotNull(getMethod(builder, "in", int.class));
        assertNotNull(getMethod(builder, "inout", int.class));
        assertNotNull("Expected getter for inout", getMethod(builder, "inout"));
        assertNotNull("Expected getter for out", getMethod(builder, "out"));
        assertNull("Unexpected setter for out", getMethod(builder, "out", int.class));

        assertNotNull(getMethod(builder, "inRef", A.class));
        assertNotNull(getMethod(builder, "inRef", Class.class));
        assertNotNull(getMethod(builder, "inoutRef", A.class));
        assertNotNull(getMethod(builder, "inoutRef", Class.class));
        assertNotNull("Expected getter for inoutRef", getMethod(builder, "inoutRef"));
        assertNotNull("Expected getter for outRef", getMethod(builder, "outRef"));
        assertNull("Unexpected setter for outRef", getMethod(builder, "outRef", A.class));
        assertNull(getMethod(builder, "outRef", Class.class));
    }

    private Method getMethod(Class<?> clazz, String methodName, Class<?>... args)
    {
        try
        {
            return clazz.getMethod(methodName, args);
        }
        catch (SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e)
        {
            return null;
        }
    }    
}
