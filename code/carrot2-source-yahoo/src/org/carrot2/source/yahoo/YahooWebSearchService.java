package org.carrot2.source.yahoo;

import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.carrot2.core.attribute.Attribute;
import org.carrot2.core.attribute.Bindable;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Input;
import org.carrot2.core.attribute.Processing;

/**
 * Sends queries to Yahoo! Web search service. Instances of this class are
 * thread-safe.
 * <p>
 * Parameters of this class correspond to Yahoo's documentation (see links below).
 * 
 * @see http://com3.devnet.re3.yahoo.com/search/web/V1/webSearch.html
 */
@Bindable
public final class YahooWebSearchService extends YahooSearchService
{
    /**
     * @see http://api.search.yahoo.com/WebSearchService/V1/webSearch
     * @see http://search.yahooapis.com/NewsSearchService/V1/newsSearch
     */
    @Init
    @Input
    @Attribute 
    private String serviceURI = "http://api.search.yahoo.com/WebSearchService/V1/webSearch";

    /** */
    @Processing
    @Input
    @Attribute
    public String language;

    /** */
    @Processing
    @Input
    @Attribute
    public String country;

    /** */
    @Processing
    @Input
    @Attribute
    public String site;

    /** */
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

        if (country != null) params.add(new NameValuePair("country", country));
        if (site != null) params.add(new NameValuePair("site", site));
        if (region != null) params.add(new NameValuePair("region", region));
        if (type != null) params.add(new NameValuePair("type", type.toString()));

        return params;
    }

    @Override
    protected String getServiceURI()
    {
        return serviceURI;
    }
}
