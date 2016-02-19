
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

package org.carrot2.core;


/**
 * A wrapper around another {@link Object} that implements {@link Object#hashCode()}
 * and {@link Object#equals(Object)} based on reference equality of the delegate
 * object.
 * 
 * <p>{@link #equals(Object)} is implemented assuming only objects of this class
 * are compared against each other (there is a cast to {@link ReferenceEquality} inside).
 * It makes little sense to compare this class with other types anyway.</p>
 */
final class ReferenceEquality
{
    private final Object delegate;
    private final int identityHashCode;

    public ReferenceEquality(Object delegate)
    {
        if (delegate == null)
            throw new IllegalArgumentException("Delegate must not be null.");

        this.delegate = delegate;
        this.identityHashCode = System.identityHashCode(delegate);
    }
    
    @Override
    public int hashCode()
    {
        return identityHashCode;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null)
        {
            return false;
        }
        return ((ReferenceEquality) other).delegate == this.delegate;
    }
}
