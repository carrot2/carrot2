/**
 *
 */
package org.carrot2.util.attribute.constraint;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.junit.Test;

/**
 *
 */
public class ConstraintUtilsTest
{
    private static final Class<?> [] CLASSES = new Class<?> []
    {
        Integer.class, Double.class
    };
    private static final Constraint CONSTRAINT = new ImplementingClassesConstraint(
        CLASSES);

    @Test
    public void testImplementingClasses()
    {
        assertEquals(CLASSES, ConstraintUtils.getImplementingClasses(CONSTRAINT));
    }

    @Test
    public void testRangeConstraint()
    {
        assertNull(ConstraintUtils.getImplementingClasses(new RangeConstraint(10, 20)));
    }

    @Test
    public void testNestedCompoundConstraint()
    {
        final CompoundConstraint compoundConstraint = new CompoundConstraint(
            new CompoundConstraint(CONSTRAINT));

        assertEquals(CLASSES, ConstraintUtils.getImplementingClasses(compoundConstraint));
    }
}
