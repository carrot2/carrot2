package org.carrot2.source.boss;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.carrot2.core.attribute.Init;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.boss.data.YSearchResponse;
import org.carrot2.util.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.httpclient.HttpClientFactory;
import org.carrot2.util.httpclient.HttpHeaders;
import org.carrot2.util.resource.ParameterizedUrlResource;
import org.simpleframework.xml.load.Persister;

import com.google.common.collect.Maps;

/**
 * A superclass shared between various Boss verticals.
 */
@Bindable
public abstract class BossSearchService
{
    /** Logger for this object. */
    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Metadata key for the originally returned {@link YSearchResponse}.
     * 
     * @see SearchEngineResponse#metadata
     */
    public static final String YBOSS_RESPONSE = "boss.response";
    
    /**
     * Application ID required for BOSS services. <b>Generate your own ID if you are
     * making a branch off the Carrot2.org's code, please</b>.
     * 
     * @label Application ID
     * @level Advanced
     */
    @Init
    @Input
    @Attribute
    public String appid = "txRLTt7V34GgabH9baqIrsnRLuy87i4dQ2kQyok0IIqlUXdw4HmxjE59xhq2_6mT0LM-";

    /**
     * Comma-separated list of sites to query. For example: <code>abc.com,cnn.com</code>.
     * 
     * @group Results filtering
     * @label Domain restriction
     * @level Medium
     */
    @Init
    @Input
    @Attribute
    public String sites;

    // http://developer.yahoo.com/search/boss/boss_guide/supp_regions_lang.html
    // TODO: CARROT-383 Add support for language selection based on the LanguageCode parameter?
    /*
     * public String lang;
     * public String region;
     */

    /**
     * BOSS engine current metadata.
     */
    protected MultipageSearchEngineMetadata metadata = DEFAULT_METADATA;

    /**
     * BOSS engine default metadata.
     */
    final static MultipageSearchEngineMetadata DEFAULT_METADATA = new MultipageSearchEngineMetadata(
        50, 1000);

    /**
     * Keep subclasses to this package.
     */
    BossSearchService()
    {
    }

    /**
     * Prepare an array of {@link NameValuePair} (parameters for the request).
     */
    protected abstract ArrayList<NameValuePair> createRequestParams(String query,
        int start, int results);

    /**
     * @return Return service URI for this service.
     */
    protected abstract String getServiceURI();

    /**
     * Sends a search query to Yahoo! and parses the result.
     */
    protected final SearchEngineResponse query(String query, int start, int results)
        throws IOException
    {
        results = Math.min(results, metadata.resultsPerPage);

        final HttpClient client = HttpClientFactory.getTimeoutingClient();
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        InputStream is = null;
        final GetMethod request = new GetMethod();
        try
        {
            final Map<String, Object> attributes = Maps.newHashMap();
            attributes.put("query", query);
            final String serviceURI = ParameterizedUrlResource.substituteAttributes(
                attributes, getServiceURI());

            request.setURI(new URI(serviceURI, false));
            request.setRequestHeader(HttpHeaders.URL_ENCODED);
            request.setRequestHeader(HttpHeaders.GZIP_ENCODING);
            request.setRequestHeader(HttpHeaders.USER_AGENT_HEADER_MOZILLA);

            final ArrayList<NameValuePair> params = createRequestParams(query, start,
                results);

            params.add(new NameValuePair("appid", appid));
            params.add(new NameValuePair("start", Integer.toString(start)));
            params.add(new NameValuePair("count", Integer.toString(results)));
            params.add(new NameValuePair("format", "xml"));
            params.add(new NameValuePair("sites", sites));

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
                response.metadata.put(SearchEngineResponse.COMPRESSION_KEY,
                    compressionUsed);

                if (logger.isDebugEnabled())
                {
                    logger.debug("Received, results: " + response.results.size()
                        + ", total: " + response.getResultsTotal());
                }

                return response;
            }
            else
            {
                // Read the output and throw an exception.
                final String m = "BOSS returned HTTP Error: " + statusCode
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
     * Parse the response stream.
     */
    private static SearchEngineResponse parseResponseXML(final InputStream is)
        throws IOException
    {
        try
        {
            final SearchEngineResponse response = new SearchEngineResponse();
            final YSearchResponse yresponse = new Persister().read(YSearchResponse.class,
                is);

            response.metadata.put(YBOSS_RESPONSE, yresponse);
            yresponse.populate(response);

            return response;
        }
        catch (Exception e0)
        {
            throw ExceptionUtils.wrapAs(IOException.class, e0);
        }
    }
}