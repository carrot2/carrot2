
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.core.local;

import java.util.Map;


/**
 * A request context object functions as a medium of communication  between
 * local components linked in a processing chain.
 * 
 * <p>
 * A request context object can carry a <code>Map</code> of parameters. This
 * map is mutable: components can override and add their own parameters to it.
 * </p>
 * 
 * <p>
 * A request context object is created by the controller for the time of
 * execution of a single query and is unusable after the time of processing of
 * a single query ends.
 * </p>
 *
 * @author Dawid Weiss
 *
 * @see LocalProcess#query(RequestContext, String)
 */
public interface RequestContext {
    /**
     * Returns a {@link Map} of parameters valid for the currently processed
     * query. The map can be modified by overriding existing entries, or
     * adding new ones.
     *
     * @return A {@link Map} object with request context parameters.
     */
    public Map getRequestParameters();

    /**
     * Returns an instance of a {@link LocalComponent}. The instance can be
     * used <b>only for the duration of the current request</b>.
     * 
     * <p>
     * This method is usually invoked by {@link LocalProcess} instances to
     * acquire components and link them in a chain
     * </p>
     *
     * @param key The component factory identifier.
     *
     * @return A ready-to-use instance of a {@link LocalComponent}.
     *
     * @throws MissingComponentException If <code>key</code> does not exist in
     *         the controller.
     * @throws Exception If the controller was unable to create a new instance
     *         of the given component, even though the factory for it exists.
     */
    public LocalComponent getComponentInstance(String key)
        throws MissingComponentException, Exception;
}
