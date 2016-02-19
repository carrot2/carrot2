
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

import org.carrot2.util.annotations.AspectModified;
import org.carrot2.util.pool.FixedSizePool;

/**
 * Creates {@link Controller}s in a number of common configurations. The most useful
 * configurations are:
 * <ul>
 * <li>{@link #createSimple()}: for one-time processing and quick experiments with the
 * code;</li>
 * <li>{@link #createCachingPooling(Class...)}: for long-running applications (e.g. web
 * applications) handling repeated (cacheable) requests.</li>
 * </ul>
 */
public final class ControllerFactory
{
    private ControllerFactory()
    {
        // No instantiation
    }
    
    /**
     * Creates a controller with no processing component pooling and no results caching.
     * The returned controller will instantiate new processing component instances for each
     * processing request and will not perform any caching of the processing results.
     * <p>
     * This controller is useful for one-time processing or fast experiments with the
     * code. For long-running applications (e.g. web applications), consider using a
     * controller with component pooling and/or caching.
     * </p>
     * 
     * @see #createPooling()
     * @see #createCaching(Class...)
     * @see #create(boolean, Class...)
     */
    public static Controller createSimple()
    {
        return create(false);
    }

    /**
     * Creates a controller with processing component pooling but with no results caching.
     * The returned controller will maintain an internal pool of processing components, so
     * that they are reused between processing requests. Soft references are used to cache
     * component instances. There is no upper bound on the number of instances the pool
     * may cache.
     * <p>
     * Use this controller in long-running applications and when your processing
     * components are expensive to create. For applications that handle large numbers of
     * repeated requests, consider using a caching and pooling controller.
     * </p>
     * 
     * @see #create(boolean, Class...)
     * @see #createPooling(int)
     */
    public static Controller createPooling()
    {
        return create(true);
    }

    /**
     * Creates a controller with processing component pooling but with no results caching.
     * The returned controller will maintain an internal fixed-size, hard-referenced
     * pool of processing components, so that they are reused between processing requests.
     * <p>
     * Use this controller in long-running applications and when your processing
     * components are expensive to create. For applications that handle large numbers of
     * repeated requests, consider using a caching and pooling controller.
     * </p>
     * 
     * @param instancePoolSize Number of instances created for a single component 
     * class-ID pair. For computational components it is sensible to set this pool to the
     * number of CPU cores available on the machine. 
     * 
     * @see #create(int, Class...)
     * @see #createPooling()
     * @see Runtime#availableProcessors()
     */
    public static Controller createPooling(int instancePoolSize)
    {
        return create(instancePoolSize);
    }

    /**
     * Creates a controller with no processing component pooling but with results caching.
     * The returned controller will maintain a cache of the processing results. For each
     * component whose results are to be cached, if there comes a repeated request for
     * processing with that component with the same set of input attributes, the result
     * will be returned from the cache. The returned controller will instantiate new
     * processing components object for each processing request whose result has not yet
     * been cached.
     * <p>
     * Uses of this specific controller are rather limited. Use it if your application is
     * handling large numbers of repeated requests but you don't want to have the
     * components pooled for some reason. Make sure the processing components are cheap to
     * create, otherwise performance will suffer.
     * </p>
     * 
     * @param cachedProcessingComponents classes of components whose output should be cached
     *            by the controller. If a superclass is provided here, e.g.
     *            {@link IDocumentSource}, all its subclasses will be subject to caching.
     *            If {@link IProcessingComponent} is provided here, output of all
     *            components will be cached.
     */
    @SafeVarargs
    public static Controller createCaching(
        Class<? extends IProcessingComponent>... cachedProcessingComponents)
    {
        return create(false, cachedProcessingComponents);
    }

    /**
     * Creates a controller with processing component pooling and results caching. The
     * returned controller combines processing component pooling and processing results
     * caching.
     * <p>
     * Use this component in long-running applications that handle repeated requests.
     * </p>
     * 
     * @param cachedProcessingComponents classes of components whose output should be cached
     *            by the controller. If a superclass is provided here, e.g.
     *            {@link IDocumentSource}, all its subclasses will be subject to caching.
     *            If {@link IProcessingComponent} is provided here, output of all
     *            components will be cached.
     */
    @SafeVarargs
    public static Controller createCachingPooling(
        Class<? extends IProcessingComponent>... cachedProcessingComponents)
    {
        return create(true, cachedProcessingComponents);
    }

    /**
     * Creates a controller with the specified pooling and caching settings.
     * 
     * @param componentPooling if <code>true</code>, component pooling 
     *      will be performed (soft pool), otherwise no component pool will be used.
     * @param cachedProcessingComponents classes of components whose output should be cached
     *            by the controller. If a superclass is provided here, e.g.
     *            {@link IDocumentSource}, all its subclasses will be subject to caching.
     *            If {@link IProcessingComponent} is provided here, output of all
     *            components will be cached.
     */
    @SafeVarargs
    public static Controller create(boolean componentPooling,
        Class<? extends IProcessingComponent>... cachedProcessingComponents)
    {
        final IProcessingComponentManager baseManager = 
            (componentPooling 
                ? new PoolingProcessingComponentManager()
                : new SimpleProcessingComponentManager());

        return new Controller(addCachingManager(baseManager, cachedProcessingComponents));
    }

    /**
     * Creates a controller with the specified fixed-size pooling and caching settings.
     * 
     * @param instancePoolSize Number of instances created for a single component class-ID
     *            pair. For computational components it is sensible to set this pool to
     *            the number of CPU cores available on the machine.
     * @param cachedProcessingComponents classes of components whose output should be
     *            cached by the controller. If a superclass is provided here, e.g.
     *            {@link IDocumentSource}, all its subclasses will be subject to caching.
     *            If {@link IProcessingComponent} is provided here, output of all
     *            components will be cached.
     */
    @SafeVarargs
    public static Controller create(int instancePoolSize,
        Class<? extends IProcessingComponent>... cachedProcessingComponents)
    {
        if (instancePoolSize <= 0)
            throw new IllegalArgumentException("Instance pool size must be greater than zero: "
                + instancePoolSize);

        final IProcessingComponentManager baseManager = 
            new PoolingProcessingComponentManager(
                new FixedSizePool<IProcessingComponent, String>(instancePoolSize));

        return new Controller(addCachingManager(baseManager, cachedProcessingComponents));
    }
    
    /**
     * Adds caching manager wrapper if caching is requested.
     */
    @AspectModified("Throws an exception in .NET")
    @SafeVarargs
    private static IProcessingComponentManager addCachingManager(
        IProcessingComponentManager baseManager, 
        Class<? extends IProcessingComponent>... cachedProcessingComponents)
    {
        if (cachedProcessingComponents.length == 0)
            return baseManager;

        return new CachingProcessingComponentManager(baseManager,
            cachedProcessingComponents);
    }
}
