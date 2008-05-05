package org.carrot2.source;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

/**
 * Usage statistics for an instance of {@link SearchEngine}. 
 */
@Bindable
public final class SearchEngineStats
{
    /**
     * Number queries handled successfully by this component.
     * 
     * @label Successful Queries
     */
    @Processing
    @Output
    @Attribute
    private int queries;

    /**
     * Number of individual page requests issued.
     * 
     * @label Page Requests
     */
    @Processing
    @Output
    @Attribute
    private int pageRequests;
    
    /* */
    public synchronized void incrQueryCount()
    {
        this.queries++;
    }

    /* */
    public synchronized void incrPageRequestCount()
    {
        this.pageRequests++;
    }    
}
