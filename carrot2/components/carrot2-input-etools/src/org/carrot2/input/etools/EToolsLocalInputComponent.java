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

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.input.xml.*;
import org.dom4j.*;


/**
 * A Carrot2 input component for the eTools service (http://www.etools.ch). 
 * For commercial licensing of the eTools feed, please e-mail: contact@comcepta.com.
 * 
 * @author Stanislaw Osinski
 */
public class EToolsLocalInputComponent
    extends XmlLocalInputComponent
{
    private static final String COUNTRY_PARAMETER_NAME = "country";
    private static final String SAFE_SEARCH_PARAMETER_NAME = "safe-search";
    private static final String DATA_SOURCES_PARAMETER_NAME = "data-sources";
    private static final String TIMEOUT_PARAMETER_NAME = "timeout";
    private static final String LANGUAGE_PARAMETER_NAME = "language";
    private static final String MAX_RECORDS_PARAMETER_NAME = "max-records";
    private static final String DATA_SOURCE_RESULTS_PARAMETER_NAME = "data-source-results";
    private static final String QUERY_PARAMETER_NAME = "query";
    private static final String PARTNER_ID_PARAMETER_NAME = "partner-id";

    private static final int MAX_DATA_SOURCE_RESULTS = 40;
    private static final int FASTEST_SOURCES_COUNT = 5;
    private static final int ALL_SOURCES_COUNT = 10;
    
    private EToolsConfig config;
    private String partnerId;


    /**
     * Creates the eTools input with the default partner id and default
     * settings.
     */
    public EToolsLocalInputComponent()
    {
        this("Carrot2");
    }
    
    /**
     * Creates the eTools input with the specified partner id and default settings.
     */
    public EToolsLocalInputComponent(String partnerId)
    {
        this(partnerId, EToolsConfig.DEFAULT_ETOOLS_CONFIG);
    }

    /**
     * Creates the eTools input with a default partner id and the specified settings.
     */
    public EToolsLocalInputComponent(EToolsConfig config)
    {
        this("Carrot2", config);
    }
    

    /**
     * Creates the eTools input with the specified partner id and specified settings.
     */
    public EToolsLocalInputComponent(String partnerId, EToolsConfig config)
    {
        super(preprocessUrlBase(config.urlBase) 
                + "?partner=${" + PARTNER_ID_PARAMETER_NAME + "}"
                + "&query=${" + QUERY_PARAMETER_NAME + "}"
                + "&dataSourceResults=${" + DATA_SOURCE_RESULTS_PARAMETER_NAME + "}"
                + "&maxRecords=${" + MAX_RECORDS_PARAMETER_NAME + "}"
                + "&language=${" + LANGUAGE_PARAMETER_NAME + "}" 
                + "&timeout=${" + TIMEOUT_PARAMETER_NAME + "}" 
                + "&dataSources=${" + DATA_SOURCES_PARAMETER_NAME + "}" 
                + "&safeSearch=${" + SAFE_SEARCH_PARAMETER_NAME + "}" 
                + "&country=${" + COUNTRY_PARAMETER_NAME + "}",
                EToolsLocalInputComponent.class
                        .getResource("etools-to-c2.xsl"));
        
        if (partnerId == null)
        {
            throw new IllegalArgumentException(
                    "The partnerId parameter must not be null.");
        }
        
        this.partnerId = partnerId;
        this.config = config;
    }


    protected Document performQuery(Map params)
        throws ProcessingException
    {
        int dataSourceResultsCount = getDataSourceResultsCount(params);

        // Put eTools-specific parameters
        params.put(PARTNER_ID_PARAMETER_NAME, partnerId);
        params.put(DATA_SOURCE_RESULTS_PARAMETER_NAME, Integer.toString(dataSourceResultsCount));
        params.put(MAX_RECORDS_PARAMETER_NAME, Integer.toString(getRequestedResults(params)));
        params.put(LANGUAGE_PARAMETER_NAME, config.language);
        params.put(TIMEOUT_PARAMETER_NAME, Integer.toString(config.timeout));
        params.put(DATA_SOURCES_PARAMETER_NAME, config.dataSources);
        params.put(SAFE_SEARCH_PARAMETER_NAME, Boolean.toString(config.safeSearch));
        params.put(COUNTRY_PARAMETER_NAME, config.country);
        
        return super.performQuery(params);
    }


    /**
     * Returns the number of results per data source, estimated based on 
     * the total requested results.
     * 
     * @param params
     */
    public int getDataSourceResultsCount(Map params)
    {
        int sources = config.dataSources.equals("all") ? ALL_SOURCES_COUNT
            : FASTEST_SOURCES_COUNT;
        int requestedResults = getRequestedResults(params);
        
        if (requestedResults == 0)
        {
            return 0;
        }
        
        int rawDataSourceResults = requestedResults / sources;
        return Math
            .min(((rawDataSourceResults + 9) / 10 + 1) * 10, MAX_DATA_SOURCE_RESULTS);
    }


    private static String preprocessUrlBase(String solrServiceUrlBase)
    {
        if (solrServiceUrlBase.endsWith("/")) {
            solrServiceUrlBase = solrServiceUrlBase.substring(0,
                    solrServiceUrlBase.length() - 1);
        }
        return solrServiceUrlBase;
    }
}
