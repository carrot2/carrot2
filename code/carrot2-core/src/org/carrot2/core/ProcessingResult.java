package org.carrot2.core;

import java.util.Collection;
import java.util.Map;

/**
 * Provides results of query processing.
 */
public interface ProcessingResult
{
    public Collection<Cluster> getClusters();

    public Collection<Document> getDocuments();

    /**
     * Mirrors the output of {@link DocumentSource#getFields()} used
     * during processing.
     */
    public Collection<Field> getFields();

    /**
     * Follow the Web context pattern naming -- "attribute" as in request, session etc.? 
     */
    public Map<String, Object> getAttributes();
}