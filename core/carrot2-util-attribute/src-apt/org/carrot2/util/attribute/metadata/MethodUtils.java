
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

package org.carrot2.util.attribute.metadata;

/**
 * Method name related utilities.
 */
public final class MethodUtils
{
    /**
     * Convert a camel-case literal to an equivalent constant convention.
     */
    public static String asConstant(String camelCase)
    {
        StringBuilder builder = new StringBuilder();
        boolean addUnderscore = false;

        char [] chars = camelCase.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            if (Character.isUpperCase(chars[i]))
            {
                if (addUnderscore || i + 1 < chars.length && Character.isLowerCase(chars[i + 1]))
                {
                    builder.append("_");
                }
                addUnderscore = false;
            }
            else
            {
                addUnderscore = true;
            }
            builder.append(Character.toUpperCase(chars[i]));
        }

        return builder.toString();
    }
}
