package org.carrot2.core;

import java.util.Collection;
import java.util.Map;

import org.carrot2.core.parameter.AttributeNames;

/**
 * Provides results of query processing.
 */
public class ProcessingResult
{
    private final Map<String, Object> attributes;

    public ProcessingResult(Map<String, Object> attributes)
    {
        this.attributes = attributes;
    }

    /**
     * Follow the Web context pattern naming -- "attribute" as in request, session etc.?
     */
    public Map<String, Object> getAttributes()
    {
        return attributes;
    }

    @SuppressWarnings("unchecked")
    public Collection<Document> getDocuments()
    {
        return (Collection<Document>) attributes.get(
            AttributeNames.DOCUMENTS);
    }

    @SuppressWarnings("unchecked")
    public Collection<Cluster> getClusters()
    {
        return (Collection<Cluster>) attributes.get(
            AttributeNames.CLUSTERS);
    }
}