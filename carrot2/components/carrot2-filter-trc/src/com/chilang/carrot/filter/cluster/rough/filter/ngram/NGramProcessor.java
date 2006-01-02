
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
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
