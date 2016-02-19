
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
 * A listener receiving events related to a {@link IControllerContext}.
 */
public interface IControllerContextListener
{
    /**
     * Invoked before the context is disposed and any keys (values) should be cleaned up
     * to allow garbage collection.
     */
    public void beforeDisposal(IControllerContext context);
}
