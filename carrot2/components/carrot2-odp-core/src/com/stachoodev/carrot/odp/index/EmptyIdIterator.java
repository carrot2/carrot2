/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp.index;

/**
 * Implementation of the {@link IdIterator} interface that returns no ids.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class EmptyIdIterator implements IdIterator
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.IdIterator#hasNext()
     */
    public boolean hasNext()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.IdIterator#next()
     */
    public int next()
    {
        return -1;
    }

}