package org.carrot2.core;

import java.util.Map;

/**
 * 
 */
public interface Controller
{
    // listener for documents
    ProcessingResult process(
        Map<String, Object> requestParameters,
        DocumentSource documentSource, ClusteringAlgorithm clusteringAlgorithm)
        throws ProcessingException;
}
