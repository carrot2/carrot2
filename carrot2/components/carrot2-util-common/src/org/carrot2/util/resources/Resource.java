package org.carrot2.util.resources;

import java.io.IOException;
import java.io.InputStream;

/**
 * Resource abstraction. Override {@link Object#toString()} to have meaningful logging
 * information at runtime.
 * 
 * @author Dawid Weiss
 */
public interface Resource
{
    /**
     * Open an input stream to the resource.
     */
    public InputStream open() throws IOException;
}
