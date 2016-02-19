
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
