package org.carrot2.dcs;

import java.util.HashMap;

/**
 * A facility for helping with management of application configuration.
 */
public final class Config
{

    /**
     * A map of default values.
     */
    private HashMap attributes = new HashMap();

    public Config()
    {
        // no instances outside of package scope.
    }

    /**
     * Sets the default value with the given key. Only the first binding
     * is important (later are discarded).
     */
    public void setDefaultValue(String key, Object value)
    {
        if (!attributes.containsKey(key))
        {
            attributes.put(key, value);
        }
    }

    /**
     * Returns a value at the given key or <code>null</code>.
     */
    public String getString(String key)
    {
        try
        {
            return (String) attributes.get(key);
        }
        catch (ClassCastException e)
        {
            throw new RuntimeException("Configuration value at key " + key + " is not a string: " + attributes.get(key));
        }
    }

    /**
     * Returns a value at the given key or <code>null</code>.
     */
    public Boolean getBoolean(String key)
    {
        try
        {
            return (Boolean) attributes.get(key);
        }
        catch (ClassCastException e)
        {
            throw new RuntimeException("Configuration value at key " + key + " is not a Boolean: " + attributes.get(key));
        }
    }

    /**
     * Same as {@link #getString(String)}, but throws an exception if the returned
     * value is <code>null</code>.
     */
    public String getRequiredString(String key)
    {
        final String value = getString(key);
        if (value == null) throw new RuntimeException("The value for key " + key + " is required.");
        return value;
    }

    /**
     * Returns a required <code>boolean</code> value.
     */
    public boolean getRequiredBoolean(String key)
    {
        final Boolean value = getBoolean(key);
        if (value == null) throw new RuntimeException("The value for key " + key + " is required.");
        return value.booleanValue();
    }

    /**
     * Returns <code>true</code> if there is a value of the given key.
     */
    public boolean hasValue(String key)
    {
        return attributes.containsKey(key);
    }
    
    /**
     * Returns the value for the given key or <code>null</code> if it's not present.
     */
    public Object getValue(String key)
    {
        return attributes.get(key);
    }

    /**
     * Returns the required value or throws an exception if not set. 
     */
    public Object getRequiredValue(String key)
    {
        final Object value = attributes.get(key);
        if (value == null) throw new RuntimeException("The value for key " + key + " is required.");
        return value;
    }
}
