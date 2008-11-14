
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.etools;

import java.util.Map;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.RequestContext;
import org.carrot2.input.xml.XmlLocalInputComponent;
import org.carrot2.util.resources.ClassResource;


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
     * Source URL with substitutable parameters. 
     */
    private final String sourceURL;


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
        super(null, new ClassResource(EToolsLocalInputComponent.class, "etools-to-c2.xsl"));

        this.sourceURL = stripEndingSlash(config.urlBase) 
            + "?partner=${" + PARTNER_ID_PARAMETER_NAME + "}"
            + "&query=${" + QUERY_PARAMETER_NAME + "}"
            + "&dataSourceResults=${" + DATA_SOURCE_RESULTS_PARAMETER_NAME + "}"
            + "&maxRecords=${" + MAX_RECORDS_PARAMETER_NAME + "}"
            + "&language=${" + LANGUAGE_PARAMETER_NAME + "}" 
            + "&timeout=${" + TIMEOUT_PARAMETER_NAME + "}" 
            + "&dataSources=${" + DATA_SOURCES_PARAMETER_NAME + "}" 
            + "&safeSearch=${" + SAFE_SEARCH_PARAMETER_NAME + "}" 
            + "&country=${" + COUNTRY_PARAMETER_NAME + "}";

        if (partnerId == null)
        {
            throw new IllegalArgumentException(
                    "The partnerId parameter must not be null.");
        }

        this.partnerId = partnerId;
        this.config = config;
    }

    /**
     * Override processing method and add custom parameters to the parameters map.
     */
    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        final Map params = requestContext.getRequestParameters();
        final int dataSourceResultsCount = getDataSourceResultsCount(params);

        // Put eTools-specific parameters
        params.put(PARTNER_ID_PARAMETER_NAME, partnerId);
        params.put(DATA_SOURCE_RESULTS_PARAMETER_NAME, Integer.toString(dataSourceResultsCount));
        params.put(MAX_RECORDS_PARAMETER_NAME, Integer.toString(getRequestedResults(params)));
        params.put(LANGUAGE_PARAMETER_NAME, config.language);
        params.put(TIMEOUT_PARAMETER_NAME, Integer.toString(config.timeout));
        params.put(DATA_SOURCES_PARAMETER_NAME, config.dataSources);
        params.put(SAFE_SEARCH_PARAMETER_NAME, Boolean.toString(config.safeSearch));
        params.put(COUNTRY_PARAMETER_NAME, config.country);

        // Pass the input URL.
        params.put(XmlLocalInputComponent.PARAM_SOURCE_XML, sourceURL);
        
        super.startProcessing(requestContext);
    }

    /**
     * Returns the number of results per data source, estimated based on 
     * the total requested results.
     * 
     * @param params
     */
    public int getDataSourceResultsCount(Map params)
    {
        int sources = config.dataSources.equals("all") 
            ? ALL_SOURCES_COUNT 
            : FASTEST_SOURCES_COUNT;
        int requestedResults = getRequestedResults(params);

        if (requestedResults == 0)
        {
            return 0;
        }
        
        int rawDataSourceResults = requestedResults / sources;
        return Math.min(((rawDataSourceResults + 9) / 10 + 1) * 10, MAX_DATA_SOURCE_RESULTS);
    }

    /**
     * 
     */
    private static String stripEndingSlash(String url)
    {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
