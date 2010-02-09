
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
import java.util.Set;

import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.simpleframework.xml.Attribute;

import com.google.common.collect.Sets;

/**
 * A simple controller implementing the life cycle described in
 * {@link IProcessingComponent}.
 * <p>
 * This controller is useful for one-time processing either with existing component
 * instances or classes of components to be created for processing. In case component
 * classes are used, for <b>each query</b> the controller creates, initializes and
 * destroys instances of all components involved in the processing. For long-running
 * applications (e.g. web applications) please consider the {@link CachingController},
 * which offers processing component pooling and result caching.
 * <p>
 * Thread-safety of processing on instantiated component (
 * {@link #process(Map, IProcessingComponent...)}) instances is not enforced in any way
 * and must be assured externally. Processing on component classes (
 * {@link #process(Map, Class...)}) is thread safe, but there is an additional overhead of
 * creating new component instances for each query (which may or may not be a performance
 * issue, this depends on a given component).
 */
public final class SimpleController implements IController
{
    /**
     * {@link IControllerContext} for this controller.
     */
    private ControllerContextImpl context;

    /**
     * Initialization attributes.
     */
    private Map<String, Object> initAttributes;

    public SimpleController()
    {
        this.context = new ControllerContextImpl();
    }

    /**
     * Initializes this controller. Initialization of {@link SimpleController}s is
     * optional. If performed, the provided <code>initAttributes</code> will be used
     * during processing, if not overridden by the attributes provided at processing time.
     * An alternative to calling this method is providing both initialization- and
     * processing-time attributes when calling {@link #process(Map, Class...)}.
     */
    public void init(Map<String, Object> initAttributes)
        throws ComponentInitializationException
    {
        this.initAttributes = initAttributes;
    }

    /**
     * Creates instances of processing components, initializes them, performs processing
     * and disposes of the component instances after processing is complete.
     * <p>
     * See class description for potential performance aspects of using this method.
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
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        // Create instances of processing components.
        final IProcessingComponent [] processingComponents = new IProcessingComponent [processingComponentClasses.length];
        for (int i = 0; i < processingComponents.length; i++)
        {
            try
            {
                processingComponents[i] = (IProcessingComponent) processingComponentClasses[i]
                    .newInstance();
            }
            catch (final InstantiationException e)
            {
                throw new ComponentInitializationException(
                    "Could not instantiate component class: "
                        + processingComponentClasses[i].getName(), e);
            }
            catch (final IllegalAccessException e)
            {
                throw new ComponentInitializationException(
                    "Could not instantiate component class: "
                        + processingComponentClasses[i].getName(), e);
            }
        }

        return process(attributes, processingComponents);
    }

    /**
     * Initializes the provided component instances, performs processing and disposes of
     * the instances after processing is complete.
     * <p>
     * See class description for potential performance and threading aspects of using this
     * method.
     * 
     * @param attributes attributes to be used during processing. {@link Input} attributes
     *            will be transferred from this map to the corresponding fields.
     *            {@link Output} attributes will be collected and stored in this map, so
     *            the map must be modifiable. Keys of the map are computed based on the
     *            <code>key</code> parameter of the {@link Attribute} annotation.
     * @param processingComponents instances of processing component to be used for
     *            processing.
     */
    public ProcessingResult process(Map<String, Object> attributes,
        IProcessingComponent... processingComponents) throws ProcessingException
    {
        if (this.context == null)
        {
            throw new IllegalStateException("Controller not initialized.");
        }

        final Set<IProcessingComponent> initializedComponents = Sets.newHashSet();

        // Merge initialization and processing attributes first
        if (initAttributes != null)
        {
            for (String initKey : initAttributes.keySet())
            {
                if (!attributes.containsKey(initKey))
                {
                    attributes.put(initKey, initAttributes.get(initKey));
                }
            }
        }

        try
        {
            // Initialize all components.
            for (final IProcessingComponent element : processingComponents)
            {
                initializedComponents.add(element);
                ControllerUtils.init(element, attributes, true, context);
            }

            ControllerUtils.performProcessing(attributes, true, processingComponents);

            return new ProcessingResult(attributes);

        }
        finally
        {
            // Finally, dispose of all components
            for (final IProcessingComponent element : initializedComponents)
            {
                element.dispose();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if (this.context != null)
        {
            this.context.dispose();
            this.context = null;
        }
    }
}
