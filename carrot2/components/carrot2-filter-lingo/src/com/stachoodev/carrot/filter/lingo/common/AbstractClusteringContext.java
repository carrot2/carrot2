
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.stachoodev.carrot.filter.lingo.common;

import ViolinStrings.Strings;

import java.util.*;


/**
 * @author Stanisław Osiński
 */
public abstract class AbstractClusteringContext {
    /**
     * Algorithm parameters
     */
    protected Map parameters;

    /**
     * The query this context handles
     */
    protected String query;

    /**
     * Snippet data in raw format
     */
    protected ArrayList snippets;

    /**
     * Preprocessed snippets (i.e. in a proper format and e.g. stemmed)
     */
    protected Snippet[] preprocessedSnippets;

    /**
     * Features describing the documents
     */
    protected Feature[] features;

    /**
     * Clustering strategy
     */
    protected ClusteringStrategy clusteringStrategy;

    /**
     * Preprocessing strategy
     */
    protected PreprocessingStrategy preprocessingStrategy;

    /**
     * Feature extraction strategy
     */
    protected FeatureExtractionStrategy featureExtractionStrategy;

    /**
     *
     */
    public AbstractClusteringContext() {
        snippets = new ArrayList();
        strongWords = new HashSet();
        parameters = new HashMap();
    }

    /**
     * The words (stemmed) contained in the query
     */
    protected HashSet queryWords;

    /**
     * Emphasized words (e.g. contained in snippets' titles)
     */
    protected HashSet strongWords;

    /**
     * Method addSnippet.
     *
     * @param snippet
     */
    public void addSnippet(Snippet snippet) {
        snippets.add(snippet);
    }

    /**
     * Returns the snippets.
     *
     * @return ArrayList
     */
    public Snippet[] getSnippets() {
        return (Snippet[]) snippets.toArray(new Snippet[snippets.size()]);
    }

    /**
     * Returns the preprocessedSnippets.
     *
     * @return Snippet[]
     */
    public Snippet[] getPreprocessedSnippets() {
        return preprocessedSnippets;
    }

    /**
     * @return String
     */
    public String getQuery() {
        return query;
    }

    /**
     * @return Feature[]
     */
    public Feature[] getFeatures() {
        return features;
    }

    /**
     * @return HashSet
     */
    public HashSet getStrongWords() {
        return strongWords;
    }

    /**
     * @return HashMap
     */
    public HashSet getQueryWords() {
        if (queryWords == null) {
            queryWords = new HashSet();

            if (query == null) {
                return queryWords;
            }

            String[] words = Strings.split(Strings.translate(query,
                        "\"'!?.@#$%^&*()-_+=|\\/`~[]{}",
                        "                           "));

            for (int i = 0; i < words.length; i++) {
                queryWords.add(words[i].toLowerCase());
            }
        }

        return queryWords;
    }

    /**
     * Sets the query.
     *
     * @param query The query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @param map
     */
    public void setParameters(Map map) {
        parameters = map;
    }

    /**
     * @param key
     *
     * @return
     */
    public Object getParameter(Object key) {
        return parameters.get(key);
    }

    /**
     * @param key
     * @param parameter
     */
    public void setParameter(Object key, Object parameter) {
        parameters.put(key, parameter);
    }
}
