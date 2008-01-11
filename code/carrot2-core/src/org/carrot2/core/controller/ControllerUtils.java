package org.carrot2.core.controller;

import java.util.Map;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.parameter.*;

/**
 * Static life cycle and controller utilities.
 * <p>
 * This code is refactored to make sure the tests can perform exactly the same sequence of
 * actions without using the controller as a whole.
 */
final class ControllerUtils
{
    /*
     * 
     */
    private ControllerUtils()
    {
        // no instances.
    }

    /**
     * Performs all life cycle actions required before processing starts.
     */
    public static void beforeProcessing(ProcessingComponent processingComponent,
        Map<String, Object> parameters, Map<String, Object> attributes)
        throws InstantiationException, ProcessingException
    {
        // Bind runtime parameters to the component.
        ParameterBinder.bind(processingComponent, parameters, BindingPolicy.RUNTIME);

        // Inject attributes in, run the hook, and extract attributes out. 
        AttributeBinder.bind(processingComponent, attributes, BindingDirection.IN);
        processingComponent.beforeProcessing();
        AttributeBinder.bind(processingComponent, attributes, BindingDirection.OUT);
    }

    /**
     * Perform all life cycle required to do processing.
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
     * Perform all life cycle actions after processing is completed.
     */
    public static void afterProcessing(ProcessingComponent processingComponent,
        Map<String, Object> attributes)
    {
        processingComponent.afterProcessing();
    }
}
