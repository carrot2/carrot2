package org.carrot2.core.parameters;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.carrot2.core.Configurable;
import org.carrot2.core.type.BoundedIntegerTypeWithDefaultValue;
import org.carrot2.core.type.Type;
import org.junit.Assert;
import org.junit.Test;

public class ParameterBuilderTest
{
	private static class TestClass
		implements Configurable
	{
	    @Binding(BindingPolicy.INSTANTIATION)
	    private int instanceField;
	    private static Type<?> INSTANCEFIELD = new BoundedIntegerTypeWithDefaultValue(5, 0, 10);

	    @Binding(BindingPolicy.RUNTIME)
	    private int runtimeField;
	    private static Type<?> RUNTIMEFIELD = new BoundedIntegerTypeWithDefaultValue(5, 0, 10);		

		@Override
		public ParameterGroup getParameters()
		{
			throw new UnsupportedOperationException();
		}
	}

	private static class BadClass
		implements Configurable
	{
	    @Binding(BindingPolicy.RUNTIME)
	    private float runtimeField;
	    private static Type<?> RUNTIMEFIELD = new BoundedIntegerTypeWithDefaultValue(5, 0, 10);		

		@Override
		public ParameterGroup getParameters()
		{
			throw new UnsupportedOperationException();
		}
	}

    @Test
    public void testGetParametersMap()
    {
        Collection<String> expected = Arrays.asList(
                new String [] {
                        "runtimeField"
                });
    
        Collection<String> actual = ParameterBuilder.getFieldMap(
                TestClass.class, BindingPolicy.RUNTIME).keySet();
    
        Assert.assertEquals(expected, new ArrayList<String>(actual));
    }
	
	@Test
	public void testGetParametersRuntime()
	{
		Collection<Parameter> expected = Arrays.asList(
				new Parameter [] {
						new Parameter("runtimeField", new BoundedIntegerTypeWithDefaultValue(5, 0, 10))
				});
	
		Collection<Parameter> actual = ParameterBuilder.getParameters(
				TestClass.class, BindingPolicy.RUNTIME);
	
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetParametersInstance()
	{
		Collection<Parameter> expected = Arrays.asList(
				new Parameter [] {
						new Parameter("instanceField", new BoundedIntegerTypeWithDefaultValue(5, 0, 10))
				});

		Collection<Parameter> actual = ParameterBuilder.getParameters(
				TestClass.class, BindingPolicy.INSTANTIATION);

		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testTypeEquality()
	{
		try {
			ParameterBuilder.getParameters(
				BadClass.class, BindingPolicy.RUNTIME);

			fail();
		} catch (RuntimeException e) {
			// ok, expected.
		}
	}	
}
