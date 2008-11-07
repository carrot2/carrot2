
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
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
 * Represents a specific configuration of a {@link ProcessingComponent}.
 */
public class ProcessingComponentConfiguration
{
    /**
     * The specific {@link ProcessingComponent} class.
     */
    public final Class<? extends ProcessingComponent> componentClass;

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
     * @param componentClass the specific {@link ProcessingComponent} class.
     * @param componentId identifier of the component.
     * @param attributes initialization attributes for this component configuration.
     */
    public ProcessingComponentConfiguration(
        Class<? extends ProcessingComponent> componentClass, String componentId,
        Map<String, Object> attributes)
    {
        this.componentClass = componentClass;
        this.componentId = componentId;
        this.attributes = attributes;
    }
    
    /**
     * Creates a new component configuration with an empty set of initialization attributes.
     * 
     * @param componentClass the specific {@link ProcessingComponent} class.
     * @param componentId identifier of the component.
     */
    public ProcessingComponentConfiguration(
        Class<? extends ProcessingComponent> componentClass, String componentId)
    {
        this(componentClass, componentId, Collections.<String, Object> emptyMap());
    }
}
