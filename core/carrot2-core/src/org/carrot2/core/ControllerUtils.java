
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.Map;

import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

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
    public static void init(IProcessingComponent processingComponent,
        Map<String, Object> attributes, boolean checkRequiredAttributes,
        IControllerContext context) throws ProcessingException
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
    public static void beforeProcessing(IProcessingComponent processingComponent,
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
            throw new ProcessingException("Attribute binding failed: " + e.getMessage(),
                e);
        }
        catch (final AttributeBindingException e)
        {
            throw new ProcessingException("Attribute binding failed: " + e.getMessage(),
                e);
        }
    }

    /**
     * Perform all life cycle required to do processing.
     */
    public static void performProcessing(IProcessingComponent processingComponent,
        Map<String, Object> attributes) throws ProcessingException
    {
        processingComponent.process();
    }

    /**
     * Perform processing on the provided {@link IProcessingComponent}s, including
     * {@link IProcessingComponent#beforeProcessing()} and
     * {@link IProcessingComponent#afterProcessing()} hooks. If requested, stores
     * processing times in the attributes map on return.
     * 
     * @param measureTime if <code>true</code>, processing time will be measured and
     *            stored in the attributes map
     * @see AttributeNames#PROCESSING_TIME_ALGORITHM
     * @see AttributeNames#PROCESSING_TIME_SOURCE
     * @see AttributeNames#PROCESSING_TIME_TOTAL
     */
    public static void performProcessing(Map<String, Object> attributes,
        boolean measureTime, IProcessingComponent... processingComponents)
    {
        for (final IProcessingComponent element : processingComponents)
        {
            performProcessing(element, attributes, measureTime);
        }
    }

    /**
     * Performs processing with the provided {@link IProcessingComponent}, including
     * {@link IProcessingComponent#beforeProcessing()} and
     * {@link IProcessingComponent#afterProcessing()} hooks. If requested, stores
     * processing times in the attributes map.
     * 
     * @param measureTime if <code>true</code>, processing time will be measured and
     *            stored in the attributes map
     * @see AttributeNames#PROCESSING_TIME_ALGORITHM
     * @see AttributeNames#PROCESSING_TIME_SOURCE
     * @see AttributeNames#PROCESSING_TIME_TOTAL
     */
    public static void performProcessing(IProcessingComponent processingComponent,
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
                if (processingComponent instanceof IDocumentSource)
                {
                    addTime(AttributeNames.PROCESSING_TIME_SOURCE, time, attributes);
                }
                if (processingComponent instanceof IClusteringAlgorithm)
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
    public static void afterProcessing(IProcessingComponent processingComponent,
        Map<String, Object> attributes)
    {
        try
        {
            processingComponent.afterProcessing();

          final Map<String, Object> outputAttributesWithNulls = Maps.newHashMap();
          AttributeBinder.bind(processingComponent, outputAttributesWithNulls, Output.class,
              Processing.class);
          attributes.putAll(Maps.filterValues(outputAttributesWithNulls, Predicates.notNull()));
        }
        catch (final InstantiationException e)
        {
            throw new ProcessingException("Attribute binding failed", e);
        }
    }
}
