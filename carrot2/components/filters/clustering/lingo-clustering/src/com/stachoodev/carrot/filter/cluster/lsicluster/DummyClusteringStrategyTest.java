

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


package com.stachoodev.carrot.filter.cluster.lsicluster;


import com.stachoodev.carrot.filter.cluster.common.*;
import junit.framework.TestCase;


/**
 * @author stachoo
 */
public class DummyClusteringStrategyTest
    extends TestCase
{
    /**
     * Constructor for DummyClusteringStrategyTest.
     *
     * @param arg0
     */
    public DummyClusteringStrategyTest(String arg0)
    {
        super(arg0);
    }

    public void testCluster()
    {
        // Nonempty data		
        DefaultClusteringContext nonemptyInput = new DefaultClusteringContext();

        nonemptyInput.addStopWord("for");
        nonemptyInput.addStopWord("a");
        nonemptyInput.addStopWord("of");
        nonemptyInput.addStopWord("and");
        nonemptyInput.addStopWord("to");
        nonemptyInput.addStopWord("in");
        nonemptyInput.addStopWord("the");

        nonemptyInput.addStem("simple", "simpl");
        nonemptyInput.addStem("really", "real");
        nonemptyInput.addStem("now", "now");
        nonemptyInput.addStem("thing", "thing");
        nonemptyInput.addStem("things", "thing");
        nonemptyInput.addStem("interest", "interest");
        nonemptyInput.addStem("words", "word");
        nonemptyInput.addStem("fiddling", "fiddl");
        nonemptyInput.addStem("separators", "separat");

        nonemptyInput.addSnippet(
            new Snippet("0", "", "Human machine interface for Lab ABC computer applications")
        );
        nonemptyInput.addSnippet(
            new Snippet("1", "", "A survey of user opinion of computer system response in time")
        );
        nonemptyInput.addSnippet(new Snippet("2", "", "The EPS user interface management system"));
        nonemptyInput.addSnippet(
            new Snippet("3", "", "System and human system engineering testing of EPS")
        );
        nonemptyInput.addSnippet(
            new Snippet("4", "", "Relation of user perceived response in time to error mesurement")
        );
        nonemptyInput.addSnippet(
            new Snippet("5", "", "The generation of random binary unordered trees")
        );
        nonemptyInput.addSnippet(new Snippet("6", "", "The intersection graph of paths in trees"));
        nonemptyInput.addSnippet(
            new Snippet("7", "", "Graph minors IV: Widths of trees and well-quasi-ordering")
        );
        nonemptyInput.addSnippet(new Snippet("8", "", "Graph minors a survey"));

/*        nonemptyInput.addSnippet(
   new Snippet("8", "ala ma kota"));
   nonemptyInput.addSnippet(
       new Snippet("8", "ola ma kota"));
   nonemptyInput.addSnippet(
       new Snippet("8", "ala ma psa"));
   nonemptyInput.addSnippet(
       new Snippet("8", "ola ma psa"));
 */
        DummyClusteringStrategy dummyClusteringStrategy = new DummyClusteringStrategy();

        nonemptyInput.setClusteringStrategy(dummyClusteringStrategy);

        Cluster [] clusters = nonemptyInput.cluster().getClusters();

        for (int i = 0; i < clusters.length; i++)
        {
            System.out.println(clusters[i]);
        }
    }
}
