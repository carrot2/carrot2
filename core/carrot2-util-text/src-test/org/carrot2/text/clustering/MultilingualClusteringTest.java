
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

package org.carrot2.text.clustering;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatClusters;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.text.clustering.MultilingualClustering.LanguageAggregationStrategy;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Before;
import org.junit.Test;

import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.collect.*;

/**
 *
 */
public class MultilingualClusteringTest extends CarrotTestCase
{
    private MultilingualClustering multilingualClustering;
    private TestMultilingualClusteringAlgorithm testMultilingualClusteringAlgorithm;

    @Before
    public void setUp()
    {
        multilingualClustering = new MultilingualClustering();
        testMultilingualClusteringAlgorithm = new TestMultilingualClusteringAlgorithm();
    }

    @Test
    public void testEmptyFlattenAll()
    {
        checkEmpty(LanguageAggregationStrategy.FLATTEN_ALL);
    }

    @Test
    public void testEmptyFlattenMajorLanguage()
    {
        checkEmpty(LanguageAggregationStrategy.FLATTEN_MAJOR_LANGUAGE);
    }

    @Test
    public void testEmptyFlattenNone()
    {
        checkEmpty(LanguageAggregationStrategy.FLATTEN_NONE);
    }
    
    @Test
    public void testEmptyMajorityLanguage()
    {
        checkEmpty(LanguageAggregationStrategy.CLUSTER_IN_MAJORITY_LANGUAGE);
    }

    @Test
    public void testNoLanguageFlattenAll()
    {
        checkNoLanguage(LanguageAggregationStrategy.FLATTEN_ALL);
    }

    @Test
    public void testNoLanguageFlattenMajorLanguage()
    {
        checkNoLanguage(LanguageAggregationStrategy.FLATTEN_MAJOR_LANGUAGE);
    }

    @Test
    public void testNoLanguageFlattenNone()
    {
        checkNoLanguage(LanguageAggregationStrategy.FLATTEN_NONE);
    }
    
    @Test
    public void testNoLanguageMajorityLanguage()
    {
        checkNoLanguage(LanguageAggregationStrategy.CLUSTER_IN_MAJORITY_LANGUAGE);
    }

    @Test
    public void testOneLanguageNontrivialClustersFlattenAll()
    {
        checkOneLanguageNontrivialClusters(LanguageAggregationStrategy.FLATTEN_ALL,
            LanguageCode.GERMAN);
    }

    @Test
    public void testOneLanguageNontrivialClustersFlattenMajorLanguage()
    {
        checkOneLanguageNontrivialClusters(LanguageAggregationStrategy.FLATTEN_MAJOR_LANGUAGE,
            LanguageCode.GERMAN);
    }

    @Test
    public void testOneLanguageNontrivialClustersFlattenNone()
    {
        checkOneLanguageNontrivialClusters(LanguageAggregationStrategy.FLATTEN_NONE,
            LanguageCode.GERMAN);
    }

    @Test
    public void testOneLanguageNontrivialClustersMajorityLanguage()
    {
        checkOneLanguageNontrivialClusters(LanguageAggregationStrategy.CLUSTER_IN_MAJORITY_LANGUAGE,
            LanguageCode.GERMAN);
    }
    
    @Test
    public void testOneLanguageOtherTopicsClusterFlattenAll()
    {
        checkOneLanguageOtherTopicsCluster(LanguageAggregationStrategy.FLATTEN_ALL);
    }

    @Test
    public void testOneLanguageOtherTopicsClusterFlattenMajorLanguage()
    {
        checkOneLanguageOtherTopicsCluster(LanguageAggregationStrategy.FLATTEN_MAJOR_LANGUAGE);
    }

    @Test
    public void testOneLanguageOtherTopicsClusterFlattenNone()
    {
        checkOneLanguageOtherTopicsCluster(LanguageAggregationStrategy.FLATTEN_NONE);
    }
    
    @Test
    public void testOneLanguageOtherTopicsClusterMajorityLanguage()
    {
        checkOneLanguageOtherTopicsCluster(LanguageAggregationStrategy.CLUSTER_IN_MAJORITY_LANGUAGE);
    }

    @Test
    public void testMoreLanguagesFlattenAll()
    {
        final List<Document> documents = documentsWithLanguages(LanguageCode.POLISH,
            LanguageCode.POLISH, LanguageCode.POLISH, LanguageCode.GERMAN,
            LanguageCode.GERMAN);

        final Cluster c1 = new Cluster("Cluster 2").addDocuments(documents.get(4));
        final Cluster co = new Cluster("Other Topics").addDocuments(
            documents.subList(0, 4)).setOtherTopics(true);

        final List<Cluster> expectedClusters = Lists.newArrayList(c1, co);

        check(documents, expectedClusters, Lists.newArrayList(LanguageCode.POLISH,
            LanguageCode.GERMAN), LanguageAggregationStrategy.FLATTEN_ALL);
    }
    
    @Test
    public void testMoreLanguagesMajorityLanguage()
    {
        final List<Document> documents = documentsWithLanguages(LanguageCode.POLISH,
            LanguageCode.POLISH, LanguageCode.GERMAN, LanguageCode.GERMAN,
            LanguageCode.GERMAN);
        
        final Cluster c1 = new Cluster("Cluster 1").addDocuments(documents.get(2), documents.get(4));
        final Cluster c2 = new Cluster("Cluster 2").addDocuments(documents.get(1), documents.get(3));
        final Cluster co = new Cluster("Other Topics").addDocuments(
            documents.get(0)).setOtherTopics(true);
        
        final List<Cluster> expectedClusters = Lists.newArrayList(c1, c2, co);
        
        check(documents, expectedClusters, Lists.newArrayList(LanguageCode.GERMAN), 
            LanguageAggregationStrategy.CLUSTER_IN_MAJORITY_LANGUAGE);
    }

    @Test
    public void testMoreLanguagesFlattenMajorLanguage()
    {
        final List<Document> documents = documentsWithLanguages(LanguageCode.POLISH,
            LanguageCode.POLISH, LanguageCode.POLISH, LanguageCode.GERMAN,
            LanguageCode.GERMAN);

        final Cluster c1 = new Cluster("Cluster 2").addDocuments(documents.get(4));
        final Cluster co = new Cluster("Other Topics").addDocuments(documents.get(3))
            .setOtherTopics(true);
        final Cluster cl = new Cluster("Other Languages").addSubclusters(new Cluster(
            "Polish").addDocuments(documents.subList(0, 3)));

        final List<Cluster> expectedClusters = Lists.newArrayList(c1, co, cl);

        check(documents, expectedClusters, Lists.newArrayList(LanguageCode.POLISH,
            LanguageCode.GERMAN), LanguageAggregationStrategy.FLATTEN_MAJOR_LANGUAGE);
    }

    @Test
    public void testMoreLanguagesFlattenMajorNone()
    {
        final List<Document> documents = documentsWithLanguages(LanguageCode.POLISH,
            LanguageCode.POLISH, LanguageCode.POLISH, LanguageCode.GERMAN,
            LanguageCode.GERMAN);

        final Cluster cg = new Cluster("German").addSubclusters(new Cluster("Cluster 2")
            .addDocuments(documents.get(4)), new Cluster("Other Topics").addDocuments(
            documents.get(3)).setOtherTopics(true));

        final Cluster cp = new Cluster("Polish").addDocuments(documents.subList(0, 3));

        final List<Cluster> expectedClusters = Lists.newArrayList(cp, cg);

        check(documents, expectedClusters, Lists.newArrayList(LanguageCode.POLISH,
            LanguageCode.GERMAN), LanguageAggregationStrategy.FLATTEN_NONE);
    }
    
    @Test
    public void testMoreLanguagesTrivialOrNoClusters()
    {
        final List<Document> documents = documentsWithLanguages(LanguageCode.POLISH,
            LanguageCode.POLISH, LanguageCode.POLISH, LanguageCode.NORWEGIAN,
            LanguageCode.NORWEGIAN);
        
        final Cluster cn = new Cluster("Norwegian").addDocuments(
            documents.subList(3, 5));
        
        final Cluster cp = new Cluster("Polish").addDocuments(documents.subList(0, 3));
        
        final List<Cluster> expectedClusters = Lists.newArrayList(cp, cn);
        
        check(documents, expectedClusters, Lists.newArrayList(LanguageCode.POLISH,
            LanguageCode.NORWEGIAN), LanguageAggregationStrategy.FLATTEN_MAJOR_LANGUAGE);
    }
    
    @Test
    public void testMoreLanguagesTrivialOrNoClustersMajorityLanguage()
    {
        final List<Document> documents = documentsWithLanguages(LanguageCode.POLISH,
            LanguageCode.POLISH, LanguageCode.POLISH, LanguageCode.NORWEGIAN,
            LanguageCode.NORWEGIAN);
        
        final Cluster co = new Cluster("Other Topics").addDocuments(
            documents).setOtherTopics(true);
        
        final List<Cluster> expectedClusters = Lists.newArrayList(co);
        
        check(documents, expectedClusters, Lists.newArrayList(LanguageCode.POLISH),
            LanguageAggregationStrategy.CLUSTER_IN_MAJORITY_LANGUAGE);
    }
    
    private void checkEmpty(final LanguageAggregationStrategy strategy)
    {
        final List<Document> documents = documentsWithLanguages();
        final List<Cluster> expectedClusters = Lists.newArrayList();

        check(documents, expectedClusters, Lists.<LanguageCode> newArrayList(), strategy);
    }

    private void checkNoLanguage(final LanguageAggregationStrategy strategy)
    {
        checkOneLanguageNontrivialClusters(strategy, null,
            multilingualClustering.defaultLanguage);
    }

    private void checkOneLanguageNontrivialClusters(final LanguageAggregationStrategy strategy,
        LanguageCode language)
    {
        checkOneLanguageNontrivialClusters(strategy, language, language);
    }

    private void checkOneLanguageNontrivialClusters(final LanguageAggregationStrategy strategy,
        LanguageCode language, LanguageCode clusteringLanguage)
    {
        final List<Document> documents = documentsWithLanguages(language, language,
            language);

        final Cluster c1 = new Cluster("Cluster 1").addDocuments(documents.get(1));
        final Cluster c2 = new Cluster("Cluster 2").addDocuments(documents.get(2));
        final Cluster co = new Cluster("Other Topics").addDocuments(documents.get(0))
            .setOtherTopics(true);

        final List<Cluster> expectedClusters = Lists.newArrayList(c1, c2, co);

        check(documents, expectedClusters, Lists.newArrayList(clusteringLanguage),
            strategy);
    }

    private void checkOneLanguageOtherTopicsCluster(final LanguageAggregationStrategy strategy)
    {
        final List<Document> documents = documentsWithLanguages(LanguageCode.NORWEGIAN,
            LanguageCode.NORWEGIAN, LanguageCode.NORWEGIAN);

        final Cluster co = new Cluster("Other Topics").addDocuments(documents.get(0),
            documents.get(1), documents.get(2)).setOtherTopics(true);

        final List<Cluster> expectedClusters = Lists.newArrayList(co);

        check(documents, expectedClusters, Lists.newArrayList(LanguageCode.NORWEGIAN),
            strategy);
    }

    private void check(final List<Document> documents,
        final List<Cluster> expectedClusters,
        final List<LanguageCode> expectedClusteringLanguages,
        LanguageAggregationStrategy languageClusteringStrategy)
    {
        multilingualClustering.languageAggregationStrategy = languageClusteringStrategy;

        final List<Cluster> actualClusters = multilingualClustering.process(documents,
            testMultilingualClusteringAlgorithm);
        assertThatClusters(actualClusters).isEquivalentTo(expectedClusters);
        assertThat(testMultilingualClusteringAlgorithm.clusteringLanguages).containsOnly(
            expectedClusteringLanguages.toArray());
    }

    /**
     * Returns a list of documents with the provided languages.
     */
    private static List<Document> documentsWithLanguages(LanguageCode... languages)
    {
        return Lists.newArrayList(Lists.transform(Arrays.asList(languages),
            new Function<LanguageCode, Document>()
            {
                public Document apply(LanguageCode language)
                {
                    return new Document().setLanguage(language);
                }
            }));
    }

    /**
     * A mock multilingual clustering algorithm.
     */
    private static class TestMultilingualClusteringAlgorithm implements
        IMonolingualClusteringAlgorithm
    {
        private Set<LanguageCode> clusteringLanguages = Sets.newHashSet();

        public List<Cluster> process(List<Document> documents, LanguageCode language)
        {
            final List<Cluster> clusters = Lists.newArrayList();

            clusteringLanguages.add(language);

            if (LanguageCode.POLISH.equals(language))
            {
                // No clusters at all
            }
            else if (LanguageCode.NORWEGIAN.equals(language))
            {
                // Return one junk cluster
                Cluster.appendOtherTopics(documents, clusters);
            }
            else
            {
                // Create some clusters
                clusters.add(new Cluster("Cluster 1"));
                clusters.add(new Cluster("Cluster 2"));

                for (int i = 1; i < documents.size(); i++)
                {
                    clusters.get(i % clusters.size()).addDocuments(documents.get(i));
                }

                Cluster.appendOtherTopics(documents, clusters);

                return Lists.newArrayList(Collections2.filter(clusters,
                    new Predicate<Cluster>()
                    {
                        public boolean apply(Cluster cluster)
                        {
                            return !cluster.getDocuments().isEmpty();
                        }
                    }));
            }
            return clusters;
        }
    }
}
