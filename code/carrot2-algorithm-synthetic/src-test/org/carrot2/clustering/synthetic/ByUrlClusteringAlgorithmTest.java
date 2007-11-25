/**
 * 
 */
package org.carrot2.clustering.synthetic;

import org.carrot2.core.ClusteringAlgorithm;
import org.carrot2.core.ClusteringAlgorithmTest;

/**
 *
 */
public class ByUrlClusteringAlgorithmTest extends ClusteringAlgorithmTest
{
    @Override
    public Class<? extends ClusteringAlgorithm> getClusteringAlgorithmClass()
    {
        return ByUrlClusteringAlgorithm.class;
    }
}
