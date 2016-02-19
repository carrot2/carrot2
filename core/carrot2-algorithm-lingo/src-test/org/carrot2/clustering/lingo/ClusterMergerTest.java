
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

import org.carrot2.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.TfTermWeighting;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for cluster merging in {@link ClusterBuilder}.
 */
public class ClusterMergerTest extends LingoProcessingComponentTestBase
{
    /** Label builder under tests */
    private ClusterBuilder clusterBuilder;

    @Before
    public void setUpClusterLabelBuilder()
    {
        clusterBuilder = new ClusterBuilder();
        clusterBuilder.labelAssigner = new SimpleLabelAssigner();
        reducer = new TermDocumentMatrixReducer();
        reducer.factorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
        desiredClusterCountBase = 25;
    }

    @Test
    public void testEmpty()
    {
        check(new int [0] []);
    }

    @Test
    public void testNoMerge()
    {
        desiredClusterCountBase = 30;
        createDocuments("", "aa . bb", "", "bb . cc", "", "cc . aa");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 2
            },

            new int []
            {
                0, 1
            },

            new int []
            {
                1, 2
            }
        };

        check(expectedDocumentIndices);
    }

    @Test
    public void testSimpleMerge()
    {
        createDocuments("aa", "aa", "aa bb", "aa bb");
        desiredClusterCountBase = 20;
        clusterBuilder.phraseLabelBoost = 0.08;
        clusterBuilder.clusterMergingThreshold = 0.4;
        preprocessingPipeline.labelFilterProcessor.minLengthLabelFilter.enabled = false;

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1
            },

            null
        };

        check(expectedDocumentIndices);
    }

    @Test
    public void testMultiMerge()
    {
        createDocuments("aa", "aa", 
                        "aa bb", "aa bb", 
                        "aa bb cc", "aa bb cc", 
                        "dd dd", "dd dd", 
                        "dd dd", "dd dd");
        preprocessingPipeline.documentAssigner.minClusterSize = 2;
        desiredClusterCountBase = 20;
        clusterBuilder.phraseLabelBoost = 0.05;
        clusterBuilder.clusterMergingThreshold = 0.2;
        preprocessingPipeline.labelFilterProcessor.minLengthLabelFilter.enabled = false;
        preprocessingPipeline.labelFilterProcessor.completeLabelFilter.enabled = false;

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                3, 4
            },

            new int []
            {
                0, 1, 2
            },

            null,

            null,
        };

        check(expectedDocumentIndices);
    }

    private void check(int [][] expectedDocumentIndices)
    {
        buildLingoModel();
        final TfTermWeighting termWeighting = new TfTermWeighting();
        clusterBuilder.buildLabels(lingoContext, termWeighting);
        clusterBuilder.assignDocuments(lingoContext);
        clusterBuilder.merge(lingoContext);

        for (int i = 0; i < expectedDocumentIndices.length; i++)
        {
            final String description = "clusterDocuments[" + i + "]";
            if (expectedDocumentIndices[i] != null)
            {
                assertThat(lingoContext.clusterDocuments[i]).as(description).isNotNull();
                assertThat(
                    lingoContext.clusterDocuments[i].asIntLookupContainer().toArray()).as(description)
                    .containsOnly(expectedDocumentIndices[i]);
            }
            else
            {
                assertThat(lingoContext.clusterDocuments[i]).as(description).isNull();
            }
        }
    }
}
