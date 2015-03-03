package org.carrot2.core;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
    protected boolean checkListContainsOnly(Object list, Class<?> elementType)
    {
        if (!(list instanceof List)) {
            throw new AssertionError(String.format(Locale.ROOT,
                "Expected a List<? extends %s>: %s",
                elementType.getName(),
                list));
        };

        for (Object element : ((List<?>) list)) {
            if (!elementType.isInstance(element)) {
                throw new AssertionError(String.format(Locale.ROOT,
                    "Expected all list elements to be of type %s, but encountered type %s: %s",
                    elementType.getName(),
                    element.getClass().getName(),
                    element));
            }
        }

        return true;
    }    
}