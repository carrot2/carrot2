package org.carrot2.core.type;

public class BoundedIntegerTypeWithDefaultValue extends BoundedIntegerType implements
    TypeWithDefaultValue<Integer>
{
    private final Integer defaultValue;

    public BoundedIntegerTypeWithDefaultValue(Integer defaultValue, Integer minValue,
        Integer maxValue)
    {
        super(minValue, maxValue);
        this.defaultValue = defaultValue;
    }

    public Integer getDefaultValue()
    {
        return defaultValue;
    }
    
    @Override
    public boolean equals(Object obj)
    {
    	if (this == obj) 
    		return true;
    	
    	if (this == null || !(obj instanceof BoundedIntegerTypeWithDefaultValue)) 
    		return false;

    	return super.equals(obj) && 
    		((BoundedIntegerTypeWithDefaultValue) obj).defaultValue.equals(defaultValue);
    }
}
