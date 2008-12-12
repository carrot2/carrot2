
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

package org.carrot2.clustering.lingo;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for cluster document assignment in {@link ClusterBuilder}.
 */
public class ClusterDocumentAssignerTest extends TermDocumentMatrixBuilderTestBase
{
    /** Matrix reducer needed for test */
    private TermDocumentMatrixReducer reducer;

    /** Label builder under tests */
    private ClusterBuilder clusterBuilder;

    @Before
    public void setUpClusterLabelBuilder()
    {
        clusterBuilder = new ClusterBuilder();
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
        createDocuments("", "aa . aa", "", "bb . bb", "", "cc . cc");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0
            },

            new int []
            {
                2
            },

            new int []
            {
                1
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
        createDocuments("aa bb", "aa bb", "cc", "cc", "aa bb", "aa bb");
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
                1
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
            assertThat(lingoContext.clusterDocuments[i].toArray()).as(
                "clusterDocuments[" + i + "]").containsOnly(expectedDocumentIndices[i]);
        }
    }
}
