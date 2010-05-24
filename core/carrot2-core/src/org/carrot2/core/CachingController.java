/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.Map;

import org.carrot2.util.attribute.Output;
import org.carrot2.util.pool.IParameterizedPool;
import org.carrot2.util.pool.SoftUnboundedPool;

/**
 * A controller implementing the life cycle described in {@link IProcessingComponent} with
 * support for component pooling and, optionally, data caching.
 * <p>
 * Calls to {@link #process(Map, Class...)} are thread-safe, although some care should be
 * given to initialization: {@link #init(Map)} should be called before other threads are
 * allowed to see this object and {@link #dispose()} should be called after all threads
 * leave {@link #process(Map, Class...)}.
 * <p>
 * Notice for {@link IProcessingComponent} developers: if data caching is used (see
 * {@link #CachingController(Class...)}), values of {@link Output} attributes produced by
 * the components whose output is to be cached (e.g., the {@link Document} instances in
 * case {@link IDocumentSource} output is cached) may be accessed concurrently and
 * therefore must be thread-safe.
 * 
 * @deprecated Please use {@link ControllerFactory#createCachingPooling(Class...)} to
 *             obtain a {@link Controller} equivalent to this one. {@link Controller}s can
 *             be further tuned with custom {@link IProcessingComponentManager}s. This
 *             class will be removed in the 3.4.0 release of Carrot2.
 */
public final class CachingController implements IController
{
    private Controller delegate = new Controller(new SimpleProcessingComponentManager());
    
    /**
     * Creates a new caching controller.
     * 
     * @param cachedComponentClasses classes of components whose output should be cached
     *            by the controller. If a superclass is provided here, e.g.
     *            {@link IDocumentSource}, all its subclasses will be subject to caching.
     *            If {@link IProcessingComponent} is provided here, output of all
     *            components will be cached.
     */
    public CachingController(
        Class<? extends IProcessingComponent>... cachedComponentClasses)
    {
        this(new SoftUnboundedPool<IProcessingComponent, String>(),
            cachedComponentClasses);
    }

    /**
     * Creates a new caching controller with a custom implementation of the component
     * pool.
     * 
     * @param componentPool the component pool to be used by the controller
     * @param cachedComponentClasses classes of components whose output should be cached
     *            by the controller. If a superclass is provided here, e.g.
     *            {@link IDocumentSource}, all its subclasses will be subject to caching.
     *            If {@link IProcessingComponent} is provided here, output of all
     *            components will be cached.
     */
    public CachingController(
        IParameterizedPool<IProcessingComponent, String> componentPool,
        Class<? extends IProcessingComponent>... cachedComponentClasses)
    {
        final IProcessingComponentManager baseManager = new PoolingProcessingComponentManager(componentPool);

        final IProcessingComponentManager manager;
        if (cachedComponentClasses.length > 0)
        {
            // Add a caching wrapper
            manager = new CachingProcessingComponentManager(baseManager,
                cachedComponentClasses);
        }
        else
        {
            manager = baseManager;
        }
        
        this.delegate = new Controller(manager);
    }


    public void init(Map<String, Object> initAttributes)
        throws ComponentInitializationException
    {
        this.delegate.init(initAttributes);
    }

    public void init(Map<String, Object> attributes,
        ProcessingComponentConfiguration... configurations)
        throws ComponentInitializationException
    {
        delegate.init(attributes, configurations);
    }

    public ProcessingResult process(Map<String, Object> attributes,
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        return delegate.process(attributes, processingComponentClasses);
    }

    public ProcessingResult process(Map<String, Object> attributes,
        Object... processingComponentClassesOrIds) throws ProcessingException
    {
        return delegate.process(attributes, processingComponentClassesOrIds);
    }

    public void dispose()
    {
        delegate.dispose();
    }
}
