package org.carrot2.util.factory;

/**
 * A {@link IFactory} that creates new instances of a given class.
 */
public final class SingletonFactory<T> implements IFactory<T>
{
    private final T singleton;

    public <E extends T> SingletonFactory(E singleton)
    {
        this.singleton = singleton;
    }

    @Override
    public T createInstance()
    {
        return singleton;
    }
}
