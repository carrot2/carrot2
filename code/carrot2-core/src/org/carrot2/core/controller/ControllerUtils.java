/**
 * 
 */
package org.carrot2.core.controller;

import java.util.Map;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.parameter.*;

/**
 *
 */
public class ControllerUtils
{
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
     * @throws ProcessingException
     */
    public static void beforeProcessing(ProcessingComponent processingComponent,
        Map<String, Object> parameters, Map<String, Object> attributes)
        throws InstantiationException, ProcessingException
    {
        ParameterBinder.bind(processingComponent, parameters, BindingPolicy.RUNTIME);
        AttributeBinder.bind(processingComponent, attributes, BindingDirection.IN);
        processingComponent.beforeProcessing();

        // It might be useful to bind outgoing attributes -- some components
        // down the chain may need to use the data bound here.
        // On the second thought...
        AttributeBinder.bind(processingComponent, attributes, BindingDirection.OUT);
    }

    /**
     * Perform all life cycle required to do processing.
     * 
     * @param processingComponent
     * @param attributes
     * @throws InstantiationException
     * @throws ProcessingException
     */
    public static void performProcessing(ProcessingComponent processingComponent,
        Map<String, Object> attributes) throws InstantiationException,
        ProcessingException
    {
        AttributeBinder.bind(processingComponent, attributes, BindingDirection.IN);
        processingComponent.performProcessing();
        AttributeBinder.bind(processingComponent, attributes, BindingDirection.OUT);
    }

    /**
     * Perform all life cycle actions after processing is complete.
     * 
     * @param processingComponent
     * @param attributes
     * @throws InstantiationException
     */
    public static void afterProcessing(ProcessingComponent processingComponent,
        Map<String, Object> attributes)
    {
        processingComponent.afterProcessing();
    }
}
