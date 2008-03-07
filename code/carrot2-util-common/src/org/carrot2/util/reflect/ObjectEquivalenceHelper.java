package org.carrot2.util.reflect;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.builder.*;

/**
 *
 */
public class ObjectEquivalenceHelper
{
    @SuppressWarnings("unused")
    private final Object delegate;

    ObjectEquivalenceHelper(Object delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this.delegate,
            ((ObjectEquivalenceHelper) obj).delegate);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(delegate);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(delegate);
    }

    public static Collection<Object> wrap(Collection<Object> collection)
    {
        // Copy original values from the collection, preserving iteration order.
        final ArrayList<Object> copy = new ArrayList<Object>(collection);

        try
        {
            collection.clear();

            // Wrap
            for (Object toWrap : copy)
            {
                collection.add(wrap(toWrap));
            }
        }
        catch (Exception e)
        {
            // If we get an exception here, we probably got an unmodifiable collection
            // and there is nothing we can do about it
            return collection;
        }
        return collection;
    }

    public static ObjectEquivalenceHelper wrap(Object object)
    {
        return new ObjectEquivalenceHelper(object);
    }
}
