
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

import java.util.Collections;
import java.util.Map;

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
        this.attributes = attributes;
    }
    
    /**
     * Creates a new component configuration with an empty set of initialization attributes.
     * 
     * @param componentClass the specific {@link IProcessingComponent} class.
     * @param componentId identifier of the component.
     */
    public ProcessingComponentConfiguration(
        Class<? extends IProcessingComponent> componentClass, String componentId)
    {
        this(componentClass, componentId, Collections.<String, Object> emptyMap());
    }
}
