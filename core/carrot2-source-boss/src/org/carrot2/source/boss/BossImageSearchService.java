
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
 * Sends queries to Yahoo! Boss Image search service. Instances of this class are
 * thread-safe.
 * <p>
 * Attributes of this class correspond to Yahoo's documentation (see links below). Note
 * the following service terms given by Yahoo: <b>You must:</b>
 * <ol>
 * <li>display the title or abstract of the image when presented in the search results;
 * <li>display at least the first 40 characters of the referurl field which indicates the
 * source page;
 * <li>for the source page referurl, use the unmodified referclickurl field to navigate
 * to the respective source page;
 * <li>If the image is made clickable, either use unmodified refereclickurl (to the
 * source HTML page) or clickurl (to the image) for the navigation link.
 * </ol>
 * 
 * @label Yahoo Boss Image Search Service
 * @see <a href="http://developer.yahoo.com/search/boss/boss_guide/">BOSS API guide</a>
 */
@Bindable(prefix = "BossImageSearchService")
public final class BossImageSearchService extends BossSearchService
{
    /**
     * Boss Image search service URI. Specifies the URI at which Yahoo Boss Image Search
     * API is available. The <code>${query}</code> place holder will be replaced with
     * the URL-encoded text of the processed query.
     * 
     * @label Service URI
     * @level Advanced
     * @group Service
     */
    @Init
    @Input
    @Attribute
    public String serviceURI = "http://boss.yahooapis.com/ysearch/images/v1/${query}";

    /**
     * If enabled, excludes offensive content from the results.
     * 
     * @see <a
     *      href="http://developer.yahoo.com/search/boss/boss_guide/Submit_Image_Queries.html">API
     *      description</a>
     * @group Results filtering
     * @label Offensive content filter
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    public boolean filter = true;

    /**
     * The size of images to fetch. Small images are generally thumbnail or icon sized.
     * Medium sized images are average sized; usually not exceeding an average screen
     * size. Large images are screen size or larger.
     * 
     * @group Results filtering
     * @label Preferred size
     * @level Medium
     */
    @Init
    @Processing
    @Input
    @Attribute
    public Dimensions dimensions = Dimensions.ALL;

    /**
     * Assembles an array of {@link NameValuePair} with request parameters.
     */
    @Override
    protected ArrayList<NameValuePair> createRequestParams(String query, int start,
        int results)
    {
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(10);

        params.add(new NameValuePair("filter", filter ? "yes" : "no"));

        if (dimensions != null)
        {
            params.add(new NameValuePair("dimensions", dimensions.parameterValue));
        }

        return params;
    }

    @Override
    protected String getServiceURI()
    {
        return serviceURI;
    }
}
