
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.pool;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.Map.Entry;

import org.carrot2.util.Pair;

import com.google.common.collect.Maps;

/**
 * An extremely simple, unbounded object pool. The pool can provide objects of may types,
 * objects get created using parameterless constructors. The pool holds objects using
 * {@link SoftReference}s, so they can be garbage collected when memory is needed.
 */
public final class SoftUnboundedPool<T, P>
{
    /*
     * TODO: [dw] Performance impact of storing soft references may not be worth it. If
     * you need a pool, you tune it to your memory capacity. A pre-warmed pool with a
     * fixed-size would be more practical to my intuition. [so] True -- there is an issue
     * for it: CARROT-192
     */

    private Map<Pair<Class<? extends T>, P>, List<SoftReference<? extends T>>> instances = Maps
        .newHashMap();

    private final IInstantiationListener<T, P> instantiationListener;
    private final IActivationListener<T, P> activationListener;
    private final IPassivationListener<T, P> passivationListener;
    private final IDisposalListener<T, P> disposalListener;

    public SoftUnboundedPool()
    {
        this(null, null, null, null);
    }

    public SoftUnboundedPool(IInstantiationListener<T, P> objectInstantiationListener,
        IActivationListener<T, P> objectActivationListener,
        IPassivationListener<T, P> objectPassivationListener,
        IDisposalListener<T, P> objectDisposalListener)
    {
        this.instantiationListener = objectInstantiationListener;
        this.activationListener = objectActivationListener;
        this.passivationListener = objectPassivationListener;
        this.disposalListener = objectDisposalListener;
    }

    /**
     * Borrows an object from the pool. If no instance is available, a parameterless
     * constructor will be used to create a new one.
     * 
     * @param clazz class of object to be borrowed
     */
    public <I extends T> I borrowObject(Class<I> clazz) throws InstantiationException,
        IllegalAccessException
    {
        return borrowObject(clazz, null);
    }

    /**
     * Borrows an object from the pool. If no instance is available, a parameterless
     * constructor will be used to create a new one.
     * 
     * @param clazz class of object to be borrowed
     * @param parameter additional parameter passed to all listeners when managing the
     *            life cycle of the pooled object. The parameter can be <code>null</code>.
     *            Exactly the same value of the parameter must be passed to
     *            {@link #returnObject(Object, Object)}. Otherwise, different instances
     *            of the same class will get mixed up within the pool.
     */
    @SuppressWarnings("unchecked")
    public <I extends T> I borrowObject(Class<I> clazz, P parameter)
        throws InstantiationException, IllegalAccessException
    {
        I instance = null;
        synchronized (this)
        {
            if (instances == null)
            {
                throw new IllegalStateException("The pool has already been disposed of");
            }

            final Pair<Class<? extends T>, P> key = new Pair<Class<? extends T>, P>(
                clazz, parameter);
            List<SoftReference<? extends T>> list = instances.get(key);
            if (list == null)
            {
                list = new ArrayList<SoftReference<? extends T>>();
                instances.put(key, list);
            }

            while (list.size() > 0 && instance == null)
            {
                instance = (I) list.remove(0).get();
            }
        }

        // Not a problem that many threads create new objects for now.
        if (instance == null)
        {
            instance = clazz.newInstance();
            if (instantiationListener != null)
            {
                instantiationListener.objectInstantiated(instance, parameter);
            }
        }

        if (activationListener != null)
        {
            activationListener.activate(instance, parameter);
        }

        return instance;
    }

    /**
     * Returns an object to the pool.
     */
    public void returnObject(T object)
    {
        returnObject(object, null);
    }

    /**
     * Returns an object to the pool.
     * 
     * @param object object to return
     * @param parameter parameter provided when borrowing the object. If the parameter was
     *            not <code>null</code> when borrowing the object, the same value must
     *            be passed here. Otherwise, different instances of the same class will
     *            get mixed up within the pool.
     */
    @SuppressWarnings("unchecked")
    public void returnObject(T object, P parameter)
    {
        if (object == null)
        {
            return;
        }

        if (passivationListener != null)
        {
            passivationListener.passivate(object, parameter);
        }

        synchronized (this)
        {
            if (instances == null)
            {
                return;
            }

            final Pair key = new Pair(object.getClass(), parameter);
            final List<SoftReference<? extends T>> list = instances.get(key);
            if (list == null)
            {
                throw new IllegalStateException(
                    "Returning an object that was never borrowed: " + object);
            }

            list.add(new SoftReference<T>(object));
        }
    }

    /**
     * Disposes of the pool. No objects can be borrowed from the pool after disposed.
     */
    public void dispose()
    {
        synchronized (this)
        {
            Map<Pair<Class<? extends T>, P>, List<SoftReference<? extends T>>> instancesRef = this.instances;
            this.instances = null;

            for (Entry<Pair<Class<? extends T>, P>, List<SoftReference<? extends T>>> entry : instancesRef
                .entrySet())
            {
                for (SoftReference<? extends T> reference : entry.getValue())
                {
                    T instance = reference.get();
                    if (instance != null && disposalListener != null)
                    {
                        disposalListener.dispose(instance, entry.getKey().objectB);
                    }
                }
            }
        }
    }
}
