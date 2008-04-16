package org.carrot2.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a number of useful method operating on {@link String}s.
 * <p>
 * Although we inherit from {@link org.apache.commons.lang.StringUtils} to explicitly show
 * the relation to it, please reference the static methods from their original class.
 */
public final class StringUtils extends org.apache.commons.lang.StringUtils
{
    private static Pattern camelCasePart;

    private StringUtils()
    {
    }

    public static <T> String toString(Iterable<T> iterable, String separator)
    {
        final StringBuilder stringBuilder = new StringBuilder();

        for (final Iterator<T> iterator = iterable.iterator(); iterator.hasNext();)
        {
            final T object = iterator.next();
            stringBuilder.append(object);
            if (iterator.hasNext())
            {
                stringBuilder.append(separator);
            }
        }

        return stringBuilder.toString();
    }

    public static int multiStringHashCode(String... strings)
    {
        int result = 0;

        for (final String string : strings)
        {
            if (string != null)
            {
                result ^= string.hashCode();
            }
        }

        return result;
    }

    public static String splitCamelCase(String camelCaseString)
    {
        if (camelCasePart == null)
        {
            camelCasePart = Pattern.compile("[A-Z][a-z0-9]*");
        }
        Matcher matcher = camelCasePart.matcher(camelCaseString);
        List<String> parts = new ArrayList<String>();
        while (matcher.find())
        {
            parts.add(matcher.group());
        }
        return join(parts, ' ');
    }
}
