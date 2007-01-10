
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

/** Settings for a single search request. */
public final class SearchRequest {
    /** {@link SearchSettings} for which this request was made. */
    private final SearchSettings searchSettings;

    /** The query for this request. Never null, but may be empty. */
    public final String query;

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

    /**
     * Parse parameters.
     */
    public SearchRequest(SearchSettings settings, Map parameterMap) {
        searchSettings = settings;

        // Parse the query
        final String query = extract(parameterMap, QueryProcessorServlet.PARAM_Q);
        if (query == null || query.trim().length() == 0) {
            this.query = "";
        } else {
            this.query = query;
        }

        // Parse input tab.
        this.inputTabIndex = lookupIndex(searchSettings.inputTabsIndex, extract(parameterMap, QueryProcessorServlet.PARAM_INPUT),
                searchSettings.getDefaultSearchInputTab());

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
        this.inputAndSizeHashCode = query + "//" 
            + getInputTab().getShortName() 
            + "//" + getInputSize();

        // save remaining options.
        this.allRequestOpts = parameterMap;
    }

    private int lookupIndex(HashMap index, String key, int defaultValue) {
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
}