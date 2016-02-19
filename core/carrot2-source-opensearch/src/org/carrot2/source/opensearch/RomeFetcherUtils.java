
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.opensearch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.carrot2.core.Document;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.StringUtils;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.io.FeedException;

/**
 * Utility methods for working with Rome fetcher.
 */
public class RomeFetcherUtils
{
    /**
     * Fetches an OpenSearch feed from the provided URL and returns the entries as Carrot2
     * {@link SearchEngineResponse}.
     * 
     * @param url the OpenSearch feed to fetch
     * @param feedFetcher Rome fetcher to use
     * @return {@link SearchEngineResponse} containing entries from the feed
     */
    @SuppressWarnings("rawtypes")
    public static SearchEngineResponse fetchUrl(final String url, FeedFetcher feedFetcher)
        throws IOException, FeedException, FetcherException, MalformedURLException
    {
        /*
         * TODO: Rome fetcher uses SUN's HttpClient and opens a persistent HTTP connection
         * (background thread that keeps reference to the class loader). This causes minor
         * memory leaks when reloading Web applications. Consider: 1) patching rome
         * fetcher sources and adding Connection: close to request headers, 2) using
         * Apache HttpClient, 3) using manual fetch of the syndication feed.
         */
        final SyndFeed feed = feedFetcher.retrieveFeed(new URL(url));
        final SearchEngineResponse response = new SearchEngineResponse();

        // The documentation does not mention that null value can be returned
        // but we've seen a NPE here:
        // http://builds.carrot2.org/browse/C2HEAD-SOURCES-4.
        if (feed != null)
        {
            final List entries = feed.getEntries();
            for (Iterator it = entries.iterator(); it.hasNext();)
            {
                final SyndEntry entry = (SyndEntry) it.next();
                final Document document = new Document();

                document.setField(Document.TITLE, clean(entry.getTitle()));
                document.setField(Document.SUMMARY, clean(entry.getDescription()
                    .getValue()));
                document.setField(Document.CONTENT_URL, entry.getLink());

                response.results.add(document);
            }
        }

        return response;
    }

    private static String clean(String string)
    {
        return StringUtils.removeHtmlTags(StringEscapeUtils.unescapeHtml(string));
    }
}
