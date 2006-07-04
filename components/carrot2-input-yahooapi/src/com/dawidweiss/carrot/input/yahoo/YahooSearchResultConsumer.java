package com.dawidweiss.carrot.input.yahoo;

import com.dawidweiss.carrot.core.local.ProcessingException;

/**
 * A consumer receiving {@link YahooSearchResult} retrieved
 * from the result.
 *  
 * @author Dawid Weiss
 */
interface YahooSearchResultConsumer {
    public void add(final YahooSearchResult result)
        throws ProcessingException;
}
