
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

import org.carrot2.core.attribute.Init;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.simpleframework.xml.Attribute;

/**
 * Performs processing using {@link IProcessingComponent}s. Implementations must enforce
 * the life cycle described in {@link IProcessingComponent}.
 */
public interface IController
{
    /**
     * Initializes this controller. This method must complete successfully before any
     * calls are made to the {@link #process(Map, Class...)} method.
     * 
     * @param attributes {@link Init}-time attributes for components instantiated in
     *  {@link #process(Map, Class...)}. 
     */
    public void init(Map<String, Object> attributes)
        throws ComponentInitializationException;

    /**
     * Shuts down this controller. No calls to {@link #process(Map, Class...)} must be
     * made after invoking this method.
     */
    public void dispose();

    /**
     * Performs processing according to the life cycle specified in
     * {@link IProcessingComponent}.
     * 
     * @param attributes attributes to be used during processing. {@link Input} attributes
     *            will be transferred from this map to the corresponding fields.
     *            {@link Output} attributes will be collected and stored in this map, so
     *            the map must be modifiable. Keys of the map are computed based on the
     *            <code>key</code> parameter of the {@link Attribute} annotation.
     * @param processingComponentClasses classes of components to be involved in
     *            processing in the order they should be arranged in the pipeline.
     */
    public ProcessingResult process(Map<String, Object> attributes,
        Class<?>... processingComponentClasses) throws ProcessingException;
}
