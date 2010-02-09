
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

import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
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
 * @see <a href="http://developer.yahoo.com/search/boss/boss_guide/Web_Search.html">API description</a>
 * @see <a href="http://developer.yahoo.com/search/boss/boss_guide/">Yahoo Boss Documentation</a>
 */
@Bindable(prefix = "BossWebSearchService")
public final class BossWebSearchService extends BossSearchService
{
    /**
     * Boss Web search service URI. Specifies the URI at which Yahoo Boss Web Search API
     * is available. The <code>${query}</code> place holder will be replaced with the
     * URL-encoded text of the processed query.
     * 
     * @label Service URI
     * @level Advanced
     * @group Service
     */
    @Init
    @Input
    @Attribute
    public String serviceURI = "http://boss.yahooapis.com/ysearch/web/v1/${query}";

    /**
     * Filters out adult or hate content. Must be a comma-separated list of content types
     * to filter out.
     * <p>
     * The following content types are supported:
     * </p>
     * <table>
     * <thead>
     * <tr>
     * <th align="left">Value</th> <th align="left">Content</th>
     * </tr>
     * </thead> <tbody>
     * <tr>
     * <td><code>-porn</code></td>
     * <td>Filters out adult content</td>
     * </tr>
     * <tr>
     * <td><code>-hate</code></td>
     * <td>Filters out hate content</td>
     * </tr>
     * </tbody>
     * </table>
     * <p>
     * Adult content filtering is supported for all languages, hate content filtering is
     * supported for English only.
     * </p>
     * 
     * @group Results filtering
     * @label Content filter
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    public OffensiveContentFilter filter;

    /**
     * Restricts search to documents of the specified types. Must be a comma-separated
     * list of the required document types or type groups.
     * <p>
     * The following document types are supported:
     * </p>
     * <table>
     * <thead>
     * <tr>
     * <th align="left">Value</th> <th align="left">Document type</th>
     * </tr>
     * </thead> <tbody>
     * <tr>
     * <td><code>html</code></td>
     * <td>HTML documents</td>
     * </tr>
     * <tr>
     * <td><code>text</code></td>
     * <td>Plain text documents</td>
     * </tr>
     * <tr>
     * <td><code>pdf</code></td>
     * <td>Portable Document Format documents</td>
     * </tr>
     * <tr>
     * <td><code>xl</code></td>
     * <td>Microsoft Excel documents</td>
     * </tr>
     * <tr>
     * <td><code>msword</code></td>
     * <td>Microsoft Word documents</td>
     * </tr>
     * <tr>
     * <td><code>ppt</code></td>
     * <td>Microsoft Power Point documents</td>
     * </tr>
     * </tbody>
     * </table>
     * <p>
     * The following document type groups are supported:
     * </p>
     * <table>
     * <thead>
     * <tr>
     * <th align="left">Value</th> <th align="left">Document type groups</th>
     * </tr>
     * </thead> <tbody>
     * <tr>
     * <td><code>msoffice</code></td>
     * <td>All Microsoft Office documents (<code>xl</code>, <code>msword</code>,
     * <code>ppt</code>)</td>
     * </tr>
     * <tr>
     * <td><code>nohtml</code></td>
     * <td>Anything else than HTML documents (<code>text</code>, <code>pdf</code>,
     * <code>xl</code>, <code>msword</code>, <code>ppt</code>)</td>
     * </tr>
     * </tbody>
     * </table>
     * <p>
     * You can also specify a format group and then exclude an item:
     * <code>type=msoffice,-ppt</code>.
     * </p>
     * 
     * @group Results filtering
     * @label Type filter
     * @level Advanced
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

        if (filter != null)
        {
            params.add(new NameValuePair("filter", filter.getApiOption()));
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

    /**
     * Constants for offensive content filtering.
     */
    public static enum OffensiveContentFilter
    {
        PORN("-porn"),

        HATE("-hate"),

        PORN_AND_HATE("-porn,-hate");
        
        /** Option to pass to the API */
        private String apiOption;

        private OffensiveContentFilter(String apiOption)
        {
            this.apiOption = apiOption;
        }

        @Override
        public String toString()
        {
            return "remove " + name().replace('_', ' ').toLowerCase();
        }

        public String getApiOption()
        {
            return apiOption;
        }
    }
}
