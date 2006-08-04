package org.carrot2.demo.components;

import org.carrot2.demo.cache.RawDocumentProducerCacheWrapper;
import org.carrot2.input.yahooapi.YahooApiInputComponent;

/**
 * An input component caching YahooAPI results.
 * 
 * @author Dawid Weiss
 */
public class InputCachedYahooAPI extends RawDocumentProducerCacheWrapper {
    public InputCachedYahooAPI() {
        super(new YahooApiInputComponent(), YahooApiInputComponent.class);
    }
}
