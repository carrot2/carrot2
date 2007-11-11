package org.carrot2.core;

import java.util.Map;

import javax.sql.DataSource;

/**
 * 
 */
public class ThreadSafeController
{
    void addClusteringAlgorithm(String id, Class<? extends ClusteringAlgorithm> clazz,
        Map<String, Object> instanceParameters)
    {
    }

    void addDataSource(String id, Class<? extends DataSource> clazz,
        Map<String, Object> instanceParameters)
    {
    }

    ProcessingResult process(String dataSourceId, String clusteringAlgorithmId,
        Map<String, Object> runtimeParameters)
    {
        return null;
    }
}
