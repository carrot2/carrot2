package org.carrot2.util.pool;

/**
 *
 */
public interface ObjectActivationListener<T>
{
    /**
     * Called before object is handed in to the caller of
     * {@link ObjectPool#borrowObject(Class)}.
     */
    public void activate(T object);
}
