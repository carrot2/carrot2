package org.carrot2.text.preprocessing;

import org.junit.Test;

/**
 * English test cases for {@link LanguageModelStemmer}.
 */
public class StemmerEnglishTest extends StemmerTestBase
{
    @Test
    public void testLowerCaseWords()
    {
        createDocuments("data mining", "data mining");

        char [][] expectedStemImages = new char [] []
        {
            "data".toCharArray(), "mine".toCharArray()
        };

        int [] expectedStemTf = new int []
        {
            2, 2
        };

        int [] expectedStemIndices = new int [2];
        expectedStemIndices[wordIndices.get("data")] = 0;
        expectedStemIndices[wordIndices.get("mining")] = 1;

        int [][] expectedStemTfByDocument = new int [] []
        {
            {
                0, 2
            },

            {
                0, 2
            }
        };

        check(expectedStemImages, expectedStemTf, expectedStemIndices,
            expectedStemTfByDocument);
    }

    @Test
    public void testUpperCaseWords()
    {
        createDocuments("DATA MINING", "DATA MINING");

        char [][] expectedStemImages = new char [] []
        {
            "data".toCharArray(), "mine".toCharArray()
        };

        int [] expectedStemTf = new int []
        {
            2, 2
        };

        int [] expectedStemIndices = new int [2];
        expectedStemIndices[wordIndices.get("DATA")] = 0;
        expectedStemIndices[wordIndices.get("MINING")] = 1;

        int [][] expectedStemTfByDocument = new int [] []
        {
            {
                0, 2
            },

            {
                0, 2
            }
        };

        check(expectedStemImages, expectedStemTf, expectedStemIndices,
            expectedStemTfByDocument);
    }

    @Test
    public void testMixedCaseWords()
    {
        createDocuments("DATA MINING Data Mining", "Data Mining Data Mining");

        char [][] expectedStemImages = new char [] []
        {
            "data".toCharArray(), "mine".toCharArray()
        };

        int [] expectedStemTf = new int []
        {
            4, 4
        };

        int [] expectedStemIndices = new int [2];
        expectedStemIndices[wordIndices.get("Data")] = 0;
        expectedStemIndices[wordIndices.get("Mining")] = 1;

        int [][] expectedStemTfByDocument = new int [] []
        {
            {
                0, 4
            },

            {
                0, 4
            }
        };

        check(expectedStemImages, expectedStemTf, expectedStemIndices,
            expectedStemTfByDocument);
    }
}
