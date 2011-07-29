
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;

import org.apache.http.*;
import org.apache.http.message.BasicNameValuePair;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.source.MultipageSearchEngine;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.httpclient.HttpUtils;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * A base {@link IDocumentSource} serving requests to Microsoft Bing API, version 2. We model
 * this into separate subclasses, specific to a particular request type (web, image, news). In
 * theory one could request web and news results at once, but in practice this is difficult
 * when paging is also needed (because they have different limits).
 * 
 * @see "http://msdn.microsoft.com/en-us/library/dd251056.aspx"
 */
@Bindable(prefix = "Bing2DocumentSource", inherit = CommonAttributes.class)
public abstract class Bing2DocumentSource extends MultipageSearchEngine
{
    /** 
     * Application ID assigned to Carrot Search s.c.
     * 
     * <b>Please use your own if you plan to use Bing with Carrot2.</b>  
     */
    public final static String CARROTSEARCH_APPID = "2679A5C568DC48B57628500D4024F83AD859633C";

    /**
     * Bing2 service URI.
     */
    private final static String SERVICE_URI = "http://api.bing.net/xml.aspx";

    /** Logger for this class. */
    private final static Logger logger = LoggerFactory.getLogger(Bing2DocumentSource.class);

    /**
     * HTTP headers used for the request.
     */
    private List<Header> HTTP_HEADERS = Arrays.asList(new Header []
    {
    });

    /**
     * Maximum concurrent threads from all instances of this subcomponents
     * sending requests to Bing.
     */
    protected static final int MAX_CONCURRENT_THREADS = 10;

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
     * Data source type.
     */
    private final SourceType sourceType;

    /**
     * Initialize with a fixed source type.
     */
    public Bing2DocumentSource(SourceType sourceType)
    {
        this.sourceType = sourceType;
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
     * Run the actual single-page search against Bing2 API.
     */
    private final SearchEngineResponse doSearch(String query, int startAt, int totalResultsRequested)
        throws Exception
    {
        final ArrayList<NameValuePair> params = Lists.newArrayList();

        /*
         * Required parameters. Bing API has a very weird syntax for identifying arrays of types,
         * see the manual.
         */
        final String sourceType = this.sourceType.toString();

        params.add(new BasicNameValuePair("AppId", appid));
        params.add(new BasicNameValuePair("Sources", sourceType));
        params.add(new BasicNameValuePair("Version", "2.2"));
        params.add(new BasicNameValuePair("Query", query));

        addIfNotEmpty(params, "Adult", adult);
        addIfNotEmpty(params, "Options", options);
        addIfNotEmpty(params, "Market", market.marketCode);

        /*
         * Append source-specific limits.
         */
        params.add(new BasicNameValuePair(sourceType + ".Offset", Integer.toString(startAt)));
        params.add(new BasicNameValuePair(sourceType + ".Count", Integer.toString(totalResultsRequested)));

        /*
         * Append source-specific options
         */
        appendSourceParams(params);
        
        /*
         * Perform the request.
         */
        final HttpUtils.Response response = HttpUtils.doGET(SERVICE_URI, params, HTTP_HEADERS);
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

    /**
     * Add a parameter if argument is not an empty string.
     */
    protected void addIfNotEmpty(ArrayList<NameValuePair> params, String paramName,
        Object value)
    {
        if (value != null)
        {
            String stringValue = value.toString();
            if (!isNullOrEmpty(stringValue))
            {
                params.add(new BasicNameValuePair(paramName, stringValue));
            }
        }
    }

    /**
     * Make this abstract so that subclasses override.
     */
    @Override
    public abstract void process() throws ProcessingException;
    
    /**
     * Append any source-specific parameters. 
     */
    protected void appendSourceParams(ArrayList<NameValuePair> params)
    {
        // Do nothing.
    }

    /*
     * Output model for deserialization using simple-xml.
     */
    interface IAdaptableToDocument
    {
        Document toDocument();
    }

    @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web")
    @Root(name = "WebResult", strict = false)
    public static class WebResult implements IAdaptableToDocument
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
        public Document toDocument()
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

    @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
    @Root(name = "NewsResult", strict = false)
    public static class NewsResult implements IAdaptableToDocument
    {
        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
        @Element(name = "Title", required = false)
        public String title;
        
        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
        @Element(name = "Source", required = false)
        public String source;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
        @Element(name = "Url", required = false)
        public String url;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
        @Element(name = "Snippet", required = false)
        public String description;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
        @Element(name = "Date", required = false)
        public String dateTime;

        /**
         * Convert to Carrot2 {@link Document}.
         */
        public Document toDocument()
        {
            final Document doc = new Document(title, description, url);
            if (source != null) doc.setField(Document.SOURCES, Arrays.asList(source));
            return doc;
        }
    }

    @Root(name = "News", strict = false)
    public static class NewsResponse
    {
        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
        @Element(name = "Total", required = false)
        public long total;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
        @Element(name = "Offset", required = false)
        public long offset;

        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
        @ElementList(type = NewsResult.class, name = "Results", required = false)
        public List<NewsResult> results;
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
        
        @Element(name = "News", required = false)
        @Namespace(reference = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news")
        public NewsResponse news;
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
            pushAsDocuments(ser, langCode, response.web.results);
        }

        if (response.news != null && response.news.results != null)
        {
            ser.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, response.news.total);
            final LanguageCode langCode = market.toLanguageCode();
            pushAsDocuments(ser, langCode, response.news.results);
        }

        return ser;
    }

    private void pushAsDocuments(SearchEngineResponse ser, final LanguageCode langCode,
        List<? extends IAdaptableToDocument> results)
    {
        for (IAdaptableToDocument wr : results)
        {
            final Document doc = wr.toDocument();
            doc.setLanguage(langCode);
            ser.results.add(doc);
        }
    }
}
