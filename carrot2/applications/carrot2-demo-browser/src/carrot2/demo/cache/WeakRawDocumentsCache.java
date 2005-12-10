package carrot2.demo.cache;

import java.util.LinkedList;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent.Result;

/**
 * A cache based on weak references. However, last few requests
 * are always stored (hard references).
 * 
 * @author Dawid Weiss
 */
final class WeakRawDocumentsCache implements RawDocumentsCache {
    private final Logger logger = Logger.getLogger(WeakRawDocumentsCache.class); 

    private final WeakHashMap cache = new WeakHashMap();
    private final LinkedList hardCache;
    private final int hardCacheSize;

    public WeakRawDocumentsCache(int hardCacheSize) {
        this.hardCacheSize = hardCacheSize;
        this.hardCache = new LinkedList();
    }
    
    public synchronized Result get(CacheEntry entry) {
        final Result result = (Result) cache.get(entry); 
        if (result != null) {
            logger.info("Returning cached data for: " + entry);
            updateRecent(entry);
        }
        return result;
    }

    public synchronized void put(CacheEntry entry, Result result) {
        logger.info("Saving cached data for: " + entry);
        cache.put(entry, result);
        updateRecent(entry);
    }

    private void updateRecent(CacheEntry entry) {
        hardCache.remove(entry);
        hardCache.addFirst(entry);
        while (hardCache.size() > hardCacheSize) {
            hardCache.removeLast();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Hard cache: " + hardCache);
        }
    }
}
