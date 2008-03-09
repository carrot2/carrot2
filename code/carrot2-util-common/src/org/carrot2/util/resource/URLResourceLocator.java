package org.carrot2.util.resource;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Looks up resources as, possibly remote, URLs.
 */
public class URLResourceLocator implements ResourceLocator
{
    private final static Logger logger = Logger.getLogger(URLResourceLocator.class);

    /**
     * If the provided <code>resource</code> is a valid URL, returns an array containing
     * the corresponding {@link Resource} instance. Otherwise, returns an empty array.
     */
    public Resource [] getAll(String resource, Class<?> clazz)
    {
        try
        {
            URL url = new URL(resource);
            return new Resource []
            {
                new URLResource(url)
            };
        }
        catch (MalformedURLException e)
        {
            logger.warn("Malformed url: " + resource, e);
            return new Resource [0];
        }
    }
}
