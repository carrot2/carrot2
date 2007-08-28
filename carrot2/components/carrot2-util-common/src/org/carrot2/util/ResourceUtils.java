package org.carrot2.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Resource loading abstraction layer.
 */
public final class ResourceUtils
{
    /**
     * Logger instance. 
     */
    private final static Logger logger = Logger.getLogger(ResourceUtils.class);

    /**
     * @see #getAllMatchingResources(String, Class)
     */
    private static final String LINGO3G_RESOURCES_PROPERTY = "resources.dir";

    /**
     * No instantiation.
     */
    private ResourceUtils()
    {
        // no instances
    }

    /**
     * Scans several locations looking for resources. The following
     * locations are scanned (and in this order):
     * <ol>
     *  <li>resources relative to a dir pointed to by a  
     *     system property named as in {@link #LINGO3G_RESOURCES_PROPERTY} (if present),</li>
     *  <li>resources relative to the current working directory,</li>
     *  <li>resources relative to current thread's context class loader,</li>
     *  <li>resources relative to <code>clazz</code>,</li>
     *  <li>resources relative to <code>clazz</code>'s class loader,</li>
     *  <li>same as above, but with a prefix <code>resources/</code></li>
     * </ol>
     * 
     * @param resourcePath Path of the resource to locate.
     * @param clazz Base class where scanning should be started.
     * @return Returns a list of URLs to matching resources.
     */
    public static URL [] getAllMatchingResources(String resourcePath, Class clazz)
    {
        final ArrayList urls = new ArrayList();
        try
        {
            scanLocations(urls, resourcePath, clazz);

            // For relative resources, re-try with resources/ prefix.
            if (!resourcePath.startsWith("/") && !resourcePath.startsWith("resources/"))
            {
                scanLocations(urls, "resources/" + resourcePath, clazz);
            }

            // Log if needed.
            if (logger.isDebugEnabled())
            {
                final StringBuffer buf = new StringBuffer();
                buf.append("Resource ").append(resourcePath);
                if (urls.size() == 0)
                {
                    buf.append(" not found.");
                }
                else
                {
                    buf.append(" found at: ");
                    for (int i = 0; i < urls.size(); i++)
                    {
                        if (i > 0) buf.append(" ");
                        buf.append(urls.get(i));
                    }
                }
                logger.debug(buf.toString());
            }
        }
        catch (IOException e)
        {
            logger.error("Scanning for resource " + resourcePath + " interrupted by an I/O error.", e);
        }

        return (URL []) urls.toArray(new URL[urls.size()]);
    }

    /**
     * Scan default locations listed 
     * in {@link #getAllMatchingResources(String, Class)}.
     */
    private static void scanLocations(List urls, String resourcePath, Class clazz)
        throws IOException
    {
        // Try system property, if present.
        final String resourceDirPath = System.getProperty(LINGO3G_RESOURCES_PROPERTY);
        if (resourceDirPath != null)
        {
            final File resourceDir = new File(resourceDirPath).getAbsoluteFile();
            if (!resourceDir.isDirectory())
            {
                logger.warn("Resource folder does not exist or is not a directory: "
                    + resourceDir.getAbsolutePath());
            }
            else
            {
                collect(urls, Collections.enumeration(
                    Arrays.asList(getResource(resourcePath, resourceDir))));
            }
        }

        // Try current working directory.
        final File cwd = new File(".").getAbsoluteFile();
        collect(urls, Collections.enumeration(
            Arrays.asList(getResource(resourcePath, cwd))));

        // Try context class loader.
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        collect(urls, cl.getResources(resourcePath));

        if (clazz != null)
        {
            // Then try class-relative resource.
            collect(urls, Collections.enumeration(
                Arrays.asList(new URL [] {clazz.getResource(resourcePath)})));

            // Try the reference class's class loader.
            if (cl != clazz.getClassLoader())
            {
                cl = clazz.getClassLoader();
                collect(urls, cl.getResources(resourcePath));
            }
        }
    }

    /**
     * Collect the resource relative to a given directory. 
     */
    private static URL [] getResource(String resourcePath, File cwd)
    {
        resourcePath = resourcePath.replace('/', File.separatorChar);
        while (resourcePath.startsWith(File.separator))
        {
            resourcePath = resourcePath.substring(1);
        }

        final File resourceFile = new File(cwd, resourcePath);
        if (resourceFile.isFile() && resourceFile.canRead())
        {
            try
            {
                return new URL[] {resourceFile.toURI().toURL()};
            }
            catch (MalformedURLException e)
            {
                logger.warn("Could not construct URL to resource file: " 
                    + resourceFile.getAbsolutePath());
            } 
        }
        return new URL[0];
    }

    /**
     * Opens an {@link InputStream} to the first available resource. 
     *
     * @see #getAllMatchingResources(String, Class)
     */
    public static InputStream getFirst(String resourceName, Class clazz)
        throws IOException
    {
        final URL [] urls = getAllMatchingResources(resourceName, clazz);
        if (urls.length == 0)
        {
            return null;
        }
        else
        {
            logger.info("Opening resource '" + resourceName + "' from: "
                + urls[0].toExternalForm());
        }
        return urls[0].openStream();
    }

    /**
     * Opens an {@link InputStream} to the first available resource. 
     * Class-relative resources are not taken into account. 
     *
     * @see #getAllMatchingResources(String, Class)
     */
    public static InputStream getFirst(String resourceName)
        throws IOException
    {
        return getFirst(resourceName, null);
    }

    /**
     * Adds new unique URLs to the list of <code>urls</code>.
     */
    static void collect(List urls, Enumeration resources)
    {
        while (resources.hasMoreElements()) {
            final URL url = (URL) resources.nextElement();
            if (url != null && !urls.contains(url)) {
                urls.add(url);
            }
        }
    }
}
