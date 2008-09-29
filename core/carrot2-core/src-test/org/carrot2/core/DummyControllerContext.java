package org.carrot2.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * {@link ControllerContext} implementation for tests only.
 */
public final class DummyControllerContext implements ControllerContext
{
    /**
     * Listeners on this context.
     */
    private final ArrayList<ControllerContextListener> listeners = Lists.newArrayList();

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
    public synchronized void removeListener(ControllerContextListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addListener(ControllerContextListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Destroy this context.
     */
    public synchronized void dispose()
    {
        for (ControllerContextListener listener : listeners)
        {
            try
            {
                listener.beforeDisposal(this);
            }
            catch (Throwable t)
            {
                Logger.getLogger(ControllerContextImpl.class).warn(
                    "Unhandled exception in context listener.", t);
            }
        }

        this.listeners.clear();
        this.attributes.clear();
    }
}
