
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

package org.carrot2.core.attribute;

import org.carrot2.util.attribute.AttributeDescriptor;

import org.carrot2.shaded.guava.common.base.Predicate;

/**
 * A predicate that tests whether an {@link AttributeDescriptor} refers to an
 * {@link Internal} attribute.
 */
public final class InternalAttributePredicate implements Predicate<AttributeDescriptor>
{
    private final Boolean configuration;

    /**
     * Evaluates to <code>true</code> for attributes that have the {@link Internal}
     * annotation, no matter the {@link Internal#configuration()} value.
     */
    public InternalAttributePredicate()
    {
        this(null);
    }

    /**
     * Evaluates to <code>true</code> for attributes that have the {@link Internal}
     * annotation whose {@link Internal#configuration()} is set to the provided value.
     */
    public InternalAttributePredicate(Boolean configuration)
    {
        this.configuration = configuration;
    }

    public boolean apply(AttributeDescriptor descriptor)
    {
        final Internal internal = descriptor.getAnnotation(Internal.class);
        if (internal != null)
        {
            if (configuration != null)
            {
                return internal.configuration() == configuration;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }
}
