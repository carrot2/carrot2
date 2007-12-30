package org.carrot2.core.controller;

import java.util.Map;

import org.carrot2.core.*;
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
     * @param processingComponents TODO: This is one of the places where generics suck:
     *            it's nice to have a varargs of class with an upper bound here, but the
     *            caller's code will get warnings (implicint construction of an array of
     *            parametrized type, which is not allowed). An alternative is to have
     *            Class<?> here, but then, class types won't be checked during
     *            compilation. Finally, a collection would solve all the problems, but
     *            this is so much hassle on the calling code's side. For now I guess I'd
     *            go with the second option.
     * @return
     * @throws InstantiationException
     * @throws InitializationException
     * @throws ProcessingException
     */
    @SuppressWarnings("unchecked")
    public ProcessingResult process(Map<String, Object> parameters,
        Map<String, Object> attributes, Class<?>... processingComponentClasses)
        throws InstantiationException, InitializationException, ProcessingException
    {
        // First, create instances
        ProcessingComponent [] processingComponents = new ProcessingComponent [processingComponentClasses.length];
        for (int i = 0; i < processingComponents.length; i++)
        {
            processingComponents[i] = (ProcessingComponent) ParameterBinder
                .createInstance(processingComponentClasses[i], parameters);
        }

        // Now initialize
        try
        {
            for (int i = 0; i < processingComponents.length; i++)
            {
                processingComponents[i].init();
            }

            try
            {
                // Perform runtime life cycle
                // Call before processing hooks
                for (int i = 0; i < processingComponents.length; i++)
                {
                    ControllerUtils.beforeProcessing(processingComponents[i], parameters,
                        attributes);
                }

                // Perform processing
                for (int i = 0; i < processingComponents.length; i++)
                {
                    ControllerUtils
                        .performProcessing(processingComponents[i], attributes);
                }

                return new ProcessingResult(attributes);
            }
            finally
            {
                // Call after processing hooks
                for (int i = 0; i < processingComponents.length; i++)
                {
                    ControllerUtils.afterProcessing(processingComponents[i], attributes);
                }
            }
        }
        finally
        {
            // Finally, dispose of all components
            for (int i = 0; i < processingComponents.length; i++)
            {
                processingComponents[i].dispose();
            }
        }
    }
}
