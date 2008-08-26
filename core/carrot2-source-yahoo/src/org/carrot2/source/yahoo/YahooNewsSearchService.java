package org.carrot2.source.yahoo;

import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

/**
 * Sends queries to Yahoo! News search service. Instances of this class are thread-safe.
 * <p>
 * Attributes of this class correspond to Yahoo's documentation (see links below).
 * 
 * @label Yahoo News Search Service
 * @see <a href="http://com3.devnet.re3.yahoo.com/search/news/V1/newsSearch.html">Yahoo
 *      News Search Documentation</a>
 */
@Bindable
public final class YahooNewsSearchService extends YahooSearchService
{
    /**
     * Yahoo News results sort orders.
     */
    public enum SortType
    {
        /**
         * Sort results by relevance 
         */
        RANK, 
        
        /**
         * Put most recent results first. 
         */
        DATE;
        
        @Override
        public String toString()
        {
            switch (this)
            {
                case RANK: return "rank";
                case DATE: return "date";
                default: throw new RuntimeException("Unknown constant: " + this.name());
            }
        }
    }

    /**
     * Yahoo! service URI to be queried.
     * 
     * @label Service URI
     * @level Advanced
     */
    @Init
    @Input
    @Attribute
    public String serviceURI =
        "http://search.yahooapis.com/NewsSearchService/V1/newsSearch";

    /**
     * The language the results are written in. Value must be one of the <a
     * href="http://developer.yahoo.com/search/languages.html">supported language</a>
     * codes. Omitting language returns results in any language.
     * 
     * @label Results Language
     * @level Medium
     */
    // TODO: maybe this should be enum?
    @Processing
    @Input
    @Attribute
    public String language;

    /**
     * A domain to restrict your searches to (e.g. www.yahoo.com). TODO: maybe it would
     * make sense to implement multiple values here (allowed by Yahoo)?
     * 
     * @label Site
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    public String site;

    /**
     * Results sort order.
     */
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

        if (language != null)
        {
            params.add(new NameValuePair("language", language));
        }
        if (site != null)
        {
            params.add(new NameValuePair("site", site));
        }
        if (sort != null)
        {
            params.add(new NameValuePair("sort", sort.toString()));
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
