
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
