
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

package org.carrot2.dcs;

import java.io.IOException;
import java.io.InputStream;

import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.IResource;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;

/**
 * Configuration of the Document Clustering Server. This configuration is initialized
 * either from command line arguments (when launched from the console) or from the
 * config.xml file when launched in a Servlet container.
 */
@Root(name = "config")
class DcsConfig
{
    /** DCS application name */
    final static String DCS_APP_NAME = "dcs";

    @Attribute(name = "cache-documents", required = false)
    boolean cacheDocuments = true;

    @Attribute(name = "cache-clusters", required = false)
    boolean cacheClusters = false;

    @Attribute(name = "xslt", required = false)
    String xslt = null;
    
    /**
     * Name of the component suite file with XML data about components and algorithms.
     */
    @Attribute(name = "component-suite-resource", required = true)
    String componentSuiteResource;

    /**
     * Console-only logger.
     */
    final Logger logger = org.slf4j.LoggerFactory.getLogger(DCS_APP_NAME);

    static DcsConfig deserialize(IResource configResource) throws Exception
    {
        if (configResource == null) throw new IOException("Resource not found.");

        final InputStream stream = configResource.open();
        try
        {
            return new Persister().read(DcsConfig.class, stream);
        }
        finally
        {
            CloseableUtils.close(stream);
        }
    }
}
