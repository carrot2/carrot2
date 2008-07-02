package org.carrot2.text.preprocessing;

import static org.carrot2.util.test.Assertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link CaseNormalizer}.
 */
public class CaseNormalizerTest extends PreprocessingComponentTestBase
{
    /** Case normalizer under tests */
    private CaseNormalizer caseNormalizer;
    
    /** Other preprocessing components required for the test */
    private Tokenizer tokenizer;

    @Before
    public void setUpPreprocessingComponents()
    {
        tokenizer = new Tokenizer();
        caseNormalizer = new CaseNormalizer();
    }
    
    @Test
    public void testEmpty()
    {
        createDocuments();

        char [][] expectedWordImages = new char [] [] {};
        int [] expectedWordTf = new int [] {};
        int [] expectedWordIndices = new int []
        {
            -1
        };
        int [][] expectedWordTfByDocument = new int [] [] {};

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testOneToken()
    {
        createDocuments("test");

        char [][] expectedWordImages = new char [] []
        {
            "test".toCharArray()
        };

        int [] expectedWordTf = new int []
        {
            1
        };

        int [] expectedWordIndices = new int []
        {
            0, -1
        };

        int [][] expectedWordTfByDocument = new int [] []
        {
            {
                0, 1
            }
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testMoreSingleDifferentTokens()
    {
        createDocuments("a simple test_symbol");

        char [][] expectedWordImages = createExpectedWordImages(new String []
        {
            "a", "test_symbol", "simple"
        });

        int [] expectedWordTf = new int []
        {
            1, 1, 1
        };

        int [] expectedWordIndices = new int []
        {
            0, 1, 2, -1
        };

        int [][] expectedWordTfByDocument = new int [] []
        {
            {
                0, 1
            },

            {
                0, 1
            },

            {
                0, 1
            }
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testMoreRepeatedDifferentTokens()
    {
        createDocuments("a simple test", "a test a");

        char [][] expectedWordImages = createExpectedWordImages(new String []
        {
            "a", "test", "simple"
        });

        int [] expectedWordTf = new int [3];
        expectedWordTf[0] = 3;
        expectedWordTf[1] = 2;
        expectedWordTf[2] = 1;

        int [] expectedWordIndices = new int []
        {
            wordIndices.get("a"), wordIndices.get("simple"), wordIndices.get("test"), -1,
            wordIndices.get("a"), wordIndices.get("test"), wordIndices.get("a"), -1
        };

        int [][] expectedWordTfByDocument = new int [3] [];
        expectedWordTfByDocument[wordIndices.get("a")] = new int []
        {
            0, 3
        };
        expectedWordTfByDocument[wordIndices.get("test")] = new int []
        {
            0, 2
        };
        expectedWordTfByDocument[wordIndices.get("simple")] = new int []
        {
            0, 1
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testOneTokenVariantEqualFrequencies()
    {
        createDocuments("abc abc ABC aBc");

        char [][] expectedWordImages = new char [] []
        {
            "abc".toCharArray()
        };

        int [] expectedWordTf = new int []
        {
            4
        };

        int [] expectedWordIndices = new int []
        {
            0, 0, 0, 0, -1
        };

        int [][] expectedWordTfByDocument = new int [] []
        {
            {
                0, 4
            }
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testDemos()
    {
        createDocuments("demo demo demos demos DEMO DEMOs Demo Demos");

        char [][] expectedWordImages = createExpectedWordImages(new String []
        {
            "demo", "demos"
        });

        int [] expectedWordTf = new int []
        {
            4, 4
        };

        int [] expectedWordIndices = new int []
        {
            wordIndices.get("demo"), wordIndices.get("demo"), wordIndices.get("demos"),
            wordIndices.get("demos"), wordIndices.get("demo"), wordIndices.get("demos"),
            wordIndices.get("demo"), wordIndices.get("demos"), -1
        };

        int [][] expectedWordTfByDocument = new int [] []
        {
            {
                0, 4
            },

            {
                0, 4
            }
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testOneTokenVariantNonequalFrequencies()
    {
        createDocuments("abc ABC ABC aBc aBc ABC");

        char [][] expectedWordImages = new char [] []
        {
            "ABC".toCharArray()
        };

        int [] expectedWordTf = new int []
        {
            6
        };

        int [] expectedWordIndices = new int []
        {
            0, 0, 0, 0, 0, 0, -1
        };

        int [][] expectedWordTfByDocument = new int [] []
        {
            {
                0, 6
            }
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testMoreTokenVariants()
    {
        createDocuments("abc bcd ABC bcD ABC efg", "aBc aBc ABC BCD bcd bcd");

        char [][] expectedWordImages = createExpectedWordImages(new String []
        {
            "ABC", "bcd", "efg"
        });

        int [] expectedWordTf = new int [3];
        expectedWordTf[wordIndices.get("ABC")] = 6;
        expectedWordTf[wordIndices.get("bcd")] = 5;
        expectedWordTf[wordIndices.get("efg")] = 1;

        int [] expectedWordIndices = new int []
        {
            wordIndices.get("ABC"), wordIndices.get("bcd"), wordIndices.get("ABC"),
            wordIndices.get("bcd"), wordIndices.get("ABC"), wordIndices.get("efg"), -1,
            wordIndices.get("ABC"), wordIndices.get("ABC"), wordIndices.get("ABC"),
            wordIndices.get("bcd"), wordIndices.get("bcd"), wordIndices.get("bcd"), -1
        };

        int [][] expectedWordTfByDocument = new int [3] [];
        expectedWordTfByDocument[wordIndices.get("ABC")] = new int []
        {
            0, 6
        };
        expectedWordTfByDocument[wordIndices.get("bcd")] = new int []
        {
            0, 5
        };
        expectedWordTfByDocument[wordIndices.get("efg")] = new int []
        {
            0, 1
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testDfThresholding()
    {
        caseNormalizer.dfCutoff = 2;
        createDocuments("a b c", "d e f", "a", "a c");

        char [][] expectedWordImages = createExpectedWordImages(new String []
        {
            "a", "c"
        });

        int [] expectedWordTf = new int [2];
        expectedWordTf[wordIndices.get("a")] = 3;
        expectedWordTf[wordIndices.get("c")] = 2;

        int [] expectedWordIndices = new int []
        {
            wordIndices.get("a"), -1, wordIndices.get("c"), -1, -1, -1, -1, -1,
            wordIndices.get("a"), -1, wordIndices.get("a"), wordIndices.get("c"), -1
        };

        int [][] expectedWordTfByDocument = new int [2] [];
        expectedWordTfByDocument[wordIndices.get("a")] = new int []
        {
            0, 1, 1, 2
        };
        expectedWordTfByDocument[wordIndices.get("c")] = new int []
        {
            0, 1, 1, 1
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testTokenFiltering()
    {
        createDocuments("a . b ,", "a . b ,");

        char [][] expectedWordImages = createExpectedWordImages(new String []
        {
            "a", "b"
        });

        int [] expectedWordTf = new int []
        {
            2, 2
        };

        int [] expectedWordIndices = new int []
        {
            wordIndices.get("a"), -1, wordIndices.get("b"), -1, -1, wordIndices.get("a"),
            -1, wordIndices.get("b"), -1, -1
        };

        int [][] expectedWordTfByDocument = new int [] []
        {
            {
                0, 2
            },

            {
                0, 2
            }
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testPunctuation()
    {
        createDocuments("aba . , aba", ", .");

        char [][] expectedWordImages = new char [] []
        {
            "aba".toCharArray()
        };

        int [] expectedWordTf = new int []
        {
            2
        };

        int [] expectedWordIndices = new int []
        {
            0, -1, -1, 0, -1, -1, -1, -1
        };

        int [][] expectedWordTfByDocument = new int [] []
        {
            {
                0, 2
            }
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    @Test
    public void testMoreDocuments()
    {
        createDocuments(null, "ABC abc", "bcd", "BCD", "ABC", "BCD", "def DEF DEF", "DEF");

        char [][] expectedWordImages = createExpectedWordImages(new String []
        {
            "DEF", "ABC", "BCD"
        });

        int [] expectedWordTf = new int [3];
        expectedWordTf[wordIndices.get("DEF")] = 4;
        expectedWordTf[wordIndices.get("ABC")] = 3;
        expectedWordTf[wordIndices.get("BCD")] = 3;

        int [] expectedWordIndices = new int []
        {
            wordIndices.get("ABC"), wordIndices.get("ABC"), -1,

            wordIndices.get("BCD"), -1, wordIndices.get("BCD"), -1,

            wordIndices.get("ABC"), -1, wordIndices.get("BCD"), -1,

            wordIndices.get("DEF"), wordIndices.get("DEF"), wordIndices.get("DEF"), -1,
            wordIndices.get("DEF"), -1,
        };

        int [][] expectedWordTfByDocument = new int [3] [];
        expectedWordTfByDocument[wordIndices.get("ABC")] = new int []
        {
            0, 2, 2, 1
        };
        expectedWordTfByDocument[wordIndices.get("BCD")] = new int []
        {
            1, 2, 2, 1
        };
        expectedWordTfByDocument[wordIndices.get("DEF")] = new int []
        {
            3, 4
        };

        checkAsserts(expectedWordImages, expectedWordTf, expectedWordIndices,
            expectedWordTfByDocument);
    }

    protected char [][] createExpectedWordImages(String [] wordImages)
    {
        char [][] expectedWordImages = new char [wordImages.length] [];
        for (int i = 0; i < wordImages.length; i++)
        {
            expectedWordImages[wordIndices.get(wordImages[i])] = wordImages[i]
                .toCharArray();
        }
        return expectedWordImages;
    }

    private void checkAsserts(char [][] expectedWordImages, int [] expectedWordTf,
        int [] expectedWordIndices, int [][] expectedWordTfByDocument)
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);

        assertThat(context.allTokens.wordIndex).as("allTokens.wordIndices").isEqualTo(
            expectedWordIndices);
        assertThat(context.allWords.image).as("allWords.images").isEqualTo(
            expectedWordImages);
        assertThat(context.allWords.tf).as("allWords.tf").isEqualTo(expectedWordTf);
        assertThat(context.allWords.tfByDocument).as("allWords.tfByDocument").isEqualTo(
            expectedWordTfByDocument);
    }

    @Override
    protected void beforePrepareWordIndices(Tokenizer temporaryTokenizer,
        CaseNormalizer temporaryCaseNormalizer)
    {
        temporaryCaseNormalizer.dfCutoff = caseNormalizer.dfCutoff;
    }
}
