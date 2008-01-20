
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

import org.carrot2.filter.trc.util.FrequencyMap;

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
