package org.carrot2.util.attribute.constraint;

import org.junit.Test;

/**
 * Test cases for {@link ValueHintEnum} constraint.
 */
public class ValueHintConstraintTest extends ConstraintTestBase<ValueHintEnum>
{
    public enum TestValueSet
    {
        VALUE_1,
        VALUE_2
    }

    static class AnnotationContainer
    {
        @ValueHintEnum(values = TestValueSet.class, strict = true)
        String strict;
        
        @ValueHintEnum(values = TestValueSet.class)
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
        assertMet("VALUE_1", "strict");
        assertMet("VALUE_1", "hint");
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
