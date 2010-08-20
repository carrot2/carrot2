
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

package org.carrot2.webapp.filter;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.Document;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for {@link QueryWordHighlighter}.
 */
public class QueryWordHighlighterTest
{
    @Test
    public void testNullQuery()
    {
        check(null, "test", null);
        check("", "test", null);
    }

    @Test
    public void testHtmlInSnippet()
    {
        check("test", "test <div>test</div>", "<b>test</b> &lt;div&gt;<b>test</b>&lt;/div&gt;");
    }

    @Test
    public void testHtmlInQuery()
    {
        check("<div> tag", "<div> tag", "<b>&lt;div&gt;</b> <b>tag</b>");
    }
    
    @Test
    public void testOneWordQuery()
    {
        check("test", "this is a test", "this is a <b>test</b>");
    }
    
    @Test
    public void testUtf8Query()
    {
        check("żółć", "Żółć żółć żÓłć", "<b>Żółć</b> <b>żółć</b> <b>żÓłć</b>");
    }
    
    @Test
    public void testSpacesInQuery()
    {
        check("raghuram   mtv   bi ography", "raghuram   mtv   bi ography", "<b>raghuram</b>   <b>mtv</b>   <b>bi</b> <b>ography</b>");
    }
    
    @Test
    public void testMultiWordQuery()
    {
        check("some test case", "many tests will fail in some case",
            "many <b>test</b>s will fail in <b>some</b> <b>case</b>");
    }
    
    @Test
    public void testCaseInsensitivity()
    {
        check("tEst", "test TEST tEst Test", "<b>test</b> <b>TEST</b> <b>tEst</b> <b>Test</b>");
    }

    @Test
    public void testSpecialCharactersQuery()
    {
        check("x23+?.", "x23+?.g zz x23", "<b>x23+?.</b>g zz x23");
    }
    
    @Test
    public void testDoubleQuotes()
    {
        check("\"the query\"", "the snippet with the query", "<b>the</b> snippet with <b>the</b> <b>query</b>");
    }
    
    private void check(String query, String snippetToHighlight, String expectedSnippet)
    {
        final Document document = new Document();
        document.setField(Document.SUMMARY, snippetToHighlight);

        final QueryWordHighlighter highlighter = new QueryWordHighlighter();
        highlighter.documents = Lists.newArrayList(document);
        highlighter.query = query;

        highlighter.process();

        final Document highlightedDocument = highlighter.documents.get(0);
        
        assertThat(
            highlightedDocument.getField(Document.SUMMARY
                + QueryWordHighlighter.HIGHLIGHTED_FIELD_NAME_SUFFIX)).isEqualTo(
            expectedSnippet);
    }
}
