
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.io.*;
import java.util.*;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.MapUtils;

/**
 * Encapsulates the results of processing. Provides access to the values of attributes
 * collected after processing and utility methods for obtaining processed documents (
 * {@link #getDocuments()})) and the created clusters ({@link #getClusters()}).
 */
public final class ProcessingResult
{
    /** Attributes collected after processing */
    private Map<String, Object> attributes = new HashMap<>();

    /** Read-only view of attributes exposed in {@link #getAttributes()} */
    private Map<String, Object> attributesView;

    /**
     * Creates a {@link ProcessingResult} with the provided <code>attributes</code>.
     * Assigns unique document identifiers if documents are present in the
     * <code>attributes</code> map (under the key {@link AttributeNames#DOCUMENTS}).
     */
    @SuppressWarnings("unchecked")
    ProcessingResult(Map<String, Object> attributes)
    {
        this.attributes = attributes;

        // Replace a modifiable collection of documents with an unmodifiable one
        final List<Document> documents = (List<Document>) attributes
            .get(AttributeNames.DOCUMENTS);
        if (documents != null)
        {
            Document.assignDocumentIds(documents);
            attributes.put(AttributeNames.DOCUMENTS,
                Collections.unmodifiableList(documents));
        }

        // Replace a modifiable collection of clusters with an unmodifiable one
        final List<Cluster> clusters = (List<Cluster>) attributes
            .get(AttributeNames.CLUSTERS);
        if (clusters != null)
        {
            Cluster.assignClusterIds(clusters);
            attributes.put(AttributeNames.CLUSTERS,
                Collections.unmodifiableList(clusters));
        }

        // Store a reference to attributes as an unmodifiable map
        this.attributesView = Collections.unmodifiableMap(attributes);

    }

    /**
     * Returns attributes fed-in and collected during processing. The returned map is
     * unmodifiable.
     * 
     * @return attributes fed-in and collected during processing
     */
    public Map<String, Object> getAttributes()
    {
        return attributesView;
    }

    /**
     * Returns a specific attribute of this result set. This method is equivalent to
     * calling {@link #getAttributes()} and then getting the required attribute from the
     * map.
     * 
     * @param key key of the attribute to return
     * @return value of the attribute
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key)
    {
        return (T) attributesView.get(key);
    }

    /**
     * Returns the documents that have been processed. The returned collection is
     * unmodifiable.
     * 
     * @return documents that have been processed or <code>null</code> if no documents are
     *         present in the result.
     */
    @SuppressWarnings("unchecked")
    public List<Document> getDocuments()
    {
        return (List<Document>) attributes.get(AttributeNames.DOCUMENTS);
    }

    /**
     * Returns the clusters that have been created during processing. The returned list is
     * unmodifiable.
     * 
     * @return clusters created during processing or <code>null</code> if no clusters were
     *         present in the result.
     */
    @SuppressWarnings("unchecked")
    public List<Cluster> getClusters()
    {
        return (List<Cluster>) attributes.get(AttributeNames.CLUSTERS);
    }
}
