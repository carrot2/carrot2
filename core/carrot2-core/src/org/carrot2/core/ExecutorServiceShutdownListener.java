package org.carrot2.core;

import java.util.concurrent.ExecutorService;

/**
 * {@link ControllerContextListener} that shuts down a given {@link ExecutorService}
 * reference in the {@link ControllerContext}.
 */
class ExecutorServiceShutdownListener implements ControllerContextListener
{
    private final  String contextKey;

    public ExecutorServiceShutdownListener(String contextKey)
    {
        this.contextKey = contextKey;
    }

    public void beforeDisposal(ControllerContext context)
    {
        ExecutorService service = ((ExecutorService) context.getAttribute(contextKey));
        if (service != null && !service.isShutdown())
        {
            service.shutdown();
        }
    }
}
