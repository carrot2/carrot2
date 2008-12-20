
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test cases for {@link UserAgentUtils}.
 */
public class UserAgentUtilsTest
{
    @Test
    public void testNull()
    {
        assertFalse(UserAgentUtils.isModernBrowser((String)null));
    }

    @Test
    public void testBlank()
    {
        assertFalse(UserAgentUtils.isModernBrowser("  "));
    }

    @Test
    public void testFirefox()
    {
        assertTrue(UserAgentUtils
            .isModernBrowser("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008070206 Firefox/3.0.1"));
    }

    @Test
    public void testOpera()
    {
        assertTrue(UserAgentUtils
            .isModernBrowser("Opera/9.20 (Macintosh; Intel Mac OS X; U; en)"));
    }

    @Test
    public void testIE7()
    {
        assertTrue(UserAgentUtils
            .isModernBrowser("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727; Zune 2.0)"));
    }

    @Test
    public void testIE6()
    {
        assertFalse(UserAgentUtils
            .isModernBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; NeosBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)"));
    }

    @Test
    public void testIE55()
    {
        assertFalse(UserAgentUtils
            .isModernBrowser("Mozilla/4.0 (compatible; MSIE 5.5; Windows 98)"));
    }
}
