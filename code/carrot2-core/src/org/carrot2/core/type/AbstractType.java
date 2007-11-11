package org.carrot2.core.type;

public abstract class AbstractType<T> implements Type<T>
{
    private Class<T> type;
    
    public AbstractType(Class<T> type)
    {
        this.type = type;
    }
    
    public Class<T> getType()
    {
        return type;
    }
    
    public abstract T valueOf(String s);
}
