/*
 * Copyright (c) 2004 Poznan Supercomputing and Networking Center
 * 10 Noskowskiego Street, Poznan, Wielkopolska 61-704, Poland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Poznan Supercomputing and Networking Center ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into
 * with PSNC.
 */

package org.carrot2.input.etools;

/**
 * Configuration parameters for the eTools input component.
 * 
 * @author Stanislaw Osinski
 */
public class EToolsConfig
{
    /** eTools input configuration with all default values */
    public static final EToolsConfig DEFAULT_ETOOLS_CONFIG = new EToolsConfig();

    /** Base URL for the eTools service */
    public final String urlBase;
    
    /** Default URL base: <code>{@value}</code> */
    public static final String DEFAULT_URL_BASE = "http://www.etools.ch/partnerSearch.do";

    /** Country to be searched ("web", "CH", "LI", "DE", "AT", "FR", "IT", "ES", "GB") */
    public final String country;
    
    /** Default country: <code>{@value}</code> */
    public static final String DEFAULT_COUNTRY = "web";

    /** Requested language of the returned results ("all", "de", "en", "es", "fr", "it") */
    public final String language;
    
    /** Default language: <code>{@value}</code> */
    public static final String DEFAULT_LANGUAGE = "en";

    /** Timeout */
    public final int timeout;
    
    /** Default timeout: <code>{@value}</code> */
    public static final int DEFAULT_TIMEOUT = 4000;

    /** 
     * Selection of data sources that are queried concurrently ("all", "fastest").
     * The 'fastest' means the five fastest data sources at that moment. 
     */
    public final String dataSources;
    
    /** Default data sources: <code>{@value}</code> */
    public static final String DEFAULT_DATA_SOURCES = "all";

    /** Offensive content exclusion */
    public final boolean safeSearch;
    
    /** Default safe search: <code>{@value}</code> */
    public static final boolean DEFAULT_SAFE_SEARCH = false;


    public EToolsConfig()
    {
        this(DEFAULT_URL_BASE, DEFAULT_COUNTRY, DEFAULT_LANGUAGE,
                DEFAULT_TIMEOUT, DEFAULT_DATA_SOURCES, DEFAULT_SAFE_SEARCH);
    }


    public EToolsConfig(final String country, final String language)
    {
        this(DEFAULT_URL_BASE, country, language, DEFAULT_TIMEOUT,
            DEFAULT_DATA_SOURCES, DEFAULT_SAFE_SEARCH);
    }


    public EToolsConfig(final String urlBase, final String country,
            final String language, final int timeout, final String dataSources,
            final boolean safeSearch)
    {
        this.urlBase = urlBase;
        this.country = country;
        this.language = language;
        this.timeout = timeout;
        this.dataSources = dataSources;
        this.safeSearch = safeSearch;
    }

}
