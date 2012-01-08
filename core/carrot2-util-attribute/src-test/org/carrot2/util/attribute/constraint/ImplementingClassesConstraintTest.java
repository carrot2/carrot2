
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
 * Test cases for {@link ImplementingClasses} constraint.
 */
public class ImplementingClassesConstraintTest extends
    ConstraintTestBase<ImplementingClasses>
{

    static class AnnotationContainer
    {
        @ImplementingClasses(classes =
        {
            String.class, Integer.class, Boolean.class
        })
        Object strictField;

        @ImplementingClasses(classes =
        {
            String.class, StringBuffer.class
        }, strict = false)
        CharSequence nonStrictField;
    }

    private static final String STRICT_FIELD_NAME = "strictField";
    private static final String NON_STRICT_FIELD_NAME = "nonStrictField";

    @Override
    Class<?> getAnnotationContainerClass()
    {
        return AnnotationContainer.class;
    }

    @Override
    Class<ImplementingClasses> getAnnotationType()
    {
        return ImplementingClasses.class;
    }

    @Override
    boolean isInvalidTypeChecked()
    {
        return false;
    }

    @Test
    public void testNull() throws Exception
    {
        assertMet(null, STRICT_FIELD_NAME);
    }

    @Test
    public void testValidStrictType() throws Exception
    {
        assertMet("test", STRICT_FIELD_NAME);
        assertMet(Integer.valueOf(10), STRICT_FIELD_NAME);
        assertMet(Boolean.FALSE, STRICT_FIELD_NAME);
    }

    @Test
    public void testInvalidStrictType() throws Exception
    {
        assertNotMet(new Object(), STRICT_FIELD_NAME);
    }

    @Test
    public void testValidNonStrictType() throws Exception
    {
        assertMet("test", NON_STRICT_FIELD_NAME);
        assertMet(new StringBuffer("test"), NON_STRICT_FIELD_NAME);
        assertMet(new StringBuilder("test"), NON_STRICT_FIELD_NAME);
    }

    @Test
    public void testInvalidNonStrictType() throws Exception
    {
        // In fact, a value that is not assignable to the field is also valid.
        // It probably doesn't make sense to implement this particular check in the
        // constraint, the assignment will fail anyway (when performing it through
        // reflection).
        assertMet(new Object(), NON_STRICT_FIELD_NAME);
    }
}
