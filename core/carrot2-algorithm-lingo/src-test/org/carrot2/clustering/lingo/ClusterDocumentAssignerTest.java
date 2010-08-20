
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

package org.carrot2.clustering.lingo;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;
import org.carrot2.text.vsm.TfTermWeighting;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for cluster document assignment in {@link ClusterBuilder}.
 */
public class ClusterDocumentAssignerTest extends LingoProcessingComponentTestBase
{
    /** Matrix reducer needed for test */
    private TermDocumentMatrixReducer reducer;

    /** Label builder under tests */
    private ClusterBuilder clusterBuilder;

    @Before
    public void setUpClusterLabelBuilder()
    {
        clusterBuilder = new ClusterBuilder();
        clusterBuilder.labelAssigner = new SimpleLabelAssigner();
        reducer = new TermDocumentMatrixReducer();
        reducer.factorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
        reducer.desiredClusterCountBase = 25;
    }

    @Test
    public void testEmpty()
    {
        check(new int [0] []);
    }

    @Test
    public void testNoPhrases()
    {
        reducer.desiredClusterCountBase = 30;
        createDocuments("", "aa . bb", "", "cc . bb", "", "cc . aa");

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
    public void testSinglePhraseNoSingleWords()
    {
        createDocuments("aa bb", "aa bb", "aa bb", "aa bb");
        reducer.desiredClusterCountBase = 10;

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1
            }
        };

        check(expectedDocumentIndices);
    }

    @Test
    public void testSinglePhraseSingleWords()
    {
        createDocuments("aa bb", "aa bb", "cc", "cc", "aa bb", "aa bb . cc");
        reducer.desiredClusterCountBase = 15;
        clusterBuilder.phraseLabelBoost = 0.3;

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 2
            },

            new int []
            {
                1, 2
            },

        };

        check(expectedDocumentIndices);
    }

    private void check(int [][] expectedDocumentIndices)
    {
        buildTermDocumentMatrix();

        reducer.reduce(lingoContext);

        final TfTermWeighting termWeighting = new TfTermWeighting();
        clusterBuilder.buildLabels(lingoContext, termWeighting);
        clusterBuilder.assignDocuments(lingoContext);

        for (int i = 0; i < expectedDocumentIndices.length; i++)
        {
            assertThat(
                lingoContext.clusterDocuments[i].asIntLookupContainer().toArray()).as(
                "clusterDocuments[" + i + "]").containsOnly(expectedDocumentIndices[i]);
        }
    }
}
