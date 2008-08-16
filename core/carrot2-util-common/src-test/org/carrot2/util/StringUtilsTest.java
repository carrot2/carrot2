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
