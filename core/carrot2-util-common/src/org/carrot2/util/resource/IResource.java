
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

import org.carrot2.util.attribute.IAssignable;

/**
 * Resource abstraction. Override {@link Object#toString()} to have meaningful logging
 * information at runtime.
 */
public interface IResource extends IAssignable
{
    /**
     * Open an input stream to the resource. Specific implementations may cache and close
     * the underlying stream, but such behavior is not required by this interface. Please
     * refer to the documentation of specific implementations for details.
     */
    public InputStream open() throws IOException;
}
