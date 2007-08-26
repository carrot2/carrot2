
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.components;

import org.carrot2.demo.cache.RawDocumentProducerCacheWrapper;
import org.carrot2.input.solr.SolrLocalInputComponent;

/**
 * An input component caching Solr results.
 *
 * @author Stanislaw Osinski
 */
public class InputCachedSolr extends RawDocumentProducerCacheWrapper {
    public InputCachedSolr() {
        super(new SolrLocalInputComponent(), SolrLocalInputComponent.class);
    }
}
