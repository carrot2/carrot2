package org.carrot2.clustering.lingo;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for cluster merging in {@link ClusterBuilder}.
 */
public class ClusterMergerTest extends TermDocumentMatrixBuilderTestBase
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
    public void testNoMerge()
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
    public void testSimpleMerge()
    {
        createDocuments("aa", "aa", "aa bb", "aa bb");
        reducer.desiredClusterCountBase = 20;
        clusterBuilder.phraseLabelBoost = 0.8;
        clusterBuilder.clusterMergingThreshold = 0.4;

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
        createDocuments("aa", "aa", "aa bb", "aa bb", "aa bb cc", "aa bb cc", "dd dd");
        reducer.desiredClusterCountBase = 20;
        clusterBuilder.clusterMergingThreshold = 0.2;
        labelFilterProcessor.completeLabelFilter.enabled = false;

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1, 2
            },

            null,

            null,

            new int []
            {
                3
            }
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
        clusterBuilder.merge(lingoContext);

        for (int i = 0; i < expectedDocumentIndices.length; i++)
        {
            final String description = "clusterDocuments[" + i + "]";
            if (expectedDocumentIndices[i] != null)
            {
                assertThat(lingoContext.clusterDocuments[i]).as(description).isNotNull();
                assertThat(lingoContext.clusterDocuments[i].toArray()).as(description)
                    .containsOnly(expectedDocumentIndices[i]);
            }
            else
            {
                assertThat(lingoContext.clusterDocuments[i]).as(description).isNull();
            }
        }
    }
}
