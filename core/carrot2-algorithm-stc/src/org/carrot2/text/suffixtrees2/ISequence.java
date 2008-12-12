
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.suffixtrees2;

/**
 * Provides all information for constructing a {@link SuffixTree}.   
 */
public interface ISequence
{
    /**
     * Returns the number of elements in the sequence.
     */
    public int size();

    /**
     * Returns a unique integer code for object at index <code>i</code>.
     */
    public int objectAt(int i);
}
