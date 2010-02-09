
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.stc;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * STC algorithm parameters. These should be eventually moved into
 * {@link STCClusteringAlgorithm}.
 */
@Bindable(prefix = "STCClusteringAlgorithm")
public final class STCClusteringParameters
{
    /**
     * Minimum word-document recurrences.
     * 
     * @group Word filtering
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2)
    public int ignoreWordIfInFewerDocs = 2;

    /**
     * Maximum word-document ratio. A number between 0 and 1, if a word exists in more
     * snippets than this ratio, it is ignored.
     * 
     * @group Word filtering
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    public double ignoreWordIfInHigherDocsPercent = 0.9d;

    /**
     * Minimum base cluster score.
     * 
     * @group Base clusters
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 10)
    public double minBaseClusterScore = 2.0d;

    /**
     * Maximum base clusters count. Trims the base cluster array after N-th position for
     * the merging phase.
     * 
     * @group Base clusters
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2)
    public int maxBaseClusters = 300;

    /**
     * Minimum documents per base cluster.
     * 
     * @group Base clusters
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2, max = 20)
    public int minBaseClusterSize = 2;

    /**
     * Maximum final clusters.
     * 
     * @group Merging and output
     * @level Basic
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    public int maxClusters = 15;

    /**
     * Base cluster merge threshold.
     * 
     * @group Merging and output
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    public double mergeThreshold = 0.6d;

    /**
     * Maximum cluster phrase overlap.
     * 
     * @group Label creation
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    public double maxPhraseOverlap = 0.6d;

    /**
     * Minimum general phrase coverage. Minimum phrase coverage to appear in cluster
     * description.
     * 
     * @group Label creation
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    public double mostGeneralPhraseCoverage = 0.5d;

    /**
     * Maximum words per label. Base clusters formed by phrases with more words than this
     * ratio are trimmed.
     * 
     * @group Label creation
     * @level Basic
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    public int maxDescPhraseLength = 4;

    /**
     * Maximum phrases per label. Maximum number of phrases from base clusters promoted
     * to the cluster's label.
     *  
     * @group Label creation
     * @level Basic
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    public int maxPhrases = 3;

    /**
     * Single term boost. A factor in calculation of the base cluster score. If greater
     * then zero, single-term base clusters are assigned this value regardless of the
     * penalty function.
     * 
     * @group Base cluster boosts
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0)
    public double singleTermBoost = 0.5d;

    /**
     * Optimal label length. A factor in calculation of the base cluster score.
     * 
     * @group Base cluster boosts
     * @level Basic
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    public int optimalPhraseLength = 3;

    /**
     * Phrase length tolerance. A factor in calculation of the base cluster score.
     * 
     * @group Base cluster boosts
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0.5)
    public double optimalPhraseLengthDev = 2.0d;

    /**
     * Document count boost. A factor in calculation of the base cluster score, boosting
     * the score depending on the number of documents found in the base cluster.
     * 
     * @group Base cluster boosts
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0)
    public double documentCountBoost = 1.0d;
}
