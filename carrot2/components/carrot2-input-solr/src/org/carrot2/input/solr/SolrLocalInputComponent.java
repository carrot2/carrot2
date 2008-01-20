
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

/**
 * Carrot2 input component for getting search results from a Solr service.
 * 
 * @author stachoo
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
    public static final String DEFAULT_SOLR_QUERY_STRING = "?q=${query}&start=0&rows=${requested-results}&indent=off";

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

    /**
     * Create a new instance of the Solr input component with empty parameters. Parameters
     * must be provided at request time.
     */
    public SolrLocalInputComponent()
    {
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
        super(getFullSolrServiceUrl(solrServiceUrlBase, DEFAULT_SOLR_QUERY_STRING),
            SolrLocalInputComponent.class.getResource("solr-to-c2.xsl"));
    }

    private static String getFullSolrServiceUrl(String solrServiceUrlBase,
        String solrQueryString)
    {
        return preprocessUrlBase(solrServiceUrlBase) + "/" + solrQueryString;
    }

    private static String preprocessUrlBase(String solrServiceUrlBase)
    {
        if (solrServiceUrlBase.endsWith("/"))
        {
            solrServiceUrlBase = solrServiceUrlBase.substring(0, solrServiceUrlBase
                .length() - 1);
        }
        return solrServiceUrlBase;
    }

    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        Map params = requestContext.getRequestParameters();

        if (defaultXml == null)
        {
            String solrServiceUrlBase = (String) params.get(PARAM_SOLR_SERVICE_URL_BASE);
            String solrQueryString = (String) params.get(PARAM_SOLR_QUERY_STRING);
            if (solrServiceUrlBase != null && solrQueryString != null)
            {
                params.put(XmlLocalInputComponent.PARAM_SOURCE_XML,
                    getFullSolrServiceUrl(solrServiceUrlBase, solrQueryString));
            }
        }

        if (defaultXslt == null)
        {
            String customXsltUrl = (String) params.get(PARAM_SOLR_XSLT);
            if (!StringUtils.isBlank(customXsltUrl))
            {
                params.put(XmlLocalInputComponent.PARAM_XSLT, customXsltUrl);
            }
            else
            {
                params.put(XmlLocalInputComponent.PARAM_XSLT,
                    SolrLocalInputComponent.class.getResource("solr-to-c2.xsl"));
            }
        }

        putIfNotPresent(params, PARAM_SOLR_ID_FIELD, DEFAULT_SOLR_ID_FIELD);
        putIfNotPresent(params, PARAM_SOLR_TITLE_FIELD, DEFAULT_SOLR_TITLE_FIELD);
        putIfNotPresent(params, PARAM_SOLR_SNIPPET_FIELD, DEFAULT_SOLR_SNIPPET_FIELD);
        putIfNotPresent(params, PARAM_SOLR_URL_FIELD, DEFAULT_SOLR_URL_FIELD);

        super.startProcessing(requestContext);
    }

    private void putIfNotPresent(Map params, String key, Object value)
    {
        if (!params.containsKey(key))
        {
            params.put(key, value);
        }
    }
}
