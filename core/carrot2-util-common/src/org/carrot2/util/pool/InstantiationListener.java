package org.carrot2.util.pool;

/**
 *
 */
public interface InstantiationListener<T, P>
{
    /**
     * Called after the object gets instantiated.
     */
    public void objectInstantiated(T object, P parameter);
}
