/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp.index;

/**
 * An implementation of the {@link com.stachoodev.carrot.odp.index.IdIterator}
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
     * @see com.stachoodev.carrot.odp.index.IdIterator#hasNext()
     */
    public boolean hasNext()
    {
        return hasNext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.IdIterator#next()
     */
    public int next()
    {
        hasNext = false;
        return value;
    }

}