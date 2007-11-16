package org.carrot2.core.parameters;

import org.carrot2.core.type.AbstractType;
import org.carrot2.core.type.TypeWithDefaultValue;

public class AnyClassTypeWithDefaultValue extends AbstractType<Object> implements TypeWithDefaultValue<Object>
{
    private Class implType;
    private Class type;

    public <T> AnyClassTypeWithDefaultValue(Class<T> type, Class<? extends T> implType)
    {
        super(Object.class);
        this.implType = implType;
        this.type = type;
    }
    
    @Override
    public Class<Object> getType()
    {
        return type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object valueOf(String s)
    {
        try
        {
            final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
            return classLoader.loadClass(s).newInstance();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Could not instantiate: " + s, e);
        }
    }

    @Override
    public Object getDefaultValue()
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
