package org.carrot2.util.factory;

/**
 * A {@link IFactory} that creates new instances of a given class.
 */
public final class NewClassInstanceFactory<T> implements IFactory<T>
{
    private final Class<? extends T> clazz;

    public NewClassInstanceFactory(Class<? extends T> clazz)
    {
        this.clazz = clazz;
    }
    
    @Override
    public T createInstance()
    {
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
