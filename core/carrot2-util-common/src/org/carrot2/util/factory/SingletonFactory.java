
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
 * A {@link IFactory} that creates new instances of a given class.
 */
public final class SingletonFactory<T> implements IFactory<T>
{
    private final T singleton;

    public <E extends T> SingletonFactory(E singleton)
    {
        this.singleton = singleton;
    }

    @Override
    public T createInstance()
    {
        return singleton;
    }
}
