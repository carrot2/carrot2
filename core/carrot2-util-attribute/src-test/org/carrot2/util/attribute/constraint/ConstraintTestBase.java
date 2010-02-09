
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

import static org.fest.assertions.Assertions.assertThat;

import java.lang.annotation.Annotation;

import org.junit.Test;

/**
 * Base class for {@link Constraint} tests.
 */
public abstract class ConstraintTestBase<T extends Annotation>
{
    void assertMet(final Object value) throws NoSuchFieldException
    {
        assertMet(value, getConstrainedFieldName());
    }

    void assertNotMet(final Object value) throws NoSuchFieldException
    {
        assertNotMet(value, getConstrainedFieldName());
    }

    void assertMet(final Object value, String fieldName) throws NoSuchFieldException
    {
        assertThat(ConstraintValidator.isMet(value, getAnnotation(fieldName))).isEmpty();
    }

    void assertNotMet(final Object value, String fieldName) throws NoSuchFieldException
    {
        final T annotation = getAnnotation(fieldName);
        assertThat(ConstraintValidator.isMet(value, annotation)).contains(annotation);
    }

    private T getAnnotation(String fieldName) throws NoSuchFieldException
    {
        return getAnnotationContainerClass().getDeclaredField(fieldName).getAnnotation(
            getAnnotationType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidType() throws Exception
    {
        if (isInvalidTypeChecked())
        {
            assertMet(new UnknownType(), getInvalidTypeCheckFieldName());
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    boolean isInvalidTypeChecked()
    {
        return true;
    }

    abstract Class<T> getAnnotationType();

    abstract Class<?> getAnnotationContainerClass();

    String getConstrainedFieldName()
    {
        return "field";
    }
    
    String getInvalidTypeCheckFieldName()
    {
        return getConstrainedFieldName();
    }

    /**
     * A type that is guaranteed not to be supported by the constraint.
     */
    static class UnknownType
    {
    }
}
