/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util;


import junit.framework.TestCase;


/**
 * The <code>HTMLTextStripperTest</code> test cases.
 */
public class HTMLTextStripperTest
    extends TestCase
{
    public HTMLTextStripperTest(String s)
    {
        super(s);
    }


    public HTMLTextStripperTest()
    {
        super();
    }

    public void testSimpleStrings()
    {
        String [][] pairs = new String [][]
            {
                { "no changes here!", "no changes here!" },
                { "", "" }
            };
        compare(pairs);
    }

    public void testIncorrectTagWithSpacesInside() {
        String [][] pairs = new String [][]
            {
                { "abc <here is a tag> def", "abc <here is a tag> def" }
            };
        compare(pairs);
    }

    public void testTagSeries() {
        String [][] pairs = new String [][]
            {
                { "abc <tag1>a<tag2>b<tag3> gh", "abc a b gh" }
            };
        compare(pairs);
    }

    public void testStartAndEndTags()
    {
        String [][] pairs = new String [][]
            {
                { "abc <start>def</start> gh", "abc def gh" }
            };
        compare(pairs);
    }
    
    
    public void testScriptTagSpanningMultipleLines() {
        String [][] pairs = new String [][]
            {
                { "abc <script type=\"text/javascript\">\n\rbubu.\n</script> gh", "abc gh" }
            };
        compare(pairs);        
    } 


    public void testStandardEntities()
    {
        String [][] pairs = new String [][]
            {
                { "abc&amp;&lt;&gt;&quot;&apos;def", "abc&<>\"'def" }
            };
        compare(pairs);
    }


    public void testNumericDecimalEntities()
    {
        String [][] pairs = new String [][]
            {
                { "abc&#65;def", "abcAdef" }
            };
        compare(pairs);
    }


    public void testNumericHexEntities()
    {
        String [][] pairs = new String [][]
            {
                { "abc&#x41;def", "abcAdef" }
            };
        compare(pairs);
    }


    public void testMissingNamedEntities()
    {
        String [][] pairs = new String [][]
            {
                { "abc&namedEntity;def", "abcdef" }
            };
        compare(pairs);
    }


    public void testIncorrectNumericalEntities()
    {
        String [][] pairs = new String [][]
            {
                { "abc&#abc;def", "abcdef" }
            };
        compare(pairs);
    }


    public void testAmpersandNotAnEntity()
    {
        String [][] pairs = new String [][]
            {
                { "abc & typical not entity.", "abc & typical not entity." },
                { "&&&&", "&&&&" }
            };
        compare(pairs);
    }


    private final void compare(String [][] pairs)
    {
        for (int i = 0; i < pairs.length; i++)
        {
            String shouldBe = normalize(pairs[i][1]); 
            String is = normalize(HTMLTextStripper.getInstance().htmlToText(pairs[i][0])); 
            if (!shouldBe.equals(is)) {
                System.out.println(shouldBe + "\n" + is + "\n\n");
            }
            assertEquals(shouldBe, is);
        }
    }


    private String normalize(String t)
    {
        String p = t.trim();
        t = "";

        for (int i = 0; i < p.length(); i++)
        {
            if (t.length() > 0)
            {
                if (
                    Character.isWhitespace(p.charAt(i))
                        && Character.isWhitespace(t.charAt(t.length() - 1))
                )
                {
                    continue;
                }
            }

            t = t + p.charAt(i);
        }

        return t;
    }
}
