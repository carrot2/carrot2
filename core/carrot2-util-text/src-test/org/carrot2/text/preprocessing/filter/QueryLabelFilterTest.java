package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.preprocessing.*;
import org.junit.Test;

/**
 * Test cases for {@link QueryLabelFilter}.
 */
public class QueryLabelFilterTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.queryLabelFilter.enabled = true;
    }

    @Test
    public void testEmpty()
    {
        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNonQueryWords()
    {
        createDocuments("aa . aa", "bb . bb");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1
        };

        check("Query word", expectedLabelsFeatureIndex);
    }

    @Test
    public void testQueryWords()
    {
        createDocuments("query . Word", "query . word");

        final int [] expectedLabelsFeatureIndex = new int [] {};

        check("Query word", expectedLabelsFeatureIndex);
    }

    @Test
    public void testPhraseWithAllQueryWords()
    {
        createDocuments("query query word", "query query word");

        final int [] expectedLabelsFeatureIndex = new int [] {};

        check("Query word", expectedLabelsFeatureIndex);
    }

    @Test
    public void testPhraseWithSomeQueryWords()
    {
        createDocuments("query word test", "query word test");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 4, 5
        };

        check("Query word", expectedLabelsFeatureIndex);
    }

    private void check(String query, int [] expectedLabelsFeatureIndex)
    {
        createPreprocessingContext(query);
        check(expectedLabelsFeatureIndex);
    }
}
