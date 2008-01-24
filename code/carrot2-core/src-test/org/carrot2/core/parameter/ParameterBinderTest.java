package org.carrot2.core.parameter;

import static org.junit.Assert.*;

import java.util.*;

import org.carrot2.core.constraint.*;
import org.junit.Test;

/**
 * 
 */
public class ParameterBinderTest
{
    public static interface ITest
    {
    }

    @Bindable
    public static class TestImpl implements ITest
    {
        @Init
        @Input
        @Parameter
        private int testIntField = 5;
    }

    @Bindable
    public static class TestBetterImpl implements ITest
    {
        @Init
        @Input
        @Parameter
        private int testIntField = 10;
    }

    @Bindable(prefix = "Test")
    public static class TestClass
    {
        @Init
        @Input
        @Parameter
        @IntRange(min = 0, max = 10)
        protected int instanceIntField = 5;

        @Init
        @Input
        @Parameter
        protected ITest instanceRefField = new TestImpl();

        @Processing
        @Input
        @Parameter
        @IntRange(min = 0, max = 10)
        protected int runtimeIntField = 5;
    }

    @Bindable(prefix = "Test")
    public static class TestSubclass extends TestClass
    {
        @Init
        @Input
        @Parameter
        @IntRange(min = 0, max = 10)
        private int subclassInstanceIntField = 5;

        @Init
        @Input
        @Parameter
        private ITest subclassInstanceRefField = new TestImpl();

        @Processing
        @Input
        @Parameter
        @IntRange(min = 0, max = 10)
        @IntModulo(modulo = 2, offset = 1)
        private int subclassRuntimeIntField = 5;
    }

    @Bindable(prefix = "Test")
    public static class TestSubclass2 extends TestClass
    {
        @Processing
        @Input
        @Parameter(key = "clone")
        protected int runtimeIntField = 7;
    }

    public static class NotBindable
    {
        @Processing
        @Input
        @Parameter
        private int test = 20;
    }

    @Test
    public void testInitInputBinding() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("Test.instanceIntField", 6);

        TestClass instance = ParameterBinder.createInstance(TestClass.class, params);

        assertEquals(6, instance.instanceIntField);
        assertTrue(instance.instanceRefField != null
            && instance.instanceRefField instanceof TestImpl);

        assertEquals(5, ((TestImpl) instance.instanceRefField).testIntField);
    }

    @Test
    public void testInitInputBindingForSubclass() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("Test.subclassInstanceIntField", 6);
        params.put("Test.instanceIntField", 7);

        TestSubclass instance = ParameterBinder
            .createInstance(TestSubclass.class, params);

        assertEquals(6, instance.subclassInstanceIntField);
        assertEquals(7, instance.instanceIntField);
        assertTrue(instance.subclassInstanceRefField != null
            && instance.subclassInstanceRefField instanceof TestImpl);
        assertTrue(instance.instanceRefField != null
            && instance.instanceRefField instanceof TestImpl);

        assertEquals(5, ((TestImpl) instance.subclassInstanceRefField).testIntField);
    }

    @Test
    public void testInitInputClassCoercion() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("Test.instanceRefField", TestBetterImpl.class);

        TestClass instance = ParameterBinder.createInstance(TestClass.class, params);
        assertEquals(5, instance.instanceIntField);
        assertTrue(instance.instanceRefField != null
            && instance.instanceRefField instanceof TestBetterImpl);

        assertEquals(10, ((TestBetterImpl) instance.instanceRefField).testIntField);
    }

    @Test
    public void testRuntimeClassCoercionAttempt() throws InstantiationException
    {
        TestClass instance = ParameterBinder.createInstance(TestClass.class, Collections
            .<String, Object> emptyMap());

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("Test.runtimeIntField", TestBetterImpl.class);

        try
        {
            ParameterBinder.bind(instance, params, Processing.class, Input.class);
            fail();
        }
        catch (RuntimeException e)
        {
            // expected
        }

        assertEquals("Field value not changed", 5, instance.runtimeIntField);
    }

    @Test
    public void testNotBindableBindingAttempt() throws InstantiationException
    {
        NotBindable instance = new NotBindable();

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", 20);

        try
        {
            ParameterBinder.bind(instance, params, Processing.class, Input.class);
            fail();
        }
        catch (RuntimeException e)
        {
            // expected
        }

        assertEquals("Field value not changed", 20, instance.test);
    }

    @Test
    public void testRuntimeBinding() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("Test.runtimeIntField", 6);

        TestClass instance = ParameterBinder.createInstance(TestClass.class, params);
        assertEquals(5, instance.runtimeIntField);

        ParameterBinder.bind(instance, params, Processing.class, Input.class);
        assertEquals(6, instance.runtimeIntField);
    }

    @Test
    public void testRuntimeBindingForSubclass() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("Test.runtimeIntField", 6);
        params.put("Test.subclassRuntimeIntField", 9);

        TestSubclass instance = ParameterBinder
            .createInstance(TestSubclass.class, params);
        assertEquals(5, instance.runtimeIntField);
        assertEquals(5, instance.subclassRuntimeIntField);

        ParameterBinder.bind(instance, params, Processing.class, Input.class);
        assertEquals(6, instance.runtimeIntField);
        assertEquals(9, instance.subclassRuntimeIntField);
    }

    @Test
    public void testFieldNamingConflict() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("Test.runtimeIntField", 6);
        params.put("clone", 9);

        TestSubclass2 instance = ParameterBinder.createInstance(TestSubclass2.class,
            params);
        assertEquals(7, instance.runtimeIntField);
        assertEquals(5, ((TestClass) instance).runtimeIntField);

        ParameterBinder.bind(instance, params, Processing.class, Input.class);
        assertEquals(6, ((TestClass) instance).runtimeIntField);
        assertEquals(9, instance.runtimeIntField);
    }

    @Test
    public void testSimpleConstraintEnforcement() throws InstantiationException
    {
        final Map<String, Object> violatingParams = new HashMap<String, Object>();
        violatingParams.put("Test.instanceIntField", 16);
        violatingParams.put("Test.runtimeIntField", 16);

        TestClass instance;
        try
        {
            instance = ParameterBinder.createInstance(TestClass.class, violatingParams);
            fail();
        }
        catch (ConstraintViolationException e)
        {
            assertEquals(16, e.getOffendingValue());
        }

        instance = ParameterBinder.createInstance(TestClass.class, Collections
            .<String, Object> emptyMap());

        try
        {
            ParameterBinder.bind(instance, violatingParams, Processing.class,
                Input.class);
            fail();
        }
        catch (ConstraintViolationException e)
        {
            assertEquals(16, e.getOffendingValue());
        }
    }

    @Test
    public void testCompoundConstraintEnforcement() throws InstantiationException
    {
        TestSubclass instance = new TestSubclass();
        final Map<String, Object> violatingParams = new HashMap<String, Object>();

        violatingParams.put("Test.subclassRuntimeIntField", 16);
        try
        {
            // First constraint violated
            ParameterBinder.bind(instance, violatingParams, Processing.class,
                Input.class);
            fail();
        }
        catch (ConstraintViolationException e)
        {
            assertEquals(16, e.getOffendingValue());
        }
        assertEquals("Field value not changed", 5, instance.subclassRuntimeIntField);

        violatingParams.put("Test.subclassRuntimeIntField", 6);
        try
        {
            // Second constraint violated
            ParameterBinder.bind(instance, violatingParams, Processing.class,
                Input.class);
            fail();
        }
        catch (ConstraintViolationException e)
        {
            assertEquals(6, e.getOffendingValue());
        }
        assertEquals("Field value not changed", 5, instance.subclassRuntimeIntField);

    }
}
