
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
import org.carrot2.input.etools.*;

/**
 * An input component caching eTools results.
 * 
 * @author Dawid Weiss
 */
public class InputCachedETools extends RawDocumentProducerCacheWrapper {
    public InputCachedETools() {
        super(new EToolsLocalInputComponent(), EToolsLocalInputComponent.class);
    }
}
