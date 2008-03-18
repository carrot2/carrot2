package org.carrot2.core;

import java.util.Map;
import java.util.Set;

import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.simpleframework.xml.Attribute;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A simple controller implementing the life cycle described in
 * {@link ProcessingComponent}.
 * <p>
 * This controller is useful for one-time processing either with existing component
 * instances or classes of components to be created for processing. In case component
 * classes are used, for <b>each query</b> the controller creates, initializes and
 * destroys instances of all components involved in the processing.
 * <p>
 * Thread-safety of processing on instantiated component instances is not enforced in any
 * way and must be assured externally. Processing on component classes is thread safe, but
 * there is an additional overhead of creating new component instances for each query
 * (which may or may not be a performance issue, this depends on a given component).
 */
public final class SimpleController implements Controller
{
    /** Attributes provided upon {@link #init(Map)} */
    private Map<String, Object> initAttributes = Maps.newHashMap();

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
        final ProcessingComponent [] processingComponents = new ProcessingComponent [processingComponentClasses.length];
        for (int i = 0; i < processingComponents.length; i++)
        {
            try
            {
                processingComponents[i] = (ProcessingComponent) processingComponentClasses[i]
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
        ProcessingComponent... processingComponents) throws ProcessingException
    {
        final Set<ProcessingComponent> initializedComponents = Sets.newHashSet();
        try
        {
            // Initialize all components.
            for (final ProcessingComponent element : processingComponents)
            {
                initializedComponents.add(element);
                ControllerUtils.init(element, initAttributes);
            }

            ControllerUtils.performProcessingWithTimeMeasurement(attributes,
                processingComponents);

            return new ProcessingResult(attributes);

        }
        finally
        {
            // Finally, dispose of all components
            for (final ProcessingComponent element : initializedComponents)
            {
                element.dispose();
            }
        }
    }

    public void dispose()
    {
        // Nothing to do
    }

    public void init(Map<String, Object> attributes)
        throws ComponentInitializationException
    {
        this.initAttributes = attributes;
    }
}
