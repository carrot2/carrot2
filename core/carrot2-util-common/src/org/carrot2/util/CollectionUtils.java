
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

package org.carrot2.util;

import java.util.Collection;

/**
 * A number of utility classes for working with {@link Collection}s.
 */
public final class CollectionUtils
{
    private CollectionUtils()
    {
        // No instantiation
    }

    /**
     * Returns the first element from the provided collection.
     * 
     * @param collection the collection to get the element from
     * @return first element from the provided collection or <code>null</code> if the
     *         collection is <code>null</code> or empty.
     */
    public static <T> T getFirst(Collection<T> collection)
    {
        if (collection == null || collection.isEmpty())
        {
            return null;
        }

        return collection.iterator().next();
    }
}
