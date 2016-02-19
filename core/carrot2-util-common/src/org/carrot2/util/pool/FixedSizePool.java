
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

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.carrot2.util.Pair;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * An object pool storing hard references to a fixed number of instantiated objects at the
 * given key. The objects are never released from the pool until {@link #dispose()} is
 * called.
 */
public final class FixedSizePool<T, P> implements IParameterizedPool<T, P>
{
    private Map<Pair<Class<? extends T>, P>, ArrayList<T>> instances = Maps.newHashMap();

    private IInstantiationListener<T, P> instantiationListener;
    private IActivationListener<T, P> activationListener;
    private IPassivationListener<T, P> passivationListener;
    private IDisposalListener<T, P> disposalListener;

    /**
     * Each key in the pool points to a list of instances. This field defines how many
     * components are kept for each key.
     */
    private final int listSizePerKey;

    /**
     * @param listSizePerKey Each key in the pool points to a list of instances. This
     *            field defines how many components are kept for each key.
     */
    public FixedSizePool(int listSizePerKey)
    {
        if (listSizePerKey <= 0)
            throw new IllegalArgumentException("Pool size must be greater than zero: "
                + listSizePerKey);
        this.listSizePerKey = listSizePerKey;
    }

    /**
     * Initialize listeners.
     */
    public void init(IInstantiationListener<T, P> objectInstantiationListener,
        IActivationListener<T, P> objectActivationListener,
        IPassivationListener<T, P> objectPassivationListener,
        IDisposalListener<T, P> objectDisposalListener)
    {
        this.instantiationListener = objectInstantiationListener;
        this.activationListener = objectActivationListener;
        this.passivationListener = objectPassivationListener;
        this.disposalListener = objectDisposalListener;
    }

    @SuppressWarnings("unchecked")
    public <I extends T> I borrowObject(Class<I> clazz, P parameter)
        throws InstantiationException, IllegalAccessException
    {
        final I instance;
        synchronized (this)
        {
            if (instances == null)
            {
                throw new IllegalStateException("The pool has already been disposed of");
            }

            Pair<Class<? extends T>, P> key = new Pair<Class<? extends T>, P>(clazz,
                parameter);

            ArrayList<T> list = instances.get(key);
            if (list == null)
            {
                instances.put(key, list = createInstancePool(clazz, parameter));
            }

            while (list.size() == 0)
            {
                try
                {
                    this.wait();
                    if (this.instances == null)
                    {
                        throw new InstantiationException("Pool disposed while waiting.");
                    }
                }
                catch (InterruptedException e)
                {
                    throw new InstantiationException(
                        "Interrupted while waiting for the object pool: " + clazz + ", "
                            + parameter);
                }
            }

            instance = (I) list.remove(list.size() - 1);
        }

        if (activationListener != null)
        {
            activationListener.activate(instance, parameter);
        }
        return instance;
    }

    /*
     * 
     */
    public void returnObject(T object, P parameter)
    {
        if (object == null) throw new IllegalArgumentException(
            "Null could not have been acquired from this pool.");

        if (passivationListener != null)
        {
            passivationListener.passivate(object, parameter);
        }

        synchronized (this)
        {
            if (instances == null)
            {
                // disposed, silently ignore.
                return;
            }

            @SuppressWarnings({
                "unchecked", "rawtypes"
            })
            final Pair<T, P> key = new Pair(object.getClass(), parameter);
            final ArrayList<T> list = instances.get(key);
            if (list == null)
            {
                throw new IllegalStateException(
                    "Returning an object that was never borrowed: " + object);
            }

            // The object must not be on the list at this point. The pool won't be large
            // enough for the linear scan to be a problem.
            for (T reference : list)
            {
                if (reference != null && reference == object)
                {
                    throw new IllegalStateException("Object has not been borrowed");
                }
            }

            list.add(object);

            // Notify all because we can't be sure notify() will awake a thread waiting
            // on the same key. We could do two nested monitors here, but it makes little
            // sense (minor performance overhead).
            this.notifyAll();
        }
    }

    /*
     * 
     */
    public void dispose()
    {
        synchronized (this)
        {
            if (this.instances == null)
            {
                // Already disposed.
                return;
            }

            final Map<Pair<Class<? extends T>, P>, ArrayList<T>> instancesRef = this.instances;
            this.instances = null;
            this.notifyAll();

            for (Entry<Pair<Class<? extends T>, P>, ArrayList<T>> entry : instancesRef.entrySet())
            {
                for (T instance : entry.getValue())
                {
                    if (instance != null && disposalListener != null)
                    {
                        disposalListener.dispose(instance, entry.getKey().objectB);
                    }
                }
            }
        }
    }

    /*
     * 
     */
    private <I extends T> ArrayList<T> createInstancePool(Class<I> clazz,
        P parameter) throws InstantiationException, IllegalAccessException
    {
        ArrayList<T> list = Lists.newArrayList();
        for (int i = 0; i < listSizePerKey; i++)
        {
            I instance = clazz.newInstance();
            if (instantiationListener != null)
            {
                instantiationListener.objectInstantiated(instance, parameter);
            }
            list.add(instance);
        }
        return list;
    }
}
