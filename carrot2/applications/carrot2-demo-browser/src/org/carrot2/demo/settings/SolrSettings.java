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

package org.carrot2.demo.settings;

import java.awt.Frame;
import java.util.*;

import javax.swing.JComponent;

import org.carrot2.demo.PersistentProcessSettingsBase;
import org.carrot2.demo.ProcessSettings;
import org.carrot2.input.solr.SolrLocalInputComponent;
import org.carrot2.util.StringUtils;

/**
 * Settings class for Lucene input component.
 * 
 * @author Dawid Weiss
 */
public final class SolrSettings extends PersistentProcessSettingsBase
{
    /** Solr service url base */
    String solrUrlBase;

    /** Solr service query string */
    String solrQueryString;

    /** Optional custom XSLT url */
    String solrXslt;
    
    /** Field names */
    String solrIdField;
    String solrTitleField;
    String solrSnippetField;
    String solrUrlField;

    public SolrSettings()
    {
        // try to load config from file
        if (!loadConfigFile())
        {
            // set the defaults
            this.solrUrlBase = SolrLocalInputComponent.DEFAULT_SOLR_SERVICE_URL_BASE;
            this.solrQueryString = SolrLocalInputComponent.DEFAULT_SOLR_QUERY_STRING;
            this.solrXslt = null;
            this.solrIdField = SolrLocalInputComponent.DEFAULT_SOLR_ID_FIELD;
            this.solrTitleField = SolrLocalInputComponent.DEFAULT_SOLR_TITLE_FIELD;
            this.solrSnippetField= SolrLocalInputComponent.DEFAULT_SOLR_SNIPPET_FIELD;
            this.solrUrlField = SolrLocalInputComponent.DEFAULT_SOLR_URL_FIELD;
        }
    }

    /**
     * Cloning constructor.
     */
    public SolrSettings(SolrSettings other)
    {
        this.solrUrlBase = other.solrUrlBase;
        this.solrQueryString = other.solrQueryString;
        this.solrXslt = other.solrXslt;
        this.solrIdField = other.solrIdField;
        this.solrTitleField = other.solrTitleField;
        this.solrSnippetField = other.solrSnippetField;
        this.solrUrlField = other.solrUrlField;
    }

    public boolean hasSettings()
    {
        return true;
    }

    public boolean isConfigured()
    {
        return !StringUtils.isBlank(solrUrlBase) && !StringUtils.isBlank(solrQueryString)
            && !StringUtils.isBlank(solrIdField) && !StringUtils.isBlank(solrTitleField)
            && !StringUtils.isBlank(solrSnippetField)
            && !StringUtils.isBlank(solrUrlField);
    }

    public Map getRequestParams() {
        if (!isConfigured()) {
            throw new RuntimeException("Not configured yet.");
        }
        final Map map = new HashMap();
        map.put(SolrLocalInputComponent.PARAM_SOLR_SERVICE_URL_BASE, solrUrlBase);
        map.put(SolrLocalInputComponent.PARAM_SOLR_QUERY_STRING, solrQueryString);
        if (!StringUtils.isBlank(solrXslt)) {
            map.put(SolrLocalInputComponent.PARAM_SOLR_XSLT, solrXslt);
        }
        map.put(SolrLocalInputComponent.PARAM_SOLR_ID_FIELD, solrIdField);
        map.put(SolrLocalInputComponent.PARAM_SOLR_TITLE_FIELD, solrTitleField);
        map.put(SolrLocalInputComponent.PARAM_SOLR_SNIPPET_FIELD, solrSnippetField);
        map.put(SolrLocalInputComponent.PARAM_SOLR_URL_FIELD, solrUrlField);
        return map;
    }

    public void dispose()
    {
    }

    final void setConfig(String solrUrlBase, String solrQueryString, String solrXslt, String solrIdField,
        String solrTitleField, String solrSnippetField, String solrUrlField)
    {
        this.solrUrlBase = solrUrlBase;
        this.solrQueryString = solrQueryString;
        this.solrXslt = solrXslt;
        this.solrIdField = solrIdField;
        this.solrTitleField = solrTitleField;
        this.solrSnippetField = solrSnippetField;
        this.solrUrlField = solrUrlField;
        saveConfigFile();
    }

    protected void initFromProperties(Properties config)
    {
        if (config.containsKey(SolrLocalInputComponent.PARAM_SOLR_SERVICE_URL_BASE))
        {
            solrUrlBase = config.getProperty(SolrLocalInputComponent.PARAM_SOLR_SERVICE_URL_BASE);
        }
        if (config.containsKey(SolrLocalInputComponent.PARAM_SOLR_QUERY_STRING))
        {
            solrQueryString = config.getProperty(SolrLocalInputComponent.PARAM_SOLR_QUERY_STRING);
        }
        if (config.containsKey(SolrLocalInputComponent.PARAM_SOLR_XSLT))
        {
            solrXslt = config.getProperty(SolrLocalInputComponent.PARAM_SOLR_XSLT);
        }
        if (config.containsKey(SolrLocalInputComponent.PARAM_SOLR_ID_FIELD))
        {
            solrIdField = config.getProperty(SolrLocalInputComponent.PARAM_SOLR_ID_FIELD);
        }
        if (config.containsKey(SolrLocalInputComponent.PARAM_SOLR_TITLE_FIELD))
        {
            solrTitleField = config.getProperty(SolrLocalInputComponent.PARAM_SOLR_TITLE_FIELD);
        }
        if (config.containsKey(SolrLocalInputComponent.PARAM_SOLR_SNIPPET_FIELD))
        {
            solrSnippetField = config.getProperty(SolrLocalInputComponent.PARAM_SOLR_SNIPPET_FIELD);
        }
        if (config.containsKey(SolrLocalInputComponent.PARAM_SOLR_URL_FIELD))
        {
            solrUrlField = config.getProperty(SolrLocalInputComponent.PARAM_SOLR_URL_FIELD);
        }
    }

    protected Properties asProperties()
    {
        Properties config = new Properties();

        if (solrUrlBase != null)
        {
            config.setProperty(SolrLocalInputComponent.PARAM_SOLR_SERVICE_URL_BASE, solrUrlBase);
        }
        if (solrQueryString != null)
        {
            config.setProperty(SolrLocalInputComponent.PARAM_SOLR_QUERY_STRING, solrQueryString);
        }
        if (solrXslt != null)
        {
            config.setProperty(SolrLocalInputComponent.PARAM_SOLR_XSLT, solrXslt);
        }
        if (solrIdField != null)
        {
            config.setProperty(SolrLocalInputComponent.PARAM_SOLR_ID_FIELD, solrIdField);
        }
        if (solrTitleField != null)
        {
            config.setProperty(SolrLocalInputComponent.PARAM_SOLR_TITLE_FIELD, solrTitleField);
        }
        if (solrSnippetField != null)
        {
            config.setProperty(SolrLocalInputComponent.PARAM_SOLR_SNIPPET_FIELD, solrSnippetField);
        }
        if (solrUrlField != null)
        {
            config.setProperty(SolrLocalInputComponent.PARAM_SOLR_URL_FIELD, solrUrlField);
        }

        return config;
    }

    public String getPropertiesFileNamePart()
    {
        return "solr-input";
    }

    public ProcessSettings createClone()
    {
        return new SolrSettings(this);
    }

    public JComponent getSettingsComponent(Frame owner)
    {
        return new SolrSettingsDialog(this);
    }
}
