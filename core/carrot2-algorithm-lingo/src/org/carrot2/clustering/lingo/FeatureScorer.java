package org.carrot2.clustering.lingo;

import org.carrot2.text.preprocessing.PreprocessingContext;

/**
 * Generates additional scores for features. Lingo will apply these scores before
 * final label selection.
 */
public interface FeatureScorer
{
    /**
     * Returns scores for features from {@link PreprocessingContext#allLabels}. A neutral
     * score is 1.0. Anything below 1.0 will lower label score, anything above 1.0 will
     * raise it.
     */
    public double [] getFeatureScores(LingoProcessingContext lingoContext);
}
