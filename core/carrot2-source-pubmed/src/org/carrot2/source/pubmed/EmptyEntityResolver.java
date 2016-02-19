
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

package org.carrot2.source.pubmed;

import java.io.IOException;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class EmptyEntityResolver implements EntityResolver
{
    Logger logger = LoggerFactory.getLogger(EmptyEntityResolver.class);

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException
    {
        if (logger.isDebugEnabled()) {
            logger.debug("Skipping entity resolution: " + publicId + ", " + systemId);
        }
        return new InputSource(new StringReader(""));
    }
}
