package org.carrot2.text.linguistic;

import static org.junit.Assert.*;

import org.carrot2.text.MutableCharArray;
import org.junit.*;

/**
 * Superclass for testing {@link LanguageModel}s.
 */
public abstract class LanguageModelTestBase
{
    /**
     * 
     */
    protected LanguageModel languageModel;

    /**
     * @return Returns language code for this test.
     */
    protected abstract LanguageCode getLanguageCode();

    /**
     * 
     */
    @Before
    public void setupLanguage()
    {
        this.languageModel = new LanguageModelFactory().getLanguage(getLanguageCode());
    }

    /**
     * 
     */
    @Test
    public void testStemmerAvailable()
    {
        assertNotNull(languageModel.getStemmer());
    }

    /**
     * 
     */
    @Test
    public void testLanguageCode()
    {
        assertEquals(getLanguageCode(), languageModel.getLanguageCode());
    }

    /**
     * 
     */
    @Test
    public void testStemming()
    {
        final String [][] testData = getStemmingTestData();
        final Stemmer stemmer = languageModel.getStemmer();

        for (String [] pair : testData)
        {
            assertEquals("Stemming difference: " + pair[0] + " should become " + pair[1]
                + " but was transformed into " + stemmer.stem(pair[0]), pair[1], stemmer
                .stem(pair[0]));
        }
    }

    /**
     * 
     */
    @Test
    public void testCommonWords()
    {
        final String [] testData = getCommonWordsTestData();
        for (String word : testData)
        {
            assertTrue(languageModel.isCommonWord(word));
        }
    }

    /**
     * Override and provide word pairs for {@link LanguageModel#getStemmer()} tests.
     * Sample data should follow this format:
     * 
     * <pre>
     * return new String [] []
     * {
     *     {
     *         &quot;inflected&quot;, &quot;base&quot;
     *     },
     *     {
     *         &quot;inflected&quot;, &quot;base&quot;
     *     },
     * };
     * </pre>
     */
    protected String [][] getStemmingTestData()
    {
        return new String [] [] {
        /* Empty by default. */
        };
    }

    /**
     * Override and provide words for testing against
     * {@link LanguageModel#isCommonWord(MutableCharArray)}).
     */
    protected String [] getCommonWordsTestData()
    {
        return new String [] {
        /* Empty by default. */
        };
    }

}
