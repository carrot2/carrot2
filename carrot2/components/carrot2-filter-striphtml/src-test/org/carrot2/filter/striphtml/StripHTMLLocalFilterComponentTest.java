package org.carrot2.filter.striphtml;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * Tests {@link StripHTMLLocalFilterComponent#toText(String)}.
 * 
 * @author Dawid Weiss
 */
public class StripHTMLLocalFilterComponentTest extends TestCase
{
    public StripHTMLLocalFilterComponentTest(String t)
    {
        super(t);
    }

    /**
     *
     */
    public void testNull() throws IOException
    {
        assertEquals(null, StripHTMLLocalFilterComponent.toText(null));
    }

    /**
     *
     */
    public void testEmpty() throws IOException
    {
        assertEquals("", StripHTMLLocalFilterComponent.toText(""));
    }

    /**
     *
     */
    public void testPlainText() throws IOException
    {
        final String text = " \tDawid Weiss\n19037\r\n";
        assertEquals(text, StripHTMLLocalFilterComponent.toText(text));
    }

    /**
     *
     */
    public void testSimpleTags() throws IOException
    {
        final String html = "This is <a href=\"buhu\">link </a>.";
        final String text = "This is link .";
        assertEquals(text, StripHTMLLocalFilterComponent.toText(html));
    }

    /**
     *
     */
    public void testEntity() throws IOException
    {
        final String html = "An entity &lt; bad entity &gt .";
        final String text = "An entity < bad entity &gt .";
        assertEquals(text, StripHTMLLocalFilterComponent.toText(html));
    }
}
