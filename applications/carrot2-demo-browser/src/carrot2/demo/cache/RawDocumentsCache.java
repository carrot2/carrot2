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
