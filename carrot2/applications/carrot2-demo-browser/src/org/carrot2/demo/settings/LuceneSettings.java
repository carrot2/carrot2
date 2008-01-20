
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

package org.carrot2.demo.settings;

import java.awt.Frame;
import java.io.*;
import java.util.*;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.carrot2.demo.PersistentProcessSettingsBase;
import org.carrot2.demo.ProcessSettings;
import org.carrot2.input.lucene.*;
import org.carrot2.util.ArrayUtils;

/**
 * Settings class for Lucene input component.
 *
 * @author Dawid Weiss
 */
public final class LuceneSettings extends PersistentProcessSettingsBase {

    private final static Logger logger = Logger.getLogger(LuceneSettings.class);

    /** Lucene index directory */
    File luceneIndexDir;

    /** Lucene Analyzer */
    Analyzer analyzer;

    /** Lucene fields to be searched */
    String [] searchFields;

    /** Content fields */
    String titleField;
    String summaryField;
    String urlField;
    boolean createSnippets;

    private Searcher searcher;
    private IndexReader indexReader;

    public LuceneSettings() {
        // try to load config from file
        loadConfigFile();
    }

    /**
     * Cloning constructor.
     */
    public LuceneSettings(LuceneSettings other) {
        this.luceneIndexDir = other.luceneIndexDir;
        this.analyzer = other.analyzer;
        this.searchFields = (String []) other.searchFields.clone();
        this.titleField = other.titleField;
        this.summaryField = other.summaryField;
        this.urlField = other.urlField;
        this.createSnippets = other.createSnippets;

        if (luceneIndexDir != null) {
            createIndexReaderAndSearcher();
        }
    }

    private void createIndexReaderAndSearcher() {
        try {
            if (searcher != null) {
                dispose();
            }

            indexReader = IndexReader.open(luceneIndexDir);
            this.searcher = new IndexSearcher(indexReader);
            logger.debug("Creating searcher: " + this.searcher);
            super.fireParamsUpdated();
        } catch (IOException e) {
            throw new RuntimeException("Could not open lucene index.", e);
        }
    }

    public ProcessSettings createClone() {
        return new LuceneSettings(this);
    }

    public Map getRequestParams() {
        if (!isConfigured()) {
            throw new RuntimeException("Not configured yet.");
        }
        final HashMap map = new HashMap();
        map.put(LuceneLocalInputComponent.LUCENE_CONFIG,
                new LuceneLocalInputComponentConfig(
                    new LuceneLocalInputComponentFactoryConfig(
                        searchFields,
                        titleField,
                        summaryField,
                        urlField,
                        (createSnippets ? LuceneLocalInputComponentFactoryConfig.LONG_PLAIN_TEXT_SUMMARY
                            : LuceneLocalInputComponentFactoryConfig.NO_SUMMARIES)),
                    indexReader, searcher, analyzer));
        return map;
    }

    public JComponent getSettingsComponent(Frame owner) {
        return new LuceneSettingsDialog(this);
    }

    public boolean hasSettings() {
        return true;
    }

    public boolean isConfigured() {
        if (this.luceneIndexDir == null || !this.luceneIndexDir.isDirectory()) {
            return false;
        }

        if (this.analyzer == null) {
            return false;
        }

        if (this.searchFields == null || this.searchFields.length == 0) {
            return false;
        }

        if (this.titleField == null || this.summaryField == null || this.urlField == null) {
            return false;
        }

        return true;
    }

    public void dispose() {
        if (searcher != null) {
            logger.debug("Closing searcher: " + this.searcher);
            try {
                searcher.close();
            } catch (IOException e) {
                logger.error("Could not close Lucene searcher.", e);
            }
        }
    }

    final void setConfig(File indexDir, String [] searchFields, String urlField,
        String titleField, String snippetField, Analyzer analyzer, boolean createSnippets)
    {
        this.luceneIndexDir = indexDir;
        this.searchFields = searchFields;
        this.urlField = urlField;
        this.titleField = titleField;
        this.summaryField = snippetField;
        this.analyzer = analyzer;
        this.createSnippets = createSnippets;
        saveConfigFile();
        createIndexReaderAndSearcher();
    }

    protected void initFromProperties(Properties config)
    {
        if (config.containsKey("index.dir"))
        {
            luceneIndexDir = new File(config.getProperty("index.dir"));
        }
        if (config.containsKey("analyzer.class"))
        {
            try
            {
                analyzer = (Analyzer) Thread.currentThread().getContextClassLoader()
                    .loadClass((String) config.getProperty("analyzer.class"))
                    .newInstance();
            }
            catch (Exception e)
            {
                throw new RuntimeException(
                    "Could not load analyzer class specified in the configuration file",
                    e);
            }
        }
        if (config.containsKey("search.fields"))
        {
            String fields = config.getProperty("search.fields");
            searchFields = fields.split(",");
        }
        titleField = config.getProperty("title.field");
        summaryField = config.getProperty("summary.field");
        urlField = config.getProperty("url.field");
        createSnippets = Boolean.valueOf(config.getProperty("snippets")).booleanValue();
    }

    protected Properties asProperties()
    {
        Properties config = new Properties();

        if (luceneIndexDir != null)
        {
            config.setProperty("index.dir", luceneIndexDir.getAbsolutePath());
        }
        if (analyzer != null)
        {
            config.setProperty("analyzer.class", analyzer.getClass().getName());
        }
        if (searchFields != null)
        {
            config.setProperty("search.fields", ArrayUtils.toString(searchFields, ","));
        }
        if (titleField != null)
        {
            config.setProperty("title.field", titleField);
        }
        if (summaryField != null)
        {
            config.setProperty("summary.field", summaryField);
        }
        if (urlField != null)
        {
            config.setProperty("url.field", urlField);
        }
        config.setProperty("snippets", Boolean.toString(createSnippets));

        return config;
    }

    public String getPropertiesFileNamePart()
    {
        return "lucene-input";
    }
}
