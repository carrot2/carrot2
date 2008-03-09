package org.carrot2.source.xml;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;

import org.carrot2.util.resource.Resource;

/**
 * 
 */
public class ParametrizedUrlResource implements Resource
{
    private final URL url;
    private final String info;

    public ParametrizedUrlResource(URL url)
    {
        this.url = url;
        this.info = "[URL: " + url.toExternalForm() + "]";
    }

    public InputStream open() throws IOException
    {
        return url.openStream();
    }

    public InputStream open(Map<String, Object> attributes) throws IOException
    {
        String urlString = url.toExternalForm();
        urlString = substituteAttributes(attributes, urlString);
        return new URL(urlString).openStream();
    }

    static String substituteAttributes(Map<String, Object> attributes, String urlString)
    {
        for (Map.Entry<String, Object> entry : attributes.entrySet())
        {
            Pattern pattern = Pattern.compile("\\$\\{" + entry.getKey() + "\\}");
            try
            {
                urlString = pattern.matcher(urlString).replaceAll(
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                // ignored
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
        if (obj instanceof ParametrizedUrlResource)
        {
            return ((ParametrizedUrlResource) obj).info.equals(this.info);
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        return this.info.hashCode();
    }
}
