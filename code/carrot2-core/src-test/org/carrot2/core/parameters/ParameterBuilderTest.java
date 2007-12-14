package org.carrot2.core.parameters;

import java.util.*;

import org.carrot2.core.Configurable;
import org.carrot2.core.constraints.IntRange;
import org.carrot2.core.constraints.RangeImplementator;
import org.junit.Assert;
import org.junit.Test;

public class ParameterBuilderTest
{
    @Bindable
    public static class TestClass
    {
        @SuppressWarnings("unused")
        @Binding(policy = BindingPolicy.INSTANTIATION)
        private int instanceField = 5;

        @SuppressWarnings("unused")
        @Binding(policy = BindingPolicy.RUNTIME)
        private int runtimeField = 5;

        @SuppressWarnings("unused")
        @Binding(policy = BindingPolicy.RUNTIME)
        @IntRange(min=0, max=10)
        private int runtimeField2 = 5;
    }

    @Test
    public void testGetParametersMap()
    {
        Collection<String> expected = Arrays.asList(new String []
        {
            "runtimeField2", "runtimeField"
        });

        Collection<String> actual = ParameterBuilder.getFieldMap(TestClass.class,
            BindingPolicy.RUNTIME).keySet();

        Assert.assertEquals(expected, new ArrayList<String>(actual));
    }

    @Test
    public void testGetParametersRuntime()
    {
        Collection<Parameter> expected = Arrays.asList(new Parameter []
        {
            new Parameter("runtimeField2", Integer.class, 5, new RangeImplementator<Integer>(0, 10)),
            new Parameter("runtimeField", Integer.class, 5, null)
        });

        Collection<Parameter> actual = ParameterBuilder.getParameters(TestClass.class,
            BindingPolicy.RUNTIME);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetParametersInstance()
    {
        Collection<Parameter> expected = Arrays.asList(new Parameter []
        {
            new Parameter("instanceField", Integer.class, 5, null),
        });

        Collection<Parameter> actual = ParameterBuilder.getParameters(TestClass.class,
            BindingPolicy.INSTANTIATION);

        Assert.assertEquals(expected, actual);
    }
}
