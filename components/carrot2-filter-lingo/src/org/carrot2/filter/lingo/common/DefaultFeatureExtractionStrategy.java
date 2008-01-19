
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


import org.carrot2.filter.lingo.util.log.TimeLogger;
import org.carrot2.filter.lingo.util.suffixarrays.*;
import org.carrot2.filter.lingo.util.suffixarrays.wrapper.Substring;
import org.carrot2.filter.lingo.util.suffixarrays.wrapper.SubstringComparator;
import org.carrot2.util.StringUtils;

import org.apache.log4j.Logger;

import java.util.*;


/**
 * @author stachoo
 */
public class DefaultFeatureExtractionStrategy
    implements FeatureExtractionStrategy {
    /** */

    /** DOCUMENT ME! */
    private static final double STRONG_TERM_SCALING = 2.5;

    /** */

    /** DOCUMENT ME! */
    private static final Logger logger = Logger.getLogger(DefaultFeatureExtractionStrategy.class);

    /** */

    /** DOCUMENT ME! */
    private LcpSuffixArray lcpSuffixArray;

    /** DOCUMENT ME! */
    private LcpSuffixArray lcpSuffixArrayReversed;

    /** */

    /** DOCUMENT ME! */
    private AbstractSnippetsIntWrapper snippetsIntWrapper;

    /** DOCUMENT ME! */
    private AbstractSnippetsIntWrapper snippetsIntWrapperReversed;

    /** */

    /** DOCUMENT ME! */
    private HashSet stopWords;

    /** DOCUMENT ME! */
    private HashSet queryWords;

    /** DOCUMENT ME! */
    private HashSet strongWords;

    /** DOCUMENT ME! */
    private HashMap inflected;

    public Feature[] extractFeatures(
        AbstractClusteringContext clusteringContext) {
        this.stopWords = ((DefaultClusteringContext) clusteringContext).getStopWords();
        this.inflected = ((DefaultClusteringContext) clusteringContext).getInflected();
        this.queryWords = ((DefaultClusteringContext) clusteringContext).getQueryWords();
        this.strongWords = ((DefaultClusteringContext) clusteringContext).getStrongWords();

        TimeLogger timeLogger = new TimeLogger();
        timeLogger.start();

        // Input data
        Snippet[] preprocessedSnippets = clusteringContext.getPreprocessedSnippets();

        // Integer wrappers for suffix sorting
        snippetsIntWrapper = new DefaultSnippetsIntWrapper(preprocessedSnippets,
                stopWords);
        snippetsIntWrapperReversed = new DefaultSnippetsIntWrapper(preprocessedSnippets,
                stopWords);
        snippetsIntWrapperReversed.reverse();
        timeLogger.logElapsedAndStart(logger, "wrappers()");

        // Do the suffix sorting
        LcpSuffixSorter lcpSuffixSorter = new DummyLcpSuffixSorter();
        lcpSuffixArray = lcpSuffixSorter.lcpSuffixSort(snippetsIntWrapper);
        lcpSuffixArrayReversed = lcpSuffixSorter.lcpSuffixSort(snippetsIntWrapperReversed);
        timeLogger.logElapsedAndStart(logger, "suffixSorting()");

        // Extract single terms
        timeLogger.start();

        Feature[] singleTerms = extractSingleTerms();
        timeLogger.logElapsedAndStart(logger, "extractSingleTerms()");

        // Sort the single terms (most frequent non-stop terms first)
        Arrays.sort(singleTerms,
            new Comparator() {
                public int compare(Object o1, Object o2) {
                    Feature f1 = (Feature) o1;
                    Feature f2 = (Feature) o2;

                    if (f1.isStopWord() && !f2.isStopWord()) {
                        return 1;
                    }

                    if (!f1.isStopWord() && f2.isStopWord()) {
                        return -1;
                    }

                    if (f1.getTf() > f2.getTf()) {
                        return -1;
                    }

                    if (f1.getTf() < f2.getTf()) {
                        return 1;
                    }

                    return f1.getText().compareToIgnoreCase(f2.getText());
                }
            });

        // Count the non-stop single term features
        int singleTermFeaturesCount = 0;

        for (singleTermFeaturesCount = 0;
                singleTermFeaturesCount < singleTerms.length;
                singleTermFeaturesCount++) {
            if (singleTerms[singleTermFeaturesCount].isStopWord()) {
                break;
            }
        }

        // Create index mapping array (term code -> index in sorted singleTerms)
        int[] indexMapping = new int[singleTerms.length];

        for (int i = 0; i < indexMapping.length; i++) {
            indexMapping[singleTerms[i].getCode()] = i;
        }

        // Extract phrase terms
        timeLogger.start();

        Feature[] phraseTerms = extractPhraseTerms(indexMapping);
        timeLogger.logElapsedAndStart(logger, "extractPhraseTerms()");

        // Create a naive unstemmed version 
        for (int i = 0; i < phraseTerms.length; i++) {
            int[] phraseFeatureIndices = phraseTerms[i].getPhraseFeatureIndices();
            StringBuffer unstemmed = new StringBuffer(StringUtils.capitalize(
                        singleTerms[phraseFeatureIndices[0]].getText()));

            for (int j = 1; j < phraseFeatureIndices.length; j++) {
                unstemmed.append(" ");
                unstemmed.append(singleTerms[phraseFeatureIndices[j]].getText());
            }

            phraseTerms[i].setText(unstemmed.toString());
        }

        timeLogger.logElapsedAndStart(logger, "unstemming()");

        // Sort the phrase terms (most frequent first)
        Arrays.sort(phraseTerms,
            new Comparator() {
                public int compare(Object o1, Object o2) {
                    Feature f1 = (Feature) o1;
                    Feature f2 = (Feature) o2;

                    if (f1.getTf() < f2.getTf()) {
                        return 1;
                    }

                    if (f1.getTf() > f2.getTf()) {
                        return -1;
                    }

                    return f1.getText().compareToIgnoreCase(f2.getText());
                }
            });

        // Combine
        Feature[] combined = new Feature[singleTerms.length +
            phraseTerms.length];
        System.arraycopy(singleTerms, 0, combined, 0, singleTerms.length);
        System.arraycopy(phraseTerms, 0, combined, singleTerms.length,
            phraseTerms.length);

        timeLogger.logElapsedAndStart(logger, "sortAndCombine()");

        return combined;
    }

    /**
     * Note: the returned array is indexed by term intWrapper term codes
     *
     * @return Substring[]
     */
    private Feature[] extractSingleTerms() {
        int termCount = snippetsIntWrapper.getDistinctWordCount();
        int[] intData = snippetsIntWrapper.asIntArray();
        int[] documentIndices = snippetsIntWrapper.getDocumentIndices();

        // The indices of the documents given term appears in (0 - the number of indices)
        int[][] termDocument = new int[termCount][snippetsIntWrapper.getDocumentCount()];
        Feature[] singleTerms = new Feature[termCount];

        for (int i = 0; i < (intData.length - 1); i++) // intData ends with -1
         {
            if (intData[i] < termCount) // skip delimiter codes (0x7fffffff)
             {
                if (singleTerms[intData[i]] == null) {
                    String textStemmed = snippetsIntWrapper.getStringRepresentation(i,
                            i + 1);

                    singleTerms[intData[i]] = new Feature("", intData[i], 1, 1);

                    // Is the feature strong ?
                    singleTerms[intData[i]].setStrong(strongWords.contains(
                            textStemmed));

                    // Store inflected text
                    if (inflected.containsKey(textStemmed)) {
                        singleTerms[intData[i]].setText(StringUtils.capitalize(
                                (String) inflected.get(textStemmed)));
                    } else {
                        singleTerms[intData[i]].setText(StringUtils.capitalize(
                                textStemmed));
                    }

                    // Mark stopwords
                    if (stopWords.contains(singleTerms[intData[i]].getText()
                                                                      .toLowerCase())) {
                        singleTerms[intData[i]].setStopWord(true);
                        singleTerms[intData[i]].setText(singleTerms[intData[i]].getText()
                                                                               .toLowerCase());
                    }

                    // Query words are also omitted
                    if (queryWords.contains(textStemmed)) {
                        singleTerms[intData[i]].setQueryWord(true);
                        singleTerms[intData[i]].setStopWord(true);
                    }
                } else {
                    singleTerms[intData[i]].increaseTf(1);
                }

                termDocument[intData[i]][documentIndices[i]]++;
            }
        }

        // Set snippetIndices arrays and tfidf
        for (int i = 0; i < singleTerms.length; i++) {
            // For strong words - correct the frequencies
            if (singleTerms[i].isStrong()) {
                for (int j = 0; j < termDocument[i].length; j++) {
                    termDocument[i][j] = (int) (termDocument[i][j] * STRONG_TERM_SCALING);
                }

                singleTerms[i].increaseTf((int) (singleTerms[i].getTf() * (STRONG_TERM_SCALING -
                    1)));
            }

            // Snippet indices
            int len = 0;

            for (int j = 0; j < termDocument[i].length; j++) {
                if (termDocument[i][j] > 0) {
                    len++;
                }
            }

            int[] snippetIndices = new int[len];
            int k = 0;

            for (int j = 0; j < snippetIndices.length; j++) {
                while (termDocument[i][k] == 0) {
                    k++;
                }

                snippetIndices[j] = k++;
            }

            singleTerms[i].setSnippetIndices(snippetIndices);

            // snippet tf
            singleTerms[i].setSnippetTf(termDocument[i]);

            // idf
            singleTerms[i].setIdf(Math.log(
                    (double) snippetsIntWrapper.getDocumentCount() / (double) snippetIndices.length));
        }

        return singleTerms;
    }

    /**
     *
     */
    private Feature[] extractPhraseTerms(int[] indexMapping) {
        Substring[] cs = extractFeatures();

        int[] suffixArray = lcpSuffixArray.getSuffixArray();
        int[] lcpArray = lcpSuffixArray.getLcpArray();
        int[] documentIndices = snippetsIntWrapper.getDocumentIndices();

        int[] termDocument = new int[snippetsIntWrapper.getDocumentCount()];
        Feature[] phraseTerms = new Feature[cs.length];

        for (int term = 0; term < phraseTerms.length; term++) {
            int termLength = cs[term].length();
            int j = 0;
            int tf = 0;

            Arrays.fill(termDocument, 0);

            // Search up and down the lcp/suffix array
            j = cs[term].getId();

            while (lcpArray[j] >= termLength) {
                tf++;
                termDocument[documentIndices[suffixArray[j]]] += 1;
                j++;
            }

            j = cs[term].getId();

            while (lcpArray[j] >= termLength) {
                tf++;
                j--;
                termDocument[documentIndices[suffixArray[j]]] += 1;
            }

            // Compact the termDocument array
            int len = 0;

            for (int i = 0; i < termDocument.length; i++) {
                if (termDocument[i] > 0) {
                    len++;
                }
            }

            int[] snippetIndices = new int[len];
            int k = 0;

            for (int i = 0; i < snippetIndices.length; i++) {
                while (termDocument[k] == 0) {
                    k++;
                }

                snippetIndices[i] = k++;
            }

            phraseTerms[term] = new Feature(snippetsIntWrapper.getStringRepresentation(
                        cs[term]), -1, cs[term].length(), tf);
            phraseTerms[term].setSnippetIndices(snippetIndices);

            // Link phrase features indices and (naively) create "unstemmed" original
            int[] phraseFeatureIndices = new int[phraseTerms[term].getLength()];

            for (int l = 0; l < phraseFeatureIndices.length; l++) {
                phraseFeatureIndices[l] = indexMapping[snippetsIntWrapper.asIntArray()[cs[term].getFrom() +
                    l]];
            }

            phraseTerms[term].setPhraseFeatureIndices(phraseFeatureIndices);

            // Set snippet tf
            phraseTerms[term].setSnippetTf((int[]) termDocument.clone());

            // Set idf
            phraseTerms[term].setIdf(Math.log(
                    (double) snippetsIntWrapper.getDocumentCount() / (double) len));
        }

        return phraseTerms;
    }

    /**
     *
     */
    private Substring[] extractFeatures() {
        // Find RCS and LCS
        Substring[] rcs = discoverRcs(lcpSuffixArray, snippetsIntWrapper);
        Substring[] lcs = discoverRcs(lcpSuffixArrayReversed,
                snippetsIntWrapperReversed);

        SubstringComparator comparator = new SubstringComparator(snippetsIntWrapper);

        // Sort the RCS alphabetically
        Arrays.sort(rcs, comparator);

        // Reverse the LCS again (to make the substrings plain)
        for (int i = 0; i < lcs.length; i++) {
            lcs[i].reverse(snippetsIntWrapper.length());
        }

        // Sort the LCS alphabetically
        Arrays.sort(lcs, comparator);

        // Intersect the RSC and LCS
        int i = 0;
        int j = 0;
        Vector result = new Vector();

        while ((i < lcs.length) && (j < rcs.length)) {
            Substring l = lcs[i];
            Substring r = rcs[j];

            if (comparator.compare(l, r) == 0) {
                rcs[j].setStringRepresentation(snippetsIntWrapper);

                if (rcs[j].length() > 1) // extract phrases longer than 1
                 {
                    result.add(rcs[j]);
                }

                i++;
                j++;

                continue;
            }

            if (comparator.compare(l, r) < 0) {
                i++;

                continue;
            }

            if (comparator.compare(l, r) > 0) {
                j++;

                continue;
            }
        }

        Substring[] completeSubstrings = (Substring[]) result.toArray(new Substring[result.size()]);

        // Sort the substring by length ascendingly
        Arrays.sort(completeSubstrings,
            new Comparator() {
                public int compare(Object s1, Object s2) {
                    if (((Substring) s1).length() > ((Substring) s2).length()) {
                        return 1;
                    } else if (((Substring) s1).length() < ((Substring) s2).length()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

        return completeSubstrings;
    }

    /**
     * Discovers Right Complete Substrings in the given LCP Suffix Array.
     *
     * @return Substring [] the discovered Right Complete substrings
     */
    private Substring[] discoverRcs(LcpSuffixArray lcpSuffixArray,
        AbstractSnippetsIntWrapper snippetsIntWrapper) {
        if (lcpSuffixArray.getSuffixArray().length == 0) {
            return new Substring[0];
        }

        int[] lcpArray = lcpSuffixArray.getLcpArray();
        int[] suffixArray = lcpSuffixArray.getSuffixArray();
        int[] stopWords = snippetsIntWrapper.getStopWordCodes();
        int[] intArray = snippetsIntWrapper.asIntArray();

        Substring[] rcsStack = new Substring[lcpArray.length];
        int sp = -1;
        int i = 1;

        Vector result = new Vector();

        while (i < lcpArray.length) {
            if (sp < 0) {
                // The impact on complexity ?
                if ((lcpArray[i] > 0) &&
                        (Arrays.binarySearch(stopWords, intArray[suffixArray[i]]) < 0)) {
                    sp++;
                    rcsStack[sp] = new Substring(i, suffixArray[i],
                            suffixArray[i] + lcpArray[i], 2);
                }

                i++;
            } else {
                int r = rcsStack[sp].getId();

                if (lcpArray[r] < lcpArray[i]) {
                    sp++;
                    rcsStack[sp] = new Substring(i, suffixArray[i],
                            suffixArray[i] + lcpArray[i], 2);
                    i++;
                } else if (lcpArray[r] == lcpArray[i]) {
                    rcsStack[sp].increaseFrequency(1);
                    i++;
                } else {
                    // The impact on complexity ?
                    if ((Arrays.binarySearch(stopWords,
                                intArray[rcsStack[sp].getTo() - 1]) >= 0) &&
                            (rcsStack[sp].length() > 1)) {
                        result.add(new Substring(rcsStack[sp].getId(),
                                rcsStack[sp].getFrom(),
                                rcsStack[sp].getTo() - 1,
                                rcsStack[sp].getFrequency()));
                    } else {
                        result.add(rcsStack[sp]);
                    }

                    Substring s = rcsStack[sp];
                    sp--;

                    if (sp >= 0) {
                        rcsStack[sp].increaseFrequency(s.getFrequency() - 1);
                    }

                    // should be here in order for the algorithm to work properly
                    if ((sp < 0) && (lcpArray[i] > 0)) {
                        sp++;
                        rcsStack[sp] = new Substring(i, suffixArray[i],
                                suffixArray[i] + lcpArray[i],
                                (2 + rcsStack[sp].getFrequency()) - 1);
                        i++;
                    }
                }
            }
        }

        return (Substring[]) result.toArray(new Substring[result.size()]);
    }
}
