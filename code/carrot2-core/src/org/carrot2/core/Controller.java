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
        Map<String, Object> attributes, DocumentSource documentSource, ClusteringAlgorithm clusteringAlgorithm)
        throws ProcessingException;
}
