package org.carrot2.util.attribute.constraint;

import static org.junit.Assert.*;

import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;

public class ConstraintCreatorTest
{
    class TestSample
    {
        @DoubleRange(min = 0.2, max = 0.5)
        public double somethingPercent = 0.8;

        @IntRange(min = 0, max = 8)
        @IntModulo(modulo = 3, offset = 1)
        public int somethingAmount = 4;
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

    @Test
    public void testCreateSingleConstraint() throws NoSuchFieldException
    {
        final TestSample sample = new TestSample();

        final Field field = TestSample.class.getField("somethingPercent");
        final DoubleRange range = field.getAnnotation(DoubleRange.class);
        final List<Constraint> constraints = ConstraintFactory.createConstraints(field
            .getAnnotations());

        assertEquals(1, constraints.size());
        final RangeConstraint rangeImpl = (RangeConstraint) constraints.get(0);
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

    @Test
    public void testCreateMultipleConstraints() throws NoSuchFieldException
    {
        final TestSample sample = new TestSample();

        final Field field = TestSample.class.getField("somethingAmount");
        final IntRange range = field.getAnnotation(IntRange.class);
        final IntModulo modulo = field.getAnnotation(IntModulo.class);
        final List<Constraint> constraints = ConstraintFactory.createConstraints(field
            .getAnnotations());

        assertEquals(2, constraints.size());

        final RangeConstraint rangeImpl = (RangeConstraint) constraints.get(0);
        assertEquals(range.min(), rangeImpl.getMin());
        assertEquals(range.max(), rangeImpl.getMax());
        assertTrue(rangeImpl.isMet(sample.somethingAmount));
        sample.somethingAmount = 9;
        assertFalse(rangeImpl.isMet(sample.somethingAmount));

        final IntModuloConstraint moduloImpl = (IntModuloConstraint) constraints.get(1);
        assertEquals(modulo.modulo(), moduloImpl.getModulo());
        assertEquals(modulo.offset(), moduloImpl.getOffset());
        assertFalse(rangeImpl.isMet(sample.somethingAmount));
        sample.somethingAmount = 4;
        assertTrue(rangeImpl.isMet(sample.somethingAmount));
    }
}
