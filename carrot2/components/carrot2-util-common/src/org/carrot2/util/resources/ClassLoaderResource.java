
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

package org.carrot2.util.resources;

import java.io.*;


/**
 * A resource loaded using a class loader. This resource provider caches 
 * the content of returned resources and closes the stream handle in
 * {@link #open()}.
 */
public final class ClassLoaderResource extends URLResource
{
    private ClassLoader clazzLoader;
    private String resource;

    public ClassLoaderResource(ClassLoader cl, String resource)
    {
        super(cl.getResource(resource));

        this.clazzLoader = cl;
        this.resource = resource;
    }

    public InputStream open() throws IOException
    {
        return ResourceUtils.prefetch(
            clazzLoader.getResourceAsStream(resource));
    }
}
