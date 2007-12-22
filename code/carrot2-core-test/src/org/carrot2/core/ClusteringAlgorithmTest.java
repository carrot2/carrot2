/**
 * 
 */
package org.carrot2.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.*;

import org.carrot2.core.parameters.BindingPolicy;
import org.carrot2.core.parameters.ParameterBinder;
import org.junit.Test;

/**
 * Simple baseline tests that apply to all clustering algorithms.
 * <p>
 * TODO: it might be interesting (not sure if possible) to have a base class for testing
 * algorithms that would automatically run the tests in two modes: 1) creating a new
 * instance for each test, 2) run all tests on one instance to make sure the algorithm
 * will not fail in production.
 * <p>
 * Some methods of this class can probably be refactored to a more general
 * {@link ProcessingComponent} testing base class.
 */
public abstract class ClusteringAlgorithmTest
{
    public abstract Class<? extends ClusteringAlgorithm> getClusteringAlgorithmClass();

    public Map<String, Object> getInstanceParameters()
    {
        return Collections.<String, Object> emptyMap();
    }

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
     * TODO: Not sure if this should hold for all algorithms though.
     */
    @Test
    public void testNoDocumentLoss()
    {
        final int documentCount = 10;

        Collection<Cluster> clusters = cluster(DocumentFactory.generate(documentCount));
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
        return cluster(documents, Collections.<String, Object> emptyMap());
    }

    /**
     * Performs clustering.
     * 
     * @param documents
     * @param parameters
     * @return
     */
    public Collection<Cluster> cluster(Collection<Document> documents,
        Map<String, Object> parameters)
    {
        return cluster(documents, parameters, new HashMap<String, Object>());
    }

    /**
     * Performs clustering.
     * 
     * @param documents
     * @param parameters
     * @return
     */
    public Collection<Cluster> cluster(Collection<Document> documents,
        Map<String, Object> parameters, Map<String, Object> attributes)
    {
        return cluster(createInstance(), documents, parameters, attributes);
    }

    /**
     * Performs clustering.
     * 
     * @param documents
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<Cluster> cluster(ClusteringAlgorithm instance,
        Collection<Document> documents, Map<String, Object> parameters,
        Map<String, Object> attributes)
    {
        try
        {
            attributes.put("documents", documents);
            ControllerImpl.beforeProcessing(instance, parameters, attributes);
            instance.performProcessing();
            ControllerImpl.afterProcessing(instance, parameters, attributes);

            return (Collection<Cluster>) attributes.get("clusters");
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException();
        }
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

    /**
     * Creates and initializes an instance of the clustering algorighm.
     * 
     * @return
     */
    public ClusteringAlgorithm createInstance()
    {
        try
        {
            final ClusteringAlgorithm instance = ParameterBinder.createInstance(
                getClusteringAlgorithmClass(), getInstanceParameters());

            instance.init();

            return instance;
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes an instance of a clustering algorithm.
     * 
     * @param instance
     * @return
     */
    public ClusteringAlgorithm initInstance(ClusteringAlgorithm instance)
    {
        try
        {
            ParameterBinder.bind(instance, getInstanceParameters(),
                BindingPolicy.INSTANTIATION);
            instance.init();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        return instance;
    }
}
