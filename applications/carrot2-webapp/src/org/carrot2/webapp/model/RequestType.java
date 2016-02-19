
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
    CLUSTERS(true),
    
    /** Documents and clusters in Carrot2 standard format */
    CARROT2(true),
    
    /** Documents in Carrot2 standard format */
    CARROT2DOCUMENTS(true),
    
    /** Error page */
    ERROR(false),
    
    /** Information about document sources */
    SOURCES(false),
    
    /** Information about attributes of a document source */
    ATTRIBUTES(false),
    
    /** Simple statistics page */
    STATS(false);
    
    /** True when Carrot2 processing is required */
    public final boolean requiresProcessing;

    private RequestType(boolean requiresProcessing)
    {
        this.requiresProcessing = requiresProcessing;
    }
}
