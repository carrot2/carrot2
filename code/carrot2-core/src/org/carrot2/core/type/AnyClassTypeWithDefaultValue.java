package org.carrot2.core.type;


public class AnyClassTypeWithDefaultValue<T> extends AbstractType<T> implements TypeWithDefaultValue<T>
{
    private Class<? extends T> implType;

    public AnyClassTypeWithDefaultValue(Class<T> type, Class<? extends T> implType)
    {
        super(type);
        this.implType = implType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T valueOf(String s)
    {
        try
        {
            final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
            return (T) classLoader.loadClass(s).newInstance();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Could not instantiate: " + s, e);
        }
    }

    @Override
    public T getDefaultValue()
    {
        try
        {
            return implType.newInstance();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Could not instantiate: "
                + implType.getName(), e);
        }
    }

}
