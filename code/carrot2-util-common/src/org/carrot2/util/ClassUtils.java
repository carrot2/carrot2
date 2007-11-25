package org.carrot2.util;


/**
 * Class related utilities.
 */
public final class ClassUtils
{
    private ClassUtils()
    {
        // no instances.
    }

    /**
     * Convert primitive types to their boxed counterparts. 
     */
    public static Class<?> boxPrimitive(Class<?> type)
    {
        if (type.isPrimitive())
        {
            if (Byte.TYPE == type) return Byte.class;
            if (Short.TYPE == type) return Short.class;
            if (Integer.TYPE == type) return Integer.class;
            if (Long.TYPE == type) return Long.class;
            if (Float.TYPE == type) return Float.class;
            if (Double.TYPE == type) return Double.class;
            if (Character.TYPE == type) return Character.class;
            throw new RuntimeException("Unknown primitive type: " + type);
        }

        return type;
    }
}
