package org.carrot2.util.pool;

import java.lang.ref.SoftReference;
import java.util.*;

import com.google.common.collect.Maps;

/**
 * An extremely simple, unbounded object pool. The pool can provide objects of may types,
 * objects get created using parameterless constructors. The pool holds objects using
 * {@link SoftReference}s, so they can be garbage collected when memory is needed.
 * 
 * TODO: [dw] Performance impact of storing soft references may not be worth it. If you need a pool,
 *            you tune it to your memory capacity. A pre-warmed pool with a fixed-size would be
 *            more practical to my intuition. [so] True -- there is an issue for it: CARROT-192
 */
public final class SoftUnboundedPool<T>
{
    private Map<Class<? extends T>, List<SoftReference<? extends T>>> instances = Maps.newHashMap();

    private final InstantiationListener<T> instantiationListener;
    private final ActivationListener<T> activationListener;
    private final PassivationListener<T> passivationListener;
    private final DisposalListener<T> disposalListener;

    public SoftUnboundedPool()
    {
        this(null, null, null, null);
    }

    public SoftUnboundedPool(InstantiationListener<T> objectInstantiationListener,
        ActivationListener<T> objectActivationListener,
        PassivationListener<T> objectPassivationListener,
        DisposalListener<T> objectDisposalListener)
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
    @SuppressWarnings("unchecked")
    public <I extends T> I borrowObject(Class<I> clazz) throws InstantiationException,
        IllegalAccessException
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

        // Not a problem that many threads create new objects for now.
        if (instance == null)
        {
            instance = clazz.newInstance();
            if (instantiationListener != null)
            {
                // TODO: should we assume listeners are thread-safe, or synchronize here?
                instantiationListener.objectInstantiated(instance);
            }
        }

        if (activationListener != null)
        {
            // TODO: should we assume listeners are thread-safe, or synchronize here?
            activationListener.activate(instance);
        }

        return instance;
    }

    /**
     * Returns an object to the pool.
     */
    @SuppressWarnings("unchecked")
    public void returnObject(T object)
    {
        if (object == null)
        {
            return;
        }

        if (passivationListener != null)
        {
            // TODO: should we assume listeners are thread-safe, or synchronize here?
            // If the listener throws an exception, we don't return the object
            passivationListener.passivate(object);
        }

        synchronized (this)
        {
            if (instances == null) {
                return;
            }

            final List<SoftReference<? extends T>> list = instances.get(object.getClass());
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
            Map<Class<? extends T>, List<SoftReference<? extends T>>> instancesRef = this.instances;
            this.instances = null;

            for (List<SoftReference<? extends T>> list : instancesRef.values())
            {
                for (SoftReference<? extends T> reference : list)
                {
                    T instance = reference.get();
                    if (instance != null && disposalListener != null)
                    {
                        // TODO: should we assume listeners are thread-safe, or
                        // synchronize here?
                        disposalListener.dispose(instance);
                    }
                }
            }
        }
    }
}
