
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
import java.util.jar.JarFile;

import org.carrot2.util.StreamUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

/**
 * This class opens a connection to a resource pointed to by an URI. Note that JAR
 * resources <b>should not</b> be accessed this way because the default handler caches
 * {@link JarFile} instances and thus locks the file. This resource provider caches the
 * content of returned resources and closes the underlying stream handle in
 * {@link #open()}.
 * 
 * @see <a href="http://issues.carrot2.org/browse/CARROT-143">Issue CARROT-143</a>
 */
@Root(name = "url-resource")
public class URLResource implements IResource
{
    /**
     * URL for the resource.
     */
    private URL url;

    /**
     * URL string, for serialization only.
     */
    @Attribute(name = "url")
    private String info;

    /**
     * For XML serialization/deserialization only, use {@link #URLResource(URL)}.
     */
    URLResource()
    {
    }

    public URLResource(URL url)
    {
        this.url = url;
        this.info = url.toExternalForm();
    }

    public InputStream open() throws IOException
    {
        return StreamUtils.prefetch(url.openStream());
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
        if (obj instanceof URLResource)
        {
            return ((URLResource) obj).info.equals(this.info);
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
}
