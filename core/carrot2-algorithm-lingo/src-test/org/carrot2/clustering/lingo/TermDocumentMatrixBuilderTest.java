package org.carrot2.clustering.lingo;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.matrix.MatrixAssertions;
import org.junit.Test;

import bak.pcj.map.IntKeyIntMap;
import bak.pcj.map.IntKeyIntOpenHashMap;

/**
 * Test cases for {@link TermDocumentMatrixBuilder}.
 */
public class TermDocumentMatrixBuilderTest extends TermDocumentMatrixBuilderTestBase
{
    @Test
    public void testEmpty()
    {
        int [] expectedTdMatrixStemIndices = new int [] {};
        double [][] expectedTdMatrixElements = new double [] [] {};

        check(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    @Test
    public void testSingleWords()
    {
        createDocuments("", "aa . bb", "", "bb . cc", "", "aa . cc . cc");

        int [] expectedTdMatrixStemIndices = new int []
        {
            2, 0, 1
        };
        double [][] expectedTdMatrixElements = new double [] []
        {
            {
                0, 1, 2
            },
            {
                1, 0, 1
            },
            {
                1, 1, 0
            }
        };

        check(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    @Test
    public void testSinglePhrase()
    {
        createDocuments("", "aa bb cc", "", "aa bb cc", "", "aa bb cc");

        int [] expectedTdMatrixStemIndices = new int []
        {
            0, 1, 2
        };
        double [][] expectedTdMatrixElements = new double [] []
        {
            {
                1, 1, 1
            },
            {
                1, 1, 1
            },
            {
                1, 1, 1
            },
        };

        check(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    @Test
    public void testSinglePhraseWithSingleWords()
    {
        createDocuments("", "aa bb cc", "", "aa bb cc", "", "aa bb cc", "",
            "ff . gg . ff . gg");

        int [] expectedTdMatrixStemIndices = new int []
        {
            0, 1, 2, 3, 4
        };
        double [][] expectedTdMatrixElements = new double [] []
        {
            {
                1, 1, 1, 0
            },
            {
                1, 1, 1, 0
            },
            {
                1, 1, 1, 0
            },
            {
                0, 0, 0, 2
            },
            {
                0, 0, 0, 2
            },
        };

        check(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    @Test
    public void testSinglePhraseWithStopWord()
    {
        createDocuments("", "aa stop cc", "", "aa stop cc", "", "aa stop cc");

        int [] expectedTdMatrixStemIndices = new int []
        {
            0, 1
        };
        double [][] expectedTdMatrixElements = new double [] []
        {
            {
                1, 1, 1
            },
            {
                1, 1, 1
            }
        };

        check(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    @Test
    public void testMatrixSizeLimit()
    {
        createDocuments("", "aa . aa", "", "bb . bb . bb", "", "cc . cc . cc . cc");

        int [] expectedTdMatrixStemIndices = new int []
        {
            2, 1
        };
        double [][] expectedTdMatrixElements = new double [] []
        {
            {
                0, 0, 4
            },
            {
                0, 3, 0
            }
        };

        matrixBuilder.maximumMatrixSize = 3 * 2;
        check(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    @Test
    public void testTitleWordBoost()
    {
        createDocuments("aa", "bb", "", "bb . cc", "", "aa . cc . cc");

        int [] expectedTdMatrixStemIndices = new int []
        {
            0, 2, 1
        };
        double [][] expectedTdMatrixElements = new double [] []
        {
            {
                2, 0, 2
            },
            {
                0, 1, 2
            },
            {
                1, 1, 0
            }
        };

        check(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    private void check(double [][] expectedTdMatrixElements,
        int [] expectedTdMatrixStemIndices)
    {
        buildTermDocumentMatrix();

        assertThat(lingoContext.tdMatrix.rows()).as("tdMatrix.rowCount").isEqualTo(
            expectedTdMatrixStemIndices.length);
        MatrixAssertions.assertThat(lingoContext.tdMatrix).isEquivalentTo(
            expectedTdMatrixElements);

        final IntKeyIntMap expectedStemToRowIndex = new IntKeyIntOpenHashMap();
        for (int i = 0; i < expectedTdMatrixStemIndices.length; i++)
        {
            expectedStemToRowIndex.put(expectedTdMatrixStemIndices[i], i);
        }

        assertThat(lingoContext.tdMatrixStemToRowIndex).as("stemToRowIndex").isEqualTo(
            expectedStemToRowIndex);
    }
}
