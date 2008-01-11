package org.carrot2.source.yahoo;

import java.util.concurrent.Callable;

import org.carrot2.source.SearchRange;

public class PageFetcher implements Callable<SearchResults>
{
    public PageFetcher(SearchRange r)
    {
    }

    @Override
    public SearchResults call() throws Exception
    {
        throw new UnsupportedOperationException();
    }
}
