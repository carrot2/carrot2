package org.carrot2.source.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.httpclient.HttpClientFactory;
import org.xml.sax.*;


/**
 * A superclass shared between Web and News searching services.
 */
@Bindable
abstract class YahooSearchService
{
    /** Logger for this object. */
    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    /** HTTP header for requests. */
    protected static final Header CONTENT_HEADER = new Header("Content-type",
            "application/x-www-form-urlencoded; charset=UTF-8");

    /** HTTP header for declaring allowed GZIP encoding. */
    protected static final Header ENCODING_HEADER = new Header("Accept-Encoding", "gzip");

    /**
     * HTTP header faking Mozilla as the user agent (otherwise compression
     * doesn't work).
     */
    protected static final Header USER_AGENT_HEADER_MOZILLA =
        new Header("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:0.9.7) Gecko/20011221");

    /** */
    public enum QueryType {
        ALL    { @Override
        public String toString() { return "all"; } },
        ANY    { @Override
        public String toString() { return "any"; } },
        PHRASE { @Override
        public String toString() { return "phrase"; } },
    }

    /**
     * Metadata key for the first result's index.
     *
     * @see SearchEngineResponse#metadata
     */
    public static final String FIRST_INDEX_KEY = "firstIndex";

    /**
     * Metadata key for the number of results actually returned.
     *
     * @see SearchEngineResponse#metadata
     */
    public static final String RESULTS_RETURNED_KEY = "resultsReturned";

    /**
     * Metadata key for the compression algorithm used to decompress
     * the returned stream.
     *
     * @see SearchEngineResponse#metadata
     */
    public static final String COMPRESSION_USED_KEY = "compressionUsed";

    /**
     * Application ID required for Yahoo! services.
     *
     * @label Application ID
     */
    @Init
    @Input
    @Attribute
    protected String appid = "carrotsearch";

    /**
     * Maximum number of results returned per page.
     *
     * @label Results Per Page
     */
    @Init
    @Input
    @Attribute
    public int resultsPerPage = 50;

    /**
     * Maximum index of reachable result.
     *
     * @label Maximum Result Index
     */
    @Init
    @Input
    @Attribute
    public int maxResultIndex = 1000;

    /**
     * Number of requests made to this service (total).
     *
     * @label Total Requests
     */
    @Processing
    @Output
    @Attribute
    private int requestCountTotal;

    /**
     * Number of requests made to this service (successful).
     *
     * @label Successful Requests
     */
    @Processing
    @Output
    @Attribute
    private int requestCount;

    /**
     * A sum of all times spent on waiting for response from the
     * service (in milliseconds).
     *
     * @label Total Request Time
     */
    @Processing
    @Output
    @Attribute
    private long requestTimeSum;

    /**
     * Maximum request time.
     *
     * @label Maximum Request Time
     */
    @Processing
    @Output
    @Attribute
    private long requestTimeMax;

    /**
     * Keeps subclasses to this package.
     */
    YahooSearchService()
    {
    }

    /**
     * Prepare an array of {@link NameValuePair} (parameters for
     * the request).
     */
    protected abstract ArrayList<NameValuePair> createRequestParams(
        String query, int start, int results);

    /**
     * @return Return service URI for this service.
     */
    protected abstract String getServiceURI();

    /**
     * Sends a search query to Yahoo! and parses the result.
     */
    protected final SearchEngineResponse query(
        String query, int start, int results)
        throws IOException
    {
        requestCountTotal++;

        // Yahoo's results start from 1.
        start++;
        results = Math.min(results, resultsPerPage);

        final HttpClient client = HttpClientFactory.getTimeoutingClient();
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        InputStream is = null;
        final GetMethod request = new GetMethod();
        final long startTime = System.currentTimeMillis();
        try
        {
            request.setURI(new URI(getServiceURI(), false));
            request.setRequestHeader(CONTENT_HEADER);
            request.setRequestHeader(ENCODING_HEADER);
            request.setRequestHeader(USER_AGENT_HEADER_MOZILLA);

            final ArrayList<NameValuePair> params = createRequestParams(query, start,
                results);
            params.add(new NameValuePair("output", "xml"));
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
                final SearchEngineResponse response = parseResponseXML(is);
                response.metadata.put(COMPRESSION_USED_KEY, compressionUsed);

                if (logger.isDebugEnabled()) {
                    logger.debug("Received, results: "
                        + response.results.size()
                        + ", total: " + response.getResultsTotal()
                        + ", first: " + response.metadata.get(FIRST_INDEX_KEY));
                }

                // Update statistics.
                final long duration = System.currentTimeMillis() - startTime;
                requestCount++;
                requestTimeMax = Math.max(requestTimeMax, duration);
                requestTimeSum += duration;

                return response;
            }
            else
            {
                // Read the output and throw an exception.
                final String m = "Yahoo returned HTTP Error: " + statusCode
                    + ", HTTP payload: " + new String(StreamUtils.readFully(is), "iso8859-1");
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
     * Parse the response stream, assuming it is XML.
     */
    private SearchEngineResponse parseResponseXML(final InputStream is) throws IOException
    {
        try
        {
            final XMLResponseParser parser = new XMLResponseParser();
            final XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();

            reader.setFeature("http://xml.org/sax/features/validation", false);
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setContentHandler(parser);

            reader.parse(new InputSource(is));

            return parser.response;
        }
        catch (final SAXException e)
        {
            final Throwable cause = e.getException();
            if (cause != null && cause instanceof IOException)
            {
                throw (IOException) cause;
            }
            throw new IOException("XML parsing exception: " + e.getMessage());
        }
        catch (final ParserConfigurationException e)
        {
            throw new IOException("Could not acquire XML parser.");
        }
    }

}