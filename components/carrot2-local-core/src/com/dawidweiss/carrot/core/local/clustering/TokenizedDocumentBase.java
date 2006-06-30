
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.core.local.clustering;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.stachoodev.util.common.*;

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
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocument#getSnippet()
     */
    public TokenSequence getSnippet()
    {
        return (TokenSequence) getProperty(PROPERTY_SNIPPET);
    }
}