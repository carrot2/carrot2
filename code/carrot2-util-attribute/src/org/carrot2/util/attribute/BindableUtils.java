package org.carrot2.util.attribute;

import java.lang.reflect.Field;
import java.util.*;

import com.google.common.collect.Lists;

/**
 * A set of utility methods for working with {@link Bindable} types.
 */
final class BindableUtils
{
    /**
     * Caches the sets of declared fields determined for class hierarchies by the
     * {@link #getFieldsFromBindableHierarchy(Class)} method.
     */
    private static final Map<Class<?>, Collection<Field>> FIELD_CACHE = new WeakHashMap<Class<?>, Collection<Field>>();

    /**
     * Returns all fields from all {@link Bindable} types in the hierarchy of the provided
     * <code>clazz</code>. The collected fields gets cached.
     */
    static Collection<Field> getFieldsFromBindableHierarchy(Class<?> clazz)
    {
        synchronized (FIELD_CACHE)
        {
            Collection<Field> fields = FIELD_CACHE.get(clazz);
            if (fields == null)
            {
                fields = new LinkedHashSet<Field>();

                if (clazz.getAnnotation(Bindable.class) != null)
                {
                    fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
                }

                final Class<?> superClass = clazz.getSuperclass();
                if (superClass != null)
                {
                    fields.addAll(getFieldsFromBindableHierarchy(superClass));
                }

                FIELD_CACHE.put(clazz, fields);
            }
            return fields;
        }
    }

    /**
     * Returns all {@link Bindable} from the hierarchy of the provided <code>clazz</code>.
     */
    static Collection<Class<?>> getClassesFromBindableHerarchy(Class<?> clazz)
    {
        final Collection<Class<?>> classes = Lists.newArrayList();

        while (clazz != null)
        {
            if (clazz.getAnnotation(Bindable.class) != null)
            {
                classes.add(clazz);
            }

            clazz = clazz.getSuperclass();
        }

        return classes;
    }

    /**
     * Computes the attribute key according to the definition in {@link Attribute#key()}.
     */
    static String getKey(Field field)
    {
        final Attribute attributeAnnotation = field.getAnnotation(Attribute.class);

        if (attributeAnnotation == null || "".equals(attributeAnnotation.key()))
        {
            final Class<?> declaringClass = field.getDeclaringClass();
            final Bindable classAnnotation = declaringClass.getAnnotation(Bindable.class);
            if (classAnnotation == null)
            {
                throw new IllegalArgumentException();
            }

            final String classPrefix = classAnnotation.prefix();
            if ("".equals(classPrefix))
            {
                return declaringClass.getName() + "." + field.getName();
            }
            else
            {
                return classPrefix + "." + field.getName();
            }

        }
        else
        {
            return attributeAnnotation.key();
        }
    }

    /**
     * Computes the attribute key according to the definition in {@link Attribute#key()}.
     */
    static String getKey(Class<?> clazz, String fieldName)
    {
        try
        {
            return getKey(clazz.getDeclaredField(fieldName));
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }
}