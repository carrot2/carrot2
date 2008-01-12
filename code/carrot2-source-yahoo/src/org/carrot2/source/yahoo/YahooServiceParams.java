package org.carrot2.source.yahoo;

import org.carrot2.core.parameter.Bindable;
import org.carrot2.core.parameter.BindingPolicy;
import org.carrot2.core.parameter.Parameter;

/**
 * Yahoo! search service parameters. Several of these parameters (if set) correspond to
 * the service documentation (links below).
 * <p>
 * TODO: As we briefly discussed, for now I'd flatten this data object to the class it
 * belongs {@link YahooService}, just as we did for {@link YahooDocumentSource}.
 * 
 * @see http://com3.devnet.re3.yahoo.com/search/
 * @see http://com3.devnet.re3.yahoo.com/search/web/V1/webSearch.html
 * @see http://developer.yahoo.com/java/howto-reqRestJava.html
 */
@Bindable
public final class YahooServiceParams
{
    @Parameter(policy = BindingPolicy.INSTANTIATION)
    public String serviceURI = "http://api.search.yahoo.com/WebSearchService/V1/webSearch";

    @Parameter(policy = BindingPolicy.INSTANTIATION)
    public String appid = "carrotsearch";

    @Parameter(policy = BindingPolicy.INSTANTIATION)
    public int maxResultsPerPage = 50;

    @Parameter(policy = BindingPolicy.INSTANTIATION)
    public String language;

    @Parameter(policy = BindingPolicy.INSTANTIATION)
    public String country;

    @Parameter(policy = BindingPolicy.INSTANTIATION)
    public String site;
}
