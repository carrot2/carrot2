
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

import org.carrot2.util.ExecutorServiceUtils;

/**
 * A base class for implementation of the {@link IProcessingComponent} interface that
 * provides empty implementations of all life cycle methods.
 */
public abstract class ProcessingComponentBase implements IProcessingComponent
{
    private IControllerContext context;

    /*
     * 
     */
    public void init(IControllerContext context)
    {
        this.context = context;
    }

    /*
     * 
     */
    public void beforeProcessing() throws ProcessingException
    {
    }

    /*
     * 
     */
    public void process() throws ProcessingException
    {
    }

    /*
     * 
     */
    public void afterProcessing()
    {
    }

    /**
     * Return the {@link IControllerContext} passed in {@link #init(IControllerContext)}.
     */
    protected final IControllerContext getContext()
    {
        if (context == null)
        {
            throw new IllegalStateException(
                "Context not available (check if you call super.init(ControllerContext)).");
        }

        return context;
    }

    /*
     * 
     */
    protected ExecutorService getSharedExecutor(int maxConcurrentThreads, Class<?> clazz)
    {
        final IControllerContext context = getContext();
        synchronized (context)
        {
            final String contextKey = clazz.getName() + ".executorService";
            ExecutorService service = (ExecutorService) context.getAttribute(contextKey);
            if (service == null)
            {
                service = ExecutorServiceUtils.createExecutorService(maxConcurrentThreads, clazz);
                context.setAttribute(contextKey, service);
                context.addListener(new ExecutorServiceShutdownListener(contextKey));
            }
            return service;
        }
    }

    /*
     * 
     */
    public void dispose()
    {
    }
}
