package org.carrot2.text.preprocessing;

import org.carrot2.text.linguistic.*;
import org.junit.Test;

/**
 * Language-independent test cases for {@link LanguageModelStemmer}.
 */
public class StemmerSyntheticTest extends StemmerTestBase
{
    @Test
    public void testEmpty()
    {
        createDocuments();

        char [][] expectedStemImages = new char [] [] {};
        int [] expectedStemTf = new int [] {};
        int [] expectedStemIndices = new int [] {};
        int [][] expectedStemTfByDocument = new int [] [] {};
        byte [][] expectedFieldIndices = new byte [] [] {};

        check(expectedStemImages, expectedStemTf, expectedStemIndices,
            expectedStemTfByDocument, expectedFieldIndices);
    }

    @Test
    public void testSingleStems()
    {
        createDocuments("abc", "bcd");

        char [][] expectedStemImages = new char [] []
        {
            "a".toCharArray(), "b".toCharArray()
        };

        int [] expectedStemTf = new int []
        {
            1, 1
        };

        int [] expectedStemIndices = new int [2];
        expectedStemIndices[wordIndices.get("abc")] = 0;
        expectedStemIndices[wordIndices.get("bcd")] = 1;

        int [][] expectedStemTfByDocument = new int [] []
        {
            {
                0, 1
            },

            {
                0, 1
            }
        };
        byte [][] expectedFieldIndices = new byte [] []
        {
            {
                0
            },
            {
                1
            }
        };

        check(expectedStemImages, expectedStemTf, expectedStemIndices,
            expectedStemTfByDocument, expectedFieldIndices);
    }

    @Test
    public void testFrequentSingleStems()
    {
        createDocuments("abc abc", "bcd bcd bcd");

        char [][] expectedStemImages = new char [] []
        {
            "a".toCharArray(), "b".toCharArray()
        };

        int [] expectedStemTf = new int []
        {
            2, 3
        };

        int [] expectedStemIndices = new int [2];
        expectedStemIndices[wordIndices.get("abc")] = 0;
        expectedStemIndices[wordIndices.get("bcd")] = 1;

        int [][] expectedStemTfByDocument = new int [] []
        {
            {
                0, 2
            },

            {
                0, 3
            }
        };
        byte [][] expectedFieldIndices = new byte [] []
        {
            {
                0
            },
            {
                1
            }
        };

        check(expectedStemImages, expectedStemTf, expectedStemIndices,
            expectedStemTfByDocument, expectedFieldIndices);
    }

    @Test
    public void testOriginalFrequencyAggregation()
    {
        createDocuments("abc acd bcd", "ade bof");

        char [][] expectedStemImages = new char [] []
        {
            "a".toCharArray(), "b".toCharArray()
        };

        int [] expectedStemTf = new int []
        {
            3, 2
        };

        int [] expectedStemIndices = new int [5];
        expectedStemIndices[wordIndices.get("abc")] = 0;
        expectedStemIndices[wordIndices.get("acd")] = 0;
        expectedStemIndices[wordIndices.get("ade")] = 0;
        expectedStemIndices[wordIndices.get("bcd")] = 1;
        expectedStemIndices[wordIndices.get("bof")] = 1;

        int [][] expectedStemTfByDocument = new int [] []
        {
            {
                0, 3
            },

            {
                0, 2
            }
        };
        byte [][] expectedFieldIndices = new byte [] []
        {
            {
                0, 1
            },
            {
                0, 1
            }
        };

        check(expectedStemImages, expectedStemTf, expectedStemIndices,
            expectedStemTfByDocument, expectedFieldIndices);
    }

    @Test
    public void testWordTfByDocumentAggregation()
    {
        createDocuments("abc acd ade", "", "ade", "bcd bof", "", "bcd", "ade", "bof");

        char [][] expectedStemImages = new char [] []
        {
            "a".toCharArray(), "b".toCharArray()
        };

        int [] expectedStemTf = new int []
        {
            5, 4
        };

        int [] expectedStemIndices = new int [5];
        expectedStemIndices[wordIndices.get("abc")] = 0;
        expectedStemIndices[wordIndices.get("acd")] = 0;
        expectedStemIndices[wordIndices.get("ade")] = 0;
        expectedStemIndices[wordIndices.get("bcd")] = 1;
        expectedStemIndices[wordIndices.get("bof")] = 1;

        int [][] expectedStemTfByDocument = new int [] []
        {
            {
                0, 3, 1, 1, 3, 1
            },

            {
                1, 2, 2, 1, 3, 1
            }
        };
        byte [][] expectedFieldIndices = new byte [] []
        {
            {
                0
            },
            {
                1
            }
        };

        check(expectedStemImages, expectedStemTf, expectedStemIndices,
            expectedStemTfByDocument, expectedFieldIndices);
    }

    @Test
    public void testNullStems()
    {
        createDocuments("aa ab", "aa bc");

        char [][] expectedStemImages = new char [] []
        {
            "aa".toCharArray(), "ab".toCharArray(), "bc".toCharArray()
        };

        int [] expectedStemTf = new int []
        {
            2, 1, 1
        };

        int [] expectedStemIndices = new int [3];
        expectedStemIndices[wordIndices.get("aa")] = 0;
        expectedStemIndices[wordIndices.get("ab")] = 1;
        expectedStemIndices[wordIndices.get("bc")] = 2;

        int [][] expectedStemTfByDocument = new int [] []
        {
            {
                0, 2
            },

            {
                0, 1
            },

            {
                0, 1
            }
        };
        byte [][] expectedFieldIndices = new byte [] []
        {
            {
                0, 1
            },
            {
                0
            },
            {
                1
            }
        };

        check(expectedStemImages, expectedStemTf, expectedStemIndices,
            expectedStemTfByDocument, expectedFieldIndices);
    }

    @Override
    protected LanguageModelFactory createLanguageModelFactory()
    {
        return new TestLanguageModelFactory();
    }
}
