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

        char [][] expectedStemImagesArray = new char [] [] {};
        int [] expectedStemTfArray = new int [] {};
        int [] expectedStemIndexesArray = new int [] {};
        int [][] expectedStemTfByDocumentArray = new int [] [] {};

        check(expectedStemImagesArray, expectedStemTfArray, expectedStemIndexesArray,
            expectedStemTfByDocumentArray);
    }

    @Test
    public void testSingleStems()
    {
        createDocuments("abc", "bcd");

        char [][] expectedStemImagesArray = new char [] []
        {
            "a".toCharArray(), "b".toCharArray()
        };

        int [] expectedStemTfArray = new int []
        {
            1, 1
        };

        int [] expectedStemIndexesArray = new int [2];
        expectedStemIndexesArray[wordIndices.get("abc")] = 0;
        expectedStemIndexesArray[wordIndices.get("bcd")] = 1;

        int [][] expectedStemTfByDocumentArray = new int [] []
        {
            {
                0, 1
            },

            {
                0, 1
            }
        };

        check(expectedStemImagesArray, expectedStemTfArray, expectedStemIndexesArray,
            expectedStemTfByDocumentArray);
    }

    @Test
    public void testFrequentSingleStems()
    {
        createDocuments("abc abc", "bcd bcd bcd");

        char [][] expectedStemImagesArray = new char [] []
        {
            "a".toCharArray(), "b".toCharArray()
        };

        int [] expectedStemTfArray = new int []
        {
            2, 3
        };

        int [] expectedStemIndexesArray = new int [2];
        expectedStemIndexesArray[wordIndices.get("abc")] = 0;
        expectedStemIndexesArray[wordIndices.get("bcd")] = 1;

        int [][] expectedStemTfByDocumentArray = new int [] []
        {
            {
                0, 2
            },

            {
                0, 3
            }
        };

        check(expectedStemImagesArray, expectedStemTfArray, expectedStemIndexesArray,
            expectedStemTfByDocumentArray);
    }

    @Test
    public void testOriginalFrequencyAggregation()
    {
        createDocuments("abc acd ade", "bcd bof");

        char [][] expectedStemImagesArray = new char [] []
        {
            "a".toCharArray(), "b".toCharArray()
        };

        int [] expectedStemTfArray = new int []
        {
            3, 2
        };

        int [] expectedStemIndexesArray = new int [5];
        expectedStemIndexesArray[wordIndices.get("abc")] = 0;
        expectedStemIndexesArray[wordIndices.get("acd")] = 0;
        expectedStemIndexesArray[wordIndices.get("ade")] = 0;
        expectedStemIndexesArray[wordIndices.get("bcd")] = 1;
        expectedStemIndexesArray[wordIndices.get("bof")] = 1;

        int [][] expectedStemTfByDocumentArray = new int [] []
        {
            {
                0, 3
            },

            {
                0, 2
            }
        };

        check(expectedStemImagesArray, expectedStemTfArray, expectedStemIndexesArray,
            expectedStemTfByDocumentArray);
    }

    @Test
    public void testWordTfByDocumentAggregation()
    {
        createDocuments("abc acd ade", "", "ade", "bcd bof", "", "bcd", "ade", "bof");

        char [][] expectedStemImagesArray = new char [] []
        {
            "a".toCharArray(), "b".toCharArray()
        };

        int [] expectedStemTfArray = new int []
        {
            5, 4
        };

        int [] expectedStemIndexesArray = new int [5];
        expectedStemIndexesArray[wordIndices.get("abc")] = 0;
        expectedStemIndexesArray[wordIndices.get("acd")] = 0;
        expectedStemIndexesArray[wordIndices.get("ade")] = 0;
        expectedStemIndexesArray[wordIndices.get("bcd")] = 1;
        expectedStemIndexesArray[wordIndices.get("bof")] = 1;

        int [][] expectedStemTfByDocumentArray = new int [] []
        {
            {
                0, 3, 1, 1, 3, 1
            },

            {
                1, 2, 2, 1, 3, 1
            }
        };

        check(expectedStemImagesArray, expectedStemTfArray, expectedStemIndexesArray,
            expectedStemTfByDocumentArray);
    }

    @Test
    public void testNullStems()
    {
        createDocuments("aa ab", "aa bc");

        char [][] expectedStemImagesArray = new char [] []
        {
            "aa".toCharArray(), "ab".toCharArray(), "bc".toCharArray()
        };

        int [] expectedStemTfArray = new int []
        {
            2, 1, 1
        };

        int [] expectedStemIndexesArray = new int [3];
        expectedStemIndexesArray[wordIndices.get("aa")] = 0;
        expectedStemIndexesArray[wordIndices.get("ab")] = 1;
        expectedStemIndexesArray[wordIndices.get("bc")] = 2;

        int [][] expectedStemTfByDocumentArray = new int [] []
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

        check(expectedStemImagesArray, expectedStemTfArray, expectedStemIndexesArray,
            expectedStemTfByDocumentArray);
    }

    @Override
    protected LanguageModelFactory createLanguageModelFactory()
    {
        return new TestLanguageModelFactory();
    }
}
