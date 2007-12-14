package org.carrot2.core.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.carrot2.core.constraints.IntRange;
import org.junit.Test;

/**
 * 
 */
public class BinderTest
{
    private final static Logger logger = Logger.getAnonymousLogger();

    public static interface ITest
    {
    }

    @Bindable
    public static class TestImpl implements ITest
    {
        @Parameter(policy = BindingPolicy.INSTANTIATION)
        private int testIntField = 5;
    }

    @Bindable
    public static class TestBetterImpl implements ITest
    {
        @Parameter(policy = BindingPolicy.INSTANTIATION)
        private int testIntField = 10;
    }

    @Bindable
    public static class TestClass
    {
        @Parameter(policy = BindingPolicy.INSTANTIATION)
        @IntRange(min = 0, max = 10)
        private int instanceIntField = 5;

        @Parameter(policy = BindingPolicy.INSTANTIATION)
        private ITest instanceRefField = new TestImpl();
    }

    @Test
    public void testInstanceBinding() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("instanceIntField", 6);

        TestClass instance = Binder.createInstance(TestClass.class, params);
        assertEquals(6, instance.instanceIntField);
        assertTrue(instance.instanceRefField != null
            && instance.instanceRefField instanceof TestImpl);

        assertEquals(5, ((TestImpl) instance.instanceRefField).testIntField);
    }

    @Test
    public void testClassCoercion() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("instanceRefField", TestBetterImpl.class);

        TestClass instance = Binder.createInstance(TestClass.class, params);
        assertEquals(5, instance.instanceIntField);
        assertTrue(instance.instanceRefField != null
            && instance.instanceRefField instanceof TestBetterImpl);

        assertEquals(10, ((TestBetterImpl) instance.instanceRefField).testIntField);
    }
}
