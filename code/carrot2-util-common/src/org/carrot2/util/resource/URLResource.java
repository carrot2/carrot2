
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resource;

import java.io.IOException;
import java.io.InputStream;
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
