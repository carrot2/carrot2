
/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
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
    
    
    public void testSimpleStrings() {
        String [][] pairs = new String [][] {
            {"no changes here!", "no changes here!"},
            {"", ""}
        };
        compare( pairs );
    }

    public void testCorrectTags() {
        String [][] pairs = new String [][] {
            {"abc <here is a tag> def", "abc def"},
            {"abc <start>def</start> gh", "abc def gh"}
        };
        compare( pairs );
    }    

    public void testStandardEntities() {
        String [][] pairs = new String [][] {
            {"abc&amp;&lt;&gt;&quot;&apos;def", "abc&<>\"'def"}
        };
        compare( pairs );
    }    

    public void testNumericDecimalEntities() {
        String [][] pairs = new String [][] {
            {"abc&#65;def", "abcAdef"}
        };
        compare( pairs );
    }    

    public void testNumericHexEntities() {
        String [][] pairs = new String [][] {
            {"abc&#x41;def", "abcAdef"}
        };
        compare( pairs );
    }

    public void testMissingNamedEntities() {
        String [][] pairs = new String [][] {
            {"abc&namedEntity;def", "abcdef"}
        };
        compare( pairs );
    }

    public void testIncorrectNumericalEntities() {
        String [][] pairs = new String [][] {
            {"abc&#abc;def", "abcdef"}
        };
        compare( pairs );
    }

    public void testAmpersandNotAnEntity() {
        String [][] pairs = new String [][] {
            {"abc & typical not entity.", "abc & typical not entity."},
            {"&&&&", "&&&&" }
        };
        compare( pairs );
    }
    
    private final void compare( String [][] pairs ) {
        for (int i=0;i<pairs.length;i++) {
            assertEquals(
                normalize(pairs[i][1]),
                normalize(HTMLTextStripper.getInstance().htmlToText(pairs[i][0])));
        }
    }

    private String normalize(String t) {
        String p = t.trim();
        t = "";
        for (int i=0;i<p.length();i++) {
            if (t.length() > 0) {
                if (Character.isWhitespace(p.charAt(i))
                    && Character.isWhitespace(t.charAt(t.length()-1))) {
                        continue;
                }
            }
            t = t + p.charAt(i);
        }
        System.out.println(t);
        return t;
    }
}
