package org.carrot2.core.parameters;

import org.carrot2.core.type.Type;
import org.carrot2.core.type.TypeWithDefaultValue;

public class Parameter
{
    public final String name;

    /**
     * TODO: assuming all parameters are optional, we should use
     * {@link TypeWithDefaultValue} here instead of the unrestricted {@link Type}.
     */
    public final Type<?> type;

    public Parameter(String name, Type<?> type)
    {
    	if (name == null || type == null) {
    		throw new IllegalArgumentException();
    	}

        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public Type<?> getType()
    {
        return type;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == this) 
    		return true;

    	if (obj == null || !(obj instanceof Parameter)) 
    		return false;

    	return ((Parameter) obj).name.equals(this.name)
    		&& ((Parameter) obj).type.equals(this.type);
    }
    
    @Override
    public int hashCode() {
    	return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name + "=" + type.getType();
    }
}
