package org.carrot2.clustering.lingo;

import static org.fest.assertions.Assertions.assertThat;

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
    }

    @Test
    public void testEmpty()
    {
        check(new int [0] []);
    }

    @Test
    public void testNoPhrases()
    {
        reducer.desiredClusterCount = 3;
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
        reducer.desiredClusterCount = 1;

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
        reducer.desiredClusterCount = 2;
        clusterBuilder.phraseLabelBoost = 0.5;

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
        clusterBuilder.assignDocuments(lingoContext, termWeighting);

        for (int i = 0; i < expectedDocumentIndices.length; i++)
        {
            assertThat(lingoContext.clusterDocuments[i].toArray()).as(
                "clusterDocuments[" + i + "]").containsOnly(expectedDocumentIndices[i]);
        }
    }
}
