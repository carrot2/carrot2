package org.carrot2.clustering.stc;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
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
    public void testClusteringDataMining()
    {
        final ProcessingResult processingResult = cluster(DOCUMENTS_DATA_MINING);
        final Collection<Cluster> clusters = processingResult.getClusters();

        assertTrue(clusters.size() > 0);
        
        /*
        for (final Cluster cluster : clusters)
        {
            System.out.println(cluster.getLabel() + " (" + cluster.getDocuments().size()
                + " documents)");
        }
        */
    }
}
