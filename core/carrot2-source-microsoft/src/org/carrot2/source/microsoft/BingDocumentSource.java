
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;

import org.apache.commons.httpclient.*;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.source.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.httpclient.HttpHeaders;
import org.carrot2.util.httpclient.HttpUtils;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * A {@link IDocumentSource} fetching {@link Document}s (search results) from Microsoft
 * Bing API.
 * 
 * @see "http://www.bing.com/developers"
 * @see "http://msdn.microsoft.com/en-us/library/dd251056.aspx"
 */
@Bindable(prefix = "BingDocumentSource", inherit = AttributeNames.class)
public final class BingDocumentSource extends MultipageSearchEngine
{
    /** Application ID assigned to Carrot Search s.c. */
    public final static String CARROTSEARCH_APPID = "EFABBE1342FC43467F3CD65B1B83F450093B16B4";

    /** Logger for this class. */
    private final static Logger logger = LoggerFactory.getLogger(BingDocumentSource.class);

    /**
     * HTTP headers used for the request.
     */
    private List<Header> HTTP_HEADERS = Arrays.asList(new Header []
    {
        HttpHeaders.USER_AGENT_HEADER_MOZILLA
    });

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * Microsoft-assigned application ID for querying the API. Please <strong>generate
     * your own ID</strong> for production deployments and branches off the Carrot2.org's
     * code.
     * 
     * @label Application ID
     * @level Advanced
     * @group Service
     */
    @Init
    @Input
    @Attribute
    @Required
    public String appid = CARROTSEARCH_APPID;

    /**
     * Language and country/region information for the request.
     * 
     * @label Market
     * @group Results filtering
     * @level Basic
     */
    @Input
    @Processing
    @Attribute
    @Required
    public MarketOption market = MarketOption.ENGLISH_UNITED_STATES;

    /**
     * Adult search restriction (porn filter).
     * 
     * @label Safe Search
     * @group Results filtering
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    public AdultOption adult;

    /**
     * Miscellaneous request options. Bing provides the following options:
     * <ul>
     * <li>DisableLocationDetection</li>
     * <li>EnableHighlighting</li>
     * </ul>
     * 
     * <p>Options should be space-separated.</p>
     * 
     * @label Request Options
     * @group Miscellaneous
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @Internal
    public String options = "DisableLocationDetection";

    /**
     * Miscellaneous Web-request specific options. Bing provides the following options:
     * <ul>
     * <li>DisableHostCollapsing</li>
     * <li>DisableQueryAlterations</li>
     * </ul>
     * 
     * <p>Options should be space-separated.</p>
     * 
     * @label Web Request Options
     * @group Miscellaneous
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @Internal
    public String webOptions;

    /**
     * Specify the allowed file types. Space-separated list of file extensions (upper-case). 
     * See <a href="http://msdn.microsoft.com/en-us/library/dd250876%28v=MSDN.10%29.aspx">Bing documentation</a>.
     * 
     * @label File Types
     * @group Results filtering
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @Internal
    public String fileTypes;

    /**
     * Microsoft Live! metadata.
     */
    static final MultipageSearchEngineMetadata metadata 
        = new MultipageSearchEngineMetadata(50, 1000);

    /**
     * Run a single query.
     */
    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata, getSharedExecutor(MAX_CONCURRENT_THREADS, getClass()));
    }
    
    /**
     * Create a single page fetcher for the search range.
     */
    @Override
    protected final Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            public SearchEngineResponse search() throws Exception
            {
                return doSearch(query, bucket.start, bucket.results);
            }
        };
    }

    /**
     * Run the actual single-page search against MSN.
     */
    private final SearchEngineResponse doSearch(String query, int startAt, int totalResultsRequested)
        throws Exception
    {
        final ArrayList<NameValuePair> params = Lists.newArrayList();

        /*
         * Required parameters. Bing API has a very weird syntax for identifying arrays of types,
         * see the manual.
         */

        params.add(new NameValuePair("AppId", appid));
        params.add(new NameValuePair("Query", query));
        params.add(new NameValuePair("Sources", "Web"));
        params.add(new NameValuePair("JsonType", "raw"));

        /*
         * Modify options.
         */
        if (options != null) params.add(new NameValuePair("Options", options));
        if (webOptions != null) params.add(new NameValuePair("Web.Options", webOptions));
        if (fileTypes != null) params.add(new NameValuePair("Web.FileType", fileTypes));

        /*
         * Optional parameters. 
         */
        params.add(new NameValuePair("Web.Offset", Integer.toString(startAt)));
        params.add(new NameValuePair("Web.Count", Integer.toString(totalResultsRequested)));
        params.add(new NameValuePair("Market", market.marketCode));

        if (adult != null) params.add(new NameValuePair("Adult", adult.toString()));

        final String serviceURI = "http://api.bing.net/xml.aspx";
        final HttpUtils.Response response = HttpUtils.doGET(serviceURI, params, HTTP_HEADERS);

        if (response.status == HttpStatus.SC_OK)
        {
            // Parse the data stream.
            final SearchEngineResponse ser = parseResponse(response.getPayloadAsStream());
            ser.metadata.put(SearchEngineResponse.COMPRESSION_KEY, response.compression);

            if (logger.isDebugEnabled())
            {
                logger.debug("Received, results: " + ser.results.size() 
                    + ", total: " + ser.getResultsTotal());
            }

            return ser;
        }
        else
        {
            // Read the output and throw an exception.
            final String m = "Bing returned HTTP Error: " + response.status
                + ", HTTP payload: " + new String(response.payload, "iso8859-1");
            logger.warn(m);
            throw new IOException(m);
        }
    }

    @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
    @Root(name = "WebResult", strict = false)
    public static class WebResult
    {
        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        @Element(name = "Title", required = false)
        public String title;
        
        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        @Element(name = "Description", required = false)
        public String description;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        @Element(name = "Url", required = false)
        public String url;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        @Element(name = "DisplayUrl", required = false)
        public String displayUrl;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        @Element(name = "CacheUrl", required = false)
        public String cacheUrl;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        @Element(name = "DateTime", required = false)
        public String dateTime;
        
        /**
         * Convert to Carrot2 {@link Document}.
         */
        Document toDocument()
        {
            final Document doc = new Document(title, description, url);
            doc.setField(Document.CLICK_URL, displayUrl);
            return doc;
        }
    }

    @Root(name = "Web", strict = false)
    public static class WebResponse 
    {
        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        @Element(name = "Total", required = false)
        public long total;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        @Element(name = "Offset", required = false)
        public long offset;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        @ElementList(type = WebResult.class, name = "Results", required = false)
        public List<WebResult> results;
    }

    @Root(name = "SearchResponse", strict = false)
    @NamespaceList({
        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/element"),
        @Namespace(prefix="web", reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
    })
    public static class BingResponse 
    {
        @org.simpleframework.xml.Attribute(name = "Version", required = true)
        public String version;
        
        @ElementList(entry = "SearchTerms", name = "Query", required = false)
        public List<String> query; 

        @Element(name = "Web", required = false)
        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
        public WebResponse web;
    }

    /**
     * Parse the response.
     */
    private SearchEngineResponse parseResponse(InputStream payloadAsStream)
        throws Exception
    {
        final BingResponse response = new Persister().read(BingResponse.class, payloadAsStream);

        SearchEngineResponse ser = new SearchEngineResponse();
        
        if (response.web != null && response.web.results != null)
        {
            ser.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, response.web.total);
            final LanguageCode langCode = market.toLanguageCode();
            for (WebResult wr : response.web.results)
            {
                final Document doc = wr.toDocument();
                doc.setLanguage(langCode);
                ser.results.add(doc);
            }
        }

        return ser;
    }
}
