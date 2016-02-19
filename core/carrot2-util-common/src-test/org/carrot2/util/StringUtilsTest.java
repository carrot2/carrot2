
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

package org.carrot2.util;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;
import static org.carrot2.util.StringUtils.*;

public class StringUtilsTest extends CarrotTestCase
{
    @Test
    public void testSplitCamelCase()
    {
        assertEquals("Camel Case Split", splitCamelCase("CamelCaseSplit"));
        assertEquals("String Utils Test", splitCamelCase("StringUtilsTest"));
    }

    @Test
    public void testSplitCamelCaseWithCapitals()
    {
        assertEquals("HTML Editor Test", splitCamelCase("HTMLEditorTest"));
        assertEquals("Simple HTML Formatter", splitCamelCase("SimpleHTMLFormatter"));
    }

    @Test
    public void testSplitCamelCaseWithDigits()
    {
        assertEquals("HTML 123 Test", splitCamelCase("HTML123Test"));
    }

    @Test
    public void testSplitArray()
    {
        assertEquals("String []", splitCamelCase("String[]"));
    }

    @Test
    public void testIFooFace()
    {
        assertEquals("I Foo Face", splitCamelCase("IFooFace"));
    }

    @Test
    public void testRemoveHtmlNoTags()
    {
        assertThat(removeHtmlTags(">test <string")).isEqualTo(">test <string");
    }

    @Test
    public void testRemoveHtmlSimpleTag()
    {
        assertThat(removeHtmlTags("<div> test </div>")).isEqualTo(" test ");
    }

    @Test
    public void testRemoveHtmlTagWithAttributes()
    {
        assertThat(removeHtmlTags("<a href='x'> test </a>")).isEqualTo(" test ");
    }

    @Test
    public void testRemoveHtmlNestedTags()
    {
        assertThat(removeHtmlTags("<a href='x'> test <span>x</span> g</a>")).isEqualTo(" test x g");
    }
}
