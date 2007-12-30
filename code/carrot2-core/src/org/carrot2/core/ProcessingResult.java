package org.carrot2.core;

import java.util.Collection;
import java.util.Map;

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
        // TODO: we need to have one place where the key "documents" is defined
        return (Collection<Document>) attributes.get("documents");
    }

    @SuppressWarnings("unchecked")
    public Collection<Cluster> getClusters()
    {
        // TODO: we need to have one place where the key "clusters" is defined
        return (Collection<Cluster>) attributes.get("clusters");
    }
}