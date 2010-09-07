
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.constraint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.carrot2.util.attribute.test.constraint.ConstraintAnnotationWithWrongImplementingClass;
import org.carrot2.util.attribute.test.constraint.TestConstraint1;
import org.carrot2.util.attribute.test.constraint.TestConstraint1Constraint;
import org.carrot2.util.attribute.test.constraint.TestConstraint2;
import org.carrot2.util.attribute.test.constraint.TestConstraint2Constraint;
import org.junit.Test;

public class ConstraintCreatorTest
{
    static class TestSample
    {
        @TestConstraint1(value = 10)
        public int intConstraint1;

        @TestConstraint1(value = 7)
        @TestConstraint2(value = 5)
        public int intConstraint1And2;

        @ConstraintAnnotationWithWrongImplementingClass
        public int wrongConstraint;
    }

    @Test
    public void testIsConstraintAnnotation()
    {
        assertTrue(ConstraintFactory.isConstraintAnnotation(TestConstraint1.class));
        assertFalse(ConstraintFactory.isConstraintAnnotation(Target.class));
    }

    @Test
    public void testCreateImplementationSuccess() throws Exception
    {
        final Constraint impl = ConstraintFactory.createImplementation(TestSample.class
            .getField("intConstraint1").getAnnotation(TestConstraint1.class));
        assertNotNull(impl);
        assertEquals(TestConstraint1Constraint.class, impl.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateImplementationException() throws Exception
    {
        ConstraintFactory.createImplementation(TestSample.class.getField(
            "wrongConstraint").getAnnotation(
            ConstraintAnnotationWithWrongImplementingClass.class));
    }

    @Test
    public void testCreateSingleConstraint() throws NoSuchFieldException
    {
        final Field field = TestSample.class.getField("intConstraint1");
        final TestConstraint1 constraint1 = field
            .getAnnotation(TestConstraint1.class);
        final List<Constraint> constraints = ConstraintFactory.createConstraints(field
            .getAnnotations());

        assertEquals(1, constraints.size());

        final TestConstraint1Constraint constraint1Impl = (TestConstraint1Constraint) constraints
            .get(0);
        assertEquals(10, constraint1.value());
        assertEquals(10, constraint1Impl.value);
    }

    @Test
    public void testCreateMultipleConstraints() throws NoSuchFieldException
    {
        final Field field = TestSample.class.getField("intConstraint1And2");
        final TestConstraint1 constraint1 = field
            .getAnnotation(TestConstraint1.class);
        final TestConstraint2 constraint2 = field
            .getAnnotation(TestConstraint2.class);
        final List<Constraint> constraints = ConstraintFactory.createConstraints(field
            .getAnnotations());

        assertEquals(2, constraints.size());

        Collections.sort(constraints, new Comparator<Constraint>()
        {
            public int compare(Constraint o1, Constraint o2)
            {
                return o1.getClass().getName()
                    .compareTo(o2.getClass().getName());
            }
        });

        final TestConstraint1Constraint constraint1Impl = (TestConstraint1Constraint) constraints
            .get(0);
        assertEquals(7, constraint1.value());
        assertEquals(7, constraint1Impl.value);

        final TestConstraint2Constraint constraint2Impl = (TestConstraint2Constraint) constraints
            .get(1);
        assertEquals(5, constraint2.value());
        assertEquals(5, constraint2Impl.value);
    }
}
