package org.carrot2.core;

import java.util.Map;

/**
 * TODO: I'm not sure if it make sense to define an interface for controllers, because
 * depending on the type of the controller, there will be different methods. E.g. a very
 * simplistic one-off controller would take component instances, initialize them, perform
 * processing and then destroy. But a more sophisticated controller would allow
 * registration of components and then the process method would probably take component
 * identifiers instead of instances. Finally, the most sophisticated, thread-safe
 * controller may need methods for managing the pool etc.
 */
public interface Controller
{
    public ProcessingResult process(Map<String, Object> requestParameters,
        Map<String, Object> attributes, DocumentSource documentSource,
        ClusteringAlgorithm clusteringAlgorithm) throws ProcessingException;
}
