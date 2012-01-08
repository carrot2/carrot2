
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
 * Test cases for {@link NotBlankConstraint}.
 */
public class NotBlankConstraintTest extends ConstraintTestBase<NotBlank>
{
    static class AnnotationContainer
    {
        @NotBlank
        String field;
    }

    @Override
    Class<?> getAnnotationContainerClass()
    {
        return AnnotationContainer.class;
    }

    @Override
    Class<NotBlank> getAnnotationType()
    {
        return NotBlank.class;
    }

    @Test
    public void testValidString() throws Exception
    {
        assertMet("  test  ");
    }

    @Test
    public void testInvalidString() throws Exception
    {
        assertNotMet("  \t");
    }
    
    @Override
    boolean isInvalidTypeChecked()
    {
        return false;
    }

    @Test
    public void testValidCharSequence() throws Exception
    {
        assertMet(new StringBuffer(" test "));
    }

    @Test
    public void testInvalidCharSequence() throws Exception
    {
        assertNotMet(new StringBuffer(" \t  "));
    }

    @Test
    public void testNull() throws Exception
    {
        assertNotMet(null);
    }
}
