package org.carrot2.core;

import java.util.Collection;
import java.util.Map;

import org.carrot2.core.controller.ControllerUtils;
import org.carrot2.core.parameter.ParameterBinder;

/**
 * This is the simplest possible controller. It is useful for one-off processing,
 * everytime processing is requested, new instances of the components get created,
 * initialized, called and disposed of.
 */
public class SimpleController
{
    /**
     * This method may seem too general, but actually it might be useful to be able to
     * call processing with only one or more than two components. The former case is handy
     * when one wants to fetch documents from some source without processing or perform
     * clustering on already available documents (this just saves dummy document producer/
     * cluster collector components). The latter case is useful when some filters are
     * required in between the source and clustering components (e.g. HTML stripping).
     * 
     * @param parameters both instatiation and runtime parameters
     * @param attributes
     * @param processingComponents
     * @return
     * @throws InstantiationException
     * @throws InitializationException
     * @throws ProcessingException
     */
    @SuppressWarnings("unchecked")
    public ProcessingResult process(Map<String, Object> parameters,
        Map<String, Object> attributes,
        Class<? extends ProcessingComponent>... processingComponentClasses)
        throws InstantiationException
    {
        // First, create and initialize instances
        ProcessingComponent [] processingComponents = new ProcessingComponent [processingComponentClasses.length];
        for (int i = 0; i < processingComponents.length; i++)
        {
            processingComponents[i] = ParameterBinder.createInstance(
                processingComponentClasses[i], parameters);
            processingComponents[i].init();
        }

        // Now let each component in the chain do the processing
        final ProcessingResult result = new ProcessingResult();

        for (int i = 0; i < processingComponents.length; i++)
        {
            try
            {
                ControllerUtils.beforeProcessing(processingComponents[i], parameters,
                    attributes);
                processingComponents[i].beforeProcessing();
                processingComponents[i].performProcessing();

                ControllerUtils.afterProcessing(processingComponents[i], attributes);
            }
            finally
            {
                processingComponents[i].afterProcessing();
            }
        }

        // Finally, dispose of all components
        for (int i = 0; i < processingComponents.length; i++)
        {
            processingComponents[i].dispose();
        }
        
        // Form the results object
        result.clusters = (Collection<Cluster>) attributes.get("clusters");
        result.documents = (Collection<Document>) attributes.get("documents");

        return result;
    }
}
