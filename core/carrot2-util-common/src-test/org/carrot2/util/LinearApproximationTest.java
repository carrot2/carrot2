package org.carrot2.util;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

/**
 * Test cases for {@link LinearApproximation}.
 */
public class LinearApproximationTest
{
    @Test
    public void testOnePoint()
    {
        LinearApproximation la = new LinearApproximation(new double []
        {
            1.0
        }, 0.0, 1.0);

        assertEquals(1.0, la.getValue(-0.5), 0.0);
        assertEquals(1.0, la.getValue(0.0), 0.0);
        assertEquals(1.0, la.getValue(0.1), 0.0);
        assertEquals(1.0, la.getValue(0.9), 0.0);
        assertEquals(1.0, la.getValue(1.0), 0.0);
        assertEquals(1.0, la.getValue(1.5), 0.0);
    }

    @Test
    public void testTwoPoints()
    {
        LinearApproximation la = new LinearApproximation(new double []
        {
            0.0, 1.0
        }, 0.0, 1.0);

        assertEquals(0.0, la.getValue(-0.5), 0.0);
        assertEquals(0.0, la.getValue(0.0), 0.0);
        assertEquals(0.1, la.getValue(0.1), 0.0);
        assertEquals(0.9, la.getValue(0.9), 0.0);
        assertEquals(1.0, la.getValue(1.0), 0.0);
        assertEquals(1.0, la.getValue(1.5), 0.0);
    }

    @Test
    public void testThreePoints()
    {
        LinearApproximation la = new LinearApproximation(new double []
        {
            0.0, 0.5, 1.5
        }, 0.0, 1.0);

        assertEquals(0.0, la.getValue(-0.5), 0.0);
        assertEquals(0.0, la.getValue(0.0), 0.0);
        assertEquals(0.1, la.getValue(0.1), 0.0);
        assertEquals(0.5, la.getValue(0.5), 0.0);
        assertEquals(0.7, la.getValue(0.6), 0.0);
        assertEquals(1.3, la.getValue(0.9), 0.0);
        assertEquals(1.5, la.getValue(1.0), 0.0);
        assertEquals(1.5, la.getValue(1.5), 0.0);
    }
}
