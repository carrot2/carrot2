
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.core.local;

import java.util.Map;

/**
 * A request context object functions as a medium of communication between local
 * components linked in a processing chain.
 * 
 * <p>
 * A request context object can carry a <code>Map</code> of parameters. This
 * map is mutable: components can override and add their own parameters to it.
 * </p>
 * 
 * <p>
 * A request context object is created by the controller for the time of
 * execution of a single query and is unusable after the time of processing of a
 * single query ends.
 * </p>
 * 
 * @author Dawid Weiss
 * @author Stanisław Osiński
 * 
 * @see LocalProcess#query(RequestContext, String)
 */
public interface RequestContext
{
    /**
     * Returns a {@link Map} of parameters valid for the currently processed
     * query. The map can be modified by overriding existing entries, or adding
     * new ones.
     * 
     * @return A {@link Map} object with request context parameters.
     */
    public Map getRequestParameters();

    /**
     * Returns an instance of a {@link LocalComponent}. The instance can be
     * used <b>only for the duration of the current request </b>.
     * 
     * <p>
     * This method is usually invoked by {@link LocalProcess}instances to
     * acquire components and link them in a chain
     * </p>
     * 
     * @param key The component factory identifier.
     * 
     * @return A ready-to-use instance of a {@link LocalComponent}.
     * 
     * @throws MissingComponentException If <code>key</code> does not exist in
     *             the controller.
     * @throws IllegalStateException in case of an attempt to get component
     *             instance after the this context has been disposed of
     * @throws Exception If the controller was unable to create a new instance
     *             of the given component, even though the factory for it
     *             exists.
     */
    public LocalComponent getComponentInstance(String key)
        throws MissingComponentException, IllegalStateException, Exception;

    /**
     * Called by the {@link LocalController} after processing this request has
     * been completed (no matter whether successfully or not). After the dispose
     * method has been called, it is not possible to get component instances
     * using the {@link #getComponentInstance(String)} method.
     * 
     * @throws Exception if errors occur during disposing
     */
    public void dispose() throws Exception;
}