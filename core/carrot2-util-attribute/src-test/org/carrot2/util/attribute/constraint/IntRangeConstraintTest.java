
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

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Test cases for {@link IntRange} constraint.
 */
public class IntRangeConstraintTest extends ConstraintTestBase<IntRange>
{
    static class AnnotationContainer
    {
        @IntRange(min = -2, max = 8)
        double field;
    }

    @Override
    Class<?> getAnnotationContainerClass()
    {
        return AnnotationContainer.class;
    }

    @Override
    Class<IntRange> getAnnotationType()
    {
        return IntRange.class;
    }

    @Test
    public void testNull() throws Exception
    {
        assertNotMet(null);
    }
    
    @Test
    public void testLessThanMin() throws Exception
    {
        assertNotMet(-10);
    }
    
    @Test
    public void testMinBound() throws Exception
    {
        assertMet(-2);
    }
    
    @Test
    public void testWithinRange() throws Exception
    {
        assertMet(0);
    }
    
    @Test
    public void testMaxBound() throws Exception
    {
        assertMet(8);
    }
    
    @Test
    public void testGreaterThanMax() throws Exception
    {
        assertNotMet(16);
    }
    
    @Test
    public void testOtherAssignableTypes() throws Exception
    {
        assertMet((byte)0);
        assertMet((short)0);
        assertMet(0);
        assertMet(new AtomicInteger(0));
    }
}
