
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.pool;

/**
 *
 */
public interface IActivationListener<T, P>
{
    /**
     * Called before object is handed in to the caller of
     * {@link SoftUnboundedPool#borrowObject(Class)}.
     */
    public void activate(T object, P parameter);
}
