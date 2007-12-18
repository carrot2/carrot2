/**
 * 
 */
package org.carrot2.core;

import static org.junit.Assert.*;

import java.util.*;

import org.carrot2.core.parameters.*;
import org.junit.Test;

import com.google.common.collect.Iterators;

/**
 * Simple baseline tests that apply to all clustering algorithms.
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
        ClusteringAlgorithm instance = prepareInstance(Collections.<Document>emptyList());
        Collection<Cluster> clusters = collectClusters(instance.getClusters());

        assertNotNull(clusters);
        assertEquals(0, clusters.size());
    }

    /**
     * Checks whether all input documents are placed in some cluster. TODO: Not sure if
     * this should hold for all algorithms though.
     */
    @Test
    public void testNoDocumentLoss()
    {
        final int documentCount = 10;

        ClusteringAlgorithm instance = prepareInstance(DocumentFactory
            .generate(documentCount));
        Collection<Cluster> clusters = collectClusters(instance.getClusters());
        Collection<Document> documentsFromClusters = collectDocuments(clusters);

        assertEquals(documentCount, documentsFromClusters.size());
    }

    public Collection<Cluster> collectClusters(Iterator<Cluster> clusters)
    {
        final ArrayList<Cluster> collected = new ArrayList<Cluster>();
        Iterators.addAll(collected, clusters);
        return collected;
    }

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

    public ClusteringAlgorithm createInstance()
    {
        try
        {
            return ParameterBinder.createInstance(getClusteringAlgorithmClass(),
                getInstanceParameters());
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ClusteringAlgorithm prepareInstance(Collection<Document> documents)
    {
        return prepareInstance(documents, Collections.<String, Object> emptyMap());
    }

    public ClusteringAlgorithm prepareInstance(Collection<Document> documents,
        Map<String, Object> runtimeParameters)
    {
        return prepareInstance(documents, runtimeParameters, createInstance());
    }

    public ClusteringAlgorithm prepareInstance(Collection<Document> documents,
        Map<String, Object> runtimeParameters, ClusteringAlgorithm instance)
    {
        try
        {
            ParameterBinder.bind(instance, runtimeParameters, BindingPolicy.RUNTIME);
            
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("documents", documents);
            AttributeBinder.bind(instance, attributes, BindingDirection.IN);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        return instance;
    }
}
