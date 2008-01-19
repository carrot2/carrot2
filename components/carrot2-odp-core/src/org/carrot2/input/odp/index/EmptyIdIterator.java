
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

package org.carrot2.input.odp.index;

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
     * @see org.carrot2.input.odp.index.IdIterator#hasNext()
     */
    public boolean hasNext()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.index.IdIterator#next()
     */
    public int next()
    {
        return -1;
    }

}