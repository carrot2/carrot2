

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


import java.util.*;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.util.StringUtils;
import com.stachoodev.util.log.TimeLogger;
import com.stachoodev.util.suffixarrays.*;
import com.stachoodev.util.suffixarrays.wrapper.Substring;
import com.stachoodev.util.suffixarrays.wrapper.SubstringComparator;


/**
 * @author stachoo
 */
public class MultilingualFeatureExtractionStrategy
    implements FeatureExtractionStrategy
{
    /** */
    private static final double STRONG_TERM_SCALING = 2.5;

    /** */
    private static final Logger logger = Logger.getLogger(
            MultilingualFeatureExtractionStrategy.class
        );

    /** */
    private Snippet [] preprocessedSnippets;

    /** */
    protected HashMap inflectedSets;
    protected HashMap stopWordSets;
    private HashSet queryWords;
    private HashSet strongWords;

    /**
     * @see com.stachoodev.carrot.filter.cluster.common.FeatureExtractionStrategy#extractFeatures(com.stachoodev.carrot.filter.cluster.common.ClusteringContext)
     */
    public Feature [] extractFeatures(AbstractClusteringContext clusteringContext)
    {
        this.queryWords = clusteringContext.getQueryWords();
        this.strongWords = clusteringContext.getStrongWords();
        this.stopWordSets = ((MultilingualClusteringContext) clusteringContext).getStopWordSets();
        this.inflectedSets = ((MultilingualClusteringContext) clusteringContext).getInflectedSets();

        TimeLogger timeLogger = new TimeLogger();
        timeLogger.start();

        // Sort the snippets by language
        preprocessedSnippets = clusteringContext.getPreprocessedSnippets();
        Arrays.sort(
            preprocessedSnippets,
            new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    Snippet s1 = (Snippet) o1;
                    Snippet s2 = (Snippet) o2;

                    return s1.getLanguage().compareTo(s2.getLanguage());
                }
            }
        );

        // Integer wrappers for suffix sorting
        MultilingualSnippetsIntWrapper snippetsIntWrapper = new MultilingualSnippetsIntWrapper(
                preprocessedSnippets,
                ((MultilingualClusteringContext) clusteringContext).getStopWordSets()
            );
        MultilingualSnippetsIntWrapper snippetsIntWrapperReversed = (MultilingualSnippetsIntWrapper) snippetsIntWrapper
            .clone();
        snippetsIntWrapperReversed.reverse();
        timeLogger.logElapsedAndStart(logger, "wrappers()");

        //		
        // Extract single terms
        //
        timeLogger.start();

        Feature [] singleTerms = extractSingleTerms(snippetsIntWrapper);

        // Sort the single terms (most frequent non-stop terms first)
        Arrays.sort(
            singleTerms,
            new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    Feature f1 = (Feature) o1;
                    Feature f2 = (Feature) o2;

                    if (f1.isStopWord() && !f2.isStopWord())
                    {
                        return 1;
                    }

                    if (!f1.isStopWord() && f2.isStopWord())
                    {
                        return -1;
                    }

                    if (f1.getTf() > f2.getTf())
                    {
                        return -1;
                    }

                    if (f1.getTf() < f2.getTf())
                    {
                        return 1;
                    }

                    return f1.getText().compareToIgnoreCase(f2.getText());
                }
            }
        );

        // Count the non-stop single term features
        int singleTermFeaturesCount = 0;

        for (
            singleTermFeaturesCount = 0; singleTermFeaturesCount < singleTerms.length;
                singleTermFeaturesCount++
        )
        {
            if (singleTerms[singleTermFeaturesCount].isStopWord())
            {
                break;
            }
        }

        // Create index mapping array (term code -> index in sorted singleTerms)
        int [] indexMapping = new int[singleTerms.length];

        for (int i = 0; i < indexMapping.length; i++)
        {
            indexMapping[singleTerms[i].getCode()] = i;
        }

        timeLogger.logElapsedAndStart(logger, "extractSingleTerms()");

        //
        // Extract phrase terms
        //
        // Do the suffix sorting
        LcpSuffixSorter lcpSuffixSorter = new DummyLcpSuffixSorter();
        HashMap lcpSuffixArrays = new HashMap();
        HashMap lcpSuffixArraysReversed = new HashMap();
        String [] languageNames = snippetsIntWrapper.getLanguageNames();

        for (int i = 0; i < languageNames.length; i++)
        {
            lcpSuffixArrays.put(
                languageNames[i],
                lcpSuffixSorter.lcpSuffixSort(
                    snippetsIntWrapper.getWrapperForLanguage(languageNames[i])
                )
            );

            lcpSuffixArraysReversed.put(
                languageNames[i],
                lcpSuffixSorter.lcpSuffixSort(
                    snippetsIntWrapperReversed.getWrapperForLanguage(languageNames[i])
                )
            );
        }

        timeLogger.logElapsedAndStart(logger, "suffixSorting()");

        // Extract phrase terms
        ArrayList featuresArrayList = new ArrayList();

        for (int i = 0; i < languageNames.length; i++)
        {
            Feature [] phrases = extractPhraseTerms(
                    indexMapping, (LcpSuffixArray) lcpSuffixArrays.get(languageNames[i]),
                    (LcpSuffixArray) lcpSuffixArraysReversed.get(languageNames[i]),
                    snippetsIntWrapper, snippetsIntWrapper.getWrapperForLanguage(languageNames[i]),
                    snippetsIntWrapperReversed.getWrapperForLanguage(languageNames[i])
                );

            featuresArrayList.addAll(Arrays.asList(phrases));
        }

        Feature [] phraseTerms = (Feature []) featuresArrayList.toArray(
                new Feature[featuresArrayList.size()]
            );
        timeLogger.logElapsedAndStart(logger, "extractPhraseTerms()");

        // Create a naive unstemmed version 
        for (int i = 0; i < phraseTerms.length; i++)
        {
            int [] phraseFeatureIndices = phraseTerms[i].getPhraseFeatureIndices();
            StringBuffer unstemmed = new StringBuffer(
                    singleTerms[phraseFeatureIndices[0]].getText()
                );

            for (int j = 1; j < phraseFeatureIndices.length; j++)
            {
                unstemmed.append(" ");
                unstemmed.append(singleTerms[phraseFeatureIndices[j]].getText());
            }

            phraseTerms[i].setText(unstemmed.toString());
        }

        timeLogger.logElapsedAndStart(logger, "unstemming()");

        // Sort the phrase terms (most frequent first)
        Arrays.sort(
            phraseTerms,
            new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    Feature f1 = (Feature) o1;
                    Feature f2 = (Feature) o2;

                    if (f1.getTf() < f2.getTf())
                    {
                        return 1;
                    }

                    if (f1.getTf() > f2.getTf())
                    {
                        return -1;
                    }

                    return f1.getText().compareToIgnoreCase(f2.getText());
                }
            }
        );

        // Combine
        Feature [] combined = new Feature[singleTerms.length + phraseTerms.length];
        System.arraycopy(singleTerms, 0, combined, 0, singleTerms.length);
        System.arraycopy(phraseTerms, 0, combined, singleTerms.length, phraseTerms.length);

        timeLogger.logElapsedAndStart(logger, "sortAndCombine()");

        return combined;
    }


    /**
     * Note: the returned array is indexed by term intWrapper term codes
     *
     * @return Substring[]
     */
    private Feature [] extractSingleTerms(AbstractSnippetsIntWrapper snippetsIntWrapper)
    {
        HashSet stopWords;
        HashMap inflected;

        int termCount = snippetsIntWrapper.getDistinctWordCount();
        int [] intData = snippetsIntWrapper.asIntArray();
        int [] documentIndices = snippetsIntWrapper.getDocumentIndices();

        // The indices of the documents given term appears in (0 - the number of indices)
        int [][] termDocument = new int[termCount][snippetsIntWrapper.getDocumentCount()];
        Feature [] singleTerms = new Feature[termCount];

        // Build term-document index		
        for (int i = 0; i < (intData.length - 1); i++) // intData ends with -1
        {
            if (intData[i] < termCount) // skip delimiter codes (0x7fffffff)
            {
                if (singleTerms[intData[i]] == null)
                {
                    String textStemmed = snippetsIntWrapper.getStringRepresentation(i, i + 1);

                    singleTerms[intData[i]] = new Feature(textStemmed, intData[i], 1, 1);

                    // Is the feature strong ?
                    singleTerms[intData[i]].setStrong(strongWords.contains(textStemmed));
                }
                else
                {
                    singleTerms[intData[i]].increaseTf(1);
                }

                termDocument[intData[i]][documentIndices[i]]++;
            }
        }

        // Set snippetIndices arrays and tfidf
        for (int i = 0; i < singleTerms.length; i++)
        {
            // For strong words - correct the frequencies
            if (singleTerms[i].isStrong())
            {
                for (int j = 0; j < termDocument[i].length; j++)
                {
                    termDocument[i][j] = (int) ((double) termDocument[i][j] * STRONG_TERM_SCALING);
                }

                singleTerms[i].increaseTf(
                    (int) ((double) singleTerms[i].getTf() * (STRONG_TERM_SCALING - 1))
                );
            }

            // Snippet indices
            int len = 0;

            for (int j = 0; j < termDocument[i].length; j++)
            {
                if (termDocument[i][j] > 0)
                {
                    len++;
                }
            }

            int [] snippetIndices = new int[len];
            int k = 0;

            for (int j = 0; j < snippetIndices.length; j++)
            {
                while (termDocument[i][k] == 0)
                {
                    k++;
                }

                snippetIndices[j] = k++;
            }

            singleTerms[i].setSnippetIndices(snippetIndices);

            // snippet tf
            singleTerms[i].setSnippetTf(termDocument[i]);

            // idf
            singleTerms[i].setIdf(
                Math.log(
                    (double) snippetsIntWrapper.getDocumentCount() / (double) snippetIndices.length
                )
            );
        }

        // Unstemming, stop-words marking
        for (int i = 0; i < singleTerms.length; i++)
        {
            String textStemmed = singleTerms[i].getText();

            // Determine the language
            String language = MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME;
            int maxFreq = 0;
            int [] snippetIndices = singleTerms[i].getSnippetIndices();
            int [] snippetTf = singleTerms[i].getSnippetTf();
            HashMap languageFreq = new HashMap();

            for (int j = 0; j < snippetIndices.length; j++)
            {
                String snippetLanguage = preprocessedSnippets[snippetIndices[j]].getLanguage();

                if (!languageFreq.containsKey(snippetLanguage))
                {
                    languageFreq.put(snippetLanguage, new Integer(0));
                }

                int freq = ((Integer) languageFreq.get(snippetLanguage)).intValue()
                    + snippetTf[snippetIndices[j]];
                languageFreq.put(snippetLanguage, new Integer(freq));

                if (freq > maxFreq)
                {
                    maxFreq = freq;
                    language = snippetLanguage;
                }
            }

            singleTerms[i].setLanguage(language);

            // Store inflected text
            inflected = (HashMap) inflectedSets.get(language);

            if ((inflected != null) && inflected.containsKey(textStemmed))
            {
                if (StringUtils.capitalizedRatio((String) inflected.get(textStemmed)) <= 0.5)
                {
                    singleTerms[i].setText(
                        StringUtils.capitalize((String) inflected.get(textStemmed))
                    );
                }
                else
                {
                    singleTerms[i].setText((String) inflected.get(textStemmed));
                }
            }
            else
            {
                if (StringUtils.capitalizedRatio(textStemmed) <= 0.5)
                {
                    singleTerms[i].setText(StringUtils.capitalize(textStemmed));
                }
                else
                {
                    singleTerms[i].setText(textStemmed);
                }
            }

            // Mark stopwords
            stopWords = (HashSet) stopWordSets.get(language);

            if ((stopWords != null) && stopWords.contains(singleTerms[i].getText().toLowerCase()))
            {
                singleTerms[i].setStopWord(true);
                singleTerms[i].setText(singleTerms[i].getText().toLowerCase());
            }

            // Query words are also omitted
            if (queryWords.contains(textStemmed.toLowerCase()))
            {
                singleTerms[i].setQueryWord(true);
                singleTerms[i].setStopWord(true);
            }
        }

        return singleTerms;
    }


    /**
     *
     */
    private Feature [] extractPhraseTerms(
        int [] indexMapping, LcpSuffixArray lcpSuffixArray, LcpSuffixArray lcpSuffixArrayReversed,
        AbstractSnippetsIntWrapper globalSnippetsIntWrapper,
        AbstractSnippetsIntWrapper snippetsIntWrapper,
        AbstractSnippetsIntWrapper snippetsIntWrapperReversed
    )
    {
        Substring [] cs = extractFeatures(
                lcpSuffixArray, lcpSuffixArrayReversed, snippetsIntWrapper,
                snippetsIntWrapperReversed
            );

        int [] suffixArray = lcpSuffixArray.getSuffixArray();
        int [] lcpArray = lcpSuffixArray.getLcpArray();
        int [] documentIndices = snippetsIntWrapper.getDocumentIndices();

        int [] termDocument = new int[globalSnippetsIntWrapper.getDocumentCount()];
        Feature [] phraseTerms = new Feature[cs.length];

        for (int term = 0; term < phraseTerms.length; term++)
        {
            int termLength = cs[term].length();
            int j = 0;
            int tf = 0;

            Arrays.fill(termDocument, 0);

            // Search up and down the lcp/suffix array
            j = cs[term].getId();

            while (lcpArray[j] >= termLength)
            {
                tf++;
                termDocument[documentIndices[suffixArray[j]]] += 1;
                j++;
            }

            j = cs[term].getId();

            while (lcpArray[j] >= termLength)
            {
                tf++;
                j--;
                termDocument[documentIndices[suffixArray[j]]] += 1;
            }

            // Compact the termDocument array
            int len = 0;

            for (int i = 0; i < termDocument.length; i++)
            {
                if (termDocument[i] > 0)
                {
                    len++;
                }
            }

            int [] snippetIndices = new int[len];
            int k = 0;

            for (int i = 0; i < snippetIndices.length; i++)
            {
                while (termDocument[k] == 0)
                {
                    k++;
                }

                snippetIndices[i] = k++;
            }

            phraseTerms[term] = new Feature(
                    snippetsIntWrapper.getStringRepresentation(cs[term]), -1, cs[term].length(), tf
                );
            phraseTerms[term].setSnippetIndices(snippetIndices);

            // Link phrase features indices
            int [] phraseFeatureIndices = new int[phraseTerms[term].getLength()];

            for (int l = 0; l < phraseFeatureIndices.length; l++)
            {
                phraseFeatureIndices[l] = indexMapping[snippetsIntWrapper.asIntArray()[cs[term]
                    .getFrom() + l]];
            }

            phraseTerms[term].setPhraseFeatureIndices(phraseFeatureIndices);

            // Set snippet tf
            phraseTerms[term].setSnippetTf((int []) termDocument.clone());

            // Set idf
            phraseTerms[term].setIdf(
                Math.log((double) snippetsIntWrapper.getDocumentCount() / (double) len)
            );
        }

        return phraseTerms;
    }


    /**
     *
     */
    private Substring [] extractFeatures(
        LcpSuffixArray lcpSuffixArray, LcpSuffixArray lcpSuffixArrayReversed,
        AbstractSnippetsIntWrapper snippetsIntWrapper,
        AbstractSnippetsIntWrapper snippetsIntWrapperReversed
    )
    {
        // Find RCS and LCS
        Substring [] rcs = discoverRcs(lcpSuffixArray, snippetsIntWrapper);
        Substring [] lcs = discoverRcs(lcpSuffixArrayReversed, snippetsIntWrapperReversed);

        SubstringComparator comparator = new SubstringComparator(snippetsIntWrapper);

        // Sort the RCS alphabetically
        Arrays.sort(rcs, comparator);

        // Reverse the LCS again (to make the substrings plain)
        for (int i = 0; i < lcs.length; i++)
        {
            lcs[i].reverse(snippetsIntWrapper.length());
        }

        // Sort the LCS alphabetically
        Arrays.sort(lcs, comparator);

        // Intersect the RSC and LCS
        int i = 0;
        int j = 0;
        Vector result = new Vector();

        while ((i < lcs.length) && (j < rcs.length))
        {
            Substring l = lcs[i];
            Substring r = rcs[j];

            if (comparator.compare(l, r) == 0)
            {
                if (rcs[j].length() > 1) // extract phrases longer than 1
                {
                    rcs[j].setStringRepresentation(snippetsIntWrapper);

                    if (
                        (result.size() == 0)
                            || !((Substring) result.get(result.size() - 1)).getStringRepresentation()
                                     .equalsIgnoreCase(rcs[j].getStringRepresentation())
                    )
                    {
                        result.add(rcs[j]);
                    }
                }

                i++;
                j++;

                continue;
            }

            if (comparator.compare(l, r) < 0)
            {
                i++;

                continue;
            }

            if (comparator.compare(l, r) > 0)
            {
                j++;

                continue;
            }
        }

        Substring [] completeSubstrings = (Substring []) result.toArray(
                new Substring[result.size()]
            );

        // Sort the substring by length ascendingly
        Arrays.sort(
            completeSubstrings,
            new Comparator()
            {
                public int compare(Object s1, Object s2)
                {
                    if (((Substring) s1).length() > ((Substring) s2).length())
                    {
                        return 1;
                    }
                    else if (((Substring) s1).length() < ((Substring) s2).length())
                    {
                        return -1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        );

        return completeSubstrings;
    }


    /**
     * Discovers Right Complete Substrings in the given LCP Suffix Array.
     *
     * @return Substring [] the discovered Right Complete substrings
     */
    private Substring [] discoverRcs(
        LcpSuffixArray lcpSuffixArray, AbstractSnippetsIntWrapper snippetsIntWrapper
    )
    {
        if (lcpSuffixArray.getSuffixArray().length == 0)
        {
            return new Substring[0];
        }

        int [] lcpArray = lcpSuffixArray.getLcpArray();
        int [] suffixArray = lcpSuffixArray.getSuffixArray();
        int [] stopWords = snippetsIntWrapper.getStopWordCodes();
        int [] intArray = snippetsIntWrapper.asIntArray();

        Substring [] rcsStack = new Substring[lcpArray.length];
        int sp = -1;
        int i = 1;

        Vector result = new Vector();

        while (i < lcpArray.length)
        {
            if (sp < 0)
            {
                // The impact on complexity ?
                if (
                    (lcpArray[i] > 0)
                        && (Arrays.binarySearch(stopWords, intArray[suffixArray[i]]) < 0)
                )
                {
                    sp++;
                    rcsStack[sp] = new Substring(
                            i, suffixArray[i], suffixArray[i] + lcpArray[i], 2
                        );
                }

                i++;
            }
            else
            {
                int r = rcsStack[sp].getId();

                if (lcpArray[r] < lcpArray[i])
                {
                    sp++;
                    rcsStack[sp] = new Substring(
                            i, suffixArray[i], suffixArray[i] + lcpArray[i], 2
                        );
                    i++;
                }
                else if (lcpArray[r] == lcpArray[i])
                {
                    rcsStack[sp].increaseFrequency(1);
                    i++;
                }
                else
                {
                    // The impact on complexity ?
                    if (
                        (Arrays.binarySearch(stopWords, intArray[rcsStack[sp].getTo() - 1]) >= 0)
                            && (rcsStack[sp].length() > 1)
                    )
                    {
                        result.add(
                            new Substring(
                                rcsStack[sp].getId(), rcsStack[sp].getFrom(),
                                rcsStack[sp].getTo() - 1, rcsStack[sp].getFrequency()
                            )
                        );
                    }
                    else
                    {
                        result.add(rcsStack[sp]);
                    }

                    Substring s = rcsStack[sp];
                    sp--;

                    if (sp >= 0)
                    {
                        rcsStack[sp].increaseFrequency(s.getFrequency() - 1);
                    }

                    // should be here in order for the algorithm to work properly
                    if ((sp < 0) && (lcpArray[i] > 0))
                    {
                        sp++;
                        rcsStack[sp] = new Substring(
                                i, suffixArray[i], suffixArray[i] + lcpArray[i],
                                (2 + rcsStack[sp].getFrequency()) - 1
                            );
                        i++;
                    }
                }
            }
        }

        return (Substring []) result.toArray(new Substring[result.size()]);
    }
}
