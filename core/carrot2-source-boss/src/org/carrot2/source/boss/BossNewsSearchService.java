
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
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * Sends queries to Yahoo! Boss News search service. Instances of this class are
 * thread-safe.
 * <p>
 * Attributes of this class correspond to Yahoo's documentation (see links below).
 * 
 * @label Yahoo Boss News Search Service
 * @see <a href="http://developer.yahoo.com/search/boss/boss_guide/News_Search.html">API
 *      description</a>
 * @see <a href="http://developer.yahoo.com/search/boss/boss_guide/">Yahoo Boss
 *      Documentation</a>
 */
@Bindable(prefix = "BossNewsSearchService")
public final class BossNewsSearchService extends BossSearchService
{
    /**
     * Boss News search service URI.
     * 
     * @label Service URI
     * @level Advanced
     * @group Service
     */
    @Init
    @Input
    @Attribute
    public String serviceURI = "http://boss.yahooapis.com/ysearch/news/v1/${query}";

    /**
     * Maximum age of returned news in days. The index stories for 30 days.
     * 
     * @group Results filtering
     * @label Age
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = 30)
    public int age = 7;

    /**
     * Assembles an array of {@link NameValuePair} with request parameters.
     */
    @Override
    protected ArrayList<NameValuePair> createRequestParams(String query, int start,
        int results)
    {
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(10);

        if (age > 0)
        {
            params.add(new NameValuePair("age", age + "d"));
        }

        return params;
    }

    @Override
    protected String getServiceURI()
    {
        return serviceURI;
    }
}
