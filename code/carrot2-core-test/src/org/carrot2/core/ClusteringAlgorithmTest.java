/**
 * 
 */
package org.carrot2.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.*;

import org.carrot2.core.attribute.AttributeNames;
import org.junit.Test;

/**
 * Simple baseline tests that apply to all clustering algorithms.
 */
public abstract class ClusteringAlgorithmTest<T extends ClusteringAlgorithm> extends
    ProcessingComponentTest<T>
{
    @Test
    public void testNoDocuments()
    {
        Collection<Cluster> clusters = cluster(Collections.<Document> emptyList());

        assertNotNull(clusters);
        assertEquals(0, clusters.size());
    }

    /**
     * Performs clustering.
     * 
     * @param documents documents to be clustered
     * @return clustering results
     */
    public Collection<Cluster> cluster(Collection<Document> documents)
    {
        attributes.put(AttributeNames.DOCUMENTS, documents);
        return controller.process(attributes, getComponentClass()).getClusters();
    }

    /**
     * Recursively collects documents from clusters.
     * 
     * @param clusters
     * @return
     */
    public Collection<Document> collectDocuments(Collection<Cluster> clusters)
    {
        return collectDocuments(clusters, new HashSet<Document>());
    }

    private Collection<Document> collectDocuments(Collection<Cluster> clusters,
        Collection<Document> documents)
    {
        for (Cluster cluster : clusters)
        {
            documents.addAll(cluster.getDocuments());
            collectDocuments(cluster.getSubclusters());
        }

        return documents;
    }
}
