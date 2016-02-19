
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

package org.carrot2.clustering.stc;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.carrot2.core.test.SampleDocumentData;
import org.carrot2.text.preprocessing.CaseNormalizer;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.io.Resources;

/**
 * Test cases for the {@link STCClusteringAlgorithm}.
 */
public class STCClusteringAlgorithmTest extends
    ClusteringAlgorithmTestBase<STCClusteringAlgorithm>
{
    @Override
    public Class<STCClusteringAlgorithm> getComponentClass()
    {
        return STCClusteringAlgorithm.class;
    }

    @Test
    public void testClusteringWithDfThreshold()
    {
        processingAttributes.put(
            AttributeUtils.getKey(CaseNormalizer.class, "dfThreshold"), 20);
        final Collection<Cluster> clustersWithThreshold = cluster(
            SampleDocumentData.DOCUMENTS_DATA_MINING).getClusters();

        // Clustering with df threshold must not fail
        assertThat(clustersWithThreshold.size()).isGreaterThan(0);
    }

    @Test
    public void testMaxClusters()
    {
        processingAttributes.put(
            AttributeUtils.getKey(STCClusteringAlgorithm.class, "maxClusters"), 9);
        
        final Collection<Cluster> clusters = 
            cluster(SampleDocumentData.DOCUMENTS_DATA_MINING).getClusters();

        // 9 + others
        assertThat(clusters.size()).isEqualTo(9 + 1);
    }

    @Test
    public void testComputeIntersection()
    {
        int [] t1;

        t1 = new int [] {0, 1, 2,   1, 2, 3};
        assertEquals(2, STCClusteringAlgorithm.computeIntersection(t1, 0, 3, t1, 3, 3));

        t1 = new int [] {0, 1, 2,   3, 5, 6};
        assertEquals(0, STCClusteringAlgorithm.computeIntersection(t1, 0, 3, t1, 3, 3));

        t1 = new int [] {0, 1, 2,   -1, 2, 6};
        assertEquals(1, STCClusteringAlgorithm.computeIntersection(t1, 0, 3, t1, 3, 3));

        t1 = new int [] {0, 1, 2,   0};
        assertEquals(1, STCClusteringAlgorithm.computeIntersection(t1, 0, 3, t1, 3, 1));
    }

    @Test
    public void testMergingBaseClustersWithStemEquivalentPhrases()
    {
        List<Document> documents = Lists.newArrayList();
        documents.add(new Document("good programs . foo1"));
        documents.add(new Document("foo2 good programs . foo2"));
        documents.add(new Document("good programs taste good"));
        documents.add(new Document("good programs are good"));

        documents.add(new Document("good programming . foo3"));
        documents.add(new Document("foo4 good programming . foo4"));
        documents.add(new Document("good programming makes you feel better"));

        // Lower base cluster score.
        STCClusteringAlgorithmDescriptor.attributeBuilder(processingAttributes)
            .minBaseClusterScore(0);

        ProcessingResult pr = cluster(documents);
        Set<String> clusterLabels = collectClusterLabels(pr);
        assertThat("Good Programs").isIn(clusterLabels);
        assertThat("Good Programming").isNotIn(clusterLabels);
    }

    /**
     * CARROT-1008: STC is not using term stems.
     */
    @Test
    public void testCarrot1008() throws Exception
    {
        ProcessingResult pr = ProcessingResult.deserialize(
            Resources.asByteSource(
                Resources.getResource(this.getClass(), "CARROT-1008.xml")).openBufferedStream());

        STCClusteringAlgorithmDescriptor.attributeBuilder(processingAttributes)
            .maxClusters(30);

        pr = cluster(pr.getDocuments());

        dumpClusterLabels(pr);
        Set<String> clusterLabels = ClusteringAlgorithmTestBase.collectClusterLabels(pr);
        assertThat(
            clusterLabels.contains("Guns") &&
            clusterLabels.contains("Gun")).isFalse();
    }
}
