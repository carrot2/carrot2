
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

import java.util.ArrayList;
import java.util.HashMap;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Package-private implementation of {@link IControllerContext}.
 */
final class ControllerContextImpl implements IControllerContext
{
    /**
     * Listeners on this context.
     */
    private final ArrayList<IControllerContextListener> listeners = Lists.newArrayList();

    /**
     * Attributes of this context.
     */
    private final HashMap<String, Object> attributes = Maps.newHashMap();

    /**
     * {@inheritDoc}
     */
    public synchronized Object getAttribute(String key)
    {
        return attributes.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setAttribute(String key, Object value)
    {
        attributes.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void removeListener(IControllerContextListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addListener(IControllerContextListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Destroy this context.
     */
    public synchronized void dispose()
    {
        for (IControllerContextListener listener : listeners)
        {
            try
            {
                listener.beforeDisposal(this);
            }
            catch (Throwable t)
            {
                org.slf4j.LoggerFactory.getLogger(ControllerContextImpl.class).warn(
                    "Unhandled exception in context listener.", t);
            }
        }

        this.listeners.clear();
        this.attributes.clear();
    }
}
