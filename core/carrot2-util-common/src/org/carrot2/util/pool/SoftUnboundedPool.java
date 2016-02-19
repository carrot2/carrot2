
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

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.Map.Entry;

import org.carrot2.util.Pair;

import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * An extremely simple, unbounded object pool. The pool can provide objects of may types,
 * objects get created using parameterless constructors. The pool holds objects using
 * {@link SoftReference}s, so they can be garbage collected when memory is needed.
 */
public final class SoftUnboundedPool<T, P> implements IParameterizedPool<T, P>
{
    private Map<Pair<Class<? extends T>, P>, List<SoftReference<T>>> instances = Maps.newHashMap();

    private IInstantiationListener<T, P> instantiationListener;
    private IActivationListener<T, P> activationListener;
    private IPassivationListener<T, P> passivationListener;
    private IDisposalListener<T, P> disposalListener;

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
        I instance = null;
        synchronized (this)
        {
            if (instances == null)
            {
                throw new IllegalStateException("The pool has already been disposed of");
            }

            final Pair<Class<? extends T>, P> key = new Pair<Class<? extends T>, P>(
                clazz, parameter);
            List<SoftReference<T>> list = instances.get(key);
            if (list == null)
            {
                list = new ArrayList<SoftReference<T>>();
                instances.put(key, list);
            }

            while (list.size() > 0 && instance == null)
            {
                // This cast goes unchecked and can be broken by bad calls, but we shift
                // the responsibility to the users of this class.
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

            @SuppressWarnings({
                "rawtypes", "unchecked"
            })
            final Pair key = new Pair(object.getClass(), parameter);
            final List<SoftReference<T>> list = instances.get(key);
            if (list == null)
            {
                throw new IllegalStateException(
                    "Returning an object that was never borrowed: " + object);
            }

            // The object must not be on the list at this point. The pool won't be large
            // enough for the linear scan to be a problem.
            for (SoftReference<T> reference : list)
            {
                final T o = reference.get();
                if (o != null && o == object)
                {
                    throw new IllegalStateException("Object has not been borrowed");
                }
            }
            
            list.add(new SoftReference<T>(object));
        }
    }

    public void dispose()
    {
        synchronized (this)
        {
            if (this.instances == null)
            {
                return;
            }
            
            Map<Pair<Class<? extends T>, P>, List<SoftReference<T>>> instancesRef = this.instances;
            this.instances = null;

            for (Entry<Pair<Class<? extends T>, P>, List<SoftReference<T>>> entry : instancesRef.entrySet())
            {
                for (SoftReference<T> reference : entry.getValue())
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
