
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
 * Defines an interface of an integer wrapper for string-type data. IntWrappers
 * are used as input data to suffix sorting algorithms.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface IntWrapper
{
    /**
     * Returns the integer array associated with this wrapper. The array must be
     * terminated with a '-1' value.
     * 
     * @return wrappers integer array
     */
    public int [] asIntArray();

    /**
     * Returns the length of this wrapper <emphasis>excluding </emphasis> the
     * '-1' terminator.
     * 
     * @return this wrapper's length
     */
    public int length();

    /**
     * Reverses the internal int array.
     */
    public void reverse();
}