
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.simplexml;

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.ReflectionUtils;
import org.simpleframework.xml.*;

import org.carrot2.shaded.guava.common.collect.ImmutableSet;

/**
 * A wrapper around typical serialized types, such as primitives. Without this wrapper
 * each value would get a class attribute indicating the specific wrapper class name
 * (longish), which would be a pain for people editing the XML files by hand. With this
 * wrapper, the type attribute corresponds to the actual Java class name.
 */
@Root(name = "value")
public class SimpleXmlWrapperValue
{
    /**
     * Type of a primitive value.
     */
    @Attribute(required = false)
    String type;

    /**
     * Value of a primitive value.
     */
    @Attribute(required = false)
    String value;

    /**
     * Generic type wrapped with a SimpleXML-annotated type or an already
     * SimpleXML-annotated type.
     */
    @Element(required = false)
    Object wrapper;

    /**
     * Type we can handle using toString() and valueOf() methods.
     */
    private static final Set<Class<?>> TO_STRING_VALUE_OF_TYPES = ImmutableSet
        .<Class<?>> of(Byte.class, Short.class, Integer.class, Long.class, Float.class,
            Double.class, Boolean.class);

    /**
     * Wraps the provided value with the serialization wrapper.
     */
    static SimpleXmlWrapperValue wrap(Object value)
    {
        final SimpleXmlWrapperValue wrapper = new SimpleXmlWrapperValue();

        if (value == null)
        {
            return wrapper;
        }

        final Class<?> valueType = value.getClass();

        if (TO_STRING_VALUE_OF_TYPES.contains(valueType))
        {
            wrapper.value = value.toString();
            wrapper.type = valueType.getName();
        }
        else if (value instanceof Character)
        {
            wrapper.value = value.toString();
            wrapper.type = Character.class.getName();
        }
        else if (value instanceof String)
        {
            wrapper.value = (String) value;
            wrapper.type = null;
        }
        else if (Class.class.isInstance(value))
        {
            wrapper.value = ((Class<?>) value).getName();
            wrapper.type = Class.class.getName();
        }
        else if (value instanceof Enum<?>)
        {
            final Enum<?> e = (Enum<?>) value;
            wrapper.value = e.name();
            wrapper.type = e.getDeclaringClass().getName();
        }
        else if (value.getClass().getAnnotation(Root.class) != null)
        {
            wrapper.wrapper = value;
        }
        else
        {
            // Try to get a wrapper.
            wrapper.wrapper = SimpleXmlWrapperValue.wrapCustom(value);
        }

        return wrapper;
    }

    /**
     * Unwraps the actual value represented by this wrapper.
     * 
     * @return the actual value or <code>null</code> if value cannot be unwrapped
     */
    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    Object unwrap()
    {
        if (value != null)
        {
            if (StringUtils.isEmpty(type))
            {
                type = String.class.getName();
            }

            final Class<?> valueType = loadClassWrapAsRuntime(type); 

            if (TO_STRING_VALUE_OF_TYPES.contains(valueType))
            {
                Method valueOfMethod;
                try
                {
                    valueOfMethod = valueType.getMethod("valueOf", String.class);
                    return valueOfMethod.invoke(null, value);
                }
                catch (Exception e)
                {
                    throw ExceptionUtils.wrapAsRuntimeException(e);
                }
            }
            else if (Character.class.getName().equals(type))
            {
                return value.length() > 0 ? value.charAt(0) : null;
            }
            else if (String.class.getName().equals(type))
            {
                return value;
            }
            else if (Class.class.getName().equals(type))
            {
                return loadClassWrapAsRuntime(value);
            }
            else if (Enum.class.isAssignableFrom(valueType))
            {
                return Enum.valueOf((Class<? extends Enum>) valueType, value);
            }
        }
        else if (wrapper != null)
        {
            if (wrapper instanceof ISimpleXmlWrapper)
            {
                return ((ISimpleXmlWrapper<?>) wrapper).getValue();
            }
            else
            {
                return wrapper;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    static <T> ISimpleXmlWrapper<T> wrapCustom(T value)
    {
        final Class<? extends ISimpleXmlWrapper<?>> wrapperClass = SimpleXmlWrappers.getWrapper(value);
        if (wrapperClass != null)
        {
            ISimpleXmlWrapper<T> newInstance;
            try
            {
                newInstance = (ISimpleXmlWrapper<T>) wrapperClass.newInstance();
            }
            catch (Exception e)
            {
                throw ExceptionUtils.wrapAsRuntimeException(e);
            }
            newInstance.setValue(value);
    
            return newInstance;
        }
        else
        {
            return null;
        }
    }

    private static Class<?> loadClassWrapAsRuntime(String className)
    {
        try
        {
            return ReflectionUtils.classForName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }
}
