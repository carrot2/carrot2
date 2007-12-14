package org.carrot2.core.constraints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Target;

import org.junit.Test;

public class ConstraintCreatorTest
{

    class TestSample
    {
        @IntRange(min = 0, max = 8)
        public int somethingAmount = 4;

        @DoubleRange(min = 0.2, max = 0.5)
        public double somethingPercent = 0.8;
    }

    @Test
    public void TestIsConstraintAnnotation()
    {
        assertTrue(ConstraintFactory.isConstraintAnnotation(IntRange.class));
        assertFalse(ConstraintFactory.isConstraintAnnotation(Target.class));
    }

    @Test
    public void testCreateImplementatorInstance()
    {
        try
        {
            Constraint<?> impl = ConstraintFactory
                .createImplementatorInstance(IntRange.class
                    .getAnnotation(IsConstraint.class));
            assertNotNull(impl);
            assertEquals(RangeImplementator.class, impl.getClass());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        try
        {
            ConstraintFactory.createImplementatorInstance(TestConstraintAnnotation.class
                .getAnnotation(IsConstraint.class));
            fail();
        }
        catch (Exception e)
        {
            // supposed to happen
        }
    }

    @Test
    public void testCreateConstraint() throws NoSuchFieldException
    {
        TestSample sample = new TestSample();
        {
            IntRange range = TestSample.class.getField("somethingAmount").getAnnotation(
                IntRange.class);
            Constraint<?> impl = ConstraintFactory.createConstraint(range);
            assertEquals(RangeImplementator.class, impl.getClass());
            RangeImplementator rangeImpl = (RangeImplementator) impl;
            assertEquals(0, range.min());
            assertEquals(8, range.max());
            assertEquals(0, rangeImpl.min);
            assertEquals(8, rangeImpl.max);
            assertTrue(rangeImpl.isMet(sample.somethingAmount));
            sample.somethingAmount = 9;
            assertFalse(rangeImpl.isMet(sample.somethingAmount));
        }
        {
            DoubleRange range = TestSample.class.getField("somethingPercent")
                .getAnnotation(DoubleRange.class);
            Constraint<?> impl = ConstraintFactory.createConstraint(range);
            assertEquals(RangeImplementator.class, impl.getClass());
            RangeImplementator rangeImpl = (RangeImplementator) impl;
            assertEquals(0.2, range.min(), 0.000001);
            assertEquals(0.5, range.max(), 0.000001);
            assertEquals(0.2, Double.class.cast(rangeImpl.min), 0.000001);
            assertEquals(0.5, Double.class.cast(rangeImpl.max), 0.000001);
            assertFalse(rangeImpl.isMet(sample.somethingPercent));
            sample.somethingPercent = 0.2;
            assertTrue(rangeImpl.isMet(sample.somethingPercent));
            sample.somethingPercent = 0;
            assertFalse(rangeImpl.isMet(sample.somethingPercent));
        }
    }
}
