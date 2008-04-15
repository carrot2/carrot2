package org.carrot2.clustering.stc;

import org.carrot2.core.test.ClusteringAlgorithmTestBase;

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
}
