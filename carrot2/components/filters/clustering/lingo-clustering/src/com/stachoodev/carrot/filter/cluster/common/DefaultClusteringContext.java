

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.carrot.filter.cluster.common;


import ViolinStrings.Strings;
import com.stachoodev.carrot.filter.cluster.lsicluster.LsiClusteringStrategy;
import com.stachoodev.util.log.TimeLogger;
import org.apache.log4j.Logger;
import java.util.*;


/**
 * Stores all data needed during clustering process.
 */
public class DefaultClusteringContext
    extends AbstractClusteringContext
{
    /** Logger */
    protected static final Logger logger = Logger.getLogger(DefaultClusteringContext.class);

    /** Inflected -> stem mapping */
    private HashMap stems;

    /** A naive stem -> inflected mapping */
    private HashMap inflected;

    /** Stop words information */
    private HashSet stopWords;

    /**
     */
    public DefaultClusteringContext()
    {
        snippets = new ArrayList();
        additionalData = new HashMap();
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
     * @return ClusteringResults
     */
    public ClusteringResults cluster()
    {
        TimeLogger timeLogger = new TimeLogger();
        TimeLogger totalTimeLogger = new TimeLogger();

        totalTimeLogger.start();
        timeLogger.start();

        preprocess();
        timeLogger.logElapsedAndStart(logger, "preprocess()");
        extractFeatures();
        timeLogger.logElapsedAndStart(logger, "extractFeatures()");

        ClusteringResults clusteringResults = clusteringStrategy.cluster(this);
        timeLogger.logElapsed(logger, "cluster()");

        totalTimeLogger.logElapsed(logger, "TOTAL");

        return clusteringResults;
    }


    /**
     *
     */
    void extractFeatures()
    {
        features = featureExtractionStrategy.extractFeatures(this);
    }


    /**
     *
     */
    void preprocess()
    {
        preprocessedSnippets = preprocessingStrategy.preprocess(this);
    }


    /**
     * Method addSnippet.
     *
     * @param snippet
     */
    public void addSnippet(Snippet snippet)
    {
        snippets.add(snippet);
    }


    /**
     * Method addStopWord.
     *
     * @param stopWord
     */
    public void addStopWord(String stopWord)
    {
        stopWords.add(stopWord);
    }


    /**
     * Method addStem.
     *
     * @param word
     * @param stem
     */
    public void addStem(String word, String stem)
    {
        stems.put(word, stem);

        if (!inflected.containsKey(stem))
        {
            inflected.put(stem, word);
        }
    }


    /**
     * Method putData.
     *
     * @param key
     * @param data
     */
    public void putData(Object key, Object data)
    {
        additionalData.put(key, data);
    }


    /**
     * Method getData.
     *
     * @param key
     *
     * @return Object
     */
    public Object getData(Object key)
    {
        return additionalData.get(key);
    }


    /**
     * Method removeData.
     *
     * @param key
     */
    public void removeData(Object key)
    {
        additionalData.remove(key);
    }


    /**
     * Returns the snippets.
     *
     * @return ArrayList
     */
    public Snippet [] getSnippets()
    {
        return (Snippet []) snippets.toArray(new Snippet[snippets.size()]);
    }


    /**
     * Returns the stems.
     *
     * @return HashMap
     */
    public HashMap getStems()
    {
        return stems;
    }


    /**
     * Returns the stopWords.
     *
     * @return HashSet
     */
    public HashSet getStopWords()
    {
        return stopWords;
    }


    /**
     * Returns the preprocessedSnippets.
     *
     * @return Snippet[]
     */
    public Snippet [] getPreprocessedSnippets()
    {
        return preprocessedSnippets;
    }


    /**
     * @return String
     */
    public String getQuery()
    {
        return query;
    }


    /**
     * Sets the query.
     *
     * @param query The query to set
     */
    public void setQuery(String query)
    {
        this.query = query;
    }


    /**
     * @return HashMap
     */
    public HashSet getQueryWords()
    {
        if (queryWords == null)
        {
            queryWords = new HashSet();

            if (query == null)
            {
                return queryWords;
            }

            String [] words = Strings.split(
                    Strings.translate(
                        query, "\"'!?.@#$%^&*()-_+=|\\/`~[]{}", "                           "
                    )
                );

            for (int i = 0; i < words.length; i++)
            {
                // Stem with every possilble stemmer. Is it good?
                if (stems.containsKey(words[i]))
                {
                    queryWords.add(stems.get(words[i]));
                }
            }
        }

        return queryWords;
    }


    /**
     * @return Feature[]
     */
    public Feature [] getFeatures()
    {
        return features;
    }


    /**
     * Sets the clusteringStrategy.
     *
     * @param clusteringStrategy The clusteringStrategy to set
     */
    public void setClusteringStrategy(ClusteringStrategy clusteringStrategy)
    {
        this.clusteringStrategy = clusteringStrategy;
    }


    /**
     * @return int
     */
    public HashMap getInflected()
    {
        return inflected;
    }


    /**
     * @return HashSet
     */
    public HashSet getStrongWords()
    {
        return strongWords;
    }
}
