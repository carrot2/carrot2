package org.carrot2.launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Launch options for {@link Launcher}.
 * 
 * @author Dawid Weiss
 */
final class LaunchOptions
{
    /**
     * A list of {@link URL}s to classpath locations.
     */
    private final ArrayList urls = new ArrayList();
    
    /**
     * Fully qualified name of the class to launch.
     */
    private String className;
    
    /**
     * Arguments to be passed to the launched class.
     */
    private String [] classArgs;

    /**
     * 
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * 
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * 
     */
    public void setClassArgs(String [] classArgs)
    {
        this.classArgs = classArgs;
    }
    
    /**
     * 
     */
    public String [] getClassArgs()
    {
        return classArgs;
    }
    
    /**
     * Adds a single JAR location to classpath.
     */
    public void addJarLocation(File jarLocation)
    {
        try
        {
            this.urls.add(jarLocation.toURL());
        }
        catch (MalformedURLException e)
        {
            throw new LaunchException("Could not create an URL to: " + jarLocation);
        }
    }

    /**
     * Adds a classpath directory (top folder of packages structure).
     */
    public void addDirLocation(File dirLocation)
    {
        try
        {
            final URL url = dirLocation.toURL();
            final String external = url.toExternalForm();
            if (!external.endsWith("/")) {
                throw new LaunchException("A folder URL should end with a '/'.");
            }
            this.urls.add(url);
        }
        catch (MalformedURLException e)
        {
            throw new LaunchException("Could not create an URL to: " + dirLocation);
        }
    }

    /**
     * @return Returns a list of {@link URL}s to classpath locations.
     */
    public URL [] getClasspathURLs()
    {
        final URL [] urls = (URL []) this.urls.toArray(new URL [this.urls.size()]);
        return urls;
    }
}
