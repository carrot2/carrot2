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
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.source.SearchEngineResponse;
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
@Bindable(prefix = "BossSearchService")
public abstract class BossSearchService
{
    /** Logger for this object. */
    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Yahoo BOSS application ID assigned to Carrot2/ Carrot Search. Use your own,
     * please. 
     */
    public final static String CARROTSEARCH_APPID = 
        "txRLTt7V34GgabH9baqIrsnRLuy87i4dQ2kQyok0IIqlUXdw4HmxjE59xhq2_6mT0LM-";
    
    /**
     * Metadata key for the originally returned {@link YSearchResponse}.
     * 
     * @see SearchEngineResponse#metadata
     */
    public static final String YBOSS_RESPONSE = "boss.response";
    
    /**
     * Application ID required for BOSS services. Please <strong>generate your own ID</strong> 
     * for production deployments and branches off the Carrot2.org's code.
     * 
     * @label Application ID
     * @level Advanced
     * @group Service
     */
    @Init
    @Input
    @Attribute
    public String appid = CARROTSEARCH_APPID;

    /**
     * Restricts search results to a set of sites. Must be a comma-separated list of site's domain
     * names, e.g. <code>abc.com,cnn.com</code>.
     * 
     * @group Results filtering
     * @label Domain restriction
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    public String sites;

    /**
     * Restricts search to the specified language and region.
     * Must be a concatenation of constants defined by the 
     * <a href="http://developer.yahoo.com/search/boss/boss_guide/supp_regions_lang.html">
     * language codes supported by the Yahoo Boss API</a>. 
     * <p>
     * The following languages and regions are currently (September 2008) supported:
     * </p>
     * <table>
     *   <thead>
     *     <tr>
     *       <th align="left">Country</th>
     *       <th align="left">Region</th>
     *       <th align="left">Language</th>
     *     </tr>
     *   </thead>
     *   
     *   <tbody>
     *   <tr><td>Argentina</td><td>ar</td> <td>es</td></tr>
     *   <tr><td>Austria</td><td>at</td> <td>de</td></tr>
     *   <tr><td>Australia</td><td>au</td> <td>en</td></tr>
     *   <tr><td>Brazil</td><td>br</td><td>pt</td></tr>
     *   <tr><td>Canada - English</td><td>ca</td><td>en</td></tr>
     *   <tr><td>Canada - French</td><td>ca</td><td>fr</td></tr>
     *   <tr><td>Catalan</td><td>ct</td><td>ca</td></tr>
     *   <tr><td>Chile</td><td>cl</td><td>es</td></tr>
     *   <tr><td>Columbia</td><td>co</td><td>es</td></tr>
     *   <tr><td>Denmark</td><td>dk</td><td>da</td></tr>
     *   <tr><td>Finland</td><td>fi</td><td>fi</td></tr>
     *   <tr><td>Indonesia - English</td><td>id</td><td>en</td></tr>
     *   <tr><td>Indonesia - Indonesian</td><td>id</td><td>id</td></tr>
     *   <tr><td>India</td><td>in</td><td>en</td></tr>
     *   <tr><td>Japan</td><td>jp</td><td>jp</td></tr>
     *   <tr><td>Korea</td><td>kr</td><td>kr</td></tr>
     *   <tr><td>Mexico</td><td>mx</td><td>es</td></tr>
     *   <tr><td>Malaysia - English</td><td>my</td><td>en</td></tr>
     *   <tr><td>Malaysia</td><td>my</td><td>ms</td></tr>
     *   <tr><td>Netherlands</td><td>nl</td><td>nl</td></tr>
     *   <tr><td>Norway</td><td>no</td><td>no</td></tr>
     *   <tr><td>New Zealand</td><td>nz</td><td>en</td></tr>
     *   <tr><td>Peru</td><td>pe</td><td>es</td></tr>
     *   <tr><td>Philippines</td><td>ph</td><td>tl</td></tr>
     *   <tr><td>Philippines - English</td><td>ph</td><td>en</td></tr>
     *   <tr><td>Russia</td><td>ru</td><td>ru</td></tr>
     *   <tr><td>Sweden</td><td>se</td><td>sv</td></tr>
     *   <tr><td>Singapore</td><td>sg</td><td>en</td></tr>
     *   <tr><td>Thailand</td><td>th</td><td>th</td></tr>
     *   <tr><td>Switzerland - German</td><td>ch</td><td>de</td></tr>
     *   <tr><td>Switzerland - French</td><td>ch</td><td>fr</td></tr>
     *   <tr><td>Switzerland - Italian</td><td>ch</td><td>it</td></tr>
     *   <tr><td>German</td><td>de</td><td>de</td></tr>
     *   <tr><td>Spanish</td><td>es</td><td>es</td></tr>
     *   <tr><td>French</td><td>fr</td><td>fr</td></tr>
     *   <tr><td>Italian</td><td>it</td><td>it</td></tr>
     *   <tr><td>United Kingdom</td><td>uk</td><td>en</td></tr>
     *   <tr><td>United States - English</td><td>us</td><td>en</td></tr>
     *   <tr><td>United States - Spanish</td><td>us</td><td>es</td></tr>
     *   <tr><td>Vietnam</td><td>vn</td><td>vi</td></tr>
     *   <tr><td>Venezuela</td><td>ve</td><td>es</td></tr>
     *   </tbody>
     * </table>
     * 
     * <p>Use {@link BossLanguageCodes#getAttributeValue()} to acquire proper constant
     * for this field.</p>
     * 
     * @see BossLanguageCodes
     * 
     * @label Language and Region
     * @level Medium
     * @group Results filtering
     */
    @Input
    @Init
    @Processing
    @Attribute
    public BossLanguageCodes languageAndRegion;

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

            if (languageAndRegion != null)
            {
                try
                {
                    params.add(new NameValuePair("lang", languageAndRegion.langCode));
                    params.add(new NameValuePair("region", languageAndRegion.regionCode));
                }
                catch (IllegalArgumentException e)
                {
                    throw new IOException("Language value: " + languageAndRegion);
                }
            }

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