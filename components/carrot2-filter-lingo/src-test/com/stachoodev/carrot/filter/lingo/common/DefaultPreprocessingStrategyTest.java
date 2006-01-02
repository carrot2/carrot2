
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.common;


import junit.framework.TestCase;


/**
 * @author stachoo
 */
public class DefaultPreprocessingStrategyTest
    extends TestCase
{
    /**
     * Constructor for DefaultPreprocessingStrategyTest.
     *
     * @param arg0
     */
    public DefaultPreprocessingStrategyTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Test for Snippet[] preprocess(ClusteringContext)
     */
    public void testPreprocessClusteringContext()
    {
        DefaultPreprocessingStrategy defaultPreprocessingStrategy = new DefaultPreprocessingStrategy();

        // Empty data
        DefaultClusteringContext emptyInput = new DefaultClusteringContext();

        assertEquals(
            "empty clustering context", 0,
            defaultPreprocessingStrategy.preprocess(emptyInput).length
        );

        // Nonempty data		
        DefaultClusteringContext nonemptyInput = new DefaultClusteringContext();

        nonemptyInput.addStopWord("this");
        nonemptyInput.addStopWord("is");
        nonemptyInput.addStopWord("a");
        nonemptyInput.addStopWord("the");
        nonemptyInput.addStopWord("of");
        nonemptyInput.addStopWord("with");
        nonemptyInput.addStopWord("some");

        nonemptyInput.addStem("simple", "simpl");
        nonemptyInput.addStem("really", "real");
        nonemptyInput.addStem("now", "now");
        nonemptyInput.addStem("thing", "thing");
        nonemptyInput.addStem("interest", "interest");
        nonemptyInput.addStem("words", "word");
        nonemptyInput.addStem("fiddling", "fiddl");
        nonemptyInput.addStem("separators", "separat");

        nonemptyInput.addSnippet(new Snippet("0", "This is a simple test. Really simple.", ""));
        nonemptyInput.addSnippet(new Snippet("1", "Now the thing of interest - stop words!", ""));
        nonemptyInput.addSnippet(
            new Snippet("2", "Some fiddling.! .? ?with? .!?. ?!.extra ? separators!!!", "")
        );

        Snippet [] nonemptyOutput = new Snippet []
            {
                new Snippet("0", "this is a simpl test . real simpl", ""),
                new Snippet("1", "now the thing of interest - stop word", ""),
                new Snippet("2", "some fiddl . with . extra . separat", "")
            };

        assertEquals(
            "nonempty clustering context", nonemptyOutput,
            defaultPreprocessingStrategy.preprocess(nonemptyInput)
        );
    }


    /**
     * @param comment
     * @param array1
     * @param array2
     */
    protected void assertEquals(String comment, Snippet [] array1, Snippet [] array2)
    {
        assertEquals(comment + ": array sizes", array1.length, array2.length);

        for (int i = 0; i < array1.length; i++)
        {
            System.out.println(array1[i].getText());
            System.out.println(array2[i].getText());
            assertEquals(comment + ": object " + i, true, array1[i].equals(array2[i]));
        }
    }
}
