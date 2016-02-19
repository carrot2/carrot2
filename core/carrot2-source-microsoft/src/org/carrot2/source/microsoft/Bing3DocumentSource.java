
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

package org.carrot2.source.microsoft;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.*;
import org.apache.http.message.BasicNameValuePair;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.source.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.constraint.NotBlank;
import org.carrot2.util.httpclient.HttpRedirectStrategy;
import org.carrot2.util.httpclient.HttpUtils;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.carrot2.shaded.guava.common.base.Strings;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * A base {@link IDocumentSource} sending requests to Bing Search API in Windows Azure
 * Marketplace. We model this into separate subclasses, specific to a particular request
 * type (web, image, news).
 * 
 * <p>Important: there are limits for free use of the above API (beyond which it is a
 * paid service).
 * 
 * @see "https://datamarket.azure.com/dataset/5ba839f1-12ce-4cce-bf57-a49d98d29a44"
 */
@Bindable(prefix = "Bing3DocumentSource", inherit = CommonAttributes.class)
public abstract class Bing3DocumentSource extends MultipageSearchEngine
{
    /**
     * System property key under which Bing app key can be passed (default).
     * You can also override the key via init or runtime attributes of course.
     */
    public static final String SYSPROP_BING3_API = "bing3.key";

    /**
     * Base service URI.
     */
    private final static String SERVICE_URI = "https://api.datamarket.azure.com/Bing/Search";

    /** Logger for this class. */
    private final static Logger logger = LoggerFactory.getLogger(Bing3DocumentSource.class);

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
     * URI suffixes depending on the {@link SourceType}.
     */
    private static final EnumMap<SourceType, String> TYPE_URI_SUFFIXES;

    /**
     * Default timeout.
     */
    private static final int BING_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);

    static {
        TYPE_URI_SUFFIXES = Maps.newEnumMap(SourceType.class);
        TYPE_URI_SUFFIXES.put(SourceType.WEB, "/Web");
        TYPE_URI_SUFFIXES.put(SourceType.IMAGE, "/Image");
        TYPE_URI_SUFFIXES.put(SourceType.NEWS, "/News");
    }

    /**
     * Microsoft-assigned application ID for querying the API. Please <strong>generate
     * your own ID</strong> for production deployments and branches off the Carrot2.org's
     * code. See <a href="https://datamarket.azure.com/dataset/5ba839f1-12ce-4cce-bf57-a49d98d29a44">http://goo.gl/usVXW</a>
     * for more.
     *
     * <p>By default takes the system property's value under key: <code>bing3.key</code>.</p>
     */
    @Init
    @Processing
    @Input
    @Attribute
    @Label("Application API key")
    @Level(AttributeLevel.BASIC)
    @Group(SERVICE)
    @Required
    @NotBlank
    public String appid = System.getProperty(SYSPROP_BING3_API);

    /**
     * Language and country/region information for the request.
     */
    @Input
    @Processing
    @Attribute
    @Label("Market")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.FILTERING)
    public MarketOption market = MarketOption.ENGLISH_UNITED_STATES;

    /**
     * Adult search restriction (porn filter).
     */
    @Processing
    @Input
    @Attribute
    @Label("Safe search")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.FILTERING)
    public AdultOption adult;

    /**
     * Latitude (north/south coordinate). Valid input values range from –90 to 90.
     */
    @Processing
    @Input
    @Attribute
    @Label("Latitude hint")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.FILTERING)
    public Double latitude;

    /**
     * Longitude (east/west coordinate). Valid input values range from –180 to 180.
     */
    @Processing
    @Input
    @Attribute
    @Label("Longitude hint")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.FILTERING)
    public Double longitude;

    /**
     * HTTP redirect response strategy (follow or throw an error).
     */
    @Input
    @Processing
    @Attribute
    @Label("HTTP redirect strategy")
    @Level(AttributeLevel.MEDIUM)
    @Group(SimpleSearchEngine.SERVICE)
    @Internal
    public HttpRedirectStrategy redirectStrategy = HttpRedirectStrategy.NO_REDIRECTS; 

    /**
     * Data source type.
     */
    private final SourceType sourceType;

    /**
     * Initialize with a fixed source type.
     */
    public Bing3DocumentSource(SourceType sourceType)
    {
        this.sourceType = sourceType;
    }
    
    @Override
    protected void process(MultipageSearchEngineMetadata metadata,
        ExecutorService executor) throws ProcessingException
    {
        if (Strings.isNullOrEmpty(appid)) {
            throw new ProcessingException("Bing API requires a key. See "
                + Bing3DocumentSource.class.getSimpleName() + " class documentation.");
        }
        super.process(metadata, executor);
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
     * Run the actual single-page search against Bing API.
     */
    private final SearchEngineResponse doSearch(String query, int startAt, int totalResultsRequested)
        throws Exception
    {
        final ArrayList<NameValuePair> params = Lists.newArrayList();

        // The quotes around the query are obligatory (!).
        params.add(new BasicNameValuePair("Query", stringValue(query)));

        addIfNotEmpty(params, "Adult", stringValue(adult));
        if (market != null) {
            addIfNotEmpty(params, "Market", stringValue(market.marketCode));
        }

        if (latitude != null)
        {
            params.add(new BasicNameValuePair("Latitude", Double.toString(latitude)));
        }

        if (longitude != null)
        {
            params.add(new BasicNameValuePair("Longitude", Double.toString(longitude)));
        }

        /*
         * Append source-specific limits.
         */
        params.add(new BasicNameValuePair("$skip", Integer.toString(startAt)));
        params.add(new BasicNameValuePair("$top", Integer.toString(totalResultsRequested)));

        /*
         * Append source-specific options
         */
        appendSourceParams(params);
        
        /*
         * Perform the request.
         */
        String serviceSuffix = TYPE_URI_SUFFIXES.get(sourceType);
        if (serviceSuffix == null) {
            throw new RuntimeException("Service suffix is null?: " + sourceType);
        }

        HttpUtils.Response response = null;
        for (int retries = 3; retries >= 0; retries--) {
            try
            {
                response = HttpUtils.doGET(
                    SERVICE_URI + serviceSuffix,
                    params, 
                    HTTP_HEADERS,
                    "", appid, 
                    BING_TIMEOUT,
                    redirectStrategy.value());
                break;
            } catch (SSLPeerUnverifiedException e) {
                if (retries == 0) {
                    throw e;
                }
                logger.warn("Bing peer authentication failure, retries: " + retries);
            }
        }

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
            final String m = "Bing returned HTTP Error: " + response.status
                + ", HTTP payload: " + new String(response.payload, "iso8859-1");

            logger.warn(m);

            if (response.status == HttpStatus.SC_SERVICE_UNAVAILABLE &&
                m.contains("Insufficient balance"))
            {
                throw new IOException("Bing API query limit depleted. See Carrot2 FAQ: http://project.carrot2.org/faq.html#bing");
            }

            // Read the output and throw an exception.
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
            if (!Strings.isNullOrEmpty(stringValue))
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

    @Root(name = "feed", strict = false)
    @NamespaceList({
        @Namespace(reference = "http://www.w3.org/2005/Atom"),
        @Namespace(prefix="m", reference = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata"),
        @Namespace(prefix="d", reference = "http://schemas.microsoft.com/ado/2007/08/dataservices"),
        @Namespace(prefix="base", reference = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/Web")
    })
    public static class AtomFeed 
    {
        @ElementList(inline = true, entry = "entry", required = false, type=AtomEntry.class)
        public List<AtomEntry> entries; 
    }

    @Root(strict = false)
    public static class AtomEntry
    {
        @Element(name = "id", required = true)
        public String id;

        @Element(name = "content", required = false)
        public ContentEntry content;
    }
    
    @Root(strict = false)
    public static class ContentEntry
    {
        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
        @Element(name = "properties", required = false)
        public EntryProperties properties;
    }

    @Root(strict = false)
    public static class EntryProperties
    {
        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "Title", required = false)
        public String title;
        
        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "Description", required = false)
        public String description;

        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "DisplayUrl", required = false)
        public String displayUrl;

        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "Url", required = false)
        public String url;

        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "Source", required = false)
        public String source;
        
        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "MediaUrl", required = false)
        public String mediaUrl;
        
        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "SourceUrl", required = false)
        public String sourceUrl;

        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "Thumbnail", required = false)
        public Thumbnail thumbnail;
    }
    
    @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    @Root(name = "Thumbnail", strict = false)
    public static class Thumbnail
    {
        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "MediaUrl", required = false)
        public String mediaUrl;

        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "Width", required = false)
        public int width;

        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "Height", required = false)
        public int height;

        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "ContentType", required = false)
        public String contentType;

        @Namespace(reference = "http://schemas.microsoft.com/ado/2007/08/dataservices")
        @Element(name = "FileSize", required = false)
        public long fileSize;
    }

    /**
     * Parse the response.
     */
    private SearchEngineResponse parseResponse(InputStream payloadAsStream)
        throws Exception
    {
        final AtomFeed response = new Persister().read(AtomFeed.class, payloadAsStream);

        final SearchEngineResponse ser = new SearchEngineResponse();
        final LanguageCode langCode = market != null ? market.toLanguageCode() : null;
        
        if (response.entries != null)
        {
            for (AtomEntry entry : response.entries)
            {
                if (entry == null ||
                    entry.content == null ||
                    entry.content.properties == null)
                {
                    continue;
                }
                final EntryProperties p = entry.content.properties;
                
                final Document doc;
                switch (sourceType)
                {
                    case IMAGE:
                        doc = new Document(p.title, p.description, p.sourceUrl);
                        break;
                    case NEWS:
                        // intentional fall through.
                    case WEB:
                        doc = new Document(p.title, p.description, p.url);
                        break;
                    default:
                        throw new RuntimeException();
                }
                
                doc.setLanguage(langCode);
                if (p.displayUrl != null) doc.setField(Document.CLICK_URL, p.displayUrl);
                if (p.source != null) doc.setField(Document.SOURCES, Arrays.asList(p.source));
                if (p.thumbnail != null && p.thumbnail.mediaUrl != null)
                {
                    doc.setField(Document.THUMBNAIL_URL, p.thumbnail.mediaUrl);
                }
                ser.results.add(doc);
            }
        }

        return ser;
    }
    
    protected static String stringValue(Object v)
    {
        if (v == null || v.toString().trim().isEmpty()) {
            return null;
        }

        return "'" + v + "'";
    }    
}
