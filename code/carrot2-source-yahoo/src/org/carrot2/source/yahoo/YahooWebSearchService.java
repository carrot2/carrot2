package org.carrot2.source.yahoo;

import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.carrot2.util.attribute.*;

/**
 * Sends queries to Yahoo! Web search service. Instances of this class are thread-safe.
 * <p>
 * Attributes of this class correspond to Yahoo's documentation (see links below).
 *
 * @label Yahoo Web Search Service
 * @see <a href="http://developer.yahoo.com/search/web/V1/webSearch.html">Yahoo Web Search
 *      Documentation</a>
 */
@Bindable
public final class YahooWebSearchService extends YahooSearchService
{
    /**
     * Yahoo! service URI to be queried.
     *
     * @label Service URI
     * @see <a href="http://api.search.yahoo.com/WebSearchService/V1/webSearch">Yahoo Web
     *      Search API</a>
     * @see <a href="http://search.yahooapis.com/NewsSearchService/V1/newsSearch">Yahoo
     *      News Search API</a>
     */
    @Init
    @Input
    @Attribute
    private String serviceURI = "http://api.search.yahoo.com/WebSearchService/V1/webSearch";

    /**
     * The language the results are written in. Value must be one of the <a
     * href="http://developer.yahoo.com/search/languages.html">supported language codes</a>.
     * Omitting language returns results in any language.
     *
     * @label Language
     */
    @Processing
    @Input
    @Attribute
    public String language;

    /**
     * The country in which to restrict your search results. Only results on web sites
     * within this country are returned. Value must be one of the <a
     * href="http://developer.yahoo.com/search/countries.html">supported country codes</a>.
     *
     * @label Country
     */
    @Processing
    @Input
    @Attribute
    public String country;

    /**
     * A domain to restrict your searches to (e.g. www.yahoo.com). TODO: maybe it would
     * make sense to implement multiple values here (allowed by Yahoo)?
     *
     * @label Site
     */
    @Processing
    @Input
    @Attribute
    public String site;

    /**
     * The regional search engine on which the service performs the search. For example,
     * region=uk will give you the search engine at uk.search.yahoo.com. Value must be one
     * of the <a href="http://developer.yahoo.com/search/regions.html">supported region
     * codes</a>.
     *
     * @label Region
     */
    @Processing
    @Input
    @Attribute
    public String region;

    /** */
    public QueryType type = QueryType.ALL;

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
            params.add(new NameValuePair("type", type.toString()));
        }

        return params;
    }

    @Override
    protected String getServiceURI()
    {
        return serviceURI;
    }
}
