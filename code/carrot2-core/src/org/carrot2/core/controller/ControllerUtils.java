/**
 * 
 */
package org.carrot2.core.controller;

import java.util.Map;

import org.carrot2.core.ProcessingComponent;
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
     * @param attributes
     * @throws InstantiationException
     */
    public static void afterProcessing(ProcessingComponent processingComponent,
        Map<String, Object> attributes)
        throws InstantiationException
    {
        AttributeBinder.bind(processingComponent, attributes, BindingDirection.OUT);
    }
}
