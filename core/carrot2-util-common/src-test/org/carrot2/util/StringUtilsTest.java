
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

package org.carrot2.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest
{
    @Test
    public void testSplitCamelCase()
    {
        assertEquals("Camel Case Split", StringUtils.splitCamelCase("CamelCaseSplit"));
        assertEquals("String Utils Test", StringUtils.splitCamelCase("StringUtilsTest"));
    }

    @Test
    public void testSplitCamelCaseWithCapitals()
    {
        assertEquals("HTML Editor Test", StringUtils.splitCamelCase("HTMLEditorTest"));
        assertEquals("Simple HTML Formatter", StringUtils.splitCamelCase("SimpleHTMLFormatter"));
    }

    @Test
    public void testSplitCamelCaseWithDigits()
    {
        assertEquals("HTML 123 Test", StringUtils.splitCamelCase("HTML123Test"));
    }

    @Test
    public void testRemoveHtmlNoTags()
    {
        assertThat(StringUtils.removeHtmlTags(">test <string"))
            .isEqualTo(">test <string");
    }

    @Test
    public void testRemoveHtmlSimpleTag()
    {
        assertThat(StringUtils.removeHtmlTags("<div> test </div>")).isEqualTo(" test ");
    }

    @Test
    public void testRemoveHtmlTagWithAttributes()
    {
        assertThat(StringUtils.removeHtmlTags("<a href='x'> test </a>")).isEqualTo(
            " test ");
    }

    @Test
    public void testRemoveHtmlNestedTags()
    {
        assertThat(StringUtils.removeHtmlTags("<a href='x'> test <span>x</span> g</a>"))
            .isEqualTo(" test x g");
    }
}
