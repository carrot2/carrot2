
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

package org.carrot2.source;

import java.util.ArrayList;
import java.util.HashMap;

import org.carrot2.core.Document;

/**
 * A single search engine response. This includes typical information returned by a search
 * engine: documents, total number of results, time of processing the query, etc.
 */
public final class SearchEngineResponse
{
    /** 
     * Total number of results available in the source (possibly an approximation). 
     */
    public static final String RESULTS_TOTAL_KEY = "resultsTotal";

    /**
     * Metadata key for the compression algorithm used to decompress the returned stream.
     */
    public static final String COMPRESSION_KEY = "compression";

    /**
     * All meta data returned in the response.
     */
    public final HashMap<String, Object> metadata = new HashMap<String, Object>(10);

    /**
     * All documents returned in the response.
     */
    public final ArrayList<Document> results = new ArrayList<Document>(100);

    /**
     * @return Returns an estimate of the total number of results or <b>-1</b> if not
     *         available.
     */
    public long getResultsTotal()
    {
        if (metadata.containsKey(RESULTS_TOTAL_KEY))
        {
            return ((Number) metadata.get(RESULTS_TOTAL_KEY)).longValue();
        }
        else
        {
            return -1;
        }
    }
}
