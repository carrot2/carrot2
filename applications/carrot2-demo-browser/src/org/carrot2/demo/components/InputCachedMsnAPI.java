
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

import org.carrot2.demo.cache.RawDocumentProducerCacheWrapper;
import org.carrot2.input.msnapi.MsnApiInputComponent;

/**
 * An input component caching YahooAPI results.
 * 
 * @author Dawid Weiss
 */
public class InputCachedMsnAPI extends RawDocumentProducerCacheWrapper {
    public InputCachedMsnAPI() {
        super(new MsnApiInputComponent(), MsnApiInputComponent.class);
    }
}
