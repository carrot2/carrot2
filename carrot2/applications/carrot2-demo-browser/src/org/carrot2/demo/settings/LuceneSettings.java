
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.settings;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.carrot2.demo.ProcessSettings;
import org.carrot2.demo.ProcessSettingsBase;
import org.carrot2.input.lucene.LuceneLocalInputComponent;
import org.carrot2.input.lucene.LuceneSearchConfig;

/**
 * Settings class for Lucene input component.
 * 
 * @author Dawid Weiss
 */
public final class LuceneSettings extends ProcessSettingsBase {

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

    private Searcher searcher;

    public LuceneSettings() {
        // nothing. uninitialized.
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

        if (luceneIndexDir != null) {
            createSearcher();
        }
    }

    private void createSearcher() {
        try {
            if (searcher != null) {
                dispose();
            }

            this.searcher = new IndexSearcher(
                    IndexReader.open(luceneIndexDir));
            logger.debug("Creating searcher: " + this.searcher);
            super.fireParamsUpdated();
        } catch (IOException e) {
            throw new RuntimeException("Could not open lucene index.", e);
        }
    }

    public ProcessSettings createClone() {
        return new LuceneSettings(this);
    }

    public HashMap getRequestParams() {
        if (!isConfigured()) {
            throw new RuntimeException("Not configured yet.");
        }
        final HashMap map = new HashMap();
        map.put(LuceneLocalInputComponent.LUCENE_CONFIG, 
                new LuceneSearchConfig(searcher,
                        analyzer, searchFields, titleField,
                        summaryField, urlField));
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

    final void setConfig(File indexDir, String [] searchFields, String urlField, String titleField, String snippetField, Analyzer analyzer) {
        this.luceneIndexDir = indexDir;
        this.searchFields = searchFields;
        this.urlField = urlField;
        this.titleField = titleField;
        this.summaryField = snippetField;
        this.analyzer = analyzer;
        createSearcher();
    }
}
