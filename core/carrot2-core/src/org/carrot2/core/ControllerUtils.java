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
public final class ControllerUtils
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
        Map<String, Object> attributes, boolean checkRequiredAttributes,
        ControllerContext context) throws ProcessingException
    {
        try
        {
            AttributeBinder.bind(processingComponent, attributes,
                checkRequiredAttributes, Input.class, Init.class);

            processingComponent.init(context);

            AttributeBinder.bind(processingComponent, attributes,
                checkRequiredAttributes, Output.class, Init.class);
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
     * {@link ProcessingComponent#afterProcessing()} hooks. If requested, stores
     * processing times in the attributes map on return.
     * 
     * @param measureTime if <code>true</code>, processing time will be measured and
     *            stored in the attributes map
     * @see AttributeNames#PROCESSING_TIME_ALGORITHM
     * @see AttributeNames#PROCESSING_TIME_SOURCE
     * @see AttributeNames#PROCESSING_TIME_TOTAL
     */
    public static void performProcessing(Map<String, Object> attributes,
        boolean measureTime, ProcessingComponent... processingComponents)
    {
        for (final ProcessingComponent element : processingComponents)
        {
            performProcessing(element, attributes, measureTime);
        }
    }

    /**
     * Performs processing with the provided {@link ProcessingComponent}, including
     * {@link ProcessingComponent#beforeProcessing()} and
     * {@link ProcessingComponent#afterProcessing()} hooks. If requested, stores
     * processing times in the attributes map.
     * 
     * @param measureTime if <code>true</code>, processing time will be measured and
     *            stored in the attributes map
     * @see AttributeNames#PROCESSING_TIME_ALGORITHM
     * @see AttributeNames#PROCESSING_TIME_SOURCE
     * @see AttributeNames#PROCESSING_TIME_TOTAL
     */
    public static void performProcessing(ProcessingComponent processingComponent,
        Map<String, Object> attributes, boolean measureTime)
    {
        long componentStart = System.currentTimeMillis();
        try
        {
            beforeProcessing(processingComponent, attributes);
            performProcessing(processingComponent, attributes);
        }
        finally
        {
            afterProcessing(processingComponent, attributes);

            if (measureTime)
            {
                long componentStop = System.currentTimeMillis();

                final long time = componentStop - componentStart;
                if (processingComponent instanceof DocumentSource)
                {
                    addTime(AttributeNames.PROCESSING_TIME_SOURCE, time, attributes);
                }
                else if (processingComponent instanceof ClusteringAlgorithm)
                {
                    addTime(AttributeNames.PROCESSING_TIME_ALGORITHM, time, attributes);
                }
                addTime(AttributeNames.PROCESSING_TIME_TOTAL, time, attributes);
            }
        }
    }

    /**
     * Adds time to the specified time attribute.
     */
    static void addTime(String key, Long timeToAdd, Map<String, Object> attributes)
    {
        if (timeToAdd == null)
        {
            return;
        }

        final Long time = (Long) attributes.get(key);
        if (time == null)
        {
            attributes.put(key, timeToAdd);
        }
        else
        {
            attributes.put(key, time + timeToAdd);
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
