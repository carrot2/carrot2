
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

package com.stachoodev.util.common;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface IntIterator
{
    /**
     * @return
     */
    public boolean hasNext();
    
    /**
     * @return
     */
    public int next();
}
