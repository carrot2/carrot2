
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

package org.carrot2.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;

/**
 * Provides a number of useful method operating on {@link String}s that are not available
 * in {@link org.apache.commons.lang.StringUtils}.
 */
public final class StringUtils
{
    private static final Pattern CAMEL_CASE_FRAGMENT = Pattern.compile(
        // a sequence of upper case letters followed by digits
        "([A-Z]{1,}(?=[0-9]+))|" +
        // a sequence of upper case letters followed by an upper letter, followed by lower case or digits
        "([A-Z]{1,}(?=[A-Z][a-z0-9]+))|" +
        // upper case letters followed by lower case letters or digits
        "([A-Z]?[A-Z]+[a-z0-9]+)|" + 
        // sequence of digits
        "([0-9]+)");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<.+?>", Pattern.CASE_INSENSITIVE);

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
        return CAMEL_CASE_FRAGMENT.matcher(camelCaseString).replaceAll("$0 ").trim();
    }

    public static String urlEncodeWrapException(String string, String encoding)
    {
        try
        {
            return URLEncoder.encode(string, encoding);
        }
        catch (UnsupportedEncodingException e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }

    public static String removeHtmlTags(String string)
    {
        return HTML_TAG_PATTERN.matcher(string).replaceAll("");
    }

    public static String identifierToHumanReadable(String string)
    {
        return WordUtils.capitalizeFully(string.replace('_', ' '));
    }
    
    /**
     * Join a list of non-null objects with <code>delim</code> and return it
     * as a string.
     */
    public static String join(String delim, Object... objects)
    {
        final StringBuilder b = new StringBuilder();
        for (Object s : objects)
        {
            if (s != null)
            {
                if (b.length() > 0) b.append(delim);
                b.append(s.toString());
            }
        }
        return b.toString();
    }

}
