package org.carrot2.core;

import java.util.Collection;
import java.util.Map;

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
            ControllerUtils.beforeProcessing(documentSource, requestParameters, attributes);
            documentSource.beforeProcessing();
            documentSource.performProcessing();
            ControllerUtils.afterProcessing(documentSource, requestParameters, attributes);
            documentSource.afterProcessing();

            // Run clustering
            ControllerUtils.beforeProcessing(clusteringAlgorithm, requestParameters, attributes);
            clusteringAlgorithm.beforeProcessing();
            clusteringAlgorithm.performProcessing();
            ControllerUtils.afterProcessing(clusteringAlgorithm, requestParameters, attributes);
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
}
