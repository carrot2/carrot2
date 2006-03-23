
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

package com.chilang.carrot.filter.cluster.rough;

import net.sf.snowball.ext.englishStemmer;

import com.chilang.carrot.filter.cluster.rough.data.*;
import com.chilang.carrot.filter.cluster.rough.filter.StopWordsSet;
import com.chilang.carrot.filter.cluster.rough.filter.TermFilter;
import com.chilang.carrot.filter.cluster.rough.filter.stemmer.Stemmer;
import com.chilang.carrot.filter.cluster.rough.transformer.Snippet2DocumentTransformer;
import com.chilang.carrot.filter.cluster.rough.trsm.*;


/**
 * Factor for common used classes
 */
public class CommonFactory {

    private CommonFactory() {}

    public static Stemmer createDefaultStemmer() {
        return new Stemmer() {
            final englishStemmer stemmer = new englishStemmer();

            public String stem(String word) {
                stemmer.setCurrent(word);
                if (stemmer.stem() == false) {
                    return word;
                }

                return stemmer.getCurrent();
            }
        };
    }

    public static StopWordsSet createStopWordsSet() {
        // TODO: Hardcoded path and only english stopwords used.
        return new StopWordsSet("stopwords/stopwords-en.txt");
    }

    /**
     * Create context from given data file
     * @param datafile
     */
    public static IRContext createDefaultContext(String datafile) {
        SnippetReader reader = new SnippetReader(datafile);
        IRContext context = new WebIRContext(
                createStopWordsSet(),
                new Snippet2DocumentTransformer(
                        createSimpleTermExtractor(createDefaultStemmer(), createStopWordsSet())));


        context.setQuery(reader.getQuery());
        context.setDocuments(reader.getSnippets());
        return context;
    }

    public static TermExtractor createSimpleTermExtractor(Stemmer stemmer, TermFilter filter) {
        return new SimpleTermExtractor(stemmer, filter);
    }

//    public static IRContext createContextFromCACM(String filename) {
//        CACMFileReader fileReader = new CACMFileReader(filename);
//        return new WebIRContext(fileReader.getSnippets());
//    }

    public static RoughSpace createRoughSpace(int[][] documentTermFrequency, int cooccurenceThreshold, double inclusionThreshold) {
        return new TermRoughSpace(documentTermFrequency, cooccurenceThreshold, inclusionThreshold);
    }
}
