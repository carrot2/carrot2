
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

package org.carrot2.core;

/**
 * Provides results of query processing.
 * 
 * @see org.carrot2.core.LocalController
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface ProcessingResult
{
    /**
     * Result of the query exactly as returned by the output component in the
     * chain.
     * 
     * @return result of the query
     */
    public Object getQueryResult();

    /**
     * The {@link RequestContext}associated with the request that produced this
     * {@link ProcessingResult}.
     * 
     * @return The {@link RequestContext}associated with the request that
     *         produced this {@link ProcessingResult}.
     */
    public RequestContext getRequestContext();
}