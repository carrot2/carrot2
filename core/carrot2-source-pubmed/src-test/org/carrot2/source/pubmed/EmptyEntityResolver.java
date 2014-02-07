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
