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

    @Override
    public int hashCode() 
    {
    	return type.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj) 
    		return true;

    	if (this == null || !(obj instanceof AbstractType<?>)) 
    		return false;

    	return ((AbstractType<?>) obj).type.equals(type);    	
    }
}
