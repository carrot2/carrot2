package org.carrot2.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a number of useful method operating on {@link String}s that are not available
 * in {@link org.apache.commons.lang.StringUtils}.
 */
public final class StringUtils
{
    private static final Pattern CAMEL_CASE_SEGMENT_PATTERN = Pattern
        .compile("[A-Z][a-z0-9]*");

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<.+?>",
        Pattern.CASE_INSENSITIVE);

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

    public static String splitCamelCase(String camelCaseString)
    {
        final Matcher matcher = CAMEL_CASE_SEGMENT_PATTERN.matcher(camelCaseString);
        final List<String> parts = new ArrayList<String>();
        while (matcher.find())
        {
            parts.add(matcher.group());
        }
        return org.apache.commons.lang.StringUtils.join(parts, ' ');
    }

    public static String urlEncodeWrapException(String string, String encoding)
    {
        try
        {
            return URLEncoder.encode(string, encoding);
        }
        catch (UnsupportedEncodingException e)
        {
            throw ExceptionUtils.wrapAs(RuntimeException.class, e);
        }
    }

    public static String removeHtmlTags(String string)
    {
        return HTML_TAG_PATTERN.matcher(string).replaceAll("");
    }
}
