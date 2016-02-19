
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
 * <p>
 * The controller context is a map of key-value pairs, attached to an initialized
 * {@link Controller} instance. The context is created in
 * {@link Controller#init()} methods and remains valid until
 * {@link Controller#dispose()} is invoked.
 * </p>
 * <p>
 * The context instance is passed to all components that take part in query processing
 * inside the controller object ({@link IProcessingComponent#init(IControllerContext)}).
 * {@link IProcessingComponent} implementations may use the context object to store data
 * shared between <i>all</i> component instances (such as thread pools, counters, etc.).
 * In such scenario it is essential to remember to attach a
 * {@link IControllerContextListener} and clean up any resources when the controller is
 * destroyed.
 * </p>
 * 
 * @see Controller#init()
 * @see Controller#dispose()
 * @see IProcessingComponent#init(IControllerContext)
 */
public interface IControllerContext
{
    /**
     * Atomically binds the given key to the value. Component implementors are encouraged
     * to use custom namespaces to avoid conflicts.
     */
    public void setAttribute(String key, Object value);

    /**
     * Atomically retrieves the value for a given key. Component implementors are
     * encouraged to use custom namespaces to avoid conflicts.
     */
    public Object getAttribute(String key);

    /**
     * Adds a {@link IControllerContextListener} to this context.
     */
    public void addListener(IControllerContextListener listener);

    /**
     * Removes a {@link IControllerContextListener} from this context.
     */
    public void removeListener(IControllerContextListener listener);
}
