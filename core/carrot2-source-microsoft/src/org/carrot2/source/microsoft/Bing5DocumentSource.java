
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft;

import java.util.concurrent.Callable;

import org.carrot2.core.IDocumentSource;
import org.carrot2.core.attribute.CommonAttributes;
import org.carrot2.source.MultipageSearchEngine;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.attribute.Bindable;

/**
 * A base {@link IDocumentSource} sending requests to Bing Search API V5.
 * 
 * <p>Important: there are limits for free use of the above API (beyond which it is a
 * paid service).
 * 
 * @see "https://msdn.microsoft.com/en-us/library/mt604056.aspx"
 */
@Bindable(prefix = "Bing5DocumentSource", inherit = CommonAttributes.class)
public abstract class Bing5DocumentSource extends MultipageSearchEngine
{
    /**
     * System property name for passing Bing API key.
     * 
     * You can also override the key per-controller or request 
     * via init or runtime attributes.
     */
    public static final String SYSPROP_BING5_API = "bing5.key";

    // TODO: modify from here on.
    // - basic web request (apache http) /response (via jackson) sending and parsing
    // - image, news search?
    // - global rate-throttling (limits)
    // - remove Bing3* impls.

    @Override
    protected Callable<SearchEngineResponse> createFetcher(SearchRange bucket) {
      return null;
    }
}
