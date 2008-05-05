package org.carrot2.source.google;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.source.*;
import org.carrot2.util.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.httpclient.HttpClientFactory;
import org.json.*;

import com.google.common.base.Predicate;

/**
 * A {@link DocumentSource} fetching {@link Document}s (search results) from Microsoft
 * Live!.
 */
@Bindable
public final class GoogleDocumentSource extends SearchEngine
{
    /** Logger for this class. */
    final static Logger logger = Logger.getLogger(GoogleDocumentSource.class);

    /** Application key assigned to <code>demo.carrot2.org</code> */
    public final static String KEY_DEMO_CARROT2_ORG = "ABQIAAAAb0120aE-we0NmwJ6odN2ExR8EGMSxyHQlExRZWWoPZ2MG_MAMhRhVwJV9deByMLLWfBq7h2Y5Clmbw";

    /**
     * Maximum concurrent threads to the API from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * Static executor for running search threads.
     */
    private final static ExecutorService executor = Executors.newFixedThreadPool(
        MAX_CONCURRENT_THREADS,
        contextClassLoaderThreadFactory(GoogleDocumentSource.class.getClassLoader()));

    /**
     * Metadata key for the compression algorithm used to decompress the returned stream.
     * 
     * @see SearchEngineResponse#metadata
     */
    private static final String COMPRESSION_USED_KEY = "compressionUsed";

    /**
     * Google-assigned application key for querying the API.
     * 
     * @see http://code.google.com/apis/ajaxsearch/documentation/#fonje
     */
    @Init
    @Input
    @Attribute
    String key = KEY_DEMO_CARROT2_ORG;

    /**
     * Your site's URI for doing Google API searches.
     */
    @Init
    @Input
    @Attribute
    String referer = "http://demo.carrot2.org";

    /**
     * API version to use.
     * 
     * @see http://code.google.com/apis/ajaxsearch/documentation/#fonje
     */
    @Init
    @Input
    @Attribute
    String version = "1.0";

    @Processing
    @Input
    @Attribute(key = AttributeNames.START)
    int start = 0;

    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    int results = 100;

    @Processing
    @Input
    @Attribute(key = AttributeNames.QUERY)
    @Required
    String query;

    @SuppressWarnings("unused")
    @Processing
    @Output
    @Attribute(key = AttributeNames.RESULTS_TOTAL)
    long resultsTotal;

    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    Collection<Document> documents;

    @Processing
    @Output
    @Attribute
    String compressionUsed;

    /**
     * Maximum number of results returned per page.
     * 
     * @label Results Per Page
     * @level Advanced
     */
    @Init
    @Input
    @Attribute
    public int resultsPerPage = 8;

    /**
     * Maximum index of reachable result.
     * 
     * @label Maximum Result Index
     * @level Advanced
     */
    @Init
    @Input
    @Attribute
    public int maxResultIndex = 32;

    /**
     * Run a single request.
     */
    @Override
    public void process() throws ProcessingException
    {
        // Always run in conservative mode with Google.
        super.searchMode = SearchMode.CONSERVATIVE;

        final SearchEngineResponse [] responses = runQuery(query, start, results,
            maxResultIndex, resultsPerPage, executor);

        if (responses.length > 0)
        {
            // Collect documents from the responses.
            documents = new ArrayList<Document>(Math.min(results, maxResultIndex));
            collectDocuments(documents, responses);

            // Filter out duplicated URLs.
            final Iterator<Document> i = documents.iterator();
            final Predicate<Document> p = new UniqueFieldPredicate(Document.CONTENT_URL);
            while (i.hasNext())
            {
                if (!p.apply(i.next()))
                {
                    i.remove();
                }
            }

            resultsTotal = responses[0].getResultsTotal();
        }
        else
        {
            documents = Collections.<Document> emptyList();
            resultsTotal = 0;
        }
    }

    /**
     * Create a single page fetcher for the search range.
     */
    @Override
    protected final Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new Callable<SearchEngineResponse>()
        {
            public SearchEngineResponse call() throws Exception
            {
                return search(query, bucket.start, bucket.results, referer, key, version);
            }
        };
    }

    /** HTTP header for requests. */
    protected static final Header CONTENT_HEADER = new Header("Content-type",
        "application/x-www-form-urlencoded; charset=UTF-8");

    /** HTTP header for declaring allowed GZIP encoding. */
    protected static final Header ENCODING_HEADER = new Header("Accept-Encoding", "gzip");

    /**
     * Base URI for Web search.
     */
    private static String webSearchServiceURI = "http://ajax.googleapis.com/ajax/services/search/web";

    /**
     * Run the actual search against Google API.
     */
    private final static SearchEngineResponse search(String query, int startAt,
        int resultsRequested, String referer, String key, String version)
        throws IOException
    {
        final HttpClient client = HttpClientFactory.getTimeoutingClient();
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        InputStream is = null;
        final GetMethod request = new GetMethod();
        try
        {
            request.setURI(new URI(webSearchServiceURI, false));
            request.setRequestHeader(CONTENT_HEADER);
            request.setRequestHeader(ENCODING_HEADER);
            request.setRequestHeader("Referer", referer);

            final ArrayList<NameValuePair> params = createRequestParams(query, startAt,
                key, version);

            request.setQueryString(params.toArray(new NameValuePair [params.size()]));

            if (logger.isInfoEnabled())
            {
                logger.info("Request params: " + request.getQueryString());
            }
            final int statusCode = client.executeMethod(request);

            // Unwrap compressed streams.
            is = request.getResponseBodyAsStream();
            final Header encoded = request.getResponseHeader("Content-Encoding");
            final String compressionUsed;
            if (encoded != null && "gzip".equalsIgnoreCase(encoded.getValue()))
            {
                logger.debug("Unwrapping GZIP compressed stream.");
                compressionUsed = "gzip";
                is = new GZIPInputStream(is);
            }
            else
            {
                compressionUsed = "(uncompressed)";
            }

            if (statusCode == HttpStatus.SC_OK
                || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE
                || statusCode == HttpStatus.SC_BAD_REQUEST)
            {
                // Parse the data stream.
                final SearchEngineResponse response = parseResponse(is);
                response.metadata.put(COMPRESSION_USED_KEY, compressionUsed);

                return response;
            }
            else
            {
                // Read the output and throw an exception.
                final String m = "Google returned HTTP Error: " + statusCode
                    + ", HTTP payload: "
                    + new String(StreamUtils.readFully(is), "iso8859-1");
                logger.warn(m);
                throw new IOException(m);
            }
        }
        finally
        {
            if (is != null)
            {
                CloseableUtils.close(is);
            }
            request.releaseConnection();
        }
    }

    /**
     * Assembles an array of {@link NameValuePair} with request parameters.
     */
    protected static ArrayList<NameValuePair> createRequestParams(String query,
        int start, String key, String version)
    {
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(10);

        params.add(new NameValuePair("q", query));
        params.add(new NameValuePair("v", version));
        params.add(new NameValuePair("start", Integer.toString(start)));
        params.add(new NameValuePair("rsz", "large"));

        params.add(new NameValuePair("key", key));

        return params;
    }

    /**
     * Parse the response stream, assuming it is JSON
     */
    private static SearchEngineResponse parseResponse(final InputStream is)
        throws IOException
    {
        final byte [] content = StreamUtils.readFully(is);
        final String jsonString = new String(content, "UTF-8");

        try
        {
            final SearchEngineResponse response = new SearchEngineResponse();

            final JSONObject json = new JSONObject(jsonString);

            if (!"200".equals(json.getString("responseStatus")))
            {
                final String responseStatus = json.getString("responseStatus");
                final String responseDetails = json.getString("responseDetails");

                throw new IOException("Google API error (" + responseStatus + "): "
                    + responseDetails);
            }

            final JSONObject responseData = json.getJSONObject("responseData");

            if (responseData.has("cursor"))
            {
                final JSONObject cursor = responseData.getJSONObject("cursor");
                response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, Long
                    .parseLong(cursor.optString("estimatedResultCount", "0")));
            }
            else
            {
                response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, 0L);
            }

            final List<Document> documents = response.results;
            final JSONArray results = responseData.getJSONArray("results");
            final int len = results.length();
            for (int i = 0; i < len; i++)
            {
                final JSONObject doc = results.getJSONObject(i);

                final String contentUrl = doc.getString("unescapedUrl");
                if (StringUtils.isEmpty(contentUrl)) continue;

                final String title = doc.optString("titleNoFormatting");
                final String summary = StringEscapeUtils.unescapeHtml(doc.optString("content"));
                documents.add(Document.create(title, summary, contentUrl));
            }

            return response;
        }
        catch (JSONException e)
        {
            throw ExceptionUtils.wrapAs(IOException.class, e);
        }
    }
}
