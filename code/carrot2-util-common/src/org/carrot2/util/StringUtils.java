/**
 * 
 */
package org.carrot2.util;

import java.util.Iterator;

/**
 * Provides a number of useful method operating on {@link String}s.
 * <p>
 * Although we inherit from {@link org.apache.commons.lang.StringUtils} to explicitly show
 * the relation to it, please reference the static methods from their original class.
 */
public final class StringUtils extends org.apache.commons.lang.StringUtils
{
    private StringUtils()
    {
    }

    public static <T> String toString(Iterable<T> iterable, String separator)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (Iterator<T> iterator = iterable.iterator(); iterator.hasNext();)
        {
            T object = iterator.next();
            stringBuilder.append(object);
            if (iterator.hasNext())
            {
                stringBuilder.append(separator);
            }
        }

        return stringBuilder.toString();
    }
}
