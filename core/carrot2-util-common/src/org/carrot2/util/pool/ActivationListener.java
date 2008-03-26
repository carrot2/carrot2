package org.carrot2.util.pool;

/**
 *
 */
public interface ActivationListener<T>
{
    /**
     * Called before object is handed in to the caller of
     * {@link SoftUnboundedPool#borrowObject(Class)}.
     */
    public void activate(T object);
}
