package org.carrot2.core;

import java.util.Map;

import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;

/**
 * Static life cycle and controller utilities (for use within the core package).
 * <p>
 * This code is refactored to make sure the tests can perform exactly the same sequence of
 * actions without using the controller as a whole.
 */
final class ControllerUtils
{
    /**
     *
     */
    private ControllerUtils()
    {
        // no instances.
    }

    /**
     * Performs all life cycle actions required upon initialization.
     */
    @SuppressWarnings("unchecked")
    public static void init(ProcessingComponent processingComponent,
        Map<String, Object> attributes) throws ProcessingException
    {
        try
        {
            AttributeBinder
                .bind(processingComponent, attributes, Input.class, Init.class);

            processingComponent.init();

            AttributeBinder.bind(processingComponent, attributes, Output.class,
                Init.class);
        }
        catch (final InstantiationException e)
        {
            throw new ProcessingException("Attribute binding failed", e);
        }

    }

    /**
     * Performs all life cycle actions required before processing starts.
     */
    @SuppressWarnings("unchecked")
    public static void beforeProcessing(ProcessingComponent processingComponent,
        Map<String, Object> attributes) throws ProcessingException
    {
        try
        {
            AttributeBinder.bind(processingComponent, attributes, Input.class,
                Processing.class);

            processingComponent.beforeProcessing();
        }
        catch (final InstantiationException e)
        {
            throw new ProcessingException("Attribute binding failed", e);
        }

    }

    /**
     * Perform all life cycle required to do processing.
     */
    public static void performProcessing(ProcessingComponent processingComponent,
        Map<String, Object> attributes) throws ProcessingException
    {
        processingComponent.process();
    }

    /**
     * Perform processing on the provided {@link ProcessingComponent}s, including
     * {@link ProcessingComponent#beforeProcessing()} and
     * {@link ProcessingComponent#afterProcessing()} hooks. Stores processing times in the
     * attributes map on return.
     */
    public static void performProcessingWithTimeMeasurement(
        Map<String, Object> attributes, ProcessingComponent... processingComponents)
    {
        long totalStart = System.currentTimeMillis();
        long sourceTime = 0;
        long algorithmTime = 0;

        try
        {
            for (final ProcessingComponent element : processingComponents)
            {
                long componentStart = System.currentTimeMillis();
                try
                {
                    beforeProcessing(element, attributes);
                    performProcessing(element, attributes);
                }
                finally
                {
                    afterProcessing(element, attributes);
                    long componentStop = System.currentTimeMillis();

                    if (element instanceof DocumentSource)
                    {
                        sourceTime += (componentStop - componentStart);
                    }
                    else if (element instanceof ClusteringAlgorithm)
                    {
                        algorithmTime += (componentStop - componentStart);
                    }
                }
            }
        }
        finally
        {
            long totalStop = System.currentTimeMillis();

            attributes
                .put(AttributeNames.PROCESSING_TIME_TOTAL, (totalStop - totalStart));
            attributes.put(AttributeNames.PROCESSING_TIME_SOURCE, sourceTime);
            attributes.put(AttributeNames.PROCESSING_TIME_ALGORITHM, algorithmTime);
        }
    }

    /**
     * Perform all life cycle actions after processing is completed.
     */
    @SuppressWarnings("unchecked")
    public static void afterProcessing(ProcessingComponent processingComponent,
        Map<String, Object> attributes)
    {
        try
        {
            processingComponent.afterProcessing();

            AttributeBinder.bind(processingComponent, attributes, Output.class,
                Processing.class);
        }
        catch (final InstantiationException e)
        {
            throw new ProcessingException("Attribute binding failed", e);
        }
    }
}
