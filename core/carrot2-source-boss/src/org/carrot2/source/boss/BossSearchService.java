
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

package org.carrot2.source.boss;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.httpclient.*;
import org.carrot2.core.attribute.*;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.httpclient.HttpHeaders;
import org.carrot2.util.httpclient.HttpUtils;
import org.carrot2.util.resource.URLResourceWithParams;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;

import com.google.common.collect.Maps;

/**
 * A superclass shared between various Boss verticals.
 */
@Bindable(prefix = "BossSearchService")
public abstract class BossSearchService
{
    /** Logger for this object. */
    protected final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass()
        .getName());

    /**
     * Yahoo BOSS application ID assigned to Carrot2/ Carrot Search. Use your own, please.
     */
    public final static String CARROTSEARCH_APPID = "txRLTt7V34GgabH9baqIrsnRLuy87i4dQ2kQyok0IIqlUXdw4HmxjE59xhq2_6mT0LM-";

    /**
     * Metadata key for the originally returned {@link YSearchResponse}.
     * 
     * @see SearchEngineResponse#metadata
     */
    public static final String YBOSS_RESPONSE = "boss.response";

    /**
     * Application ID required for BOSS services. Please <strong>generate your own
     * ID</strong> for production deployments and branches off the Carrot2.org's code.
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
     * Restricts search results to a set of sites. Must be a comma-separated list of
     * site's domain names, e.g. <code>abc.com,cnn.com</code>.
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
     * Restricts search to the specified language and region. Must be a concatenation of
     * constants defined by the <a
     * href="http://developer.yahoo.com/search/boss/boss_guide/supp_regions_lang.html">
     * language codes supported by the Yahoo Boss API</a>.
     * <p>
     * The following languages and regions are currently (July 2009) supported:
     * </p>
     * <table>
     * <thead>
     * <tr>
     * <th align="left">Country</th>
     * <th align="left">Region</th>
     * <th align="left">Language</th>
     * </tr>
     * </thead> <tbody>
     * <tr>
     * <td>Argentina</td>
     * <td>ar</td>
     * <td>es</td>
     * </tr>
     * <tr>
     * <td>Austria</td>
     * <td>at</td>
     * <td>de</td>
     * </tr>
     * <tr>
     * <td>Australia</td>
     * <td>au</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>Brazil</td>
     * <td>br</td>
     * <td>pt</td>
     * </tr>
     * <tr>
     * <td>Canada - English</td>
     * <td>ca</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>Canada - French</td>
     * <td>ca</td>
     * <td>fr</td>
     * </tr>
     * <tr>
     * <td>Catalan</td>
     * <td>ct</td>
     * <td>ca</td>
     * </tr>
     * <tr>
     * <td>Chile</td>
     * <td>cl</td>
     * <td>es</td>
     * </tr>
     * <tr>
     * <td>Columbia</td>
     * <td>co</td>
     * <td>es</td>
     * </tr>
     * <tr>
     * <td>Czech Republic</td>
     * <td>cz</td>
     * <td>cs</td>
     * </tr>
     * <tr>
     * <td>Denmark</td>
     * <td>dk</td>
     * <td>da</td>
     * </tr>
     * <tr>
     * <td>Finland</td>
     * <td>fi</td>
     * <td>fi</td>
     * </tr>
     * <tr>
     * <td>Hong Kong</td>
     * <td>hk</td>
     * <td>tzh</td>
     * </tr>
     * <tr>
     * <td>Hungary Hungary</td>
     * <td>hu</td>
     * <td>hu</td>
     * </tr>
     * <tr>
     * <td>Indonesia - English</td>
     * <td>id</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>Indonesia - Indonesian</td>
     * <td>id</td>
     * <td>id</td>
     * </tr>
     * <tr>
     * <td>Israel</td>
     * <td>il</td>
     * <td>he</td>
     * </tr>
     * <tr>
     * <td>India</td>
     * <td>in</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>Japan</td>
     * <td>jp</td>
     * <td>jp</td>
     * </tr>
     * <tr>
     * <td>Korea</td>
     * <td>kr</td>
     * <td>kr</td>
     * </tr>
     * <tr>
     * <td>Mexico</td>
     * <td>mx</td>
     * <td>es</td>
     * </tr>
     * <tr>
     * <td>Malaysia - English</td>
     * <td>my</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>Malaysia</td>
     * <td>my</td>
     * <td>ms</td>
     * </tr>
     * <tr>
     * <td>Netherlands</td>
     * <td>nl</td>
     * <td>nl</td>
     * </tr>
     * <tr>
     * <td>Norway</td>
     * <td>no</td>
     * <td>no</td>
     * </tr>
     * <tr>
     * <td>New Zealand</td>
     * <td>nz</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>Peru</td>
     * <td>pe</td>
     * <td>es</td>
     * </tr>
     * <tr>
     * <td>Philippines</td>
     * <td>ph</td>
     * <td>tl</td>
     * </tr>
     * <tr>
     * <td>Philippines - English</td>
     * <td>ph</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>Russia</td>
     * <td>ru</td>
     * <td>ru</td>
     * </tr>
     * <tr>
     * <td>Romania</td>
     * <td>ro</td>
     * <td>ro</td>
     * </tr>
     * <tr>
     * <td>Sweden</td>
     * <td>se</td>
     * <td>sv</td>
     * </tr>
     * <tr>
     * <td>Singapore</td>
     * <td>sg</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>Taiwan</td>
     * <td>tw</td>
     * <td>tzh</td>
     * </tr>
     * <tr>
     * <td>Thailand</td>
     * <td>th</td>
     * <td>th</td>
     * </tr>
     * <tr>
     * <td>Turkey</td>
     * <td>tr</td>
     * <td>tr</td>
     * </tr>
     * <tr>
     * <td>Switzerland - German</td>
     * <td>ch</td>
     * <td>de</td>
     * </tr>
     * <tr>
     * <td>Switzerland - French</td>
     * <td>ch</td>
     * <td>fr</td>
     * </tr>
     * <tr>
     * <td>Switzerland - Italian</td>
     * <td>ch</td>
     * <td>it</td>
     * </tr>
     * <tr>
     * <td>German</td>
     * <td>de</td>
     * <td>de</td>
     * </tr>
     * <tr>
     * <td>Spanish</td>
     * <td>es</td>
     * <td>es</td>
     * </tr>
     * <tr>
     * <td>French</td>
     * <td>fr</td>
     * <td>fr</td>
     * </tr>
     * <tr>
     * <td>Italian</td>
     * <td>it</td>
     * <td>it</td>
     * </tr>
     * <tr>
     * <td>United Kingdom</td>
     * <td>uk</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>United States - English</td>
     * <td>us</td>
     * <td>en</td>
     * </tr>
     * <tr>
     * <td>United States - Spanish</td>
     * <td>us</td>
     * <td>es</td>
     * </tr>
     * <tr>
     * <td>Vietnam</td>
     * <td>vn</td>
     * <td>vi</td>
     * </tr>
     * <tr>
     * <td>Venezuela</td>
     * <td>ve</td>
     * <td>es</td>
     * </tr>
     * </tbody>
     * </table>
     * <p>
     * Use {@link BossLanguageCodes#getAttributeValue()} to acquire proper constant for
     * this field.
     * </p>
     * 
     * @see BossLanguageCodes
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

        final ArrayList<NameValuePair> params = createRequestParams(query, start, results);
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

        final String serviceURI = substituteAttributes(
            getServiceURI(), new NameValuePair("query", query));

        final HttpUtils.Response response = HttpUtils.doGET(serviceURI, params, Arrays
            .asList(new Header []
            {
                HttpHeaders.USER_AGENT_HEADER_MOZILLA
            }));

        final int statusCode = response.status;

        if (statusCode == HttpStatus.SC_OK
            || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE
            || statusCode == HttpStatus.SC_BAD_REQUEST)
        {
            // Parse the data stream.
            final SearchEngineResponse ser = parseResponseXML(response
                .getPayloadAsStream());
            ser.metadata.put(SearchEngineResponse.COMPRESSION_KEY, response.compression);

            if (logger.isDebugEnabled())
            {
                logger.debug("Received, results: " + ser.results.size() + ", total: "
                    + ser.getResultsTotal());
            }

            return ser;
        }
        else
        {
            // Read the output and throw an exception.
            final String m = "BOSS returned HTTP Error: " + statusCode
                + ", HTTP payload: " + new String(response.payload, "iso8859-1");
            logger.warn(m);
            throw new IOException(m);
        }
    }

    /**
     * Performs attribute substitution.
     */
    private static String substituteAttributes(String parameterizedURL,
        NameValuePair... pairs)
    {
        final HashMap<String, Object> attributes = Maps.newHashMap();
        for (NameValuePair nameValue : pairs)
        {
            attributes.put(nameValue.getName(), nameValue.getValue());
        }
        return URLResourceWithParams.substituteAttributes(parameterizedURL, attributes);
    }

    /**
     * Parse the response stream.
     */
    private SearchEngineResponse parseResponseXML(final InputStream is)
        throws IOException
    {
        try
        {
            final SearchEngineResponse response = new SearchEngineResponse();
            final YSearchResponse yresponse = new Persister().read(YSearchResponse.class,
                is);

            response.metadata.put(YBOSS_RESPONSE, yresponse);
            yresponse.populate(response, languageAndRegion != null ? languageAndRegion
                .toLanguageCode() : null);

            return response;
        }
        catch (Exception e0)
        {
            throw ExceptionUtils.wrapAs(IOException.class, e0);
        }
    }
}
