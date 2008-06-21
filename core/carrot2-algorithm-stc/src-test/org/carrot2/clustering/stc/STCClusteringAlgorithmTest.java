package org.carrot2.clustering.stc;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;

import org.carrot2.core.Cluster;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.carrot2.text.preprocessing.Preprocessor;
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
    public void testClusteringWithDfCutOff()
    {
        processingAttributes.put(Preprocessor.class.getName() + ".dfCutoff", 20);
        final Collection<Cluster> clustersWithCutOff = cluster(DOCUMENTS_DATA_MINING)
            .getClusters();

        // Clustering with df cut-off must not fail
        assertThat(clustersWithCutOff.size()).isGreaterThan(0);
    }
}
