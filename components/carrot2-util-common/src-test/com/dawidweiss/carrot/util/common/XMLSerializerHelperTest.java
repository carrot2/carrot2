
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util.common;


import junit.framework.TestCase;


/**
 * The <code>XMLSerializerHelper</code> test cases.
 */
public class XMLSerializerHelperTest
    extends TestCase
{
    public XMLSerializerHelperTest(String s)
    {
        super(s);
    }

    /**
     * Test regular string (no change should be made).
     */
    public void testNoEscapedString()
    {
        String str = "abcdef \n\r\t xy.";
        assertEquals(str, XMLSerializerHelper.instance.toValidXmlText(str, false));
    }


    /**
     * Test default entities.
     */
    public void testDefaultEntities()
    {
        String str = ":&\"'<>:";
        String expected = ":&amp;&quot;&apos;&lt;&gt;:";
        assertEquals(expected, XMLSerializerHelper.instance.toValidXmlText(str, false));
    }


    /**
     * Test silent omission of invalid XML characters.
     */
    public void testSilentTreatmentOfInvalidCharacters()
    {
        String str = ":" + (char) 0x1b + ":";
        String expected = "::";

        try
        {
            assertEquals(expected, XMLSerializerHelper.instance.toValidXmlText(str, false));
        }
        catch (IllegalArgumentException e)
        {
            fail("silent treatment of an invalid char sequence caused an exception.");
        }
    }


    /**
     * Test exception on treatment of invalid XML characters.
     */
    public void testExceptionOnTreatmentOfInvalidCharacters()
    {
        String str = ":" + (char) 0x1b + ":";
        String expected = "::";

        try
        {
            assertEquals(expected, XMLSerializerHelper.instance.toValidXmlText(str, true));
            fail("Exception was expected.");
        }
        catch (IllegalArgumentException e)
        {
            // this is all right. we expected it.
        }
    }


    /**
     * Tests whether the newInstance factory method is usable.
     */
    public void testNewInstanceAccessor()
    {
        XMLSerializerHelper.getInstance().toValidXmlText("", false);
    }
}
