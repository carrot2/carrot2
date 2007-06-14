
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

import java.util.*;

import javax.servlet.http.Cookie;

/**
 * All available search settings and options.
 * 
 * @author Dawid Weiss
 */
public final class SearchSettings {
    /** An array of {@link TabSearchInput}s. */
    final ArrayList inputTabs = new ArrayList();

    /** An array of {@link TabAlgorithm}s */
    final ArrayList algorithms = new ArrayList();

    /** Lookup indexes for input tabs and algorithms. */
    final Map inputTabsIndex = new HashMap();
    final Map algorithmsIndex = new HashMap();

    /** Allowed input sizes */
    int [] allowedInputSizes;

    /** Default input size. */
    private int defaultInputSizeIndex;

    /** Default tab index. */
    private int defaultTabIndex;

    /** Default algorithm index. */
    private int defaultAlgorithmIndex;

    /**
     * Adds a search tab to the list of available tabs.
     */
    public void add(TabSearchInput tab) {
        this.inputTabsIndex.put(tab.getShortName(), new Integer(inputTabs.size()));
        this.inputTabs.add(tab);
    } 

    /**
     * Adds an algorithm to the settings.
     */
    public void add(TabAlgorithm algorithm) {
        this.algorithmsIndex.put(algorithm.getShortName(), new Integer(algorithms.size()));
        this.algorithms.add(algorithm);
    } 
    
    /**
     * Sets the allowed input sizes and the default size.
     */
    public void setAllowedInputSizes(int [] allowedSizes, int defaultSize) {
        this.allowedInputSizes = new int [allowedSizes.length];
        System.arraycopy(allowedSizes, 0, this.allowedInputSizes, 0, allowedSizes.length);
        
        // Lookup the value index.
        for (int i = 0; i < allowedInputSizes.length; i++) {
            if (allowedInputSizes[i] == defaultSize) {
                this.defaultInputSizeIndex = i;
                return;
            }
        }
        throw new IllegalArgumentException("The default size " + defaultSize + " is not among the allowed sizes.");
    }

    /**
     * Returns the index of the default search input tab.
     */
    protected int getDefaultSearchInputTab() {
        return defaultTabIndex;
    }

    /**
     * Returns the index of the default algorithm.
     */
    protected int getDefaultAlgorithm() {
        return defaultAlgorithmIndex;
    }

    /**
     * Default input size index.
     */
    protected int getDefaultInputSizeIndex() {
        return this.defaultInputSizeIndex;
    }
    
    /**
     * Parses request arguments and returns a valid
     * search request object.
     */
    public SearchRequest parseRequest(Map parameterMap, Cookie [] cookies) {
        return new SearchRequest(this, parameterMap, cookies);
    }

    /**
     * Fail-safe string-to-integer parsing. 
     */
    protected final int parseInt(String valueAsString, int defaultValue, int min, int max) {
        if (valueAsString == null) {
            return defaultValue;
        } else {
            try {
                final int tmp = Integer.parseInt(valueAsString);
                if (tmp >= min && tmp <= max) {
                    return tmp;
                } else {
                    return defaultValue;
                }
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }

    public List getInputTabs() {
        return this.inputTabs;
    }

    public List getAlgorithms() {
        return this.algorithms;
    }

    public int[] getAllowedInputSizes() {
        return this.allowedInputSizes;
    }

    public void setDefaultTabIndex(int defaultTabIndex) {
        if (defaultTabIndex < 0 || defaultTabIndex > this.inputTabs.size()) {
            throw new IllegalArgumentException();
        }
        this.defaultTabIndex = defaultTabIndex;
    }

    public void setDefaultAlgorithmIndex(int defaultAlgorithmIndex) {
        if (defaultAlgorithmIndex < 0 || defaultAlgorithmIndex > this.algorithms.size()) {
            throw new IllegalArgumentException();
        }
        this.defaultAlgorithmIndex = defaultAlgorithmIndex;
    }
}
