

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



/**
 * Multilingual LSI clusterer  Stoplists for German, French, Dutch, Italian and Spanish obtained
 * from www.aspseek.org
 */
package com.stachoodev.carrot.filter.cluster.common;


import com.dawidweiss.carrot.filter.stemming.DirectStemmer;
import com.stachoodev.carrot.filter.cluster.lsicluster.LsiClusteringStrategy;
import com.stachoodev.util.log.TimeLogger;
import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;


/**
 * Stores all data needed during clustering process.
 */
public class MultilingualClusteringContext
    extends AbstractClusteringContext
{
    /** Logger */
    protected static final Logger logger = Logger.getLogger(MultilingualClusteringContext.class);

    /** Undefined language name */
    /** Filesystem directory from which algorithm data may be read. */
    private File dataDir;

    /** Language processing */
    private HashMap stopWordSets;
    private HashMap nonStopWordSets;
    private HashMap stemSets;
    private HashMap inflectedSets;
    private HashMap stemmers;

    /** */
    public static final String UNIDENTIFIED_LANGUAGE_NAME = "unidentified";

    /**
     * This constructor will probably be used for test purposes only.
     */
    public MultilingualClusteringContext()
    {
        this(new File(System.getProperty("user.dir")));
    }


    /**
     * @param dataDir
     */
    public MultilingualClusteringContext(File dataDir)
    {
        this.dataDir = dataDir;

        stopWordSets = new HashMap();
        nonStopWordSets = new HashMap();
        stemSets = new HashMap();
        inflectedSets = new HashMap();
        stemmers = new HashMap();

        nonStopWordSets.put(
            MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME, new HashSet()
        );

        initLanguageProcessing();

        preprocessingStrategy = new MultilingualPreprocessingStrategy();
        featureExtractionStrategy = new MultilingualFeatureExtractionStrategy();
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
    private void initLanguageProcessing()
    {
        File stopWordsDir = new File(dataDir.getAbsolutePath() + File.separator + "stopwords");

        logger.debug("Looking for stopword files in: " + stopWordsDir.getAbsolutePath());

        if (stopWordsDir.exists() && stopWordsDir.isDirectory())
        {
            File [] stopWordFiles = stopWordsDir.listFiles();

            for (int i = 0; i < stopWordFiles.length; i++)
            {
                // Load stopwords
                String language = stopWordFiles[i].getName();

                try
                {
                    stopWordSets.put(language, readStopWordsSet(stopWordFiles[i]));

                    logger.debug(
                        "Adding file: " + stopWordFiles[i].getAbsolutePath()
                        + " as a stop words set named: " + language
                    );
                }
                catch (IOException e)
                {
                    logger.error(
                        "Error reading stopwords file: " + stopWordFiles[i].getAbsolutePath(), e
                    );

                    continue;
                }

                // Prepare hash maps
                nonStopWordSets.put(language, new HashSet());
                stemSets.put(language, new HashMap());
                inflectedSets.put(language, new HashMap());
            }
        }
    }


    /**
     * Reads a set of stop words and returns it as a HashSet
     *
     * @param stopWordsFile
     *
     * @return
     */
    private HashSet readStopWordsSet(File stopWordsFile)
        throws IOException
    {
        Reader r = null;
        HashSet stopWordsSet = new HashSet();

        try
        {
            r = new InputStreamReader(new FileInputStream(stopWordsFile), "UTF-8");

            StreamTokenizer st = new StreamTokenizer(r);

            int token;

            while ((token = st.nextToken()) != StreamTokenizer.TT_EOF)
            {
                switch (token)
                {
                    case StreamTokenizer.TT_WORD:
                        stopWordsSet.add(st.sval);

                        break;

                    default:}
            }
        }
        finally
        {
            if (r != null)
            {
                try
                {
                    r.close();
                }
                catch (IOException x)
                {
                    logger.error("Cannot close file."); /* not much we can do. */
                }
            }
        }

        return stopWordsSet;
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
     * @return HashMap
     */
    public HashSet getQueryWords()
    {
        if (queryWords == null)
        {
            super.getQueryWords();

            HashSet queryWordsStemmed = new HashSet();

            for (Iterator words = queryWords.iterator(); words.hasNext();)
            {
                String word = (String) words.next();

                // Stem with every possilble stemmer. Is it good?
                Iterator keys = stemmers.keySet().iterator();

                while (keys.hasNext())
                {
                    String key = (String) keys.next();
                    DirectStemmer stemmer = (DirectStemmer) stemmers.get(key);

                    String stemmed = stemmer.getStem(word.toCharArray(), 0, word.length());

                    if ((stemmed != null) && !stemmed.equals(word))
                    {
                        queryWordsStemmed.add(stemmed);
                    }
                }
            }

            queryWords.addAll(queryWordsStemmed);
        }

        return queryWords;
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
     * @return
     */
    public HashMap getInflectedSets()
    {
        return inflectedSets;
    }


    /**
     * @return
     */
    public HashMap getStemmers()
    {
        return stemmers;
    }


    /**
     * @return
     */
    public HashMap getStopWordSets()
    {
        return stopWordSets;
    }


    /**
     * @return
     */
    public HashMap getStemSets()
    {
        return stemSets;
    }


    /**
     * @param map
     */
    public void setStemmers(HashMap map)
    {
        stemmers = map;
    }


    /**
     * @return
     */
    public HashMap getNonStopWordSets()
    {
        return nonStopWordSets;
    }
}
