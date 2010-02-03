
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
 * Resource abstraction. Override {@link Object#toString()} to have meaningful logging
 * information at runtime.
 */
public interface IResource
{
    /**
     * Open an input stream to the resource.
     */
    public InputStream open() throws IOException;
}
