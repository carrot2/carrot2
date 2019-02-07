
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.util.HashMap;
import java.util.Map;

/**
 * A number of utility methods for working with {@link Attribute}s.
 */
public final class AttributeUtils
{
    private AttributeUtils()
    {
        // No instantiation
    }

    /**
     * Computes the attribute key according to the definition in {@link Attribute#key()}.
     * 
     * @param clazz class containing the attribute
     * @param fieldName name of the field representing the attribute
     * @return key of the attribute
     */
    public static String getKey(Class<?> clazz, String fieldName)
    {
        try
        {
            return BindableUtils.getKey(clazz.getDeclaredField(fieldName));
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


    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
    static {
        PRIMITIVE_TO_WRAPPER_TYPE = new HashMap<>();
        PRIMITIVE_TO_WRAPPER_TYPE.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER_TYPE.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER_TYPE.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER_TYPE.put(double.class, Double.class);
        PRIMITIVE_TO_WRAPPER_TYPE.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER_TYPE.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER_TYPE.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER_TYPE.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER_TYPE.put(void.class, Void.class);
    }

    static <T> Class<T> toBoxed(Class<T> type) {
        Class<T> wrapped = (Class<T>) PRIMITIVE_TO_WRAPPER_TYPE.get(type);
        return (wrapped == null) ? type : wrapped;
    }

    public static RuntimeException propagate(Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        if (t instanceof Error) {
            throw (Error) t;
        }
        throw new RuntimeException(t);
    }
}
