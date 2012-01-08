
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
}
