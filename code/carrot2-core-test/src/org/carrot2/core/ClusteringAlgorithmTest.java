/**
 * 
 */
package org.carrot2.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.*;

import org.carrot2.core.controller.SimpleController;
import org.carrot2.core.parameter.AttributeNames;
import org.junit.Test;

/**
 * Simple baseline tests that apply to all clustering algorithms.
 * <p>
 * TODO: Replace custom lifecycle in this class to utilizing a controller.
 * <p>
 * Some methods of this class can probably be refactored to a more general
 * {@link ProcessingComponent} testing base class.
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
     * Checks whether all input documents are placed in some cluster.
     * <p>
     * TODO: Not sure if this should hold for all algorithms though. [DW] No, I guess it
     * doesn't make sense to forge the junk cluster (and therefore this test).
     */
    @Test
    public void testNoDocumentLoss()
    {
        final int documentCount = 10;

        Collection<Cluster> clusters = cluster(TestDocumentFactory.DEFAULT
            .generate(documentCount));
        Collection<Document> documentsFromClusters = collectDocuments(clusters);

        assertEquals(documentCount, documentsFromClusters.size());
    }

    /**
     * Performs clustering.
     * 
     * @param documents
     * @return
     */
    public Collection<Cluster> cluster(Collection<Document> documents)
    {
        return cluster(documents, new HashMap<String, Object>());
    }

    /**
     * Performs clustering.
     * 
     * @param documents
     * @param parameters
     * @return
     */
    public Collection<Cluster> cluster(Collection<Document> documents,
        Map<String, Object> attributes)
    {
        return cluster(createInstance(), documents, attributes);
    }

    /**
     * Performs clustering.
     */
    @SuppressWarnings("unchecked")
    public Collection<Cluster> cluster(ClusteringAlgorithm instance,
        Collection<Document> documents, Map<String, Object> attributes)
    {
        attributes.put(AttributeNames.DOCUMENTS, documents);
        SimpleController controller = new SimpleController();
        controller.process(attributes, instance);
        return (Collection<Cluster>) attributes.get(AttributeNames.CLUSTERS);
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
