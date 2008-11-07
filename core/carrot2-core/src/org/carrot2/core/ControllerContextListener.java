
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

/**
 * A listener receiving events related to a {@link ControllerContext}.
 */
public interface ControllerContextListener
{
    /**
     * Invoked before the context is disposed and any keys (values) should be cleaned up
     * to allow garbage collection.
     * 
     * @see Controller#dispose()
     */
    public void beforeDisposal(ControllerContext context);
}
