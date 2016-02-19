
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

package org.carrot2.util.pool;

/**
 * Pooled object activation listener.
 */
public interface IActivationListener<T, P>
{
    /**
     * Called before object is handed in to the caller of
     * {@link IParameterizedPool#borrowObject(Class, Object)}.
     */
    public void activate(T object, P parameter);
}
