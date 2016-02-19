
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.stc;

import java.util.ArrayList;

import com.carrotsearch.hppc.BitSet;
import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * A cluster candidate in the process of building STC clusters.
 */
final class ClusterCandidate
{
    /**
     * This cluster's score.
     */
    float score;
    
    /**
     * Indexes of documents this cluster covers.
     */
    BitSet documents;

    /**
     * Pairs of integers denoting a range of indices in {@link GeneralizedSuffixTree.SequenceBuilder#input}
     * forming this cluster's label.
     */
    final ArrayList<int[]> phrases;

    /**
     * Cached cardinality of {@link #documents}.
     */
    int cardinality;

    /* For cluster merging. */
    ClusterCandidate()
    {
        this.phrases = Lists.newArrayList();
        this.documents = new BitSet();
    }

    /* */
    ClusterCandidate(int [] phraseIndices, BitSet documents, int cardinality, float score)
    {
        assert documents.cardinality() == cardinality;

        phrases = Lists.newArrayListWithCapacity(1);
        phrases.add(phraseIndices);

        this.documents = documents;
        this.score = score;
        this.cardinality = cardinality;
    }
}
