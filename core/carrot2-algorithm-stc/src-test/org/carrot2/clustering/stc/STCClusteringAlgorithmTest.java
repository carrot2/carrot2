package org.carrot2.clustering.stc;

import static org.junit.Assert.assertTrue;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.junit.Test;

import com.google.common.collect.Maps;

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
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRepeatedClusteringWithCache()
    {
        final Controller controller = new CachingController(DocumentSource.class);
        controller.init(new HashMap());

        final HashMap processingAttributes = Maps.newHashMap();
        processingAttributes.put(AttributeNames.DOCUMENTS, DOCUMENTS_DATA_MINING);

        controller.process(processingAttributes, STCClusteringAlgorithm.class);
        controller.process(processingAttributes, STCClusteringAlgorithm.class);
    }
}
