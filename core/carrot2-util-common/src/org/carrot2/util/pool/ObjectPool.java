package org.carrot2.util.pool;

import java.lang.ref.SoftReference;
import java.util.*;

import com.google.common.collect.Maps;

/**
 * An extremely simple, unbounded object pool. The pool can provide objects of may types,
 * objects get created using parameterless constructors. The pool holds objects using
 * {@link SoftReference}s, so they can be garbage collected when memory is needed.
 */
public class ObjectPool<T>
{
    Map<Class<? extends T>, List<SoftReference<? extends T>>> instances = Maps
        .newHashMap();

    /**
     * Borrows an object from the pool. If no instance is available, a parameterless
     * constructor will be used to create a new one.
     * 
     * @param clazz class of object to be borrowed
     * @param instantiationListener if not-<code>null</code>, the listener will be
     *            notified if a new instance is created (but not if borrowed from the
     *            pool).
     */
    @SuppressWarnings("unchecked")
    public <I extends T> I borrowObject(Class<I> clazz,
        ObjectInstantiationListener<T> instantiationListener)
        throws InstantiationException, IllegalAccessException
    {
        I instance = null;
        synchronized (this)
        {
            if (instances == null)
            {
                throw new IllegalStateException("The pool has already been disposed of");
            }

            List<SoftReference<? extends T>> list = instances.get(clazz);
            if (list == null)
            {
                list = new ArrayList<SoftReference<? extends T>>();
                instances.put(clazz, list);
            }

            while (list.size() > 0 && instance == null)
            {
                instance = (I) list.remove(0).get();
            }
        }

        // Not a problem that many threads create new objects for now
        if (instance == null)
        {
            instance = clazz.newInstance();
            if (instantiationListener != null)
            {
                instantiationListener.objectInstantiated(instance);
            }
        }

        return instance;
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
     * Returns an object to the pool.
     */
    @SuppressWarnings("unchecked")
    public void returnObject(T object)
    {
        if (object == null || instances == null)
        {
            return;
        }

        synchronized (this)
        {
            List<SoftReference<? extends T>> list = instances.get(object.getClass());
            if (list == null)
            {
                throw new IllegalStateException(
                    "Returning an object that was never borrowed.");
            }

            list.add(new SoftReference<T>(object));
        }
    }

    /**
     * Disposes of the pool. No objects can be borrowed from the pool after disposed.
     */
    public void dispose(ObjectDisposalListener<T> disposalListener)
    {
        synchronized (this)
        {
            Map<Class<? extends T>, List<SoftReference<? extends T>>> instancesRef = this.instances;
            this.instances = null;

            for (List<SoftReference<? extends T>> list : instancesRef.values())
            {
                for (SoftReference<? extends T> reference : list)
                {
                    T instance = reference.get();
                    if (instance != null && disposalListener != null)
                    {
                        disposalListener.objectDisposed(instance);
                    }
                }
            }
        }
    }
}
