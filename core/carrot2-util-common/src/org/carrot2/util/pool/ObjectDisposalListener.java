package org.carrot2.util.pool;

/**
 *
 */
public interface ObjectDisposalListener<T>
{
    public void objectDisposed(T object);
}
