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
    /* TODO: Finalize this class? */

    /** Attributes collected after processing */
    private final Map<String, Object> attributes;

    /**
     * Creates a {@link ProcessingResult} with the provided <code>attributes</code>.
     * Assigns unique document identifiers if documents are present in the
     * <code>attributes</code> map (under the key {@link AttributeNames#DOCUMENTS}).
     */
    ProcessingResult(Map<String, Object> attributes)
    {
        this.attributes = Collections.unmodifiableMap(attributes);
        assignDocumentIds();
    }

    /**
     * Assigns sequential identifiers to documents.
     */
    private void assignDocumentIds()
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
     * @return documents that have been processed or <code>null</code> if no documents
     * are present in the result.
     */
    @SuppressWarnings("unchecked")
    public Collection<Document> getDocuments()
    {
        final Collection<Document> documents = (Collection<Document>) attributes
            .get(AttributeNames.DOCUMENTS);
        if (documents != null)
        {
            return Collections.unmodifiableCollection(documents);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the clusters that have been created during processing. The returned
     * collection is unmodifiable.
     * 
     * @return clusters created during processing or <code>null</code> if no clusters
     * were present in the result.
     */
    @SuppressWarnings("unchecked")
    public Collection<Cluster> getClusters()
    {
        final Collection<Cluster> clusters = (Collection<Cluster>) attributes
            .get(AttributeNames.DOCUMENTS);
        if (clusters != null)
        {
            return Collections.unmodifiableCollection(clusters);
        }
        else
        {
            return null;
        }
    }
}