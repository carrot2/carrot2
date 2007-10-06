
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.fetcher;

import java.util.ArrayList;
import java.util.TreeSet;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.clustering.RawDocumentBase;

/**
 * {@link ParallelFetcher} for debugging/ testing purposes.
 */
public class DebugParallelFetcher extends ParallelFetcher
{
    final static class FetchInfo implements Comparable
    {
        public final int startAt;
        public final int fetchSize;
        
        public FetchInfo(int startAt, int fetchSize)
        {
            this.startAt = startAt;
            this.fetchSize = fetchSize;
        }
        
        public int compareTo(Object o)
        {
            final int difference = this.startAt - ((FetchInfo) o).startAt;
            if (difference == 0)
            {
                throw new RuntimeException("Something wrong -- no two FetchInfos should be" +
                        " in the same collection.");
            }
            return difference;
        }
    }

    private final ArrayList fetchedResults = new ArrayList();
    private final TreeSet fetchInfos = new TreeSet();

    private final long totalEstimated;

    /**
     * 
     */
    public DebugParallelFetcher(String fetcherName, String query, 
        int startAtIndex, int resultsRequired, int maximumResultIndex, int singleFetchSize,
        long totalEstimated)
    {
        super(fetcherName, query, startAtIndex, resultsRequired, 
            maximumResultIndex, singleFetchSize);
        this.totalEstimated = totalEstimated;
    }

    /**
     * 
     */
    public SingleFetcher getFetcher()
    {
        return new SingleFetcher()
        {
            public SearchResult fetch(String query, final int startAt, int fetchSize) 
                throws ProcessingException
            {
                fetchInfos.add(new FetchInfo(startAt, fetchSize));

                if (startAt > totalEstimated)
                {
                    return new SearchResult(new RawDocument [0], startAt, totalEstimated);
                }
                
                if (startAt + fetchSize > totalEstimated)
                {
                    fetchSize = (int) totalEstimated - startAt;
                }

                final RawDocument [] rawDocs = new RawDocument [fetchSize];

                int j = 0;
                for (int i = startAt; i < startAt + fetchSize; i++, j++)
                {
                    rawDocs[j] = createRawDocument(i);
                }

                return new SearchResult(rawDocs, startAt, totalEstimated);
            }
        };
    }

    /**
     * 
     */
    public void pushResults(int at, final RawDocument rawDocument) 
        throws ProcessingException
    {
        fetchedResults.add(rawDocument);
    }

    /**
     * 
     */
    public ArrayList getResults()
    {
        return fetchedResults;
    }    

    /**
     * 
     */
    static RawDocument createRawDocument(int id)
    {
        final Integer oid = new Integer(id);
        return new RawDocumentBase("nourl://" + id, "title", "snippet")
        {
            public Object getId()
            {
                return oid;
            }
            
            public boolean equals(Object obj)
            {
                if (obj instanceof RawDocumentBase) {
                    
                }
                return this.getId().equals(((RawDocumentBase) obj).getId());
            }
            
            public int hashCode()
            {
                return this.getId().hashCode();
            }
        };
    }

    /**
     * 
     */
    public FetchInfo [] getFetchInfos()
    {
        return (FetchInfo []) this.fetchInfos.toArray(new FetchInfo [fetchInfos.size()]);
    }
}
