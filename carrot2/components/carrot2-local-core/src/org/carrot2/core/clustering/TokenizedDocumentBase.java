
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.clustering;

import org.carrot2.core.linguistic.tokens.TokenSequence;
import org.carrot2.util.PropertyProviderBase;

/**
 * An abstract implementation of the {@link TokenizedDocument}interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class TokenizedDocumentBase extends PropertyProviderBase implements TokenizedDocument
{
    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.TokenizedDocument#getSnippet()
     */
    public TokenSequence getSnippet()
    {
        return (TokenSequence) getProperty(PROPERTY_SNIPPET);
    }
}