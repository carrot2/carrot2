package org.carrot2.core.parameter;

import java.util.*;

import org.carrot2.core.constraint.*;
import org.junit.Assert;
import org.junit.Test;

public class ParameterDescriptorBuilderTest
{
    @Bindable(prefix = "Test")
    public static class TestClass
    {
        @SuppressWarnings("unused")
        @Parameter(policy = BindingPolicy.INSTANTIATION)
        private int instanceField = 5;

        @SuppressWarnings("unused")
        @Parameter(policy = BindingPolicy.RUNTIME)
        @IntRange(min = 0, max = 10)
        @IntModulo(modulo = 2)
        private int runtimeField = 5;

        @SuppressWarnings("unused")
        @Parameter(policy = BindingPolicy.RUNTIME)
        @IntRange(min = 0, max = 10)
        private int runtimeField2 = 5;
    }

    @Bindable(prefix = "Test")
    public static class TestSubclass extends TestClass
    {
        @SuppressWarnings("unused")
        @Parameter(policy = BindingPolicy.INSTANTIATION)
        private int subclassInstanceField = 5;

        @SuppressWarnings("unused")
        @Parameter(policy = BindingPolicy.RUNTIME)
        private int subclassRuntimeField = 5;
    }

    @Test
    public void testGetParametersMap()
    {
        Collection<String> expected = Arrays.asList(new String []
        {
            "Test.runtimeField2", "Test.runtimeField"
        });

        Collection<String> actual = ParameterDescriptorBuilder.getParameterFieldMap(
            TestClass.class, BindingPolicy.RUNTIME).keySet();

        Assert.assertEquals(expected, new ArrayList<String>(actual));
    }

    @Test
    public void testGetParametersMapForSubclass()
    {
        Collection<String> expected = Arrays.asList(new String []
        {
            "Test.subclassRuntimeField", "Test.runtimeField2", "Test.runtimeField"
        });

        Collection<String> actual = ParameterDescriptorBuilder.getParameterFieldMap(
            TestSubclass.class, BindingPolicy.RUNTIME).keySet();

        Assert.assertEquals(expected, new ArrayList<String>(actual));
    }

    @Test
    public void testGetParametersRuntime() throws SecurityException, NoSuchFieldException
    {
        Collection<ParameterDescriptor> expected = Arrays
            .asList(new ParameterDescriptor []
            {
                new ParameterDescriptor("Test.runtimeField2", 5, new RangeConstraint(0,
                    10), TestClass.class.getDeclaredField("runtimeField2")),
                new ParameterDescriptor("Test.runtimeField", 5, new CompoundConstraint(
                    new RangeConstraint(0, 10), new IntModuloConstraint(2, 0)),
                    TestClass.class.getDeclaredField("runtimeField"))
            });

        Collection<ParameterDescriptor> actual = ParameterDescriptorBuilder
            .getParameterDescriptors(TestClass.class, BindingPolicy.RUNTIME);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetParametersRuntimeForSubclass() throws SecurityException,
        NoSuchFieldException
    {
        Collection<ParameterDescriptor> expected = Arrays
            .asList(new ParameterDescriptor []
            {
                new ParameterDescriptor("Test.subclassRuntimeField", 5, null,
                    TestSubclass.class.getDeclaredField("subclassRuntimeField")),
                new ParameterDescriptor("Test.runtimeField2", Integer.class,
                    new RangeConstraint(0, 10), TestClass.class
                        .getDeclaredField("runtimeField2")),
                new ParameterDescriptor("Test.runtimeField", Integer.class,
                    new CompoundConstraint(new RangeConstraint(0, 10),
                        new IntModuloConstraint(2, 0)), TestClass.class
                        .getDeclaredField("runtimeField")),
            });

        Collection<ParameterDescriptor> actual = ParameterDescriptorBuilder
            .getParameterDescriptors(TestSubclass.class, BindingPolicy.RUNTIME);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetParametersInstance() throws SecurityException,
        NoSuchFieldException
    {
        Collection<ParameterDescriptor> expected = Arrays
            .asList(new ParameterDescriptor []
            {
                new ParameterDescriptor("Test.instanceField", 5, null, TestClass.class
                    .getDeclaredField("instanceField")),
            });

        Collection<ParameterDescriptor> actual = ParameterDescriptorBuilder
            .getParameterDescriptors(TestClass.class, BindingPolicy.INSTANTIATION);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetParametersInstanceForSubclass() throws SecurityException,
        NoSuchFieldException
    {
        Collection<ParameterDescriptor> expected = Arrays
            .asList(new ParameterDescriptor []
            {
                new ParameterDescriptor("Test.subclassInstanceField", 5, null,
                    TestSubclass.class.getDeclaredField("subclassInstanceField")),
                new ParameterDescriptor("Test.instanceField", 5, null, TestClass.class
                    .getDeclaredField("instanceField")),
            });

        Collection<ParameterDescriptor> actual = ParameterDescriptorBuilder
            .getParameterDescriptors(TestSubclass.class, BindingPolicy.INSTANTIATION);

        Assert.assertEquals(expected, actual);
    }
}
