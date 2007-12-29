package org.carrot2.core.parameter;

import java.lang.reflect.Field;
import java.util.*;

final class BindableUtils
{
    public static String getFieldName(Field field)
    {
        final String key = getKey(field);
        if ("".equals(key))
        {
            final Class<?> declaringClass = field.getDeclaringClass();
            Bindable classAnnotation = declaringClass.getAnnotation(Bindable.class);
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
            return key;
        }
    }

    public static String getKey(Field field)
    {
        Attribute attributeAnnotation = field.getAnnotation(Attribute.class);
        if (attributeAnnotation != null)
        {
            return attributeAnnotation.key();
        }
        
        Parameter parameterAnnotation = field.getAnnotation(Parameter.class);
        if (parameterAnnotation != null)
        {
            return parameterAnnotation.key();
        }
        
        throw new IllegalArgumentException();
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