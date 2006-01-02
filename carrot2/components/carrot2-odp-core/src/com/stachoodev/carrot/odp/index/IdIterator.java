
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp.index;

/**
 * Iterates through topic identifiers returned by the a
 * {@link com.stachoodev.carrot.odp.index.PrimaryTopicIndex}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface IdIterator
{
    /**
     * Returns true if there are more ids to retrieve.
     * 
     * @return true if there are more ids to retrieve.
     */
    public boolean hasNext();

    /**
     * Returns the next id. If the last call to the {@link #hasNext()}method
     * returned <code>false</code> the result is unspecified.
     * 
     * @return the next id.
     */
    public int next();
}