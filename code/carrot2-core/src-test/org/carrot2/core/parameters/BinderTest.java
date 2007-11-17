package org.carrot2.core.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.carrot2.core.Configurable;
import org.carrot2.core.type.AnyClassTypeWithDefaultValue;
import org.carrot2.core.type.BoundedIntegerTypeWithDefaultValue;
import org.carrot2.core.type.Type;
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

    public static class TestImpl implements ITest
    {
        @Binding(BindingPolicy.INSTANTIATION)
        private int testIntField;
        public static Type<?> TEST_INT_FIELD = new BoundedIntegerTypeWithDefaultValue(5, 0, 10);        
    }
    
    public static class TestClass implements Configurable
    {
        @Binding(BindingPolicy.INSTANTIATION)
        private int instanceIntField;
        public static Type<?> INSTANCE_INT_FIELD = new BoundedIntegerTypeWithDefaultValue(5,
            0, 10);

        @Binding(BindingPolicy.INSTANTIATION)
        private ITest instanceRefField;
        public static Type<?> INSTANCE_REF_FIELD = new AnyClassTypeWithDefaultValue<ITest>(
            ITest.class, TestImpl.class);

        @Override
        public ParameterGroup getParameters()
        {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void testInstanceBinding()
        throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("instanceIntField", 6);

        TestClass instance = Binder.createInstance(TestClass.class, params);
        assertEquals(6, instance.instanceIntField);
        assertTrue(instance.instanceRefField != null 
            && instance.instanceRefField instanceof TestImpl);
        
        assertEquals(5, ((TestImpl) instance.instanceRefField).testIntField);
    }
}
