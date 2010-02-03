
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.stc;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.carrot2.core.Cluster;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.carrot2.core.test.SampleDocumentData;
import org.carrot2.text.preprocessing.CaseNormalizer;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Ignore;
import org.junit.Test;

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
            AttributeUtils.getKey(STCClusteringParameters.class, "maxClusters"), 9);
        
        final Collection<Cluster> clusters = 
            cluster(SampleDocumentData.DOCUMENTS_DATA_MINING).getClusters();

        // 9 + others
        assertThat(clusters.size()).isEqualTo(9 + 1);
    }

    @Test @Ignore
    public void testSampleClustering()
    {
        this.processingAttributes.put(
            AttributeUtils.getKey(STCClusteringParameters.class, "maxPhraseOverlap"), 
            0.6d);

        final Collection<Cluster> clusters = 
            cluster(SampleDocumentData.DOCUMENTS_DATA_MINING).getClusters();

        for (Cluster c : clusters)
        {
            System.out.println("[" + c.getDocuments().size() + ", "
                + c.getScore() + "]: " + c.getLabel());
        }
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
}
