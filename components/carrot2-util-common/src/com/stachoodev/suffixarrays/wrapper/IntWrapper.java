/*
 * Carrot2 Project Copyright (C) 2002-2004, Dawid Weiss Portions (C)
 * Contributors listed in carrot2.CONTRIBUTORS file. All rights reserved. Refer
 * to the full license file "carrot2.LICENSE" in the root folder of the CVS
 * checkout or at: http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.stachoodev.suffixarrays.wrapper;

/**
 * Defines an interface of an integer wrapper for string-type data. IntWrappers
 * are used as input data to suffix sorting algorithms.
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