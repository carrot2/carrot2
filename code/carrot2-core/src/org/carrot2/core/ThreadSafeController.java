package org.carrot2.core;

import java.util.Map;

/**
 * 
 */
public class ThreadSafeController
{
    void addClusteringAlgorithm(String id, Class clazz,
        Map<String, Object> instanceParameters)
    {
    }

    void addDataSource(String id, Class clazz, Map<String, Object> instanceParameters)
    {
    }

    ProcessingResult process(String dataSourceId, String clusteringAlgorithmId,
        Map<String, Object> runtimeParameters)
    {
        return null;
    }
}
