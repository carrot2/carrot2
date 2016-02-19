
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

package org.carrot2.clustering.lingo;

import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.TfTermWeighting;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for label building in {@link ClusterBuilder}.
 */
public class ClusterLabelBuilderTest extends LingoProcessingComponentTestBase
{
    /** Label builder under tests */
    private ClusterBuilder clusterBuilder;

    @Before
    public void setUpClusterLabelBuilder()
    {
        clusterBuilder = new ClusterBuilder();
        clusterBuilder.labelAssigner = new SimpleLabelAssigner();
        reducer = new TermDocumentMatrixReducer();
    }

    @Test
    public void testEmpty()
    {
        buildModelAndCheck(new int [0]);
    }

    @Test
    public void testNoPhrases()
    {
        createDocuments("", "aa . bb", "", "bb . cc", "", "cc . aa");
        final int [] expectedFeatureIndex = new int []
        {
            0, 1, 2
        };

        desiredClusterCountBase  = 30;
        buildModelAndCheck(expectedFeatureIndex);
    }

    @Test
    public void testSinglePhraseNoSingleWords()
    {
        createDocuments("aa bb", "aa bb", "aa bb", "aa bb");

        final int [] expectedFeatureIndex = new int []
        {
            2
        };

        desiredClusterCountBase  = 10;
        buildModelAndCheck(expectedFeatureIndex);
    }

    @Test
    public void testSinglePhraseSingleWords()
    {
        createDocuments("aa bb", "aa bb", "cc", "cc", "aa bb", "aa bb . cc");
        clusterBuilder.phraseLabelBoost = 0.5;

        final int [] expectedFeatureIndex = new int []
        {
            2, 3
        };

        desiredClusterCountBase  = 15;
        buildModelAndCheck(expectedFeatureIndex);
    }

    @Test
    public void testQueryWordsRemoval()
    {
        createDocuments("query word . aa", "query word . aa", "query . word",
            "query . word . aa");
        clusterBuilder.phraseLabelBoost = 0.5;

        final int [] expectedFeatureIndex = new int []
        {
            0
        };

        desiredClusterCountBase = 10;
        createPreprocessingContext("query word");
        buildModelAndCheck(expectedFeatureIndex);
    }

    @Test
    public void testExternalFeatureScores()
    {
        createDocuments("aa bb", "aa bb", "cc", "cc", "cc", "cc", "aa bb", "aa bb", "dd",
            "dd", "dd", "dd", "ee ff", "ee ff", "ee ff", "ee ff");
        clusterBuilder.phraseLabelBoost = 0.5;
        desiredClusterCountBase  = 15;

        final int [] expectedFeatureIndex = new int []
        {
            6, 7, 2, 3
        };
        buildModelAndCheck(expectedFeatureIndex);

        // Make a copy of feature indices
        final int [] featureIndex = lingoContext.preprocessingContext.allLabels.featureIndex;

        for (int i = 0; i < featureIndex.length; i++)
        {
            clusterBuilder.featureScorer = new OneLabelFeatureScorer(i, 2);
            check(new int []
            {
                featureIndex[i], featureIndex[i], featureIndex[i], featureIndex[i]
            });
        }
    }

    private static class OneLabelFeatureScorer implements IFeatureScorer
    {
        private int labelIndex;
        private double score;

        OneLabelFeatureScorer(int labelIndex, double score)
        {
            this.labelIndex = labelIndex;
            this.score = score;
        }

        public double [] getFeatureScores(LingoProcessingContext lingoContext)
        {
            final double [] scores = new double [lingoContext.preprocessingContext.allLabels.featureIndex.length];
            scores[labelIndex] = score;
            return scores;
        }
    }

    private void buildModelAndCheck(int [] expectedFeatureIndex)
    {
        buildLingoModel();
        check(expectedFeatureIndex);
    }

    private void check(int [] expectedFeatureIndex)
    {
        clusterBuilder.buildLabels(lingoContext, new TfTermWeighting());
        assertThat(lingoContext.clusterLabelFeatureIndex).as("clusterLabelFeatureIndex")
            .containsOnly(expectedFeatureIndex);
    }
}
