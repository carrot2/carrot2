
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

package org.carrot2.text.preprocessing;

import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
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
                // HASHMAP-order dependent.
                3, 1, 1, 1, 0, 3,
            },

            {
                // HASHMAP-order dependent.    
                3, 1, 1, 2, 2, 1,
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

    @Test
    public void testAllQueryWords()
    {
        createDocuments("q1 q2", "q3");

        int [] expectedFordsFlag = new int [3];
        expectedFordsFlag[wordIndices.get("q1")] = AllWords.FLAG_QUERY;
        expectedFordsFlag[wordIndices.get("q2")] = AllWords.FLAG_QUERY;
        expectedFordsFlag[wordIndices.get("q3")] = AllWords.FLAG_QUERY;

        check("q1 q2 q3", expectedFordsFlag);
    }

    @Test
    public void testSomeQueryWords()
    {
        createDocuments("test q2", "aa q1");

        int [] expectedFordsFlag = new int [4];
        expectedFordsFlag[wordIndices.get("q1")] = AllWords.FLAG_QUERY;
        expectedFordsFlag[wordIndices.get("q2")] = AllWords.FLAG_QUERY;

        check("q1 q2 q3", expectedFordsFlag);
    }

    @Test
    public void testNoQueryWords()
    {
        createDocuments("q2", "aa q1");

        int [] expectedFordsFlag = new int [3];

        check("q3", expectedFordsFlag);
    }

    @Test
    public void testBlankQuery()
    {
        createDocuments("q2", "aa q1");

        int [] expectedFordsFlag = new int [3];

        check("", expectedFordsFlag);
    }

    @Test
    public void testNullQuery()
    {
        createDocuments("q2", "aa q1");

        int [] expectedFordsFlag = new int [3];

        check(null, expectedFordsFlag);
    }

    @Test
    public void testDifferentStemsInQuery()
    {
        createDocuments("que01 que02", "test word");

        int [] expectedFordsFlag = new int [4];
        expectedFordsFlag[wordIndices.get("que01")] = AllWords.FLAG_QUERY;
        expectedFordsFlag[wordIndices.get("que02")] = AllWords.FLAG_QUERY;

        check("que04", expectedFordsFlag);
    }

    @Override
    protected ILanguageModelFactory createLanguageModelFactory()
    {
        return new TestLanguageModelFactory();
    }
}
