
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Test cases for {@link DoubleRange} constraint.
 */
public class DoubleRangeConstraintTest extends ConstraintTestBase<DoubleRange>
{
    static class AnnotationContainer
    {
        @DoubleRange(min = -0.5, max = 8)
        double field;
    }

    @Override
    Class<?> getAnnotationContainerClass()
    {
        return AnnotationContainer.class;
    }

    @Override
    Class<DoubleRange> getAnnotationType()
    {
        return DoubleRange.class;
    }

    @Test
    public void testNull() throws Exception
    {
        assertNotMet(null);
    }
    
    @Test
    public void testLessThanMin() throws Exception
    {
        assertNotMet(-10.0);
    }
    
    @Test
    public void testMinBound() throws Exception
    {
        assertMet(-0.5);
    }
    
    @Test
    public void testWithinRange() throws Exception
    {
        assertMet(0.0);
    }

    @Test
    public void testMaxBound() throws Exception
    {
        assertMet(8.0);
    }
    
    @Test
    public void testGreaterThanMax() throws Exception
    {
        assertNotMet(16.0);
    }
    
    @Test
    public void testOtherAssignableTypes() throws Exception
    {
        assertMet((byte)0);
        assertMet((short)0);
        assertMet(0);
        assertMet(0L);
        assertMet(0.0f);
        assertMet(new BigDecimal("0"));
        assertMet(new BigInteger("0"));
        assertMet(new AtomicInteger(0));
    }
}
