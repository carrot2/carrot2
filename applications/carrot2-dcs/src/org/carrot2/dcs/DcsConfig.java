/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import java.io.IOException;

import org.carrot2.util.StreamUtils;
import org.carrot2.util.resource.IResource;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

/**
 * Configuration of the Document Clustering Server.
 */
@Root(name = "config")
class DcsConfig
{
    @Attribute(name = "default-results-from-external-source", required = false)
    int defaultResultsFromExternalSource = 50;

    @Attribute(name = "max-results-from-external-source", required = false)
    int maxResultsFromExternalSource = 200;

    @Attribute(name = "access-control-allow-origin", required = false)
    String accessControlAllowOrigin = null;

    static DcsConfig deserialize(IResource configResource) throws Exception
    {
        if (configResource == null)
        {
            throw new IOException("Resource not found.");
        }

        return new Persister().read(DcsConfig.class,
            StreamUtils.prefetch(configResource.open()));
    }
}
