package org.carrot2.util.pool;

/**
 *
 */
public interface InstantiationListener<T>
{
    /**
     * Called after the object gets instantiated.
     */
    public void objectInstantiated(T object);
}
