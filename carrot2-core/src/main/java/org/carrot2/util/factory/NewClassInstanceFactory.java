
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

package org.carrot2.util.factory;

import java.util.function.Supplier;

/**
 * A {@link Supplier} that creates new instances of a given class.
 */
public final class NewClassInstanceFactory<T> implements Supplier<T>
{
    private final Class<? extends T> clazz;

    public NewClassInstanceFactory(Class<? extends T> clazz)
    {
        this.clazz = clazz;
    }
    
    @Override
    public T get()
    {
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
