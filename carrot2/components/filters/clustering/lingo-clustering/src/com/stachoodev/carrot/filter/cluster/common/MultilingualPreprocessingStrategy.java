

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


import gnu.regexp.RE;
import gnu.regexp.REException;

import java.util.*;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.filter.stemming.DirectStemmer;
import com.dawidweiss.carrot.util.StringUtils;


/**
 *
 */
public class MultilingualPreprocessingStrategy
    implements PreprocessingStrategy
{
    /** Logger */
    protected static final Logger logger = Logger.getLogger(
            MultilingualPreprocessingStrategy.class
        );

    /** The sentence delimiters over which phrases cannot be spanned */
    private static final String [] DEFAULT_SENTENCE_DELIMITERS = { ".", "?", "!", "|", ";" };

    /** Sentence delimiters */
    private String [] sentenceDelimiters;

    /** Linguistic information */
    protected Map stemSets;
    protected Map inflectedSets;
    protected Map stopWordSets;
    protected Map nonStopWordSets;
    protected Map stemmers;
    protected Set strongWords;
    protected Set queryWords;
    protected Set lowCaseWords;
    protected Map caseCheck;

    /** */
    protected Map inflectedFreqSets;

    /** Common stop-words */
    protected Set commonStopWords;

    /**
     * @see java.lang.Object#Object()
     */
    public MultilingualPreprocessingStrategy()
    {
        this(DEFAULT_SENTENCE_DELIMITERS);
    }


    /**
     * Method DefaultPreprocessingStrategy.
     *
     * @param sentenceDelimiters
     */
    public MultilingualPreprocessingStrategy(String [] sentenceDelimiters)
    {
        this.sentenceDelimiters = sentenceDelimiters;

        commonStopWords = new HashSet();
        commonStopWords.add("ii"); // Roman numbers more then 1-character long
        commonStopWords.add("iii"); // Only the most likely to occur ;)
        commonStopWords.add("iv");
        commonStopWords.add("vi");
        commonStopWords.add("vii");
        commonStopWords.add("viii");
        commonStopWords.add("ix");
        commonStopWords.add("xi");
        commonStopWords.add("xii");
        commonStopWords.add("xiii");
        commonStopWords.add("xiv");
        commonStopWords.add("xv");
    }

    /**
     * @see com.stachoodev.carrot.filter.cluster.common.PreprocessingStrategy#preprocess(com.stachoodev.carrot.filter.cluster.common.Snippet)
     */
    public Snippet [] preprocess(AbstractClusteringContext clusteringContext)
    {
        Snippet [] snippets = clusteringContext.getSnippets();
        Snippet [] preprocessedSnippets = new Snippet[snippets.length];

        stopWordSets = ((MultilingualClusteringContext) clusteringContext).getStopWordSets();
        nonStopWordSets = ((MultilingualClusteringContext) clusteringContext).getNonStopWordSets();
        stemSets = ((MultilingualClusteringContext) clusteringContext).getStemSets();
        inflectedSets = ((MultilingualClusteringContext) clusteringContext).getInflectedSets();
        strongWords = ((MultilingualClusteringContext) clusteringContext).getStrongWords();
        queryWords = ((MultilingualClusteringContext) clusteringContext).getQueryWords();
        stemmers = ((MultilingualClusteringContext) clusteringContext).getStemmers();

        inflectedFreqSets = new HashMap();

        lowCaseWords = new HashSet();
        caseCheck = new HashMap();

        // Clean and guess language
        for (int i = 0; i < snippets.length; i++)
        {
            preprocessedSnippets[i] = preprocess(snippets[i]);
        }

        // Change "unidentified" to the most common language
        HashMap languageFreq = new HashMap();
        String mostCommonLanguage = MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME;
        int maxLanguageFreq = 0;

        for (int i = 0; i < preprocessedSnippets.length; i++)
        {
            if (!languageFreq.containsKey(preprocessedSnippets[i].getLanguage()))
            {
                languageFreq.put(preprocessedSnippets[i].getLanguage(), new Integer(1));

                if (
                    (maxLanguageFreq < 1)
                        && !preprocessedSnippets[i].getLanguage().equals(
                            MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME
                        )
                )
                {
                    maxLanguageFreq = 1;
                    mostCommonLanguage = preprocessedSnippets[i].getLanguage();
                }
            }
            else
            {
                int freq = ((Integer) languageFreq.get(preprocessedSnippets[i].getLanguage()))
                    .intValue();

                languageFreq.put(preprocessedSnippets[i].getLanguage(), new Integer(freq + 1));

                if (
                    (maxLanguageFreq < (freq + 1))
                        && !preprocessedSnippets[i].getLanguage().equals(
                            MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME
                        )
                )
                {
                    maxLanguageFreq = freq + 1;
                    mostCommonLanguage = preprocessedSnippets[i].getLanguage();
                }
            }
        }

        for (int i = 0; i < snippets.length; i++)
        {
            if (
                preprocessedSnippets[i].getLanguage().equals(
                        MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME
                    )
            )
            {
                preprocessedSnippets[i].setLanguage(mostCommonLanguage);
            }

            preprocessedSnippets[i] = stemming(preprocessedSnippets[i]);
        }

        // Create inflectedSets
        Iterator languages = inflectedFreqSets.keySet().iterator();

        while (languages.hasNext())
        {
            String language = (String) languages.next();
            HashMap inflectedFreq = (HashMap) inflectedFreqSets.get(language);

            HashMap inflected = new HashMap();
            inflectedSets.put(language, inflected);

            Iterator stems = inflectedFreq.keySet().iterator();

            while (stems.hasNext())
            {
                String stem = (String) stems.next();

                HashMap inflectedForStem = (HashMap) inflectedFreq.get(stem);

                if (inflectedForStem != null)
                {
                    int maxFreq = 0;
                    String bestInflected = stem;
                    Iterator inflecteds = inflectedForStem.keySet().iterator();

                    while (inflecteds.hasNext())
                    {
                        String infl = (String) inflecteds.next();
                        Integer freq = (Integer) inflectedForStem.get(infl);

                        if (freq.intValue() > maxFreq)
                        {
                            maxFreq = freq.intValue();
                            bestInflected = infl;
                        }
                    }

                    inflected.put(stem, bestInflected);
                }
            }
        }

        return preprocessedSnippets;
    }


    /**
     * Method clean.
     *
     * @param string
     *
     * @return String
     */
    protected Snippet preprocess(Snippet snippet)
    {
        String title = clean(snippet.getTitle());
        String body = clean(snippet.getBody());
        String language = guessLanguage(
                (title.equals("") ? ""
                                  : (title + " ")) + "." + (body.equals("") ? ""
                                                                            : (" " + body))
            );

        Snippet preprocessedSnippet = new Snippet(snippet.getId(), title, body, language);

        return preprocessedSnippet;
    }


    /**
     * @param snippet
     *
     * @return
     */
    protected Snippet stemming(Snippet snippet)
    {
        Snippet stemmedSnippet = new Snippet(
                snippet.getId(), stemming(snippet.getTitle(), snippet.getLanguage(), true),
                stemming(snippet.getBody(), snippet.getLanguage(), false), snippet.getLanguage()
            );

        return stemmedSnippet;
    }


    /**
     * @param text
     * @param language
     * @param strong
     */
    private String stemming(String text, String language, boolean strong)
    {
        StringBuffer stringBuffer = new StringBuffer();
        StringTokenizer stringTokenizer = new StringTokenizer(text);

        DirectStemmer stemmer = (DirectStemmer) stemmers.get(language);
        HashMap stems = (HashMap) stemSets.get(language);
        HashSet stopWords = (HashSet) stopWordSets.get(language);
        HashSet nonStopWords = (HashSet) nonStopWordSets.get(language);

        if (!inflectedFreqSets.containsKey(language))
        {
            inflectedFreqSets.put(language, new HashMap());
        }

        HashMap inflectedFreq = (HashMap) inflectedFreqSets.get(language);

        while (stringTokenizer.hasMoreTokens())
        {
            String token = stringTokenizer.nextToken();

            if (token.equals("."))
            {
                if (stringBuffer.length() > 0)
                {
                    stringBuffer.append(" .");
                }

                continue;
            }

            // Remove one-character-long terms          
            if (
                (token.length() < 2)
                    && ((stopWords == null)
                    || ((stopWords != null) && !stopWords.contains(token.toLowerCase())))
            )
            {
                continue;
            }

            // Remove overly long terms
            if (token.length() > 25)
            {
                continue;
            }

            // Case processing
            if (StringUtils.capitalizedRatio(token) > 0.5)
            {
                if (lowCaseWords.contains(token.toLowerCase()))
                {
                    token = token.toLowerCase();
                }
            }
            else
            {
                token = token.toLowerCase();
            }

            // Stemming
            if (
                (stemmer != null)
                    && !language.equalsIgnoreCase(
                        MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME
                    ) && !stopWords.contains(token)
            )
            {
                String stem;

                if (!stems.containsKey(token))
                {
                    synchronized (stemmer)
                    {
                        stem = stemmer.getStem(token.toCharArray(), 0, token.length());
                    }

                    if (stem != null) // ineffective !
                    {
                        stems.put(token, stem);
                    }
                    else
                    {
                        stems.put(token, token);
                    }
                }
                else
                {
                    stem = (String) stems.get(token);
                }

                if (!inflectedFreq.containsKey(stem))
                {
                    inflectedFreq.put(stem, new HashMap());
                }

                HashMap inflectedForStem = (HashMap) inflectedFreq.get(stem);

                if (!inflectedForStem.containsKey(token))
                {
                    inflectedForStem.put(token, new Integer(1));
                }
                else
                {
                    Integer freq = (Integer) inflectedForStem.get(token);
                    inflectedForStem.put(token, new Integer(freq.intValue() + 1));
                }

                token = (String) stems.get(token);
            }

            // Strong terms
            if (strong)
            {
                strongWords.add(token);
            }

            // Non-stop words
            if (
                language.equalsIgnoreCase(MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME)
                    || !stopWords.contains(token)
            )
            {
                nonStopWords.add(token);
            }

            if (stringBuffer.length() == 0)
            {
                stringBuffer.append(token);
            }
            else
            {
                stringBuffer.append(" ");
                stringBuffer.append(token);
            }
        }

        return stringBuffer.toString();
    }


    /**
     * @param snippet
     */
    private String guessLanguage(String text)
    {
        StringTokenizer stringTokenizer = new StringTokenizer(text);

        HashMap stopWordFrequencies = new HashMap();
        String language = MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME;
        int maxStopWordFrequency = 0;

        while (stringTokenizer.hasMoreTokens())
        {
            String token = stringTokenizer.nextToken();

            if (StringUtils.capitalizedRatio(token) > 0.5)
            {
                continue;
            }

            if (token.equals("."))
            {
                continue;
            }

            Iterator keys = stopWordSets.keySet().iterator();

            while (keys.hasNext())
            {
                String key = (String) keys.next();
                HashSet stopWords = (HashSet) stopWordSets.get(key);

                if (stopWords.contains(token))
                {
                    if (!stopWordFrequencies.containsKey(key))
                    {
                        stopWordFrequencies.put(key, new Integer(1));

                        if (1 > maxStopWordFrequency)
                        {
                            maxStopWordFrequency = 1;
                            language = key;
                        }
                    }
                    else
                    {
                        int stopWordFrequency = ((Integer) stopWordFrequencies.get(key)).intValue();
                        stopWordFrequencies.put(key, new Integer(stopWordFrequency + 1));

                        if ((stopWordFrequency + 1) > maxStopWordFrequency)
                        {
                            maxStopWordFrequency = stopWordFrequency + 1;
                            language = key;
                        }
                    }
                }
            }
        }

        // Check for "draws"
        boolean draw = false;
        HashSet values = new HashSet();

        for (Iterator val = stopWordFrequencies.values().iterator(); val.hasNext();)
        {
            Integer v = (Integer) val.next();

            if (v.intValue() == maxStopWordFrequency)
            {
                if (!values.contains(v))
                {
                    values.add(v);
                }
                else
                {
                    draw = true;

                    break;
                }
            }
        }

        return (draw ? MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME
                     : language);
    }

    /**
     * Regular expression for matching numbers. TODO: this code should be perhaps replaced with
     * tokenizer class from carrot-utils
     */
    private static final RE numberPattern;

    static
    {
        try
        {
            numberPattern = new gnu.regexp.RE("[0-9]+([.,][0-9]*)?");
        }
        catch (REException e)
        {
            System.err.println("Cannot compile regular expression pattern for numbers: " + e);
            throw new java.lang.Error(
                "Cannot compile regular expression pattern for numbers: " + e
            );
        }
    }

    /**
     * @param stringBuffer
     * @param stems
     * @param text
     */
    private String clean(String text)
    {
        StringBuffer stringBuffer = new StringBuffer();
        StringTokenizer stringTokenizer = new StringTokenizer(filterCharacters(text));

        boolean appendDelimiter = false;
        boolean prependDelimiter = false;
        boolean delimiterAdded = false;

        while (stringTokenizer.hasMoreTokens())
        {
            String token = stringTokenizer.nextToken();

            // Remove HTML entities
            if ((token.indexOf('&') >= 0) && (token.indexOf(';') >= 0))
            {
                continue;
            }

            // Remove web addresses
            if (token.indexOf('.') >= 0)
            {
                continue;
            }

            // Remove common stopwords
            if (commonStopWords.contains(token.toLowerCase()))
            {
                continue;
            }

            // Remove numbers
            if (numberPattern.isMatch(token))
            {
                continue;
            }

            // Remove delimiters from the beginning
            boolean moreDelimiters = true;

            while (moreDelimiters)
            {
                moreDelimiters = false;

                for (int i = 0; i < sentenceDelimiters.length; i++)
                {
                    if (token.startsWith(sentenceDelimiters[i]))
                    {
                        prependDelimiter = true;
                        moreDelimiters = true;
                        token = token.substring(1);

                        break;
                    }
                }
            }

            // Remove delimiters from the end
            moreDelimiters = true;

            while (moreDelimiters)
            {
                moreDelimiters = false;

                for (int i = 0; i < sentenceDelimiters.length; i++)
                {
                    if (token.endsWith(sentenceDelimiters[i]))
                    {
                        token = token.substring(0, token.length() - 1);
                        moreDelimiters = true;
                        appendDelimiter = true;

                        break;
                    }
                }
            }

            // Store the token for further case processing
            if (!caseCheck.containsKey(token.toLowerCase()))
            {
                caseCheck.put(token.toLowerCase(), token);
            }
            else
            {
                String originalCase = (String) caseCheck.get(token.toLowerCase());

                if (!token.equals(originalCase))
                {
                    lowCaseWords.add(token.toLowerCase());
                }
            }

            if (prependDelimiter && (stringBuffer.length() != 0) && !delimiterAdded) // don't prepend the "." at the beginning of the string
            {
                stringBuffer.append(". ");
                delimiterAdded = true;
            }

            if (token.length() > 0)
            {
                stringBuffer.append(token);
                stringBuffer.append(" ");
                delimiterAdded = false;
            }

            if (appendDelimiter && stringTokenizer.hasMoreTokens() && !delimiterAdded) // don't append the "." at the end of the string
            {
                stringBuffer.append(". ");
                delimiterAdded = true;
            }

            prependDelimiter = false;
            appendDelimiter = false;
        }

        return stringBuffer.toString().trim();
    }


    /**
     * @param text
     *
     * @return
     */
    private String filterCharacters(String text)
    {
        StringBuffer filtered = new StringBuffer();
        char [] chars = text.toCharArray();

        boolean spaceOccurred = false;
        boolean ampOccurred = false;

        for (int i = 0; i < chars.length; i++)
        {
            if (Character.isWhitespace(chars[i]))
            {
                filtered.append(chars[i]);
                spaceOccurred = true;
                ampOccurred = false;
            }

            if ((chars[i] == '&') && spaceOccurred)
            {
                filtered.append(chars[i]);
                ampOccurred = true;
                spaceOccurred = false;
            }

            if ((chars[i] == ';') && ampOccurred)
            {
                filtered.append(chars[i]);
                ampOccurred = false;
                spaceOccurred = false;
            }

            if (Character.isLetterOrDigit(chars[i]))
            {
                filtered.append(chars[i]);
                spaceOccurred = false;
            }

            if ((chars[i] == '-') || (chars[i] == '/'))
            {
                filtered.append(' ');
                spaceOccurred = false;
            }

            if ((chars[i] == '?') || (chars[i] == '.') || (chars[i] == '!'))
            {
                filtered.append('.');
                spaceOccurred = false;
            }
        }

        return filtered.toString();
    }
}
