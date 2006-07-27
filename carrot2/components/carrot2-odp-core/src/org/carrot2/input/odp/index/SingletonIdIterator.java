
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

package org.carrot2.input.odp.index;

/**
 * An implementation of the {@link org.carrot2.input.odp.index.IdIterator}
 * interface that returns only one value.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SingletonIdIterator implements IdIterator
{
    /** */
    private int value;

    /** */
    private boolean hasNext;

    /**
     * Creates a new instance of the iterator.
     * 
     * @param value to be returned
     */
    public SingletonIdIterator(int value)
    {
        this.value = value;
        hasNext = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.index.IdIterator#hasNext()
     */
    public boolean hasNext()
    {
        return hasNext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.index.IdIterator#next()
     */
    public int next()
    {
        hasNext = false;
        return value;
    }

}