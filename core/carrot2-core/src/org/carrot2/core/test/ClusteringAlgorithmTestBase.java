/**
 *
 */
package org.carrot2.core.test;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.fest.assertions.Assertions;
import org.junit.Test;

import com.google.common.collect.*;

/**
 * Simple baseline tests that apply to all clustering algorithms.
 */
public abstract class ClusteringAlgorithmTestBase<T extends ClusteringAlgorithm> extends
    ProcessingComponentTestBase<T>
{
    /**
     * A set of sample documents returned for the query <i>data mining</i>.
     */
    protected final static Collection<Document> DOCUMENTS_DATA_MINING = SampleDocumentData.DOCUMENTS_DATA_MINING;

    /**
     * A set of sample documents returned for the query <i>dawid</i>.
     */
    protected final static Collection<Document> DOCUMENTS_DAWID = SampleDocumentData.DOCUMENTS_DAWID;

    /**
     * A test to check if the algorithm does not fail with no documents.
     */
    @Test
    public void testNoDocuments()
    {
        final Collection<Cluster> clusters = cluster(Collections.<Document> emptyList())
            .getClusters();

        assertNotNull(clusters);
        assertEquals(0, clusters.size());
    }

    @Test
    public void testClusteringDataMining()
    {
        final ProcessingResult processingResult = cluster(DOCUMENTS_DATA_MINING);
        final Collection<Cluster> clusters = processingResult.getClusters();

        assertThat(clusters.size()).isGreaterThan(0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRepeatedClusteringWithCache()
    {
        final Controller controller = new CachingController(DocumentSource.class);
        controller.init(new HashMap());

        final HashMap processingAttributes = Maps.newHashMap();
        processingAttributes.put(AttributeNames.DOCUMENTS, DOCUMENTS_DATA_MINING);

        controller.process(processingAttributes, getComponentClass());
        controller.process(processingAttributes, getComponentClass());
    }

    /**
     * Performs a very simple stress test using {@link CachingController}. The test is
     * performed with default init attributes.
     */
    @Test
    public void testStress() throws InterruptedException, ExecutionException
    {
        final int numberOfThreads = 4;
        final int queriesPerThread = 25;

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
                    return cachingController
                        .process(localAttributes, getComponentClass());
                }
            });
        }

        cachingController.init(initAttributes);

        try
        {
            List<Future<ProcessingResult>> results = executorService.invokeAll(callables);
            Multimap<Integer, List<Cluster>> clusterings = Multimaps
                .newArrayListMultimap();

            // Group results by query
            for (Future<ProcessingResult> future : results)
            {
                final ProcessingResult processingResult = future.get();
                final Integer dataSetIndex = (Integer) processingResult.getAttributes()
                    .get("dataSetIndex");

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
                    assertThat(firstClusterList).isEquivalentTo(iterator.next());
                }
            }
        }
        finally
        {
            executorService.shutdown();
        }
    }

    /**
     * Performs clustering using {@link #simpleController}.
     * 
     * @param documents Documents to be clustered.
     * @return {@link ProcessingResult} returned from the controller.
     */
    public ProcessingResult cluster(Collection<Document> documents)
    {
        // A little hacky, but looks like the simplest way to ensure a single
        // initialization per one test case
        if (!initAttributes.isEmpty())
        {
            simpleController.init(initAttributes);
            initAttributes.clear();
        }

        processingAttributes.put(AttributeNames.DOCUMENTS, documents);
        return simpleController.process(processingAttributes, getComponentClass());
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
}
