package org.carrot2.core.attribute;

import java.util.*;

import org.carrot2.core.constraint.*;
import org.junit.Assert;
import org.junit.Test;

public class AttributeDescriptorBuilderTest
{
    @Bindable(prefix = "Test")
    public static class TestClass
    {
        @SuppressWarnings("unused")
        @Init
        @Input
        @Attribute
        private int instanceField = 5;

        @SuppressWarnings("unused")
        @Processing
        @Input
        @Attribute
        @IntRange(min = 0, max = 10)
        @IntModulo(modulo = 2)
        private int runtimeField = 5;

        @SuppressWarnings("unused")
        @Processing
        @Input
        @Attribute
        @IntRange(min = 0, max = 10)
        private int runtimeField2 = 5;
    }

    @Bindable(prefix = "Test")
    public static class TestSubclass extends TestClass
    {
        @SuppressWarnings("unused")
        @Init
        @Input
        @Attribute
        private int subclassInstanceField = 5;

        @SuppressWarnings("unused")
        @Processing
        @Input
        @Attribute
        private int subclassRuntimeField = 5;
    }

    @Test
    public void testGetParametersMap()
    {
        Collection<String> expected = Arrays.asList(new String []
        {
            "Test.runtimeField2", "Test.runtimeField"
        });

        Collection<String> actual = AttributeDescriptorBuilder.getParameterFieldMap(
            TestClass.class, Processing.class, Input.class).keySet();

        Assert.assertEquals(expected, new ArrayList<String>(actual));
    }

    @Test
    public void testGetParametersMapForSubclass()
    {
        Collection<String> expected = Arrays.asList(new String []
        {
            "Test.subclassRuntimeField", "Test.runtimeField2", "Test.runtimeField"
        });

        Collection<String> actual = AttributeDescriptorBuilder.getParameterFieldMap(
            TestSubclass.class, Processing.class, Input.class).keySet();

        Assert.assertEquals(expected, new ArrayList<String>(actual));
    }

    @Test
    public void testGetParametersRuntime() throws SecurityException, NoSuchFieldException
    {
        Collection<AttributeDescriptor> expected = Arrays
            .asList(new AttributeDescriptor []
            {
                new AttributeDescriptor("Test.runtimeField2", 5, new RangeConstraint(0,
                    10), TestClass.class.getDeclaredField("runtimeField2")),
                new AttributeDescriptor("Test.runtimeField", 5, new CompoundConstraint(
                    new RangeConstraint(0, 10), new IntModuloConstraint(2, 0)),
                    TestClass.class.getDeclaredField("runtimeField"))
            });

        Collection<AttributeDescriptor> actual = AttributeDescriptorBuilder
            .getParameterDescriptors(new TestClass(), Processing.class, Input.class);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetParametersRuntimeForSubclass() throws SecurityException,
        NoSuchFieldException
    {
        Collection<AttributeDescriptor> expected = Arrays
            .asList(new AttributeDescriptor []
            {
                new AttributeDescriptor("Test.subclassRuntimeField", 5, null,
                    TestSubclass.class.getDeclaredField("subclassRuntimeField")),
                new AttributeDescriptor("Test.runtimeField2", Integer.class,
                    new RangeConstraint(0, 10), TestClass.class
                        .getDeclaredField("runtimeField2")),
                new AttributeDescriptor("Test.runtimeField", Integer.class,
                    new CompoundConstraint(new RangeConstraint(0, 10),
                        new IntModuloConstraint(2, 0)), TestClass.class
                        .getDeclaredField("runtimeField")),
            });

        Collection<AttributeDescriptor> actual = AttributeDescriptorBuilder
            .getParameterDescriptors(new TestSubclass(), Processing.class,
                Input.class);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetParametersInstance() throws SecurityException,
        NoSuchFieldException
    {
        Collection<AttributeDescriptor> expected = Arrays
            .asList(new AttributeDescriptor []
            {
                new AttributeDescriptor("Test.instanceField", 5, null, TestClass.class
                    .getDeclaredField("instanceField")),
            });

        Collection<AttributeDescriptor> actual = AttributeDescriptorBuilder
            .getParameterDescriptors(new TestClass(), Init.class, Input.class);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetParametersInstanceForSubclass() throws SecurityException,
        NoSuchFieldException
    {
        Collection<AttributeDescriptor> expected = Arrays
            .asList(new AttributeDescriptor []
            {
                new AttributeDescriptor("Test.subclassInstanceField", 5, null,
                    TestSubclass.class.getDeclaredField("subclassInstanceField")),
                new AttributeDescriptor("Test.instanceField", 5, null, TestClass.class
                    .getDeclaredField("instanceField")),
            });

        Collection<AttributeDescriptor> actual = AttributeDescriptorBuilder
            .getParameterDescriptors(new TestSubclass(), Init.class, Input.class);

        Assert.assertEquals(expected, actual);
    }
}
