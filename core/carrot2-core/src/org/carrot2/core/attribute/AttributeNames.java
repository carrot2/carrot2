
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.attribute;

/**
 * Certain constant attribute names. Note that not all attributes need to be specified
 * here.
 * 
 * @see SharedAttributesDescriptor.Keys
 */
public final class AttributeNames
{
    /**
     * @see SharedAttributesDescriptor.Keys#START
     */
    public static final String START = "start";

    /**
     * @see SharedAttributesDescriptor.Keys#RESULTS
     */
    public static final String RESULTS = "results";

    /**
     * @see SharedAttributesDescriptor.Keys#QUERY
     */
    public static final String QUERY = "query";

    /**
     * @see SharedAttributesDescriptor.Keys#RESULTS_TOTAL
     */
    public static final String RESULTS_TOTAL = "results-total";

    /**
     * @see SharedAttributesDescriptor.Keys#DOCUMENTS
     */
    public static final String DOCUMENTS = "documents";

    /**
     * @see SharedAttributesDescriptor.Keys#CLUSTERS
     */
    public static final String CLUSTERS = "clusters";

    /**
     * @see SharedAttributesDescriptor.Keys#PROCESSING_TIME_TOTAL
     */
    public static final String PROCESSING_TIME_TOTAL = "processing-time-total";

    /**
     * @see SharedAttributesDescriptor.Keys#PROCESSING_TIME_SOURCE
     */
    public static final String PROCESSING_TIME_SOURCE = "processing-time-source";

    /**
     * @see SharedAttributesDescriptor.Keys#PROCESSING_TIME_ALGORITHM
     */
    public static final String PROCESSING_TIME_ALGORITHM = "processing-time-algorithm";

    /**
     * @see SharedAttributesDescriptor.Keys#PROCESSING_RESULT_TITLE
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
