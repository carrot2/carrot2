package org.carrot2.util.pool;

/**
 *
 */
public interface ObjectPassivationListener<T>
{
    /**
     * Called after the object is returned to the pool.
     */
    public void passivate(T object);
}
