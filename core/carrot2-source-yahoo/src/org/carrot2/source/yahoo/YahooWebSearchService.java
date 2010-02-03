
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

package org.carrot2.source.yahoo;

import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ValueHintEnum;

/**
 * Sends queries to Yahoo! Web search service. Instances of this class are thread-safe.
 * <p>
 * Attributes of this class correspond to Yahoo's documentation (see links below).
 * 
 * @label Yahoo Web Search Service
 * @see <a href="http://developer.yahoo.com/search/web/V1/webSearch.html">Yahoo Web Search
 *      Documentation</a>
 */
@Bindable(prefix = "YahooWebSearchService")
public final class YahooWebSearchService extends YahooSearchService
{
    /**
     * Yahoo! service URI to be queried.
     * 
     * @label Service URI
     * @level Advanced
     * @see <a href="http://api.search.yahoo.com/WebSearchService/V1/webSearch">Yahoo Web
     *      Search API</a>
     * @see <a href="http://search.yahooapis.com/NewsSearchService/V1/newsSearch">Yahoo
     *      News Search API</a>
     */
    @Init
    @Input
    @Attribute
    public String serviceURI = "http://api.search.yahoo.com/WebSearchService/V1/webSearch";

    /**
     * A domain to restrict your searches to (e.g. www.yahoo.com). TODO: maybe it would
     * make sense to implement multiple values here (allowed by Yahoo)?
     * 
     * @group Results filtering
     * @label Domain restriction
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    public String site;

    /**
     * The regional search engine on which the service performs the search. For example,
     * setting region to <code>uk</code> will give you the search engine at
     * uk.search.yahoo.com. Value must be one of the <a
     * href="http://developer.yahoo.com/search/regions.html">supported region codes</a>.
     * 
     * @see <a href="http://developer.yahoo.com/search/regions.html">Supported regions</a>
     * @group Results filtering
     * @label Region
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    @ValueHintEnum(values = YahooRegionCodes.class)
    public String region;

    /**
     * The country in which to restrict your search results. Only results on web sites
     * within this country are returned. Value must be one of the <a
     * href="http://developer.yahoo.com/search/countries.html">supported country codes</a>.
     * 
     * @group Results filtering
     * @label Country
     * @level Medium
     */ 
    @Processing
    @Input
    @Attribute
    @ValueHintEnum(values = YahooCountryCodes.class)
    public String country;
    
    /**
     * Assembles an array of {@link NameValuePair} with request parameters.
     */
    @Override
    protected ArrayList<NameValuePair> createRequestParams(String query, int start,
        int results)
    {
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(10);

        params.add(new NameValuePair("query", query));
        params.add(new NameValuePair("start", Integer.toString(start)));
        params.add(new NameValuePair("results", Integer.toString(results)));

        params.add(new NameValuePair("appid", appid));

        if (country != null)
        {
            params.add(new NameValuePair("country", country));
        }
        if (site != null)
        {
            params.add(new NameValuePair("site", site));
        }
        if (region != null)
        {
            params.add(new NameValuePair("region", region));
        }
        if (type != null)
        {
            params.add(new NameValuePair("type", type.getApiOption()));
        }

        return params;
    }

    @Override
    protected String getServiceURI()
    {
        return serviceURI;
    }
}
