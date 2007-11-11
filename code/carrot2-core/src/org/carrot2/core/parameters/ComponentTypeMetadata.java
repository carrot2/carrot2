package org.carrot2.core.parameters;


public class ComponentTypeMetadata extends TypeMetadata
{
    public final Class<?> clazz;

    public <T> ComponentTypeMetadata(Class<T> clazz)
    {
        this.clazz = clazz;
    }
    
    @Override
    public String toString()
    {
        return "{class assignable to " + clazz.getName() + "}";
    }    
}
