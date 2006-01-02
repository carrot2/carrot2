
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package carrot2.demo.cache;

import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent.Result;

/**
 * A cache for storing results of
 * {@link com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer}.
 *   
 * @author Dawid Weiss
 */
interface RawDocumentsCache {
    public Result get(CacheEntry entry);
    public void put(CacheEntry entry, Result result);
}
