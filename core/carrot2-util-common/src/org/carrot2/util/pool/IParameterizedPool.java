
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
 * A parameterized pool of objects. Each borrowed object is characterized by its class and
 * an arbitrary parameter. The [class, parameter] pair uniquely identifies a "class"
 * (equivalence class) of pooled objects.
 * <p>
 * Please see {@link SoftUnboundedPool} for a reference implementation.
 * </p>
 */
public interface IParameterizedPool<T, P>
{
    /**
     * Initializes the pool with a number of listeners. The appropriate listeners
     * <b>must</b> be called at the relevant stages of a pooled object's life cycle.
     */
    public void init(IInstantiationListener<T, P> objectInstantiationListener,
        IActivationListener<T, P> objectActivationListener,
        IPassivationListener<T, P> objectPassivationListener,
        IDisposalListener<T, P> objectDisposalListener);

    /**
     * Borrows an object from the pool. If no instance is available, a parameterless
     * constructor should be used to create a new one.
     * 
     * @param clazz class of object to be borrowed
     * @param parameter additional parameter determining a possible sub type within the
     *            same class of objects being borrowed. A combination of class and
     *            parameter uniquely identifies a "class" (equivalence class) of pooled
     *            objects. The parameter is assumed to correctly implement the
     *            {@link Object#equals(Object)} and {@link Object#hashCode()} methods. The
     *            parameter can be <code>null</code>. The implementation must pass the
     *            parameter to all listeners when managing the life cycle of the pooled
     *            object. It is the callers responsibility to ensure that exactly the same
     *            value of the parameter is passed to the corresponding
     *            {@link #borrowObject(Class, Object)} and
     *            {@link #returnObject(Object, Object)} methods.
     */
    public <I extends T> I borrowObject(Class<I> clazz, P parameter)
        throws InstantiationException, IllegalAccessException;

    /**
     * Returns an object to the pool.
     * 
     * @param object object to return
     * @param parameter parameter provided when borrowing the object. If the parameter was
     *            not <code>null</code> when borrowing the object, the same value will be
     *            passed here.
     */
    public void returnObject(T object, P parameter);

    /**
     * Disposes of the pool. No objects can be borrowed from the pool after disposed.
     */
    public void dispose();

}
