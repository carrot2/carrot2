package org.carrot2.util.factory;

/**
 * A factory of <code>T</code>.
 */
public interface IFactory<T>
{
    public T createInstance();
}
