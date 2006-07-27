
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

package org.carrot2.filter.lingo.common;

import ViolinStrings.Strings;

import org.carrot2.filter.lingo.lsicluster.LsiClusteringStrategy;
import org.carrot2.filter.lingo.util.log.TimeLogger;

import org.apache.log4j.Logger;

import java.util.*;


/**
 * Stores all data needed during clustering process.
 */
public class DefaultClusteringContext extends AbstractClusteringContext {
    /**
     * Logger
     */
    protected static final Logger logger = Logger.getLogger(DefaultClusteringContext.class);

    /**
     * Inflected -> stem mapping
     */
    private HashMap stems;

    /**
     * A naive stem -> inflected mapping
     */
    private HashMap inflected;

    /**
     * Stop words information
     */
    private HashSet stopWords;

    /**
     */
    public DefaultClusteringContext() {
        snippets = new ArrayList();
        stems = new HashMap();
        inflected = new HashMap();
        stopWords = new HashSet();

        preprocessingStrategy = new DefaultPreprocessingStrategy();
        featureExtractionStrategy = new DefaultFeatureExtractionStrategy();
        clusteringStrategy = new LsiClusteringStrategy();
    }

    /**
     * Method cluster.
     *
     * @return Cluster array.
     */
    public Cluster[] cluster() {
        TimeLogger timeLogger = new TimeLogger();
        TimeLogger totalTimeLogger = new TimeLogger();

        totalTimeLogger.start();
        timeLogger.start();

        preprocess();
        timeLogger.logElapsedAndStart(logger, "preprocess()");
        extractFeatures();
        timeLogger.logElapsedAndStart(logger, "extractFeatures()");

        Cluster[] clusteringResults = clusteringStrategy.cluster(this);
        timeLogger.logElapsed(logger, "cluster()");

        totalTimeLogger.logElapsed(logger, "TOTAL");

        return clusteringResults;
    }

    /**
     *
     */
    void extractFeatures() {
        features = featureExtractionStrategy.extractFeatures(this);
    }

    /**
     *
     */
    void preprocess() {
        preprocessedSnippets = preprocessingStrategy.preprocess(this);
    }

    /**
     * Method addSnippet.
     *
     * @param snippet
     */
    public void addSnippet(Snippet snippet) {
        snippets.add(snippet);
    }

    /**
     * Method addStopWord.
     *
     * @param stopWord
     */
    public void addStopWord(String stopWord) {
        stopWords.add(stopWord);
    }

    /**
     * Method addStem.
     *
     * @param word
     * @param stem
     */
    public void addStem(String word, String stem) {
        stems.put(word, stem);

        if (!inflected.containsKey(stem)) {
            inflected.put(stem, word);
        }
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
     * Returns the stems.
     *
     * @return HashMap
     */
    public HashMap getStems() {
        return stems;
    }

    /**
     * Returns the stopWords.
     *
     * @return HashSet
     */
    public HashSet getStopWords() {
        return stopWords;
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
     * Sets the query.
     *
     * @param query The query to set
     */
    public void setQuery(String query) {
        this.query = query;
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
                // Stem with every possilble stemmer. Is it good?
                if (stems.containsKey(words[i])) {
                    queryWords.add(stems.get(words[i]));
                }
            }
        }

        return queryWords;
    }

    /**
     * @return Feature[]
     */
    public Feature[] getFeatures() {
        return features;
    }

    /**
     * Sets the clusteringStrategy.
     *
     * @param clusteringStrategy The clusteringStrategy to set
     */
    public void setClusteringStrategy(ClusteringStrategy clusteringStrategy) {
        this.clusteringStrategy = clusteringStrategy;
    }

    /**
     * @return int
     */
    public HashMap getInflected() {
        return inflected;
    }

    /**
     * @return HashSet
     */
    public HashSet getStrongWords() {
        return strongWords;
    }
}
