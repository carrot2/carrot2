
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.carrot2.core.attribute.AttributeNames;

/**
 * Encapsulates the results of processing. Provides access to the values of attributes
 * collected after processing and utility methods for obtaining processed documents (
 * {@link #getDocuments()})) and the created clusters ({@link #getClusters()}).
 */
public final class ProcessingResult extends AttributeSet
{
    public final static String DOCUMENTS = AttributeNames.DOCUMENTS;
    public final static String CLUSTERS = AttributeNames.CLUSTERS;
    public final static String QUERY = AttributeNames.QUERY;

    /**
     * Creates a {@link ProcessingResult} with the provided 
     * <code>attributes</code>.
     */
    public ProcessingResult(Map<String, Object> attributes)
    {
        super(attributes);

        // Sanity checks.
        assert checkListContainsOnly(attributes.get(DOCUMENTS), Document.class) &&
               checkListContainsOnly(attributes.get(CLUSTERS), Cluster.class);

        // Consistency checks.
        assert checkClustersReferenceDocuments();
    }

    /**
     * Returns the list documents that have been processed or an empty list if
     * no documents were present.
     */
    @SuppressWarnings("unchecked")
    public List<Document> getDocuments()
    {
        List<Document> docs = (List<Document>) getAttribute(DOCUMENTS);
        return (docs != null ? docs : Collections.<Document> emptyList());
    }

    /**
     * Returns the clusters that have been created during processing or an empty list
     * if no documents were present.
     */
    @SuppressWarnings("unchecked")
    public List<Cluster> getClusters()
    {
        List<Cluster> clusters = (List<Cluster>) attributes.get(CLUSTERS);
        return (clusters != null ? clusters : Collections.<Cluster> emptyList());
    }

    /**
     * Returns the query associated with the processing result (or <code>null</code>).
     */
    public String getQuery()
    {
        return getAttribute(QUERY, String.class);
    }

    private boolean checkClustersReferenceDocuments()
    {
        IdentityHashMap<Document, Boolean> documents = new IdentityHashMap<Document, Boolean>();
        for (Document doc : getDocuments()) {
            documents.put(doc, Boolean.TRUE);
        }

        IdentityHashMap<Cluster, Boolean> visited = new IdentityHashMap<Cluster, Boolean>();
        ArrayDeque<Cluster> q = new ArrayDeque<Cluster>(getClusters());
        while (!q.isEmpty()) {
            final Cluster c = q.pop();
            if (visited.put(c, Boolean.TRUE) != null) {
                throw new AssertionError("Clusters don't form a plain tree: " + c);
            }
            q.addAll(c.getSubclusters());
            for (Document doc : c.getDocuments()) {
                if (!documents.containsKey(doc)) {
                    throw new AssertionError(String.format(Locale.ROOT,
                        "Cluster references a document from outside documents list: %s => %s",
                        c,
                        doc));
                }
            }
        }
        List<Cluster> clusters = getClusters();
        if (clusters != null) {
        }
        return true;
    }    
}
