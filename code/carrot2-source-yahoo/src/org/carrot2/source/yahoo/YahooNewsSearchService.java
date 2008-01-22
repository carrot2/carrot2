package org.carrot2.source.yahoo;

import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.carrot2.core.parameter.*;

/**
 * Sends queries to Yahoo! News search service. Instances of this class are
 * thread-safe.
 * <p>
 * Parameters of this class correspond to Yahoo's documentation (see links below).
 * 
 * @see http://com3.devnet.re3.yahoo.com/search/news/V1/newsSearch.html 
 */
@Bindable
public final class YahooNewsSearchService extends YahooSearchService
{
    /** Logger for this class. */
    static final Logger logger = Logger.getLogger(YahooNewsSearchService.class);

    /** */
    public enum SortType {
        RANK { public String toString() { return "rank"; } },
        DATE { public String toString() { return "date"; } },
    }

    /** */
    @Init
    @Input
    @Parameter 
    private String serviceURI = "http://search.yahooapis.com/NewsSearchService/V1/newsSearch";

    /** */
    @BeforeProcessing
    @Input
    @Parameter
    public String language;

    /** */
    @BeforeProcessing
    @Input
    @Parameter
    public String country;

    /** */
    @BeforeProcessing
    @Input
    @Parameter
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
