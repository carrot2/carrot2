
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

/** Settings for a single search request. */
public final class SearchRequest {
    /** {@link SearchSettings} for which this request was made. */
    private final SearchSettings searchSettings;

    /** The query in the form user entered it. Never null, but may be empty. */
    public final String query;

    /**
     * Expanded query. Can be <code>null</code> if there is no query expansion done, or
     * if after expansion the query has not changed.
     */
    public final String expandedQuery;

    /** Input tab index. */
    public final int inputTabIndex;

    /** Algorithm index */
    public final int algorithmIndex;

    /** Requested input size index. */
    public final int inputSizeIndex;

    /** hashcode of the combination of inputTab and size */
    public final String inputAndSizeHashCode;

    /** All options passed in the query */
    public final Map allRequestOpts;

    /** All cookies that came with the request */
    public final Map cookies;

    /**
     * Extra request options that may need to be appended to the URLs for
     * IFRAMES, so that these parameters are available e.g. while fetching documents.
     */
    public Map extraRequestOpts;

    /**
     * Parse parameters.
     * @param cookieArray
     * @param queryExpander
     */
    public SearchRequest(SearchSettings settings, Map parameterMap, Cookie [] cookieArray,
        QueryExpander queryExpander) {
        searchSettings = settings;

        // Parse the query
        final String query = extract(parameterMap, QueryProcessorServlet.PARAM_Q);
        if (query == null || query.trim().length() == 0) {
            this.query = "";
        } else {
            this.query = query;
        }

        // Perform query expansion
        if (queryExpander != null)
        {
            this.expandedQuery = queryExpander.expandQuery(query, parameterMap);
        }
        else
        {
            this.expandedQuery = null;
        }


        // copy cookies into a map
        this.cookies = new HashMap();
        if (cookieArray != null)
        {
            for (int i = 0; i < cookieArray.length; i++)
            {
                this.cookies.put(cookieArray[i].getName(), cookieArray[i].getValue());
            }
        }

        // Parse input tab.
        int defaultInputIndex = searchSettings.getDefaultSearchInputTab();
        if (cookies.containsKey(Constants.COOKIE_ACTIVE_TAB))
        {
            defaultInputIndex = lookupIndex(searchSettings.inputTabsIndex,
                (String) cookies.get(Constants.COOKIE_ACTIVE_TAB), defaultInputIndex);
        }
        this.inputTabIndex = lookupIndex(searchSettings.inputTabsIndex, extract(
            parameterMap, QueryProcessorServlet.PARAM_INPUT), defaultInputIndex);

        // Parse the algorithm
        this.algorithmIndex = lookupIndex(searchSettings.algorithmsIndex, extract(parameterMap, QueryProcessorServlet.PARAM_ALG),
                searchSettings.getDefaultAlgorithm());

        // Parse the input size
        final String value = extract(parameterMap, QueryProcessorServlet.PARAM_SIZE);
        int tmp = searchSettings.getDefaultInputSizeIndex();
        if (value != null) {
            final int resNum = searchSettings.parseInt(value, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            for (int i = searchSettings.allowedInputSizes.length - 1; i >= 0; i--) {
                if (searchSettings.allowedInputSizes[i] == resNum) {
                    tmp = i;
                    break;
                }
            }
        }
        this.inputSizeIndex = tmp;

        // calculate hash code.
        this.inputAndSizeHashCode = getActualQuery() + "//"
            + getInputTab().getShortName()
            + "//" + getInputSize();

        // save remaining options.
        this.allRequestOpts = parameterMap;

        // compute query string extension
        extraRequestOpts = new HashMap();
        final String[] parameterNames = queryExpander.getParameterNames();
        if (parameterNames != null)
        {
            for (int i = 0; i < parameterNames.length; i++)
            {
                final String val = extract(parameterMap, parameterNames[i]);
                if (val != null)
                {
                    extraRequestOpts.put(parameterNames[i], val);
                }
            }
        }

    }

    private int lookupIndex(Map index, String key, int defaultValue) {
        final Integer value = (Integer) index.get(key);
        if (value == null) return defaultValue;
        else return value.intValue();
    }

    private String extract(Map parameterMap, String key) {
        final String [] values = (String []) parameterMap.get(key);
        if (values == null) return null;
        else return values[0];
    }

    public TabSearchInput getInputTab() {
        return (TabSearchInput) searchSettings.inputTabs.get(inputTabIndex);
    }

    public TabAlgorithm getAlgorithm() {
        return (TabAlgorithm) searchSettings.algorithms.get(algorithmIndex);
    }

    public int getInputSize() {
        return searchSettings.allowedInputSizes[inputSizeIndex];
    }

    public String getInputAndSizeHashCode() {
        return inputAndSizeHashCode;
    }

    public Map getRequestArguments() {
        return this.allRequestOpts;
    }

    /**
     * Returns the actual query to be performed.on the search engine.
     *
     * @return
     */
    public String getActualQuery() {
        if (expandedQuery != null)
        {
            return expandedQuery;
        }
        else
        {
            return query;
        }
    }
}