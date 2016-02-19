
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

import java.util.concurrent.ExecutorService;

/**
 * {@link IControllerContextListener} that shuts down a given {@link ExecutorService}
 * reference in the {@link IControllerContext}.
 */
class ExecutorServiceShutdownListener implements IControllerContextListener
{
    private final String contextKey;

    public ExecutorServiceShutdownListener(String contextKey)
    {
        this.contextKey = contextKey;
    }

    public void beforeDisposal(IControllerContext context)
    {
        ExecutorService service = ((ExecutorService) context.getAttribute(contextKey));
        if (service != null && !service.isShutdown())
        {
            service.shutdown();
        }
    }
}
