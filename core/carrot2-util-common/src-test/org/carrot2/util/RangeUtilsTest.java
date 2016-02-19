
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

/**
 * Test cases for {@link RangeUtils}.
 */
public class RangeUtilsTest extends CarrotTestCase
{
    @Test
    public void testIntZeroRange()
    {
        checkIntRange(10, 10, 1, 1);
    }

    @Test
    public void testIntLessThan11Range()
    {
        checkIntRange(1, 5, 1, 1);
        checkIntRange(0, 10, 1, 1);
    }

    @Test
    public void testInt11To29Range()
    {
        checkIntRange(0, 11, 5, 5);
        checkIntRange(0, 29, 5, 5);
    }

    @Test
    public void testInt30To59Range()
    {
        checkIntRange(0, 30, 10, 5);
        checkIntRange(0, 59, 10, 5);
    }

    @Test
    public void testInt60To100Range()
    {
        checkIntRange(0, 60, 20, 10);
        checkIntRange(0, 100, 20, 10);
    }

    @Test
    public void testDoubleZeroRange()
    {
        checkDoubleRange(10.0, 10.0, 0.2, 0.1);
    }

    @Test
    public void testDoubleLessThan1Range()
    {
        checkDoubleRange(0, 0.5, 0.2, 0.1);
        checkDoubleRange(0, 1.0, 0.2, 0.1);
    }

    @Test
    public void testDouble1To3Range()
    {
        checkDoubleRange(0, 1.5, 0.5, 0.25);
        checkDoubleRange(0, 2.9, 0.5, 0.25);
    }

    @Test
    public void testDouble3To6Range()
    {
        checkDoubleRange(0, 3.0, 1, 0.5);
        checkDoubleRange(0, 5.9, 1, 0.5);
    }

    @Test
    public void testDouble6To10Range()
    {
        checkDoubleRange(0, 6, 2, 1);
        checkDoubleRange(0, 9.9, 2, 1);
    }

    private void checkIntRange(int min, int max, int expectedMajor, int expectedMinor)
    {
        assertEquals(expectedMajor, RangeUtils.getIntMajorTicks(min, max));
        assertEquals(expectedMinor, RangeUtils.getIntMinorTicks(min, max));

        // Check reverse
        assertEquals(expectedMajor, RangeUtils.getIntMajorTicks(max, min));
        assertEquals(expectedMinor, RangeUtils.getIntMinorTicks(max, min));
    }

    private void checkDoubleRange(double min, double max, double expectedMajor,
        double expectedMinor)
    {
        assertEquals(expectedMajor, RangeUtils.getDoubleMajorTicks(min, max), 0.001);
        assertEquals(expectedMinor, RangeUtils.getDoubleMinorTicks(min, max), 0.001);

        // Check reverse
        assertEquals(expectedMajor, RangeUtils.getDoubleMajorTicks(max, min), 0.001);
        assertEquals(expectedMinor, RangeUtils.getDoubleMinorTicks(max, min), 0.001);
    }
}
