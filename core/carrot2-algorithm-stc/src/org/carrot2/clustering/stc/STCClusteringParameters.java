package org.carrot2.clustering.stc;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.*;

/**
 * STC algorithm parameters. These should be eventually moved into
 * {@link STCClusteringAlgorithm}.
 * 
 * @author Dawid Weiss
 */
@Bindable
public final class STCClusteringParameters
{
    /**
     * Minimum base cluster score.
     */
    @Processing
    @Input
    @Attribute
    public double minBaseClusterScore = 2.0d;

    /**
     * Minimum word-document recurrences.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2, max = Integer.MAX_VALUE)
    public int ignoreWordIfInFewerDocs = 2;

    /**
     * Maximum word-document ratio. A number between 0 and 1, if a word exists in more
     * snippets than this ratio, it is ignored.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    public double ignoreWordIfInHigherDocsPercent = 0.9d;

    /**
     * Maximum base clusters count. Trims the base cluster array after N-th position for
     * the merging phase.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2, max = Integer.MAX_VALUE)
    public int maxBaseClusters = 300;

    /**
     * Minimum documents per base cluster.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2, max = Integer.MAX_VALUE)
    public int minBaseClusterSize = 2;

    /**
     * Maximum final clusters.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = Integer.MAX_VALUE)
    public int maxClusters = 15;

    /**
     * Base cluster merge threshold.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    public double mergeThreshold = 0.6d;

    /**
     * Maximum cluster phrase overlap.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    public double maxPhraseOverlap = 0.3d;

    /**
     * Minimum general phrase coverage. Minimum phrase coverage to appear in cluster
     * description.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    public double mostGeneralPhraseCoverage = 0.5d;

    /**
     * Maximum words per label. Labels containing more words than this ratio are trimmed.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = Integer.MAX_VALUE)
    public int maxDescPhraseLength = 4;

    /**
     * Single term boost. A factor in calculation of the base cluster score. If greater
     * then zero, single-term base clusters are assigned this value regardless of the
     * penalty function.
     * 
     * @see STCEngine#calculateModifiedBaseClusterScore(int, int, STCParameters)
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    public double singleTermBoost = 0.5d;

    /**
     * Optimal label length. A factor in calculation of the base cluster score.
     * 
     * @see STCEngine#calculateModifiedBaseClusterScore(int, int, STCParameters)
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = Integer.MAX_VALUE)
    public int optimalPhraseLength = 3;

    /**
     * Optimal label length deviation. A factor in calculation of the base cluster score.
     * 
     * @see STCEngine#calculateModifiedBaseClusterScore(int, int, STCParameters)
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 1, max = Integer.MAX_VALUE)
    public double optimalPhraseLengthDev = 2.0d;

    /**
     * Document count boost. A factor in calculation of the base cluster score, boosting
     * the score depending on the number of documents found in the base cluster.
     * 
     * @see STCEngine#calculateModifiedBaseClusterScore(int, int, STCParameters)
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = Integer.MAX_VALUE)
    public double documentCountBoost = 1.0d;
}
