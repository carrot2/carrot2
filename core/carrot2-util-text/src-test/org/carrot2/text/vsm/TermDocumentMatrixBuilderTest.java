
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

package org.carrot2.text.vsm;

import org.carrot2.matrix.MatrixAssertions;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.junit.Test;

import com.carrotsearch.hppc.IntIntHashMap;

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
        preprocessingPipeline.documentAssigner.minClusterSize = 1;

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

        System.out.println(context);
        check(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    @Test
    public void testMatrixSizeLimit()
    {
        createDocuments("", "aa . aa", "", "bb . bb . bb", "", "cc . cc . cc . cc");
        preprocessingPipeline.documentAssigner.minClusterSize = 1;

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

    @Test
    public void testCarrot905()
    {
        createDocuments("", "aa . bb", "", "bb . cc", "", "aa . cc . cc");

        PreprocessingContext context = preprocessingPipeline.preprocess(
            this.context.documents, 
            this.context.query, 
            this.context.language.getLanguageCode());

        // The preprocessing pipeline will produce increasing indices in tfByDocument,
        // so to reproduce the bug, we need to perturb them, e.g. reverse. 
        final int [][] tfByDocument = context.allStems.tfByDocument;
        for (int s = 0; s < tfByDocument.length; s++)
        {
            final int [] stemTfByDocument = tfByDocument[s];
            for (int i = 0; i < stemTfByDocument.length / 4; i++)
            {
                int t = stemTfByDocument[i * 2];
                stemTfByDocument[i * 2] = stemTfByDocument[(stemTfByDocument.length / 2 - i - 1) * 2];
                stemTfByDocument[(stemTfByDocument.length / 2 - i - 1) * 2] = t;
                
                t = stemTfByDocument[i * 2 + 1];
                stemTfByDocument[i * 2 + 1] = stemTfByDocument[(stemTfByDocument.length / 2 - i - 1) * 2 + 1];
                stemTfByDocument[(stemTfByDocument.length / 2 - i - 1) * 2 + 1] = t;
            }
        }

        vsmContext = new VectorSpaceModelContext(context);
        matrixBuilder.buildTermDocumentMatrix(vsmContext);
        matrixBuilder.buildTermPhraseMatrix(vsmContext);

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

        checkOnly(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    private void check(double [][] expectedTdMatrixElements,
        int [] expectedTdMatrixStemIndices)
    {
        buildTermDocumentMatrix();
        checkOnly(expectedTdMatrixElements, expectedTdMatrixStemIndices);
    }

    void checkOnly(double [][] expectedTdMatrixElements,
        int [] expectedTdMatrixStemIndices)
    {
        assertThat(vsmContext.termDocumentMatrix.rows()).as("tdMatrix.rowCount")
            .isEqualTo(expectedTdMatrixStemIndices.length);
        MatrixAssertions.assertThat(vsmContext.termDocumentMatrix).isEquivalentTo(
            expectedTdMatrixElements);

        final IntIntHashMap expectedStemToRowIndex = new IntIntHashMap();
        for (int i = 0; i < expectedTdMatrixStemIndices.length; i++)
        {
            expectedStemToRowIndex.put(expectedTdMatrixStemIndices[i], i);
        }

        assertThat((Object) vsmContext.stemToRowIndex).isEqualTo(expectedStemToRowIndex);
    }
}
