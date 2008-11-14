
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
 * Iterates through topic identifiers returned by the a
 * {@link org.carrot2.input.odp.index.PrimaryTopicIndex}.
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