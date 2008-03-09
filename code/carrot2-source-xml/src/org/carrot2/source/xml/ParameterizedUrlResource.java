package org.carrot2.source.xml;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;

import org.carrot2.util.resource.Resource;

/**
 * A {@link Resource} implementation that allows URLs to be parameterized. The attribute
 * place holders are of format: <code>${attribute}</code> and will be replaced before
 * the contents is fetched from the URL when the {@link #open(Map)} method is used.
 */
public class ParameterizedUrlResource implements Resource
{
    private final URL url;
    private final String info;

    /**
     * Creates an instance with the provided <code>url</code>;
     */
    public ParameterizedUrlResource(URL url)
    {
        this.url = url;
        this.info = "[URL: " + url.toExternalForm() + "]";
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
        urlString = substituteAttributes(attributes, urlString);
        return new URL(urlString).openStream();
    }

    /**
     * Performs attribute substitution.
     */
    static String substituteAttributes(Map<String, Object> attributes, String urlString)
    {
        for (Map.Entry<String, Object> entry : attributes.entrySet())
        {
            // In theory, we could cache the patterns, but the gains are not worth it
            Pattern pattern = Pattern.compile("${" + entry.getKey() + "}",
                Pattern.LITERAL);
            try
            {
                urlString = pattern.matcher(urlString).replaceAll(
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }
        return urlString;
    }

    @Override
    public String toString()
    {
        return info;
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof ParameterizedUrlResource)
        {
            return ((ParameterizedUrlResource) obj).info.equals(this.info);
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        return this.info.hashCode();
    }
}
