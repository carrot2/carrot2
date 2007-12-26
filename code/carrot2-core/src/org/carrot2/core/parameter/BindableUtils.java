package org.carrot2.core.parameter;

import java.lang.reflect.Field;
import java.util.*;

final class BindableUtils
{
    public static String getPrefix(Object instance)
    {
        final Bindable bindable = instance.getClass().getAnnotation(Bindable.class);
        if (bindable == null)
        {
            throw new IllegalArgumentException();
        }

        if (bindable.prefix().equals(""))
        {
            return instance.getClass().getName();
        }
        else
        {
            return bindable.prefix();
        }
    }

    public static Collection<Field> getFieldsFromBindableHierarchy(Class<?> clazz)
    {
        Set<Field> fields = new HashSet<Field>();

        if (clazz.getAnnotation(Bindable.class) != null)
        {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null)
        {
            fields.addAll(getFieldsFromBindableHierarchy(superClass));
        }

        return fields;
    }
}