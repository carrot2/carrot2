

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


package com.dawidweiss.carrot.adapters.localfilter;


import junit.framework.TestCase;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Attempts to cluster a set of documents using Lingo filter accessed using remote-to-local
 * adapter.
 */
public class TestRemoteLingoWithLocalAdapter
    extends TestCase
{
    public TestRemoteLingoWithLocalAdapter(String s)
    {
        super(s);
    }


    public TestRemoteLingoWithLocalAdapter()
    {
    }

    /**
     * Attempt to run remote lingo component (running on localhost:8080) as a local service.
     */
    public void testAccessRemoteLingoWithLocalAdapter()
        throws Exception
    {
        RemoteToLocalAdapter localLingo = new RemoteToLocalAdapter(
                new URL(
                    "http://localhost:8080/lsi-cluster-filter/service/MultilingualLsiClusterer"
                )
            );

        // create a list of Hit objects for the predefined stream.
        SAXBuilder builder = new SAXBuilder();
        Element root = builder.build(
                new InputStreamReader(
                    this.getClass().getResourceAsStream("data-mining.xml"), "UTF-8"
                )
            ).getRootElement();
        List docs = root.getChildren("document");
        List hits = new ArrayList(docs.size());

        for (Iterator i = docs.iterator(); i.hasNext();)
        {
            final Element doc = (Element) i.next();
            hits.add(
                new Hit()
                {
                    public String getSnippet()
                    {
                        return doc.getChildText("snippet");
                    }


                    public String getURL()
                    {
                        return doc.getChildText("url");
                    }


                    public String getTitle()
                    {
                        return doc.getChildText("title");
                    }
                }
            );
        }

        Iterator clusters = localLingo.clusterHits(hits.iterator(), "data mining");

        int count = dumpClusters(clusters, 0);
        System.out.println("Clusters: " + count);
    }


    private int dumpClusters(Iterator clusters, int indent)
    {
        if (clusters == null)
        {
            return 0;
        }

        int count = 0;

        while (clusters.hasNext())
        {
            count++;

            Cluster c = (Cluster) clusters.next();

            for (int i = 0; i < indent; i++)
            {
                System.out.print("  ");
            }

            System.out.println(Arrays.asList(c.getNamePhrases()));

            // check if documents are really there.
            int docs = 0;

            for (Iterator j = c.getHits(); (j != null) && j.hasNext(); docs++)
            {
                Hit hit = (Hit) j.next();

                for (int i = 0; i < indent; i++)
                {
                    System.out.print("  ");
                }

                System.out.print(" >>");
                System.out.println(hit.getURL() + " --> " + hit.getTitle());
            }

            count += dumpClusters(c.getSubClusters(), indent + 1);
        }

        return count;
    }
}
