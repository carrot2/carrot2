package org.carrot2.source.boss;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

/**
 * Sends queries to Yahoo! Boss Web search service. Instances of this class are
 * thread-safe.
 * <p>
 * Attributes of this class correspond to Yahoo's documentation (see links below).
 * 
 * @label Yahoo Boss Web Search Service
 * @see <a href="http://developer.yahoo.com/search/boss/boss_guide/">Yahoo Boss
 *      Documentation</a>
 */
@Bindable
public final class BossWebSearchService extends BossSearchService
{
    /**
     * Boss Web search service URI.
     * 
     * @label Service URI
     * @level Advanced
     */
    @Init
    @Input
    @Attribute
    public String serviceURI = "http://boss.yahooapis.com/ysearch/web/v1/${query}";

    /**
     * Filter out adult or hate content. Adult content is supported for all languages,
     * hate content is supported for English only.
     * 
     * @see http://developer.yahoo.com/search/boss/boss_guide/Web_Search.html
     * @group Results filtering
     * @label Content filter
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    public List<FilterConst> filter;

    /**
     * Restrict search to documents of a given type. Supports the following document
     * types:
     * <ul>
     * <li>html</li>
     * <li>text</li>
     * <li>pdf</li>
     * <li>xl (Microsoft Excel: xls, xla, xl)</li>
     * <li>msword (Microsoft Word)</li>
     * <li>ppt (Microsoft Power Point)</li>
     * </ul>
     * The following type groups are supported:
     * <ul>
     * <li>msoffice (xl, msword, ppt)</li>
     * <li>nonhtml (text, pdf, xl, msword, ppt)</li>
     * </ul>
     * <p>
     * You can also specify a format group then exclude an item:
     * 
     * <pre>
     * type=msoffice,-ppt
     * </pre>
     * 
     * @see http://developer.yahoo.com/search/boss/boss_guide/Web_Search.html
     * @group Results filtering
     * @label Type filter
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    public String type;

    /**
     * Assembles an array of {@link NameValuePair} with request parameters.
     */
    @Override
    protected ArrayList<NameValuePair> createRequestParams(String query, int start,
        int results)
    {
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(10);

        if (filter != null && filter.size() > 0)
        {
            params.add(new NameValuePair("filter", StringUtils.join(filter, ',')));
        }

        if (type != null)
        {
            params.add(new NameValuePair("type", type));
        }

        return params;
    }

    @Override
    protected String getServiceURI()
    {
        return serviceURI;
    }
}
