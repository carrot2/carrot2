package org.carrot2.source.xml;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.carrot2.util.resource.*;

/**
 * Looks up resources as, possibly remote, URLs.
 */
public class ParametrizedUrlResourceLocator implements ResourceLocator
{
    private final static Logger logger = Logger.getLogger(ParametrizedUrlResourceLocator.class);

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
                new ParametrizedUrlResource(url)
            };
        }
        catch (MalformedURLException e)
        {
            logger.warn("Malformed url: " + resource, e);
            return new Resource [0];
        }
    }
}
