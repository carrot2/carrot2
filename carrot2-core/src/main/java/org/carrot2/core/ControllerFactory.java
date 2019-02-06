
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
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
 * <li>{@link #createPooling}: for long-running applications (e.g. web
 * applications) handling repeated requests.</li>
 * </ul>
 */
public final class ControllerFactory
{
    private ControllerFactory()
    {
        // No instantiation
    }
    
    /**
     * Creates a controller with no processing component pooling.
     * The returned controller will instantiate new processing component instances for each
     * processing request.
     * <p>
     * This controller is useful for one-time processing or fast experiments with the
     * code. For long-running applications (e.g. web applications), consider using a
     * controller with component pooling.
     * </p>
     * 
     * @see #createPooling()
     */
    public static Controller createSimple()
    {
        return new Controller(new SimpleProcessingComponentManager());
    }

    /**
     * Creates a controller with processing component pooling
     * .
     * The returned controller will maintain an internal pool of processing components, so
     * that they are reused between processing requests. Soft references are used to cache
     * component instances. There is no upper bound on the number of instances the pool
     * may cache.
     *
     * @see #createPooling(int)
     */
    public static Controller createPooling()
    {
        return new Controller(new PoolingProcessingComponentManager());
    }

    /**
     * Creates a controller with processing component pooling and a fixed pool size.
     *
     * The returned controller will maintain an internal fixed-size, hard-referenced
     * pool of processing components, so that they are reused between processing requests.
     *
     * @param instancePoolSize Number of instances created for a single component 
     * class-ID pair. For computational components it is sensible to set this pool to the
     * number of CPU cores available on the machine. 
     * 
     * @see #createPooling()
     * @see Runtime#availableProcessors()
     */
    public static Controller createPooling(int instancePoolSize)
    {
        if (instancePoolSize <= 0)
            throw new IllegalArgumentException("Instance pool size must be greater than zero: "
                + instancePoolSize);

        final IProcessingComponentManager baseManager =
            new PoolingProcessingComponentManager(
                new FixedSizePool<>(instancePoolSize));

        return new Controller(baseManager);
    }
}
