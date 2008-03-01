package org.carrot2.core;

import java.util.*;

import org.carrot2.core.attribute.AttributeNames;

/**
 * Encapsulates the results of processing. Provides access to the values of attributes
 * collected after processing and utility methods for obtaining processed documents ({@link #getDocuments()}))
 * and the created clusters ({@link #getClusters()}).
 */
public final class ProcessingResult
{
    /** Attributes collected after processing */
    private final Map<String, Object> attributes;

    /**
     * Creates a {@link ProcessingResult} with the provided <code>attributes</code>.
     * Assigns unique document identifiers if documents are present in the
     * <code>attributes</code> map (under the key {@link AttributeNames#DOCUMENTS}).
     */
    @SuppressWarnings("unchecked")
    ProcessingResult(Map<String, Object> attributes)
    {
        // Replace a modifiable collection of documents with an unmodifiable one
        final Collection<Document> documents = (Collection<Document>) attributes
            .get(AttributeNames.DOCUMENTS);
        if (documents != null)
        {
            attributes.put(AttributeNames.DOCUMENTS, Collections
                .unmodifiableCollection(documents));
        }

        // Replace a modifiable collection of clusters with an unmodifiable one
        final Collection<Cluster> clusters = (Collection<Cluster>) attributes
            .get(AttributeNames.CLUSTERS);
        if (clusters != null)
        {
            attributes.put(AttributeNames.CLUSTERS, Collections
                .unmodifiableCollection(clusters));
        }

        // Store a reference to attributes as an unmodifiable map
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
            for (final Document document : documents)
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
     *         are present in the result.
     */
    @SuppressWarnings("unchecked")
    public Collection<Document> getDocuments()
    {
        return (Collection<Document>) attributes.get(AttributeNames.DOCUMENTS);
    }

    /*
     * TODO: Returning a list of clusters instead of a (possibly atrificial) cluster with
     * subclusters adds a little complexity to recursive methods operating on clusters (a
     * natural entry point is a method taking one cluster and acting on subclusters
     * recursively). If we have to start with a list of clusters, we have to handle this
     * special case separately...
     */

    /**
     * Returns the clusters that have been created during processing. The returned
     * collection is unmodifiable.
     * 
     * @return clusters created during processing or <code>null</code> if no clusters
     *         were present in the result.
     */
    @SuppressWarnings("unchecked")
    public Collection<Cluster> getClusters()
    {
        return (Collection<Cluster>) attributes.get(AttributeNames.CLUSTERS);
    }
}