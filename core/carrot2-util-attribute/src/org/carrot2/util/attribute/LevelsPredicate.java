
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import com.google.common.base.Predicate;

/**
 * A predicate that tests whether an {@link AttributeDescriptor} belongs to any of the
 * provided levels.
 */
public final class LevelsPredicate implements Predicate<AttributeDescriptor>
{
    private final AttributeLevel [] levels;

    public LevelsPredicate(AttributeLevel... levels)
    {
        this.levels = levels;
    }

    public boolean apply(AttributeDescriptor descriptor)
    {
        for (AttributeLevel level : levels)
        {
            if (level.equals(descriptor.metadata.getLevel()))
            {
                return true;
            }
        }

        return false;
    }
}
