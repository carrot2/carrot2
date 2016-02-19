
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

/**
 * A decorator that returns the first non-null instance returned 
 * by the delegate factory.
 */
public final class CachedInstanceFactoryDecorator<T> implements IFactory<T>
{
    private IFactory<T> factory;
    private T instance;

    public CachedInstanceFactoryDecorator(IFactory<T> factory)
    {
        this.factory = factory;
    }

    @Override
    public T createInstance()
    {
        if (instance == null)
        {
            instance = factory.createInstance();
        }

        return instance;
    }
}
