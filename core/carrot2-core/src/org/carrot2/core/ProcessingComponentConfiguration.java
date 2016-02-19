
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.*;

import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Represents a specific configuration of a {@link IProcessingComponent}.
 */
public class ProcessingComponentConfiguration
{
    /**
     * The specific {@link IProcessingComponent} class.
     */
    public final Class<? extends IProcessingComponent> componentClass;

    /**
     * Identifier of the component.
     */
    public final String componentId;

    /**
     * Initialization attributes for this component configuration.
     */
    public final Map<String, Object> attributes;

    /**
     * Creates a new component configuration.
     * 
     * @param componentClass the specific {@link IProcessingComponent} class.
     * @param componentId identifier of the component.
     * @param attributes initialization attributes for this component configuration.
     */
    public ProcessingComponentConfiguration(
        Class<? extends IProcessingComponent> componentClass, String componentId,
        Map<String, Object> attributes)
    {
        this.componentClass = componentClass;
        this.componentId = componentId;
        this.attributes = Collections.unmodifiableMap(Maps.newHashMap(attributes));
    }

    /**
     * Creates a new component configuration with an empty set of initialization
     * attributes.
     * 
     * @param componentClass the specific {@link IProcessingComponent} class.
     * @param componentId identifier of the component.
     */
    public ProcessingComponentConfiguration(
        Class<? extends IProcessingComponent> componentClass, String componentId)
    {
        this(componentClass, componentId, Collections.<String, Object> emptyMap());
    }

    static Map<String, ProcessingComponentConfiguration> indexByComponentId(
        ProcessingComponentConfiguration... configurations)
    {
        final HashMap<String, ProcessingComponentConfiguration> componentIdToConfiguration = Maps
            .newHashMapWithExpectedSize(configurations.length);
        for (ProcessingComponentConfiguration configuration : configurations)
        {
            if (componentIdToConfiguration.put(configuration.componentId, configuration) != null)
            {
                throw new IllegalArgumentException("Duplicate processing component id: "
                    + configuration.componentId);
            }
        }
        return Collections.unmodifiableMap(componentIdToConfiguration);
    }
}
