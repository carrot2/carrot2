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
    public static void init(IProcessingComponent processingComponent,
        Map<String, Object> inputAttributes, Map<String, Object> outputAttributes,
        boolean checkRequiredAttributes, IControllerContext context)
        throws ComponentInitializationException
    {
        try
        {
            AttributeBinder.bind(processingComponent, inputAttributes,
                checkRequiredAttributes, Input.class, Init.class);

            processingComponent.init(context);

            AttributeBinder.bind(processingComponent, outputAttributes,
                checkRequiredAttributes, Output.class, Init.class);
        }
        catch (final InstantiationException e)
        {
            throw new ComponentInitializationException("Attribute binding failed", e);
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
     * Performs processing with the provided {@link IProcessingComponent}, including
     * {@link IProcessingComponent#beforeProcessing()} and
     * {@link IProcessingComponent#afterProcessing()} hooks. Please note that outputAttributes
     * <strong>will not</strong> be copied back to the inputAttributes. 
     */
    public static void performProcessing(IProcessingComponent processingComponent,
        Map<String, Object> inputAttributes, Map<String, Object> outputAttributes)
    {
        try
        {
            beforeProcessing(processingComponent, inputAttributes);
            processingComponent.process();
        }
        finally
        {
            afterProcessing(processingComponent, outputAttributes);
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
            AttributeBinder.bind(processingComponent, outputAttributesWithNulls,
                Output.class, Processing.class);
            attributes.putAll(Maps.filterValues(outputAttributesWithNulls, Predicates
                .notNull()));
        }
        catch (final InstantiationException e)
        {
            throw new ProcessingException("Attribute binding failed", e);
        }
    }
}
