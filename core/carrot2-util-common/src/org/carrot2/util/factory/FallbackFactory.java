
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

package org.carrot2.util.factory;

import org.slf4j.Logger;

import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.base.Throwables;

/**
 * Fallback to the first factory that returns a value.
 */
public final class FallbackFactory<T> implements IFactory<T>
{
    private final IFactory<T> defaultFactory;
    private final IFactory<T> fallbackFactory;
    
    private final String failureMessage;
    private final Logger logger;

    /** Verifies if T instances are functional or if fallback should be used. */
    private final Predicate<T> verifier;

    public FallbackFactory(
        IFactory<T> defaultFactory, IFactory<T> fallbackFactory, Predicate<T> verifier,
        Logger logger, String failureMessage)
    {
        this.defaultFactory = defaultFactory;
        this.fallbackFactory = fallbackFactory;
        this.failureMessage = failureMessage;
        this.logger = logger;
        this.verifier = verifier;
    }

    /**
     * Creates an instance of <code>T</code>, making sure it is functional. 
     */
    @Override
    public final T createInstance()
    {
        try
        {
            T instance = defaultFactory.createInstance();
            if (verifier.apply(instance))
            {
                return instance;
            }

            logger.warn(failureMessage, "(false from predicate)");
            return fallbackFactory.createInstance();
        }
        catch (Throwable t)
        {
            if (logger.isDebugEnabled())
                logger.warn(failureMessage, t.toString() + "\n" + Throwables.getStackTraceAsString(t));
            else
                logger.warn(failureMessage, t.toString());

            return fallbackFactory.createInstance();
        }
    }
}
