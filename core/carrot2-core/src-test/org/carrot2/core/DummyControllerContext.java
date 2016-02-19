
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

/**
 * {@link IControllerContext} implementation for tests only.
 */
public final class DummyControllerContext implements IControllerContext
{
    private ControllerContextImpl delegate;

    public DummyControllerContext()
    {
        delegate = new ControllerContextImpl();
    }

    public synchronized void addListener(IControllerContextListener listener)
    {
        delegate.addListener(listener);
    }

    public synchronized void dispose()
    {
        delegate.dispose();
    }

    public synchronized Object getAttribute(String key)
    {
        return delegate.getAttribute(key);
    }

    public synchronized void removeListener(IControllerContextListener listener)
    {
        delegate.removeListener(listener);
    }

    public synchronized void setAttribute(String key, Object value)
    {
        delegate.setAttribute(key, value);
    }
}
