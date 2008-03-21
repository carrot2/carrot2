package org.carrot2.util.pool;

/**
 *
 */
public interface ObjectInstantiationListener<T>
{
    /**
     * Called after the object gets instantiated.
     */
    public void objectInstantiated(T object);
}
