/**
 * 
 * @author chilang
 * Created 2003-09-11, 01:41:04.
 */
package com.chilang.carrot.filter.cluster.rough.filter.ngram;

import com.chilang.util.FrequencyMap;

import java.util.Collection;

class NGramProcessor {
    //Map of ngrams -> frequency
    FrequencyMap ngramFrequencies;

    NGramProcessor() {
        ngramFrequencies = new FrequencyMap();
    }

    void process(NGram nGram) {
        ngramFrequencies.add(nGram);
    }

    NGram[] getNGrams() {
        Collection entries = ngramFrequencies.getInternalMap().keySet();
        return (NGram[])entries.toArray(new NGram[entries.size()]);
    }

    FrequencyMap getFrequencyMap() {
        return ngramFrequencies;
    }
}
