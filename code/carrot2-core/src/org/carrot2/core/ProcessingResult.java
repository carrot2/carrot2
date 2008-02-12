package org.carrot2.core;

import java.util.*;

import org.carrot2.core.attribute.AttributeNames;

/**
 * Encapsulates the results of processing. Provides access to the values of attributes
 * collected after processing and utility methods for obtaining processed documents ({@link #getDocuments()}))
 * and the created clusters ({@link #getClusters()}).
 */
public class ProcessingResult
{
    /** Attributes collected after processing */
    private final Map<String, Object> attributes;

    /**
     * Creates a {@link ProcessingResult} with the provided <code>attributes</code>.
     */
    ProcessingResult(Map<String, Object> attributes)
    {
        this.attributes = Collections.unmodifiableMap(attributes);
        assignDocumenIds();
    }

    /**
     * Assigns sequential identifiers to documents.
     */
    private void assignDocumenIds()
    {
        final Collection<Document> documents = getDocuments();
        if (documents != null)
        {
            int id = 0;
            for (Document document : documents)
            {
                document.id = id++;
            }
        }
    }

    /**
     * Returns attributes fed-in and collected during processing. The returned map is
     * unmodifiable.
     * 
     * @return attributes fed-in and collected during processing
     */
    public Map<String, Object> getAttributes()
    {
        return attributes;
    }

    /**
     * Returns the documents that have been processed. The returned collection is
     * unmodifiable.
     * 
     * @return documents that have been processed
     */
    @SuppressWarnings("unchecked")
    public Collection<Document> getDocuments()
    {
        return Collections.unmodifiableCollection((Collection<Document>) attributes
            .get(AttributeNames.DOCUMENTS));
    }

    /**
     * Returns the clusters that have been created during processing.. The returned
     * collection is unmodifiable.
     * 
     * @return clusters created during processing
     */
    @SuppressWarnings("unchecked")
    public Collection<Cluster> getClusters()
    {
        return Collections.unmodifiableCollection((Collection<Cluster>) attributes
            .get(AttributeNames.CLUSTERS));
    }
}