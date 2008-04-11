package org.carrot2.util.reflect;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.commons.lang.builder.*;

/**
 * Facilitates comparing collections of object that do not need to implement
 * {@link Object#equals(Object)} and {@link Object#hashCode()} for production, but would
 * be much easier to unit-test if they had these methods implemented. Using the
 * {@link #wrap(Object, boolean)} method, you can wrap your object with an
 * {@link ObjectEquivalenceHelper}, which implements the {@link Object#equals(Object)}
 * and {@link Object#hashCode()} through reflection in a way that is sufficient for most
 * of the unit tests.
 */
public class ObjectEquivalenceHelper
{
    /** The original object */
    private final Object delegate;

    /**
     * A private constructor.
     */
    private ObjectEquivalenceHelper(Object delegate)
    {
        this.delegate = delegate;
    }

    /**
     * Reflection-based implementation (reads fields of the wrapped object).
     */
    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this.delegate,
            ((ObjectEquivalenceHelper) obj).delegate);
    }

    /**
     * Reflection-based implementation (reads fields of the wrapped object).
     */
    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(delegate);
    }

    /**
     * Reflection-based implementation (reads fields of the wrapped object).
     */
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(delegate);
    }

    /**
     * Wraps a collection of objects in {@link ObjectEquivalenceHelper}s.
     * Collection-typed fields of objects will also be recursively wrapped.
     * 
     * @param collection collection of objects to be wrapped
     * @return the provided collection, for convenience
     */
    public static Collection<Object> wrap(Collection<Object> collection)
    {
        return wrap(collection, true);
    }

    /**
     * Wraps a collection of objects in {@link ObjectEquivalenceHelper}s.
     * 
     * @param collection collection of objects to be wrapped
     * @param wrapFields if <code>true</code>, collection-typed fields of each object
     *            will also be recursively wrapped
     * @return the provided collection, for convenience
     */
    public static Collection<Object> wrap(Collection<Object> collection,
        boolean wrapFields)
    {
        // Copy original values from the collection, preserving iteration order.
        final ArrayList<Object> copy = new ArrayList<Object>(collection);

        try
        {
            collection.clear();

            // Wrap
            for (Object toWrap : copy)
            {
                collection.add(wrap(toWrap, wrapFields));
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

    /**
     * Wraps an objects in an {@link ObjectEquivalenceHelper}. Collection-typed fields
     * the object will also be recursively wrapped.
     * 
     * @param object object to be wrapped
     * @return the wrapped object
     */
    public static ObjectEquivalenceHelper wrap(Object object)
    {
        return wrap(object, true);
    }

    /**
     * Wraps an objects in an {@link ObjectEquivalenceHelper}.
     * 
     * @param object object to be wrapped
     * @param wrapFields if <code>true</code>, collection-typed fields of each object
     *            will also be recursively wrapped
     * @return the wrapped object
     */
    @SuppressWarnings("unchecked")
    public static ObjectEquivalenceHelper wrap(Object object, boolean wrapFields)
    {
        if (wrapFields)
        {
            Collection<Field> fields = getFieldsFromClassHierarchy(object.getClass());
            for (Field field : fields)
            {
                Object value = null;
                try
                {
                    field.setAccessible(true);
                    value = field.get(object);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }

                if (value != null && (value instanceof Collection))
                {
                    wrap((Collection) value);
                }
            }
        }

        return new ObjectEquivalenceHelper(object);
    }

    /**
     * Caches fields read by the {@link #getFieldsFromClassHierarchy(Class)} method.
     */
    private static final Map<Class<?>, Collection<Field>> FIELD_CACHE = new WeakHashMap<Class<?>, Collection<Field>>();

    /**
     * Returns all declared fields from the provided class' hierarchy.
     */
    static Collection<Field> getFieldsFromClassHierarchy(Class<?> clazz)
    {
        synchronized (FIELD_CACHE)
        {
            Collection<Field> fields = FIELD_CACHE.get(clazz);
            if (fields == null)
            {
                fields = new LinkedHashSet<Field>();

                fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

                final Class<?> superClass = clazz.getSuperclass();
                if (superClass != null)
                {
                    fields.addAll(getFieldsFromClassHierarchy(superClass));
                }

                FIELD_CACHE.put(clazz, fields);
            }
            return fields;
        }
    }
}
