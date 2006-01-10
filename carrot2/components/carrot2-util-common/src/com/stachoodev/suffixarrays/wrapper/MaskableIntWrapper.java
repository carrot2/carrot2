
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.stachoodev.suffixarrays.wrapper;

/**
 * An integer wrapper that is capable of representing groups of symbols. The
 * {@link #SECONDARY_BITS} lowest order bits represent different types of
 * symbols within one group (e.g. variants of the same stem), while the
 * remaining bits represent distinct groups.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface MaskableIntWrapper extends IntWrapper
{
    /**
     * The number of lowest order bits reserved for representing different
     * symbols within a group.
     */
    public static final int SECONDARY_BITS = 5;
    public static final int SECONDARY_OFFSET = (1 << SECONDARY_BITS);
    public static final int SECONDARY_MASK = ~(SECONDARY_OFFSET - 1);
}