
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test;

import static org.carrot2.core.test.SampleDocumentData.DOCUMENTS_DATA_MINING;
import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatClusters;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.Document;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.Platform;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.BindableMetadata;
import org.fest.assertions.Assertions;
import org.junit.Assume;
import org.junit.Test;

import com.carrotsearch.randomizedtesting.annotations.Nightly;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import org.carrot2.shaded.guava.common.base.Strings;
import org.carrot2.shaded.guava.common.collect.ArrayListMultimap;
import org.carrot2.shaded.guava.common.collect.ImmutableMap;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.shaded.guava.common.collect.Multimap;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * Simple baseline tests that apply to all clustering algorithms.
 */
public abstract class ClusteringAlgorithmTestBase<T extends IClusteringAlgorithm> 
    extends ProcessingComponentTestBase<T>
{
    /**
     * Algorithms are bindable, so their metadata should always be available.
     */
    @Test
    public void testMetadataAvailable()
    {
        Class<? extends IClusteringAlgorithm> c = getComponentClass();
        Assume.assumeTrue(c.getAnnotation(Bindable.class) != null);
        
        BindableMetadata metadata = BindableMetadata.forClassWithParents(c);
        assertNotNull(metadata);
        assertNotNull(metadata.getAttributeMetadata());
    }

    /**
     * A test to check if the algorithm does not fail with no documents.
     */
    @Test
    public void testNoDocuments()
    {
        final Collection<Cluster> clusters = 
            cluster(Collections.<Document> emptyList()).getClusters();

        assertNotNull(clusters);
        assertEquals(0, clusters.size());
    }

    /**
     * @see "http://issues.carrot2.org/browse/CARROT-400"
     */
    @Test
    public void testEmptyDocuments()
    {
        final List<Document> documents = Lists.newArrayList();
        final int documentCount = randomIntBetween(1, 100);
        for (int i = 0; i < documentCount; i++)
        {
            documents.add(new Document());
        }

        final List<Cluster> clusters = cluster(documents).getClusters();

        assertNotNull(clusters);
        assertEquals(1, clusters.size());
        assertThat(clusters.get(0).size()).isEqualTo(documentCount);
    }

    @Test
    public void testClusteringDataMining()
    {
        final ProcessingResult processingResult = cluster(DOCUMENTS_DATA_MINING);
        final Collection<Cluster> clusters = processingResult.getClusters();

        assertThat(clusters.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    @ThreadLeakLingering(linger = 5000)
    public void testRepeatedClusteringWithCache()
    {
        // Caching controller is not available for .NET at the moment.
        assumeTrue("Java test only.", Platform.getPlatform() == Platform.JAVA);

        final Controller controller = getCachingController(initAttributes, IClusteringAlgorithm.class);

        final Map<String, Object> processingAttributes = ImmutableMap.of(
            AttributeNames.DOCUMENTS, (Object) DOCUMENTS_DATA_MINING);

        controller.process(processingAttributes, getComponentClass());
        controller.process(processingAttributes, getComponentClass());

        controller.dispose();
    }

    /**
     * Performs a very simple stress test using a pooling {@link Controller}. The
     * test is performed with default init attributes.
     */
    @Nightly @Test 
    @ThreadLeakLingering(linger = 5000)
    public void testStress() throws InterruptedException, ExecutionException
    {
        final int numberOfThreads = randomIntBetween(1, 10);
        final int queriesPerThread = scaledRandomIntBetween(5, 25);

        /*
         * This yields a pooling controller effectively, because no cache interfaces are passed.
         */
        @SuppressWarnings("unchecked")
        final Controller controller = getCachingController(initAttributes);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<ProcessingResult>> callables = Lists.newArrayList();
        for (int i = 0; i < numberOfThreads * queriesPerThread; i++)
        {
            final int dataSetIndex = i;
            callables.add(new Callable<ProcessingResult>()
            {
                public ProcessingResult call() throws Exception
                {
                    Map<String, Object> localAttributes = Maps.newHashMap();
                    localAttributes.put(AttributeNames.DOCUMENTS, SampleDocumentData.ALL
                        .get(dataSetIndex % SampleDocumentData.ALL.size()));
                    localAttributes.put("dataSetIndex", dataSetIndex);
                    return controller.process(localAttributes, getComponentClass());
                }
            });
        }

        try
        {
            List<Future<ProcessingResult>> results = executorService.invokeAll(callables);
            Multimap<Integer, List<Cluster>> clusterings = ArrayListMultimap.create();

            // Group results by query
            for (Future<ProcessingResult> future : results)
            {
                final ProcessingResult processingResult = future.get();
                final Integer dataSetIndex = (Integer) processingResult.getAttributes().get("dataSetIndex");
                clusterings.put(dataSetIndex, processingResult.getClusters());
            }

            // Make sure results are the same within each data set
            for (Integer dataSetIndex : clusterings.keySet())
            {
                Collection<List<Cluster>> clustering = clusterings.get(dataSetIndex);
                Iterator<List<Cluster>> iterator = clustering.iterator();
                if (!iterator.hasNext())
                {
                    continue;
                }

                final List<Cluster> firstClusterList = iterator.next();
                Assertions.assertThat(firstClusterList).isNotEmpty();
                while (iterator.hasNext())
                {
                    assertThatClusters(firstClusterList).isEquivalentTo(iterator.next());
                }
            }
        }
        finally
        {
            executorService.shutdown();
        }
    }

    /**
     * Performs clustering using {@link Controller}.
     * 
     * @param documents Documents to be clustered.
     * @return {@link ProcessingResult} returned from the controller.
     */
    public ProcessingResult cluster(Collection<Document> documents)
    {
        processingAttributes.put(AttributeNames.DOCUMENTS, documents);
        Controller controller = getSimpleController(initAttributes);
        try {
            ProcessingResult process = controller.process(processingAttributes, getComponentClass());
            return process;
        } finally {
            controller.dispose();
            super.simpleController = null;
        }
    }

    /**
     * Recursively collects documents from clusters.
     */
    public Collection<Document> collectDocuments(Collection<Cluster> clusters)
    {
        return collectDocuments(clusters, new HashSet<Document>());
    }

    /*
     * 
     */
    private Collection<Document> collectDocuments(Collection<Cluster> clusters,
        Collection<Document> documents)
    {
        for (final Cluster cluster : clusters)
        {
            documents.addAll(cluster.getDocuments());
            collectDocuments(cluster.getSubclusters());
        }

        return documents;
    }

    public static Set<String> collectClusterLabels(ProcessingResult pr)
    {
        final Set<String> clusterLabels = Sets.newHashSet();
        new Cloneable()
        {
            public void dumpClusters(List<Cluster> clusters, int depth) 
            {
                for (Cluster c : clusters) {
                    clusterLabels.add(c.getLabel());
                    if (c.getSubclusters() != null) {
                        dumpClusters(c.getSubclusters(), depth + 1);
                    }
                }
            }
        }.dumpClusters(pr.getClusters(), 0);

        return clusterLabels;
    }
    
    public static void dumpClusterLabels(ProcessingResult pr)
    {
        new Cloneable()
        {
            public void dumpClusters(List<Cluster> clusters, int depth) 
            {
                String indent = Strings.repeat("  ", depth);
                for (Cluster c : clusters) {
                    System.out.println(indent + c.getLabel());
                    if (c.getSubclusters() != null) {
                        dumpClusters(c.getSubclusters(), depth + 1);
                    }
                }
            }
        }.dumpClusters(pr.getClusters(), 0);
    }    
}
