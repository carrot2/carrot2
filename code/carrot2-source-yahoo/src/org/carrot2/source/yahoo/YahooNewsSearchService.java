package org.carrot2.source.yahoo;

import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.carrot2.core.attribute.Attribute;
import org.carrot2.core.attribute.Bindable;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Input;
import org.carrot2.core.attribute.Processing;

/**
 * Sends queries to Yahoo! News search service. Instances of this class are thread-safe.
 * <p>
 * Attributes of this class correspond to Yahoo's documentation (see links below).
 * 
 * @see <a href="http://com3.devnet.re3.yahoo.com/search/news/V1/newsSearch.html">Yahoo
 *      News Search Documentation</a>
 */
@Bindable
public final class YahooNewsSearchService extends YahooSearchService
{
    /** */
    public enum SortType
    {
        RANK
        {
            public String toString()
            {
                return "rank";
            }
        },
        DATE
        {
            public String toString()
            {
                return "date";
            }
        },
    }

    /**
     * Yahoo service URI to be queried.
     */
    @Init
    @Input
    @Attribute
    private String serviceURI = "http://search.yahooapis.com/NewsSearchService/V1/newsSearch";

    /**
     * The language the results are written in. Value must be one of the <a
     * href="http://developer.yahoo.com/search/languages.html">supported language</a>
     * codes. Omitting language returns results in any language.
     */
    @Processing
    @Input
    @Attribute
    public String language;

    /** 
     * TODO: It doesn't look like the news service has the 'country' parameter. 
     */
    @Processing
    @Input
    @Attribute
    public String country;

    /** 
     * A domain to restrict your searches to (e.g. www.yahoo.com). 
     */
    @Processing
    @Input
    @Attribute
    public String site;

    /** */
    public QueryType type = QueryType.ALL;

    /** */
    public SortType sort = SortType.RANK;

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

        if (country != null) params.add(new NameValuePair("country", country));
        if (site != null) params.add(new NameValuePair("site", site));
        if (sort != null) params.add(new NameValuePair("sort", sort.toString()));
        if (type != null) params.add(new NameValuePair("type", type.toString()));

        return params;
    }

    @Override
    protected String getServiceURI()
    {
        return serviceURI;
    }
}
