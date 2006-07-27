package org.carrot2.input.yahooapi;

import org.carrot2.core.ProcessingException;

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
