package org.carrot2.webapp.model;

/**
 * The data to include in the response.
 */
public enum RequestType
{
    /** Page, documents and clusters */
    FULL(true),

    /** Only page */
    PAGE(false),

    /** Only documents */
    DOCUMENTS(true),

    /** Only clusters */
    CLUSTERS(true);
    
    /** True when Carrot2 processing is required */
    public final boolean requiresProcessing;

    private RequestType(boolean requiresProcessing)
    {
        this.requiresProcessing = requiresProcessing;
    }
}