package org.carrot2.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.carrot2.core.parameters.Binder;
import org.carrot2.core.parameters.BindingPolicy;

public class ControllerImpl implements Controller
{
    @Override
    public ProcessingResult process(Map<String, Object> requestParameters,
        DocumentSource documentSource, ClusteringAlgorithm clusteringAlgorithm)
    {
        try
        {
            final ProcessingResult result = new ProcessingResult();
            
            Binder.bind(documentSource, requestParameters, BindingPolicy.RUNTIME);

            result.documents = new ArrayList<Document>();
            for (Iterator<Document> i = documentSource.getDocuments(); i.hasNext();)
            {
                result.documents.add(i.next());
            }

            requestParameters.put("documents", result.documents);
            Binder.bind(clusteringAlgorithm, requestParameters, BindingPolicy.RUNTIME);

            result.clusters = new ArrayList<Cluster>();
            for (Iterator<Cluster> i = clusteringAlgorithm.getClusters(); i.hasNext();)
            {
                result.clusters.add(i.next());
            }

            return result;
        }
        catch (InstantiationException e)
        {
            throw new ProcessingException(e);
        }
    }
}
