

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
import java.util.*;


/**
 * @author stachoo To change this generated comment go to  Window>Preferences>Java>Code
 *         Generation>Code Template
 */
/**
 * @author stachoo
 */
public abstract class AbstractClusteringContext
{
    /** Algorithm parameters */
    protected Map parameters;

    /** The query this context handles */
    protected String query;

    /** Snippet data in raw format */
    protected ArrayList snippets;

    /** Preprocessed snippets (i.e. in a proper format and e.g. stemmed) */
    protected Snippet [] preprocessedSnippets;

    /** Features describing the documents */
    protected Feature [] features;

    /** Additional information that specific  algorithms may wish to store. */
    protected HashMap additionalData;

    /** Clustering strategy */
    protected ClusteringStrategy clusteringStrategy;

    /** Preprocessing strategy */
    protected PreprocessingStrategy preprocessingStrategy;

    /** Feature extraction strategy */
    protected FeatureExtractionStrategy featureExtractionStrategy;

    /**
     *
     */
    public AbstractClusteringContext()
    {
        snippets = new ArrayList();
        additionalData = new HashMap();
        strongWords = new HashSet();
        parameters = new HashMap();
    }

    /** The words (stemmed) contained in the query */
    protected HashSet queryWords;

    /** Emphasized words (e.g. contained in snippets' titles) */
    protected HashSet strongWords;

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
     * @return Feature[]
     */
    public Feature [] getFeatures()
    {
        return features;
    }


    /**
     * @return HashSet
     */
    public HashSet getStrongWords()
    {
        return strongWords;
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
    public void setQuery(String query)
    {
        this.query = query;
    }


    /**
     * @param map
     */
    public void setParameters(Map map)
    {
        parameters = map;
    }


    /**
     * @param key
     *
     * @return
     */
    public Object getParameter(Object key)
    {
        LinkedList param = (LinkedList) parameters.get(key);

        if (param != null)
        {
            return (String) param.get(0);
        }
        else
        {
            return null;
        }
    }


    /**
     * @param key
     * @param parameter
     */
    public void setParameter(Object key, Object parameter)
    {
        parameters.put(key, parameter);
    }
}
