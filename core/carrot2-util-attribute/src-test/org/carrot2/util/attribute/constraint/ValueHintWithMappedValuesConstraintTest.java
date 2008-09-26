package org.carrot2.util.attribute.constraint;

import org.junit.Test;

/**
 * Test cases for {@link ValueHintEnum} constraint with restriction 
 * enum implementing {@link ValueHintMapping}.
 */
public class ValueHintWithMappedValuesConstraintTest extends ConstraintTestBase<ValueHintEnum>
{
    public enum TestMappedValueSet implements ValueHintMapping
    {
        VALUE_1("v1"),
        VALUE_2("v2");

        private final String value;
        
        private TestMappedValueSet(String value)
        {
            this.value = value;
        }

        public String getAttributeValue()
        {
            return value;
        }

        public String getUserFriendlyName()
        {
            return this.name().toLowerCase().replace('_', ' ');
        }
    }

    static class AnnotationContainer
    {
        @ValueHintEnum(values = TestMappedValueSet.class, strict = true)
        String strict;
        
        @ValueHintEnum(values = TestMappedValueSet.class)
        String hint;
    }

    @Override
    Class<?> getAnnotationContainerClass()
    {
        return AnnotationContainer.class;
    }

    @Override
    Class<ValueHintEnum> getAnnotationType()
    {
        return ValueHintEnum.class;
    }

    @Test
    public void testValueIsAHint() throws Exception
    {
        assertMet("v1", "strict");
        assertMet("v2", "strict");
        assertNotMet("VALUE_1", "strict");

        assertMet("v1", "hint");
    }

    @Test
    public void testValueNotAHint() throws Exception
    {
        assertNotMet("", "strict");
        assertMet("", "hint");
    }

    @Test
    public void testNull() throws Exception
    {
        assertMet(null, "strict");
        assertMet(null, "hint");
    }
    
    @Override
    String getConstrainedFieldName()
    {
        return "strict";
    }
}
