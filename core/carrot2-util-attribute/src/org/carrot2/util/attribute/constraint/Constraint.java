package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;

/**
 * A base class for implementing constraints.
 */
public abstract class Constraint
{
    /** Annotation corresponding to this constraint */
    protected Annotation annotation;

    /**
     * Checks if the provided <code>value</code> meets this constraint.
     */
    protected abstract boolean isMet(Object value);

    /**
     * Checks if the provided <code>value</code> can be assigned to the type defined by
     * <code>clazz</code>. If not, an {@link IllegalArgumentException} will be thrown.
     */
    protected void checkAssignableFrom(Class<?> clazz, Object value)
    {
        if (!clazz.isAssignableFrom(value.getClass()))
        {
            throw new IllegalArgumentException("Expected an instance of "
                + clazz.getClass().getName() + " but found " + value.getClass().getName());
        }
    }
    
    public final void populate(Annotation annotation)
    {
        this.annotation = annotation;
        populateCustom(annotation);
    }
    
    protected void populateCustom(Annotation annotation)
    {
    }
}
