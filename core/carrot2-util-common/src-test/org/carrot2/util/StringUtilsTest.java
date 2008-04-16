package org.carrot2.util;

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

}
