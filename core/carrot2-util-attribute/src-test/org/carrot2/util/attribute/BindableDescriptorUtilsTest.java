package org.carrot2.util.attribute;

import static org.junit.Assert.*;
import org.junit.Test;

public class BindableDescriptorUtilsTest
{
    /**
     * 
     */
    @Test
    public void testGetDescriptorClassName()
    {
        assertEquals(
            "MyClassDescriptor", 
            BindableDescriptorUtils.getDescriptorClassName("MyClass"));

        assertEquals(
            "com.mypackage.MyClassDescriptor", 
            BindableDescriptorUtils.getDescriptorClassName("com.mypackage.MyClass"));

        assertEquals(
            "com.mypackage.MyClassDescriptor", 
            BindableDescriptorUtils.getDescriptorClassName("com.mypackage.Outer$MyClass"));

        assertEquals(
            "com.mypackage.MyClassDescriptor", 
            BindableDescriptorUtils.getDescriptorClassName("com.mypackage.Outer$Nested$MyClass"));
    }
}
