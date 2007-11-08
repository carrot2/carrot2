package org.carrot2.util.resources;

import java.io.*;
import java.net.URL;


/**
 * 
 */
public class URLResource implements Resource
{
    private final URL url;
    private final String info;

    public URLResource(URL url)
    {
        this.url = url;
        this.info = "[URL: " + url.toExternalForm() + "]";
    }

    public InputStream open() throws IOException
    {
        return url.openStream();
    }

    public String toString() {
        return info;
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj instanceof URLResource) {
            return ((URLResource) obj).info.equals(this.info);
        }
        return false;
    }

    public int hashCode()
    {
        return this.info.hashCode();
    }
}
