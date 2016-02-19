
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

package org.carrot2.clustering.kmeans;

import java.util.List;
import java.util.Set;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.carrot2.core.test.SampleDocumentData;
import org.carrot2.core.test.assertions.Carrot2CoreAssertions;
import org.carrot2.text.clustering.MultilingualClustering.LanguageAggregationStrategy;
import org.carrot2.text.clustering.MultilingualClusteringDescriptor;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Sets;

public class BisectingKMeansClusteringAlgorithmTest extends
    ClusteringAlgorithmTestBase<BisectingKMeansClusteringAlgorithm>
{
    @Override
    public Class<BisectingKMeansClusteringAlgorithm> getComponentClass()
    {
        return BisectingKMeansClusteringAlgorithm.class;
    }

    @Test
    public void smokeTest()
    {
        final List<Document> documents = Lists.newArrayList();
        documents.add(new Document("WordA . WordA"));
        documents.add(new Document("WordB . WordB"));
        documents.add(new Document("WordC . WordC"));
        documents.add(new Document("WordA . WordA"));
        documents.add(new Document("WordB . WordB"));
        documents.add(new Document("WordC . WordC"));

        BisectingKMeansClusteringAlgorithmDescriptor.attributeBuilder(
            processingAttributes).labelCount(1).partitionCount(3);
        final List<Cluster> clusters = cluster(documents).getClusters();

        assertNotNull(clusters);
        assertEquals(3, clusters.size());
        Carrot2CoreAssertions.assertThat(clusters.get(0)).hasLabel("WordA");
        Carrot2CoreAssertions.assertThat(clusters.get(1)).hasLabel("WordB");
        Carrot2CoreAssertions.assertThat(clusters.get(2)).hasLabel("WordC");
    }

    @Test
    public void testMultilingualSplit() throws Exception
    {
        BisectingKMeansClusteringAlgorithmDescriptor.attributeBuilder(processingAttributes)
            .labelCount(1).partitionCount(3);

        MultilingualClusteringDescriptor.attributeBuilder(processingAttributes)
            .languageAggregationStrategy(LanguageAggregationStrategy.FLATTEN_NONE);

        final ProcessingResult pr = cluster(SampleDocumentData.DOCUMENTS_SALSA_MULTILINGUAL);
        final List<Cluster> clusters = pr.getClusters();
        final Set<String> clusterNames = Sets.newHashSet();
        for (Cluster c : clusters) {
            clusterNames.add(c.getLabel());
        }

        assertThat(clusterNames).contains("English", "Italian", "French", "Spanish", "German");
    }
}
