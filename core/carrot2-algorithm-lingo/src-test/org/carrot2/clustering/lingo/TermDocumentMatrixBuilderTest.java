package org.carrot2.clustering.lingo;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.matrix.MatrixAssertions;
import org.carrot2.text.linguistic.LanguageModelFactory;
import org.carrot2.text.preprocessing.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link TermDocumentMatrixBuilder}.
 */
public class TermDocumentMatrixBuilderTest extends PreprocessingComponentTestBase
{
    /** Matrix builder under tests */
    private TermDocumentMatrixBuilder matrixBuilder;

    @Before
    public void setUpMatrixBuilder()
    {
        matrixBuilder = new TermDocumentMatrixBuilder();
        matrixBuilder.termWeighting = new TfTermWeighting();
    }

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
        Tokenizer tokenizer = new Tokenizer();
        CaseNormalizer caseNormalizer = new CaseNormalizer();
        LanguageModelStemmer languageModelStemmer = new LanguageModelStemmer();
        PhraseExtractor phraseExtractor = new PhraseExtractor();
        StopListMarker stopListMarker = new StopListMarker();
        LabelFilterProcessor labelFilterProcessor = new LabelFilterProcessor();

        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        phraseExtractor.extractPhrases(context);
        stopListMarker.mark(context);
        labelFilterProcessor.process(context);

        LingoProcessingContext lingoContext = new LingoProcessingContext(context);

        matrixBuilder.build(lingoContext);

        assertThat(lingoContext.tdMatrix.rows()).as("tdMatrix.rowCount").isEqualTo(
            expectedTdMatrixStemIndices.length);
        assertThat(lingoContext.tdMatrixStemIndices).as("tdMatrixStemIndices").isEqualTo(
            expectedTdMatrixStemIndices);
        MatrixAssertions.assertThat(lingoContext.tdMatrix).isEquivalentTo(
            expectedTdMatrixElements);
    }

    @Override
    protected LanguageModelFactory createLanguageModelFactory()
    {
        return new TestLanguageModelFactory();
    }
}
