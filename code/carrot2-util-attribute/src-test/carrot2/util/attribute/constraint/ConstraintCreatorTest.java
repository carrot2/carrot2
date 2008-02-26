package carrot2.util.attribute.constraint;

import static org.junit.Assert.*;

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
    public void testCreateImplementationSuccess() throws InstantiationException,
        IllegalAccessException
    {
        final Constraint impl = ConstraintFactory.createImplementation(IntRange.class
            .getAnnotation(IsConstraint.class));
        assertNotNull(impl);
        assertEquals(RangeConstraint.class, impl.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateImplementationException() throws InstantiationException,
        IllegalAccessException
    {
        ConstraintFactory.createImplementation(TestConstraintAnnotation.class
            .getAnnotation(IsConstraint.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateConstraint() throws NoSuchFieldException
    {
        final TestSample sample = new TestSample();

        {
            final IntRange range = TestSample.class.getField("somethingAmount").getAnnotation(
                IntRange.class);
            final Constraint impl = ConstraintFactory.createConstraint(range);
            assertEquals(RangeConstraint.class, impl.getClass());
            final RangeConstraint rangeImpl = (RangeConstraint) impl;
            assertEquals(0, range.min());
            assertEquals(8, range.max());
            assertEquals(0, rangeImpl.getMin());
            assertEquals(8, rangeImpl.getMax());
            assertTrue(rangeImpl.isMet(sample.somethingAmount));
            sample.somethingAmount = 9;
            assertFalse(rangeImpl.isMet(sample.somethingAmount));
        }

        {
            final DoubleRange range = TestSample.class.getField("somethingPercent")
                .getAnnotation(DoubleRange.class);
            final Constraint impl = ConstraintFactory.createConstraint(range);
            assertEquals(RangeConstraint.class, impl.getClass());
            final RangeConstraint rangeImpl = (RangeConstraint) impl;
            assertEquals(0.2, range.min(), 0.000001);
            assertEquals(0.5, range.max(), 0.000001);
            assertEquals(0.2, Double.class.cast(rangeImpl.getMin()), 0.000001);
            assertEquals(0.5, Double.class.cast(rangeImpl.getMax()), 0.000001);
            assertFalse(rangeImpl.isMet(sample.somethingPercent));
            sample.somethingPercent = 0.2;
            assertTrue(rangeImpl.isMet(sample.somethingPercent));
            sample.somethingPercent = 0;
            assertFalse(rangeImpl.isMet(sample.somethingPercent));
        }
    }
}
