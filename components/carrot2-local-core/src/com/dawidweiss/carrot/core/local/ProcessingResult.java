/*
 * ProcessingResult.java
 * 
 * Created on 2004-07-01
 */
package com.dawidweiss.carrot.core.local;

/**
 * Provides results of query processing.
 * 
 * @see com.dawidweiss.carrot.core.local.LocalController
 * @author stachoo
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