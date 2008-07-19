package org.carrot2.clustering.lingo;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;

import org.carrot2.core.Cluster;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.carrot2.text.preprocessing.CaseNormalizer;
import org.junit.Test;

/**
 * Test cases for the {@link LingoClusteringAlgorithm}
 */
public class LingoClusteringAlgorithmTest extends
    ClusteringAlgorithmTestBase<LingoClusteringAlgorithm>
{
    @Override
    public Class<LingoClusteringAlgorithm> getComponentClass()
    {
        return LingoClusteringAlgorithm.class;
    }

    @Test
    public void testClusteringWithDfThreshold()
    {
        processingAttributes.put(CaseNormalizer.class.getName() + ".dfThreshold", 20);
        final Collection<Cluster> clustersWithThreshold = cluster(DOCUMENTS_DATA_MINING)
            .getClusters();

        // Clustering with df threshold must not fail
        assertThat(clustersWithThreshold.size()).isGreaterThan(0);
    }
}
