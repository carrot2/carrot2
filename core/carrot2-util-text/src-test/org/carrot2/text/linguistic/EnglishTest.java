package org.carrot2.text.linguistic;

/**
 * 
 */
public class EnglishTest extends LanguageModelTestBase
{
    protected LanguageCode getLanguageCode()
    {
        return LanguageCode.ENGLISH;
    }

    protected String [][] getStemmingTestData()
    {
        return new String [] []
        {
            {
                "pulps", "pulp"
            },
            {
                "driving", "drive"
            },
            {
                "king's", "king"
            },
            {
                "mining", "mine"
            }
        };
    }

    protected String [] getCommonWordsTestData()
    {
        return new String []
        {
            "and", "or", "to", "from"
        };
    }
}
