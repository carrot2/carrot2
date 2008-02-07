package org.carrot2.core.controller;

import java.util.Map;

import org.carrot2.core.*;

/**
 * <p>
 * This is the simplest possible controller. It is useful for one-off processing either
 * with existing component instances or just classes of components to be involved in
 * processing.
 * <p>
 * The processing cycle
 */
public final class SimpleController
{
    /**
     * <p>
     * Every time processing is requested, new instances of the components get created,
     * initialized, called and disposed of.
     * <p>
     * This method may seem too general, but actually it might be useful to be able to
     * call processing with only one or more than two components. The former case is handy
     * when one wants to fetch documents from some source without processing or perform
     * clustering on already available documents (this just saves dummy document producer/
     * cluster collector components). The latter case is useful when some filters are
     * required in between the source and clustering components (e.g. HTML stripping).
     * 
     * @param attributes A map of attributes passed between components during processing.
     * @param processingComponentClasses This is one of the places where generics suck:
     *            it's nice to have a varargs of class with an upper bound here, but the
     *            caller's code will get warnings (implicit construction of an array of
     *            parameterized type, which is not allowed). An alternative is to have
     *            Class<?> here, but then, class types won't be checked during
     *            compilation. Finally, a collection would solve all the problems, but
     *            this is so much hassle on the calling code's side. For now I guess I'd
     *            go with the second option.
     */
    @SuppressWarnings("unchecked")
    public ProcessingResult process(Map<String, Object> attributes,
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        // Create instances of processing components.
        final ProcessingComponent [] processingComponents = new ProcessingComponent [processingComponentClasses.length];
        for (int i = 0; i < processingComponents.length; i++)
        {
            try
            {
                processingComponents[i] = (ProcessingComponent) processingComponentClasses[i]
                    .newInstance();
            }
            catch (InstantiationException e)
            {
                throw new ComponentInitializationException(
                    "Could not instantiate component class: "
                        + processingComponentClasses[i].getName(), e);
            }
            catch (IllegalAccessException e)
            {
                throw new ComponentInitializationException(
                    "Could not instantiate component class: "
                        + processingComponentClasses[i].getName(), e);
            }
        }

        return process(attributes, processingComponents);
    }

    /**
     * Run full processing cycle on already instantiated components.
     */
    public ProcessingResult process(Map<String, Object> attributes,
        ProcessingComponent... processingComponents) throws ProcessingException
    {
        try
        {
            // Initialize all components.
            for (int i = 0; i < processingComponents.length; i++)
            {
                ControllerUtils.init(processingComponents[i], attributes);
            }

            try
            {
                // Call before processing hook.
                for (int i = 0; i < processingComponents.length; i++)
                {
                    ControllerUtils.beforeProcessing(processingComponents[i], attributes);
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
