
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
 * Test cases for {@link ValueHintEnum} constraint with restriction 
 * enum implementing {@link IValueHintMapping}.
 */
public class ValueHintWithMappedValuesConstraintTest extends ConstraintTestBase<ValueHintEnum>
{
    public enum TestMappedValueSet implements IValueHintMapping
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
        @ValueHintEnum(values = TestMappedValueSet.class)
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
        assertMet("v1", "field");
    }

    @Test
    public void testNotHintValue() throws Exception
    {
        assertMet("", "field");
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
}
