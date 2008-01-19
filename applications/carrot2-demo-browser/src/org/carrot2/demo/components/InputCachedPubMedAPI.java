
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

package org.carrot2.demo.components;

import org.carrot2.demo.cache.*;
import org.carrot2.input.pubmed.*;


/**
 * An input component caching PubMed results.
 * 
 * @author Stanislaw Osinski
 */
public class InputCachedPubMedAPI
    extends RawDocumentProducerCacheWrapper
{
    public InputCachedPubMedAPI()
    {
        super(new PubMedInputComponent(), PubMedInputComponent.class);
    }
}
