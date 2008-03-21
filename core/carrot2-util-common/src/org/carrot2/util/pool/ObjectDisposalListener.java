package org.carrot2.util.pool;

/**
 *
 */
public interface ObjectDisposalListener<T>
{
    /**
     * Called before the object is disposed of.
     */
    public void dispose(T object);
}
