package org.carrot2.util.xslt;

import javax.xml.transform.*;

/**
 * A no-op {@link URIResolver}.
 */
public final class NopURIResolver implements URIResolver
{
    public Source resolve(String href, String base) throws TransformerException
    {
        return null;
    }
}