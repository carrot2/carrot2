
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

import java.util.Map;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;

/**
 * A component management strategy used by a {@link Controller}.
 */
public interface IProcessingComponentManager
{
    /**
     * Called upon initialization of the {@link Controller}.
     * 
     * @param context controller context
     * @param attributes global initialization attributes provided to the controller
     * @param configurations component configurations provided to the controller
     */
    public void init(IControllerContext context, Map<String, Object> attributes,
        ProcessingComponentConfiguration... configurations);

    /**
     * Prepares a component for processing. Specific managers may simply instantiate a new
     * component object, fetch a component from a pool or wrap a component prepared by a
     * delegate manager etc.
     * 
     * @param clazz class of the component to prepare
     * @param id Identifier of the component to prepare. May be <code>null</code>.
     * @param inputAttributes input attributes for all components requested to perform
     *            processing. Both {@link Init}- and {@link Processing}-time attributes
     *            will be provided. Managers must not modify this map.
     * @param outputAttributes storage for output attributes
     */
    public IProcessingComponent prepare(Class<? extends IProcessingComponent> clazz,
        String id, Map<String, Object> inputAttributes,
        Map<String, Object> outputAttributes);

    /**
     * Called after processing completed. This method is called regardless of whether the
     * processing completed successfully or with an error.
     *
     * @param component Component instance returned from {@link #prepare}.
     * @param id The same identifier of the component as used in the call to 
     *           {@link #prepare}. May be <code>null</code>.
     */
    public void recycle(IProcessingComponent component, String id);

    /**
     * Called upon disposal of the controller.
     */
    public void dispose();
}
