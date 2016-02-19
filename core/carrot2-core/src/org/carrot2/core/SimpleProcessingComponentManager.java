
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

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.Input;

/**
 * An {@link IProcessingComponentManager} that creates a new component for each processing
 * request.
 */
public class SimpleProcessingComponentManager implements IProcessingComponentManager
{
    /** Controller context */
    private IControllerContext context;

    @Override
    public synchronized void init(IControllerContext context,
        Map<String, Object> attributes,
        ProcessingComponentConfiguration... configurations)
    {
        assert context != null;

        // This will ensure that one manager is used with only one controller
        if (this.context != null)
        {
            throw new IllegalStateException("This manager has already been initialized.");
        }

        this.context = context;
    }

    @Override
    public IProcessingComponent prepare(Class<? extends IProcessingComponent> clazz,
        String id, Map<String, Object> inputAttributes,
        Map<String, Object> outputAttributes)
    {
        IProcessingComponent component = null;
        try
        {
            component = clazz.newInstance();
            ControllerUtils.init(component, inputAttributes, outputAttributes, true,
                context);

            // To support a scenario where processing input attributes are provided
            // at init-time, we need to bind them here as well.
            AttributeBinder.set(component, inputAttributes, false, Input.class,
                Processing.class);

            return component;
        }
        catch (final InstantiationException e)
        {
            throw new ComponentInitializationException(
                "Could not instantiate component class: " + clazz.getName(), e);
        }
        catch (final IllegalAccessException e)
        {
            throw new ComponentInitializationException(
                "Could not instantiate component class: " + clazz.getName(), e);
        }
        catch (final ComponentInitializationException e)
        {
            // Dispose of the component we failed to initialize
            component.dispose();
            throw e;
        }
    }

    @Override
    public void recycle(IProcessingComponent component, String id)
    {
        // Dispose of the component right after it's been used.
        component.dispose();
    }

    @Override
    public void dispose()
    {
        // Do nothing
    }
}
