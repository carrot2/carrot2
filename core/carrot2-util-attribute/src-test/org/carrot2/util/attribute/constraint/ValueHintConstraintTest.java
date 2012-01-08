
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.constraint;

import org.junit.Test;

/**
 * Test cases for {@link ValueHintEnum} constraint.
 */
public class ValueHintConstraintTest extends ConstraintTestBase<ValueHintEnum>
{
    static class AnnotationContainer
    {
        @ValueHintEnum(values = TestValueSet.class)
        String field;
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
    public void testHintValue() throws Exception
    {
        assertMet("VALUE_1", "field");
    }

    @Test
    public void testOtherValues() throws Exception
    {
        assertMet("anything", "field");
    }

    @Test
    public void testNull() throws Exception
    {
        assertMet(null, "field");
    }

    @Override
    String getConstrainedFieldName()
    {
        return "field";
    }

    public enum TestValueSet
    {
        VALUE_1,
        VALUE_2
    }
}
