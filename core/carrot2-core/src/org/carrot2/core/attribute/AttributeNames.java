
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

package org.carrot2.core.attribute;

/**
 * Constants for common attribute names, only for the use in Carrot2 core code. When
 * calling Carrot2 APIs, use {@link CommonAttributesDescriptor.Keys}.
 * 
 * @see CommonAttributesDescriptor.Keys
 */
public final class AttributeNames
{
    /**
     * @see CommonAttributesDescriptor.Keys#START
     */
    public static final String START = "start";

    /**
     * @see CommonAttributesDescriptor.Keys#RESULTS
     */
    public static final String RESULTS = "results";

    /**
     * @see CommonAttributesDescriptor.Keys#QUERY
     */
    public static final String QUERY = "query";

    /**
     * @see CommonAttributesDescriptor.Keys#RESULTS_TOTAL
     */
    public static final String RESULTS_TOTAL = "results-total";

    /**
     * @see CommonAttributesDescriptor.Keys#DOCUMENTS
     */
    public static final String DOCUMENTS = "documents";

    /**
     * @see CommonAttributesDescriptor.Keys#CLUSTERS
     */
    public static final String CLUSTERS = "clusters";

    /**
     * @see CommonAttributesDescriptor.Keys#PROCESSING_TIME_TOTAL
     */
    public static final String PROCESSING_TIME_TOTAL = "processing-time-total";

    /**
     * @see CommonAttributesDescriptor.Keys#PROCESSING_TIME_SOURCE
     */
    public static final String PROCESSING_TIME_SOURCE = "processing-time-source";

    /**
     * @see CommonAttributesDescriptor.Keys#PROCESSING_TIME_ALGORITHM
     */
    public static final String PROCESSING_TIME_ALGORITHM = "processing-time-algorithm";

    /**
     * @see CommonAttributesDescriptor.Keys#PROCESSING_RESULT_TITLE
     */
    public static final String PROCESSING_RESULT_TITLE = "processing-result.title";

    /*
     *
     */
    private AttributeNames()
    {
        // No instances.
    }
}
