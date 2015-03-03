package org.carrot2.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Subclasses hold and expose attributes.
 */
abstract class AttributeSet
{
    /** 
     * Attributes collected after processing (read-only view).
     */
    protected final Map<String, Object> attributes;

    public AttributeSet(Map<String, Object> attributes)
    {
        this.attributes = Collections.unmodifiableMap(attributes);
    }

    /**
     * Returns the attributes collected during processing. The returned map is
     * unmodifiable.
     */
    public Map<String, Object> getAttributes()
    {
        return attributes;
    }

    /**
     * Returns a specific attribute of this result set.
     */
    public Object getAttribute(String key)
    {
        return attributes.get(key);
    }

    /**
     * Return a specific attribute cast to the given class.
     */
    public <T> T getAttribute(String key, Class<T> cast)
    { 
      return cast.cast(getAttribute(key));
    }
    
    /**
     * Verify if elements of a list are of a given type.
     */
    protected boolean assertListContainsOnly(Object list, Class<?> elementType)
    {
        assert list instanceof List<?>;
        for (Object element : ((List<?>) list)) {
            assert elementType.isInstance(element);
        }
        return true;
    }    
}