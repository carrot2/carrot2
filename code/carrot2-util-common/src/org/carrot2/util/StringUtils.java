/**
 * 
 */
package org.carrot2.util;

import java.util.Iterator;

/**
 *
 */
public final class StringUtils
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
