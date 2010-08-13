package org.carrot2.util.attribute.metadata;

import java.io.InputStream;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * Class path locations lookup.
 */
public interface IClassPathLookup
{
    void init(ProcessingEnvironment e)
        throws Exception;

    InputStream getResourceOrNull(String metadataName);
}
