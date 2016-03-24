
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.filter;

import java.util.Map;

import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Before;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Test cases for {@link QueryWordHighlighter}.
 */
public class QueryWordHighlighterTest extends CarrotTestCase
{
    private Map<String, Object> attrs;


    @Test
    public void contentTruncation()
    {
        QueryWordHighlighterDescriptor.attributeBuilder(attrs)
            .maxContentLength(5);

        check(null, "12345678", "12345...");
    }

    @Test
    public void contentTruncationAndHighlighting()
    {
        QueryWordHighlighterDescriptor.attributeBuilder(attrs)
            .maxContentLength(5);

        check("abc", "abc abc abc", "<b>abc</b> a...");
    }
    
    @Test
    public void testExcludedPatterns()
    {
        QueryWordHighlighterDescriptor.attributeBuilder(attrs)
            .dontHighlightPattern("(?:and)|(?:or)");

        check("foo and bar", "test foo and bar", "test <b>foo</b> and <b>bar</b>");
    }

    @Test
    public void testSanitizePatterns()
    {
        QueryWordHighlighterDescriptor.attributeBuilder(attrs)
            .querySanitizePattern("[+]");

        check("+foo +bar", "test foo and bar", "test <b>foo</b> and <b>bar</b>");
    }

    @Test
    public void testNullQuery()
    {
        check(null, "test", "test");
        check("", "test", "test");
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

    @Before
    public void setup() {
        attrs = Maps.newHashMap();
    }
    
    private void check(String query, String snippetToHighlight, String expectedSnippet)
    {
        final Document document = new Document();
        document.setField(Document.SUMMARY, snippetToHighlight);

        final Controller controller = ControllerFactory.createSimple();
        try
        {
            controller.init(attrs);
            ProcessingResult result = controller.process(
                Lists.newArrayList(document),
                query, 
                QueryWordHighlighter.class);
            
            final Document highlightedDocument = result.getDocuments().get(0);
            assertThat((String) highlightedDocument.getField(Document.SUMMARY + QueryWordHighlighter.HIGHLIGHTED_FIELD_NAME_SUFFIX)).isEqualTo(expectedSnippet);
        }
        finally
        {
            controller.dispose();
        }
    }
}
