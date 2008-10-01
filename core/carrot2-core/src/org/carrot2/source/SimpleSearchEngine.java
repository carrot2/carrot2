package org.carrot2.source;

import org.carrot2.core.DocumentSource;
import org.carrot2.core.ProcessingException;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.Bindable;

/**
 * A base class facilitating implementation of {@link DocumentSource}s wrapping external
 * search engines. This implementation assumes that all requested results can be fetched
 * from the search engine using one request.
 * 
 * @see MultipageSearchEngine
 */
@Bindable
public abstract class SimpleSearchEngine extends SearchEngineBase
{
    /**
     * Requests and returns results from the underlying search engine.
     * 
     * @throws Exception in case of problems with the underlying search engine
     */
    protected abstract SearchEngineResponse fetchSearchResponse() throws Exception;

    @Override
    public void process() throws ProcessingException
    {
        try
        {
            final SearchEngineResponse response = fetchSearchResponse();
            documents = response.results;
            resultsTotal = response.getResultsTotal();
            compressed = false;
            final String compression = (String) response.metadata
                .get(SearchEngineResponse.COMPRESSION_KEY);
            if (compression != null && "gzip".contains(compression))
            {
                compressed = true;
            }
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAs(ProcessingException.class, e);
        }
    }
}
