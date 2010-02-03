
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * A resource relative to a class. This resource provider caches the content of returned
 * resources and closes the stream handle in {@link #open()}.
 */
public final class ClassResource extends URLResource
{
    private final Class<?> clazz;
    private final String resource;

    public ClassResource(Class<?> clazz, String resource)
    {
        super(clazz.getResource(resource));

        this.clazz = clazz;
        this.resource = resource;
    }

    @Override
    public InputStream open() throws IOException
    {
        return ResourceUtils.prefetch(clazz.getResourceAsStream(resource));
    }
}
