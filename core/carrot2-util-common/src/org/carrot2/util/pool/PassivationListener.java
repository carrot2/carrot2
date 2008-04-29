package org.carrot2.util.pool;

/**
 *
 */
public interface PassivationListener<T, P>
{
    /**
     * Called after the object is returned to the pool.
     */
    public void passivate(T object, P parameter);
}
