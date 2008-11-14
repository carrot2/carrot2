
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

package org.carrot2.input.solr;

import java.util.Map;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.RequestContext;
import org.carrot2.input.xml.XmlLocalInputComponent;
import org.carrot2.util.StringUtils;
import org.carrot2.util.resources.ClassResource;

/**
 * Carrot2 input component for getting search results from a Solr service.
 * 
 * @author Stanislaw Osinski
 */
public class SolrLocalInputComponent extends XmlLocalInputComponent
{
    /**
     * A request-time parameter that specifies the Solr service url base, e.g.
     * <code>http://localhost:8983/solr/select</code>. Value of this parameter must be
     * of type {@link java.lang.String}.
     */
    public static final String PARAM_SOLR_SERVICE_URL_BASE = "org.carrot2.input.solr.url.base";

    /**
     * Default value of the {@link #PARAM_SOLR_SERVICE_URL_BASE} parameter:
     * <code>{@value}</code>
     */
    public static final String DEFAULT_SOLR_SERVICE_URL_BASE = "http://localhost:8983/solr/select";

    /**
     * A request-time parameter that specifies the query string. Value of this parameter
     * must be of type {@link java.lang.String}.
     */
    public static final String PARAM_SOLR_QUERY_STRING = "solr.query-string";

    /**
     * Default value of the {@link #PARAM_SOLR_QUERY_STRING} parameter:
     * <code>{@value}</code>
     */
    public static final String DEFAULT_SOLR_QUERY_STRING = 
        "?q=${query}&start=0&rows=${requested-results}&indent=off";

    /**
     * A request-time parameter that specifies an URL to the custom XSLT style sheet to be
     * used to convert Solr output to Carrot2 format. Value must be of type
     * {@link java.lang.String} and must be a vaild URL.
     */
    public static final String PARAM_SOLR_XSLT = "solr.xslt";

    public static final String PARAM_SOLR_TITLE_FIELD = "solr.title-field";
    public static final String DEFAULT_SOLR_TITLE_FIELD = "title";

    public static final String PARAM_SOLR_SNIPPET_FIELD = "solr.snippet-field";
    public static final String DEFAULT_SOLR_SNIPPET_FIELD = "description";

    public static final String PARAM_SOLR_URL_FIELD = "solr.url-field";
    public static final String DEFAULT_SOLR_URL_FIELD = "url";

    public static final String PARAM_SOLR_ID_FIELD = "solr.id-field";
    public static final String DEFAULT_SOLR_ID_FIELD = "id";

    /** URL to SOLR service with substitutable parameters. */
    private final String defaultSolrServiceUrl;

    /**
     * Create a new instance of the Solr input component with empty parameters. Parameters
     * must be provided at request time.
     */
    public SolrLocalInputComponent()
    {
        defaultSolrServiceUrl = null;
    }

    /**
     * Creates a new instance of the Solr input component with default Solr query string.
     * 
     * @param solrServiceUrlBase the base url (including protocol, host name, port and
     *            path) at which the Solr service is ruinning, e.g.
     *            <code>http://localhost:8983/solr/select</code>.
     */
    public SolrLocalInputComponent(String solrServiceUrlBase)
    {
        super(null, new ClassResource(SolrLocalInputComponent.class, "solr-to-c2.xsl"));

        defaultSolrServiceUrl = getFullSolrServiceUrl(
            solrServiceUrlBase, DEFAULT_SOLR_QUERY_STRING);
    }

    /**
     * 
     */
    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        final Map params = requestContext.getRequestParameters();

        final String solrServiceUrlBase = (String) params.get(PARAM_SOLR_SERVICE_URL_BASE);
        final String solrQueryString = (String) params.get(PARAM_SOLR_QUERY_STRING);
        if (solrServiceUrlBase != null && solrQueryString != null)
        {
            params.put(XmlLocalInputComponent.PARAM_SOURCE_XML,
                getFullSolrServiceUrl(solrServiceUrlBase, solrQueryString));
        }
        else
        {
            params.put(XmlLocalInputComponent.PARAM_SOURCE_XML, defaultSolrServiceUrl);
        }

        if (defaultXSLT == null)
        {
            final String customXsltUrl = (String) params.get(PARAM_SOLR_XSLT);
            if (StringUtils.isBlank(customXsltUrl))
            {
                // This means the caller did not comply with the class contract: used
                // the empty constructor, but did not provide valid parameters.
                throw new ProcessingException("Required parameter missing: " + PARAM_SOLR_XSLT);
            }

            params.put(XmlLocalInputComponent.PARAM_XSLT, customXsltUrl);
        }

        putIfNotPresent(params, PARAM_SOLR_ID_FIELD, DEFAULT_SOLR_ID_FIELD);
        putIfNotPresent(params, PARAM_SOLR_TITLE_FIELD, DEFAULT_SOLR_TITLE_FIELD);
        putIfNotPresent(params, PARAM_SOLR_SNIPPET_FIELD, DEFAULT_SOLR_SNIPPET_FIELD);
        putIfNotPresent(params, PARAM_SOLR_URL_FIELD, DEFAULT_SOLR_URL_FIELD);

        super.startProcessing(requestContext);
    }

    /*
     * 
     */
    private void putIfNotPresent(Map params, String key, Object value)
    {
        if (!params.containsKey(key))
        {
            params.put(key, value);
        }
    }
    
    /*
     * 
     */
    private static String getFullSolrServiceUrl(String solrServiceUrlBase, String solrQueryString)
    {
        return stripEndingSlash(solrServiceUrlBase) + "/" + solrQueryString;
    }

    /*
     * 
     */
    private static String stripEndingSlash(String url)
    {
        if (url.endsWith("/"))
        {
            url = url.substring(0, url
                .length() - 1);
        }
        return url;
    }
}
