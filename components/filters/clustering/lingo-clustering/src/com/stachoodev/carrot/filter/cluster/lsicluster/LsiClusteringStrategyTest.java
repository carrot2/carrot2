

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


import java.util.HashMap;

import junit.framework.TestCase;

import com.dawidweiss.carrot.filter.stemming.porter.PorterStemmer;
import com.stachoodev.carrot.filter.cluster.common.*;


/**
 * @author stachoo
 */
public class LsiClusteringStrategyTest
    extends TestCase
{
    /**
     * Constructor for DummyClusteringStrategyTest.
     *
     * @param arg0
     */
    public LsiClusteringStrategyTest(String arg0)
    {
        super(arg0);
    }

    public void testCluster()
    {
        // Nonempty data		
        MultilingualClusteringContext nonemptyInput = new MultilingualClusteringContext();
        HashMap stemmers = new HashMap();
        stemmers.put("english", new PorterStemmer());

/*        try
   {
       stemmers.put("polish", new Lametyzator());
   }
   catch (IllegalArgumentException e1)
   {
       e1.printStackTrace();
   }
   catch (IOException e1)
   {
       e1.printStackTrace();
   }*/
        nonemptyInput.setStemmers(stemmers);

        nonemptyInput.setQuery("computer science");

/*
   nonemptyInput.addSnippet(
       new Snippet("0", "", "ethernet network ethernet network"));
   nonemptyInput.addSnippet(
       new Snippet("0", "", "fddi network fddi"));
   nonemptyInput.addSnippet(
       new Snippet("0", "", "ethernet ethernet"));
 */
        nonemptyInput.addSnippet(
            new Snippet(
                "0", "ABC",
                "de Computer science Human machine interface for Lab ABC computer applications"
            )
        );
        nonemptyInput.addSnippet(
            new Snippet(
                "1", "",
                "de Computer science A survey of user opinion of computer system response in time"
            )
        );
        nonemptyInput.addSnippet(new Snippet("2", "", "The EPS user interface management system"));
        nonemptyInput.addSnippet(
            new Snippet(
                "3", "", "de Computer science System and human system engineering testing of EPS"
            )
        );
        nonemptyInput.addSnippet(
            new Snippet(
                "4", "",
                "de Computer science Relation of user perceived response in time to error mesurement"
            )
        );
        nonemptyInput.addSnippet(
            new Snippet("5", "", "de The generation of random binary unordered trees")
        );
        nonemptyInput.addSnippet(
            new Snippet("6", "", "Computer science The intersection graph of paths in trees")
        );
        nonemptyInput.addSnippet(
            new Snippet(
                "7", "", "Computer science Graph minors IV: Widths of trees and well-quasi-ordering"
            )
        );
        nonemptyInput.addSnippet(new Snippet("8", "", "Computer science Graph minors a survey"));
        nonemptyInput.addSnippet(new Snippet("9", "", "ala psa"));
        nonemptyInput.addSnippet(new Snippet("10", "", "ola psa"));
        nonemptyInput.addSnippet(new Snippet("11", "", "ola psa"));
        nonemptyInput.addSnippet(new Snippet("13", "", "De de La bla de bla bla de"));
        nonemptyInput.addSnippet(new Snippet("14", "", "De La bla de bla bla de"));
        nonemptyInput.addSnippet(new Snippet("15", "", "Das blu blu"));
        nonemptyInput.addSnippet(new Snippet("16", "", "Das blu blu"));

        ClusteringStrategy lsiClusteringStrategy = new LsiClusteringStrategy();

        nonemptyInput.setClusteringStrategy(lsiClusteringStrategy);

        Cluster [] clusters = nonemptyInput.cluster().getClusters();

        System.out.println("Result clusters");
        System.out.println("---------------\n");

        for (int i = 0; i < clusters.length; i++)
        {
            System.out.println(clusters[i]);
        }
    }
}
