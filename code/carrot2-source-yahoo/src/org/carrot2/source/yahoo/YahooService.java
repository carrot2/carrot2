package org.carrot2.source.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.carrot2.core.parameter.Attribute;
import org.carrot2.core.parameter.Bindable;
import org.carrot2.core.parameter.BindingDirection;
import org.carrot2.core.parameter.BindingPolicy;
import org.carrot2.core.parameter.Parameter;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.httpclient.HttpClientFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Sends queries to Yahoo! search service. Instances of this class are thread-safe.
 */
@Bindable
public final class YahooService
{
    private final static Logger logger = Logger.getLogger(YahooService.class);

    private static final Header CONTENT_HEADER = new Header("Content-type",
        "application/x-www-form-urlencoded; charset=UTF-8");

    private static final Header ENCODING_HEADER = new Header("Accept-Encoding", "gzip");

    /**
     * A key for the first result's index.
     * 
     * @see SearchEngineResponse#metadata 
     */
    public static final String FIRST_INDEX_KEY = "firstIndex";

    /**
     * A key for the number of results actually returned.
     * 
     * @see SearchEngineResponse#metadata 
     */
    public static final String RESULTS_RETURNED_KEY = "resultsReturned";

    /**
     * Service location and parameters.
     */
    @Parameter(policy = BindingPolicy.INSTANTIATION)
    public YahooServiceParams serviceParams = new YahooServiceParams();

    /**
     * Number of requests made to the search service (total).
     */
    @Attribute(bindingDirection = BindingDirection.OUT)
    private volatile int requestCount;
    
    /**
     * Sends a Web search query to Yahoo!.
     */
    public SearchEngineResponse query(String query, int start, int results) throws IOException
    {
        // Yahoo's results start from 1.
        start++;
        results = Math.min(results, serviceParams.resultsPerPage);

        final HttpClient client = HttpClientFactory.getTimeoutingClient();
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        InputStream is = null;
        final GetMethod request = new GetMethod();
        try
        {
            request.setURI(new URI(serviceParams.serviceURI, false));
            request.setRequestHeader(CONTENT_HEADER);
            request.addRequestHeader(ENCODING_HEADER);

            final ArrayList<NameValuePair> params = createRequestParams(query, start,
                results);
            params.add(new NameValuePair("output", "xml"));
            request.setQueryString(params.toArray(new NameValuePair [params.size()]));

            logger.info("Request params: " + request.getQueryString());
            requestCount++;
            final int statusCode = client.executeMethod(request);

            // Unwrap compressed streams.
            is = request.getResponseBodyAsStream();
            final Header encoded = request.getResponseHeader("Content-Encoding");
            if (encoded != null && "gzip".equals(encoded.getValue()))
            {
                logger.debug("Unwrapping GZIP compressed stream.");
                is = new GZIPInputStream(is);
            }

            if (statusCode == HttpStatus.SC_OK
                || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE
                || statusCode == HttpStatus.SC_BAD_REQUEST)
            {
                // Parse the data stream.
                final SearchEngineResponse response = parseResponseXML(is);

                if (logger.isDebugEnabled()) {
                    logger.debug("Received, results: " 
                        + response.results.size()
                        + ", total: " + response.getResultsTotal()
                        + ", first: " + response.metadata.get(FIRST_INDEX_KEY));
                }

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
            if (is != null) CloseableUtils.closeIgnoringException(is);
            request.releaseConnection();
        }
    }

    /**
     * Parse the response stream.
     */
    private SearchEngineResponse parseResponseXML(final InputStream is)
        throws IOException
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
        catch (SAXException e)
        {
            final Throwable cause = e.getException();
            if (cause != null && cause instanceof IOException)
            {
                throw (IOException) cause;
            }
            throw new IOException("XML parsing exception: " + e.getMessage(), e);
        }
        catch (ParserConfigurationException e)
        {
            throw new IOException("Could not acquire XML parser.", e);
        }
    }

    /**
     * Assembles an array of {@link NameValuePair} with request parameters.
     */
    private ArrayList<NameValuePair> createRequestParams(String query, int start,
        int results)
    {
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(10);

        params.add(new NameValuePair("query", query));
        params.add(new NameValuePair("start", Integer.toString(start)));
        params.add(new NameValuePair("results", Integer.toString(results)));

        params.add(new NameValuePair("appid", serviceParams.appid));

        if (serviceParams.country != null) params.add(new NameValuePair("country",
            serviceParams.country));

        if (serviceParams.site != null) params.add(new NameValuePair("site",
            serviceParams.site));

        return params;
    }
}
