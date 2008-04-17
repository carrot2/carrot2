package org.carrot2.clustering.stc;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.IntRange;

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
     * Minimum word-document recurrences.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2, max = Integer.MAX_VALUE)
    // @AttributeGroup(label="Word filtering")
    public int ignoreWordIfInFewerDocs = 2;

    /**
     * Maximum word-document ratio. A number between 0 and 1, if a word exists in more
     * snippets than this ratio, it is ignored.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    // @AttributeGroup(label="Word filtering")
    public double ignoreWordIfInHigherDocsPercent = 0.9d;

    /**
     * Minimum base cluster score.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 10)
    // @AttributeGroup(label="Base clusters")
    public double minBaseClusterScore = 2.0d;

    /**
     * Maximum base clusters count. Trims the base cluster array after N-th position for
     * the merging phase.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2, max = Integer.MAX_VALUE)
    // @AttributeGroup(label="Base clusters")
    public int maxBaseClusters = 300;

    /**
     * Minimum documents per base cluster.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2, max = 20)
    // @AttributeGroup(label="Base clusters")    
    public int minBaseClusterSize = 2;

    /**
     * Maximum final clusters.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = Integer.MAX_VALUE)
    // @AttributeGroup(label="Merging and output")    
    public int maxClusters = 15;

    /**
     * Base cluster merge threshold.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    // @AttributeGroup(label="Merging and output")    
    public double mergeThreshold = 0.6d;

    /**
     * Maximum cluster phrase overlap.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    // @AttributeGroup(label="Label creation")    
    public double maxPhraseOverlap = 0.3d;

    /**
     * Minimum general phrase coverage. Minimum phrase coverage to appear in cluster
     * description.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    // @AttributeGroup(label="Label creation")    
    public double mostGeneralPhraseCoverage = 0.5d;

    /**
     * Maximum words per label. Labels containing more words than this ratio are trimmed.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = Integer.MAX_VALUE)
    // @AttributeGroup(label="Label creation")    
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
    @DoubleRange(min = 0, max = Double.MAX_VALUE)
    // @AttributeGroup(label="Base cluster boosts")    
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
    // @AttributeGroup(label="Base cluster boosts")    
    public int optimalPhraseLength = 3;

    /**
     * Phrase length tolerance. A factor in calculation of the base cluster score.
     * 
     * @see STCEngine#calculateModifiedBaseClusterScore(int, int, STCParameters)
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0.5, max = Double.MAX_VALUE)
    // @AttributeGroup(label="Base cluster boosts")    
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
    @DoubleRange(min = 0, max = Double.MAX_VALUE)
    // @AttributeGroup(label="Base cluster boosts")    
    public double documentCountBoost = 1.0d;
}
