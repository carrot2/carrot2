
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

package org.carrot2.input.opensearch;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.clustering.RawDocumentBase;
import org.carrot2.core.clustering.RawDocumentsConsumer;
import org.carrot2.util.StringUtils;
import org.carrot2.util.URLEncoding;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;

/**
 * A wrapper for an Open Search service.
 * 
 * @author Julien Nioche
 */
public class OpenSearchService {
    private final static Logger log = Logger.getLogger(OpenSearchService.class);

    private final String urlService;

    private final FeedFetcher feedFetcher;

    /**
     * An OpenSearch service is initialized with a URLtemplate such as
     * <tt>http://blogs.icerocket.com/search?q={searchTerms}&rss=1&os=1&p={startPage}&n={count}</tt>.
     */
    public OpenSearchService(String URLTemplate) {
        urlService = URLTemplate;
        this.feedFetcher = new HttpURLFeedFetcher();
    }

    /**
     * Searches an OpenSearch Service and retrieves a maximum of
     * <code>requestedResults</code> snippets. May throw an exception if
     * service is no longer available.
     * 
     * @throws IOException If an I/O exception occurred.
     * @throws FetcherException
     * @throws FeedException
     * @throws IllegalArgumentException
     * @throws ProcessingException 
     */
    public OpenSearchResult [] query(final String query,
        final int requestedResults, RawDocumentsConsumer documentConsumer)
        throws IOException, IllegalArgumentException, FeedException,
        FetcherException, ProcessingException
    {

        final ArrayList result = new ArrayList(requestedResults);

        // convert the query to a UTF-8 URL
        final String codedQuery = URLEncoding.encode(query, "UTF-8");

        // builds queries until the number of results if found
        // or there are no more results available
        int pagenumber = 1;

        // results counter
        int resultIndex = 1;

fetch:  while (true) {
            // builds the URL by replacing the variables in the template
            String feedUrl = urlService.replaceFirst("\\{searchTerms\\}", codedQuery);

            // check whether the site supports startPage
            // otherwise we'll rely on count to get the results
            final boolean supportsPaging = (urlService.indexOf("startPage") != -1);
            if (supportsPaging) {
                // specify the page number in the query
                feedUrl = feedUrl.replaceFirst("\\{startPage.?\\}", 
                    Integer.toString(pagenumber));
            }

            // check whether the site supports startIndex
            // otherwise we'll rely on count to get the results
            final boolean supportsIndex = (urlService.indexOf("startIndex") != -1);
            if (supportsIndex) {
                // specify the index number in the query
                feedUrl = feedUrl.replaceFirst("\\{startIndex.?\\}", 
                    Integer.toString(resultIndex));
            }

            // specify the number of results in the page
            feedUrl = feedUrl.replaceFirst("\\{count.?\\}", "" + requestedResults);
            log.info("Fetching: " + feedUrl);

            // reads the Feed
            final SyndFeed feed = feedFetcher.retrieveFeed(new URL(feedUrl));
            final List entries = feed.getEntries();

            if (entries.size() == 0) {
                break fetch;
            }

            // Convert the entries into OpenSearchResults
            for (int entry = 0; entry < entries.size(); entry++) {
                final SyndEntry sy = (SyndEntry) entries.get(entry);
                final String syURL = sy.getLink();
                final String syTITLE = sy.getTitle();
                final String syDESCRIPTION = sy.getDescription().getValue();
                final OpenSearchResult current = new OpenSearchResult(syURL, syTITLE, syDESCRIPTION);
                result.add(current);
                final int id = resultIndex;
                resultIndex++;
                
                if (documentConsumer != null) {
                    documentConsumer.addDocument(new RawDocumentBase(current.url, 
                        StringUtils.removeMarkup(current.title), 
                        StringUtils.removeMarkup(current.summary)){
                        public Object getId() {
                            return Integer.toString(id);
                        }
                    });
                }
                
                // Check that we did not exceed the number of results.
                if (result.size() >= requestedResults) {
                    break fetch;
                }
            }

            if (!supportsPaging && !supportsIndex) {
                break fetch;
            }

            pagenumber++;
        }

        log.info("Found: " + result.size() + " results for " + query);
        return (OpenSearchResult[]) result.toArray(new OpenSearchResult[result.size()]);
    }
}
