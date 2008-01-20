
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.ngram;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.Snippet;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.StopWordFilter;
import org.carrot2.filter.trc.carrot.tokenizer.ITokenizer;
import org.carrot2.filter.trc.carrot.tokenizer.TokenizerFactory;
import org.carrot2.filter.trc.util.FrequencyMap;


/**
 * Collects n-ngrams from collection of snippets.
 * Count n-gram term frequency and document frequency.
 */
public class NGramCollector implements SnipperProcessor {


    //max length of n-gram to be extracted
    private int nGramMaxLength;

    //map of n-gram -> document frequency (number of document in which n-gram exists)
    private FrequencyMap documentFrequency;

    //map of n-gram -> overall term frequency
    // (number of overall occurences of n-gram in the whole collection)
    private FrequencyMap termFrequency;

    private NGramGenerator nGramGenerator;
    private NGramProcessor nGramProcessor;
    private ITokenizer tokenizer;


    public NGramCollector(int nGramMaxLength, StopWordFilter filter) {
        this.nGramMaxLength = nGramMaxLength;
        this.nGramProcessor = new NGramProcessor();
//        this.nGramGenerator = new NGramGenerator(nGramMaxLength, nGramProcessor);
        this.nGramGenerator = new NGramGenerator(nGramMaxLength, nGramProcessor, new NGramFactory(filter));        
        this.tokenizer = TokenizerFactory.getTokenizer();
        this.documentFrequency = new FrequencyMap();
        this.termFrequency = new FrequencyMap();
    }

    public void process(Snippet snippet) {
        //clear current frequency map
        nGramProcessor.getFrequencyMap().clear();
        //process title
        tokenizer.restartTokenizer(snippet.getTitle() == null ? "" : snippet.getTitle());
        nGramGenerator.process(tokenizer);
        //process description
        tokenizer.restartTokenizer(snippet.getDescription() == null ? "" : snippet.getDescription());
        nGramGenerator.process(tokenizer);

        //increase df
        documentFrequency.addAll(nGramProcessor.getFrequencyMap().keySet());
        //increase tf
        termFrequency.merge(nGramProcessor.getFrequencyMap());

    }

    public FrequencyMap getDocumentFrequency() {
        return documentFrequency;
    }

    public FrequencyMap getTermFrequency() {
        return termFrequency;
    }
}
