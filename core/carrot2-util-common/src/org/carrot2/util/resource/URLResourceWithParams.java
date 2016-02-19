
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

package org.carrot2.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

import org.carrot2.util.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

/**
 * A {@link IResource} implementation that allows URLs to be parameterized. The attribute
 * place holders are of format: <code>${attribute}</code> and will be replaced before the
 * contents is fetched from the URL when the {@link #open(Map)} method is used.
 */
@Root(name = "parameterized-url-resource")
public class URLResourceWithParams implements IResource
{
    /**
     * Public address of the resource.
     */
    private URL url;

    /**
     * URL string, for serialization only.
     */
    @Attribute(name = "url")
    private String info;

    /**
     * For XML serialization/deserialization only, use
     * {@link #URLResourceWithParams(URL)}
     */
    URLResourceWithParams()
    {
    }

    /**
     * Creates an instance with the provided <code>url</code>;
     */
    public URLResourceWithParams(URL url)
    {
        this.url = url;
        this.info = url.toExternalForm();
    }

    /**
     * Opens the underlying URL <strong>without</strong> attribute substitution.
     */
    public InputStream open() throws IOException
    {
        return url.openStream();
    }

    /**
     * Opens the underlying URL substituting attribute place holders beforehand.
     * 
     * @param attributes values of attributes to be replaced in the corresponding place
     *            holders. If a place holder is of form: <code>${attributeX}</code>, it
     *            will be replaced by the value found (if any) in the attributes map under
     *            the <code>attributeX</code> key.
     */
    public InputStream open(Map<String, Object> attributes) throws IOException
    {
        String urlString = url.toExternalForm();
        urlString = substituteAttributes(urlString, attributes);
        return new URL(urlString).openStream();
    }

    /**
     * Performs attribute substitution.
     */
    public static String substituteAttributes(String parameterizedURL,
        Map<String, Object> attributes)
    {
        for (Map.Entry<String, Object> entry : attributes.entrySet())
        {
            // In theory, we could cache the patterns, but the gains are not worth it
            final Pattern pattern = Pattern.compile(formatAttributePlaceholder(entry
                .getKey()), Pattern.LITERAL);
            parameterizedURL = pattern.matcher(parameterizedURL).replaceAll(
                StringUtils.urlEncodeWrapException(entry.getValue().toString(), "UTF-8"));
        }
        return parameterizedURL;
    }

    /**
     * Returns <code>true</code> if the <code>urlTemplate</code> contains the
     * <code>attributePlaceholderName</code>.
     */
    public static boolean containsAttributePlaceholder(String urlTemplate,
        String attributePlaceholderName)
    {
        return urlTemplate.contains(formatAttributePlaceholder(attributePlaceholderName));
    }

    /**
     * Returns attribute place holder based on the attribute name.
     */
    public static String formatAttributePlaceholder(String attributePlaceholderName)
    {
        return "${" + attributePlaceholderName + "}";
    }

    @Override
    public String toString()
    {
        return this.info;
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof URLResourceWithParams)
        {
            return ((URLResourceWithParams) obj).info.equals(this.info);
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        return this.info.hashCode();
    }

    public URL getUrl()
    {
        return url;
    }

    @Commit
    void afterDeserialization() throws MalformedURLException
    {
        url = new URL(info);
    }

    public static URLResourceWithParams valueOf(String string)
    {
        try
        {
            // We don't check if the URL indeed contains parameters. A parameterless
            // URL will be handled correctly anyway.
            return new URLResourceWithParams(new URL(string));
        }
        catch (MalformedURLException e)
        {
            return null;
        }
    }
}
