package org.carrot2.core;

import java.util.Collection;
import java.util.Map;

/**
 * Provides results of query processing.
 */
public class ProcessingResult
{
    public Collection<Cluster> clusters;
    public Collection<Document> documents;
    public Map<String,Object> attributes;

    public Collection<Cluster> getClusters()
    {
        return clusters;
    }

    public Collection<Document> getDocuments()
    {
        return documents;
    }

    /**
     * Follow the Web context pattern naming -- "attribute" as in request, session etc.?
     */
    public Map<String, Object> getAttributes()
    {
        return attributes;
    }
}