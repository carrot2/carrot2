package org.carrot2.core;

import java.util.Map;

import org.carrot2.core.attribute.Init;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.simpleframework.xml.Attribute;

/**
 * Performs processing using {@link ProcessingComponent}s. Implementations must enforce
 * the life cycle described in {@link ProcessingComponent}.
 */
public interface Controller
{
    /**
     * Initializes this controller. This method must complete successfully before any
     * calls are made to the {@link #process(Map, Class...)} method.
     * 
     * @param attributes {@link Init}-time attributes for the component
     */
    public void init(Map<String, Object> attributes)
        throws ComponentInitializationException;

    /**
     * s Disposed of this controller. No calls to {@link #process(Map, Class...)} must be
     * made after invoking this method.
     */
    public void dispose();

    /**
     * Performs processing according to the life cycle specified in
     * {@link ProcessingComponent}.
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
