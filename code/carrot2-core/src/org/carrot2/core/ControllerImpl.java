package org.carrot2.core;

import java.util.*;

import org.carrot2.core.parameters.*;

public class ControllerImpl implements Controller
{
    @SuppressWarnings("unchecked")
    @Override
    public ProcessingResult process(Map<String, Object> requestParameters,
        Map<String, Object> attributes, DocumentSource documentSource,
        ClusteringAlgorithm clusteringAlgorithm)
    {
        // TODO: exception handling
        try
        {
            final ProcessingResult result = new ProcessingResult();

            // Run document source
            beforeProcessing(documentSource, requestParameters, attributes);
            documentSource.beforeProcessing();
            documentSource.performProcessing();
            afterProcessing(documentSource, requestParameters, attributes);
            documentSource.afterProcessing();

            // Run clustering
            beforeProcessing(clusteringAlgorithm, requestParameters, attributes);
            clusteringAlgorithm.beforeProcessing();
            clusteringAlgorithm.performProcessing();
            afterProcessing(clusteringAlgorithm, requestParameters, attributes);
            clusteringAlgorithm.afterProcessing();

            // Form the results object
            result.clusters = (Collection<Cluster>) attributes.get("clusters");
            result.documents = (Collection<Document>) attributes.get("documents");

            return result;
        }
        catch (InstantiationException e)
        {
            throw new ProcessingException(e);
        }
    }

    /**
     * Performs all life cycle actions required before processing starts. This code is
     * refactored to make sure the tests can perform exactly the same sequence of actions
     * without using the controller as a whole (think of hassle with dummy document
     * sources for testing clustering algorithms, after all, that's why we have these
     * annotations :)
     * 
     * @param processingComponent
     * @param parameters
     * @param attributes
     * @throws InstantiationException
     */
    public static void beforeProcessing(ProcessingComponent processingComponent,
        Map<String, Object> parameters, Map<String, Object> attributes)
        throws InstantiationException
    {
        ParameterBinder.bind(processingComponent, parameters, BindingPolicy.RUNTIME);
        AttributeBinder.bind(processingComponent, attributes, BindingDirection.IN);
    }

    /**
     * Perform all life cycle actions after processing is complete.
     * 
     * @param processingComponent
     * @param parameters
     * @param attributes
     * @throws InstantiationException
     */
    public static void afterProcessing(ProcessingComponent processingComponent,
        Map<String, Object> parameters, Map<String, Object> attributes)
        throws InstantiationException
    {
        AttributeBinder.bind(processingComponent, attributes, BindingDirection.OUT);
    }
}
