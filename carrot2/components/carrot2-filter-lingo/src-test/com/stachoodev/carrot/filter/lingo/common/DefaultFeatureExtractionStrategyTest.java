

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */


package com.stachoodev.carrot.filter.lingo.common;


import junit.framework.TestCase;


/**
 * @author stachoo
 */
public class DefaultFeatureExtractionStrategyTest
    extends TestCase
{
    /**
     * Constructor for DefaultFeatureExtractionStrategyTest.
     *
     * @param arg0
     */
    public DefaultFeatureExtractionStrategyTest(String arg0)
    {
        super(arg0);
    }

    public void testExtractFeatures()
    {
        DefaultFeatureExtractionStrategy defaultFeatureExtractionStrategy = new DefaultFeatureExtractionStrategy();

        // Empty data
        DefaultClusteringContext emptyInput = new DefaultClusteringContext();
        emptyInput.preprocess();

        assertEquals(
            "empty clustering context", 0,
            defaultFeatureExtractionStrategy.extractFeatures(emptyInput).length
        );

        // Nonempty data		
        DefaultClusteringContext nonemptyInput = new DefaultClusteringContext();

        nonemptyInput.addStopWord("this");
        nonemptyInput.addStopWord("is");
        nonemptyInput.addStopWord("a");
        nonemptyInput.addStopWord("the");
        nonemptyInput.addStopWord("of");
        nonemptyInput.addStopWord("with");
        nonemptyInput.addStopWord("and");
        nonemptyInput.addStopWord("some");

//		nonemptyInput.addStopWord("more");
        nonemptyInput.addStem("simple", "simpl");
        nonemptyInput.addStem("really", "real");
        nonemptyInput.addStem("now", "now");
        nonemptyInput.addStem("thing", "thing");
        nonemptyInput.addStem("things", "thing");
        nonemptyInput.addStem("interest", "interest");
        nonemptyInput.addStem("words", "word");
        nonemptyInput.addStem("fiddling", "fiddl");
        nonemptyInput.addStem("separators", "separat");

        nonemptyInput.setQuery("\"test\"");

        nonemptyInput.addSnippet(new Snippet("0", "", "a simple test. a simple test."));
        nonemptyInput.addSnippet(new Snippet("1", "", "more of a simple test ."));
        nonemptyInput.addSnippet(new Snippet("2", "", "more of a simple test ."));

        nonemptyInput.preprocess();

        Feature [] features = defaultFeatureExtractionStrategy.extractFeatures(nonemptyInput);

        for (int i = 0; i < features.length; i++)
        {
            System.out.println(features[i].toString());
        }
    }
}
