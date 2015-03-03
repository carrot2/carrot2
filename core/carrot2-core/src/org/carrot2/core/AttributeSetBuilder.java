package org.carrot2.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

abstract class AttributeSetBuilder<T>
{
    protected final Map<String, Object> attributes = new HashMap<>();

    protected AttributeSetBuilder<T> attr(String key, Object value)
    {
        if (attributes.containsKey(key)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                "Attribute already bound: %s => %s (attempted: %s)",
                key,
                attributes.get(key),
                value));
        }
    
        attributes.put(key, value);
        return this;
    }

    protected final Map<String, Object> cloneAndClearAttributes()
    {
        final HashMap<String, Object> cloned = new HashMap<>(attributes);
        this.attributes.clear();
        return cloned;
    }

    public abstract T build();
}