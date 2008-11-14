
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

package org.carrot2.filter.lingo.common;

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
     * @return snippets as array 
     */
    public Snippet[] getSnippets() {
        return (Snippet[]) snippets.toArray(new Snippet[snippets.size()]);
    }

    /**
     * Returns the snippets
     * 
     * @return snippets as ArrayList
     */
    public ArrayList getSnippetsAsArrayList() {
        return snippets;
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

    public void setQuery(String query) {
        this.query = query;
    }

    public void setParameters(Map map) {
        parameters = map;
    }

    public Object getParameter(Object key) {
        return parameters.get(key);
    }

    public void setParameter(Object key, Object parameter) {
        parameters.put(key, parameter);
    }
}
