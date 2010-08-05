
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

package org.carrot2.text.vsm;

import static org.carrot2.matrix.MatrixAssertions.assertThat;

import org.fest.assertions.Assertions;
import org.junit.Test;

import org.apache.mahout.math.matrix.*;

/**
 * Test cases for phrase matrix building.
 */
@SuppressWarnings("deprecation")
public class PhraseMatrixBuilderTest extends TermDocumentMatrixBuilderTestBase
{
    @Test
    public void testEmpty()
    {
        check(null);
    }

    @Test
    public void testNoPhrases()
    {
        createDocuments("", "aa . bb", "", "bb . cc", "", "aa . cc . cc");
        check(null);
    }

    @Test
    public void testSinglePhraseNoSingleWords()
    {
        createDocuments("", "aa bb cc", "", "aa bb cc", "", "aa bb cc");

        double [][] expectedPhraseMatrixElements = new double [] []
        {
            {
                0.577, 0.577, 0.577
            }
        };

        check(expectedPhraseMatrixElements);
    }

    @Test
    public void testTwoPhrasesNoSingleWords()
    {
        createDocuments("ee ff", "aa bb cc", "ee ff", "aa bb cc", "ee ff", "aa bb cc");

        double [][] expectedPhraseMatrixElements = new double [] []
        {
            {
                0.707, 0.707, 0, 0, 0
            },
            {
                0, 0, 0.577, 0.577, 0.577
            }
        };

        check(expectedPhraseMatrixElements);
    }

    @Test
    public void testSinglePhraseSingleWords()
    {
        createDocuments("", "aa bb cc", "", "aa bb cc", "", "aa bb cc",
            "ff . gg . ff . gg", "", "ff . gg . ff . gg");

        double [][] expectedPhraseMatrixElements = new double [] []
        {
            {
                0, 0, 0.577, 0.577, 0.577
            }
        };

        check(expectedPhraseMatrixElements);
    }

    @Test
    public void testSinglePhraseWithStopWord()
    {
        createDocuments("", "aa stop cc", "", "aa stop cc", "", "aa stop cc");

        double [][] expectedPhraseMatrixElements = new double [] []
        {
            {
                0.707, 0.707
            }
        };

        check(expectedPhraseMatrixElements);
    }

    private void check(double [][] expectedPhraseMatrixElements)
    {
        buildTermDocumentMatrix();
        matrixBuilder.buildTermPhraseMatrix(vsmContext);
        final DoubleMatrix2D phraseMatrix = vsmContext.termPhraseMatrix;

        if (expectedPhraseMatrixElements == null)
        {
            Assertions.assertThat(phraseMatrix).isNull();
        }
        else
        {
            assertThat(phraseMatrix).isEquivalentTo(expectedPhraseMatrixElements, 0.01);
        }
    }
}
