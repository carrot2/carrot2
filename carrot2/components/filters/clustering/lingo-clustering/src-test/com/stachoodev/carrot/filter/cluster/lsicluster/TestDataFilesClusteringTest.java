

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
import com.stachoodev.util.log.TimeLogger;

import junit.framework.TestCase;
import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.put.util.xml.JDOMHelper;


/**
 * @author Dawid Weiss
 */
public class TestDataFilesClusteringTest
    extends TestCase
{
 
    public TestDataFilesClusteringTest(String arg0)
    {
        super(arg0);
    }


    public void testClusteringOfDataFiles()
        throws Exception
    {
        org.apache.log4j.BasicConfigurator.configure();
        Logger logger = Logger.getLogger("tests.performance");

        File dataDir = new File("data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            fail("'data' directory not available: "
                + dataDir.getAbsolutePath());
        }

        File [] tests = dataDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".xml");
            }
        });

        for (int f = 0; f < tests.length ; f++) {
            SAXBuilder builder = new SAXBuilder();
            FileInputStream is = new FileInputStream( tests[f] );
            Document doc;
            try {
                doc = builder.build(is);
            }
            finally {
                is.close();
            }            

            List documentList = JDOMHelper.getElements("searchresult/document", doc.getRootElement());

            logger.info("Clustering: " +
                tests[f].getName() + ", " + documentList.size() + " documents.");     

    
            TimeLogger tlogger = new TimeLogger();
            tlogger.start();
    
            Map lingoOptions = new HashMap();
            lingoOptions.put("stemmer.english",
                "com.dawidweiss.carrot.filter.stemming.porter.PorterStemmer");
            lingoOptions.put("stemmer.polish", 
                "com.dawidweiss.carrot.filter.stemming.lametyzator.Lametyzator");
            lingoOptions.put("preprocessing.class",
                // "com.stachoodev.carrot.filter.cluster.common.MultilingualPreprocessingStrategy");
                   CarrotLibTokenizerPreprocessingStrategy.class.getName());
            lingoOptions.put("lsi.threshold.clusterAssignment", "0.150");
            lingoOptions.put("lsi.threshold.candidateCluster",  "0.775");

            MultilingualClusteringContext clusteringContext 
                = new MultilingualClusteringContext(new File("../.."), lingoOptions);
            
            for (Iterator j = documentList.iterator(); j.hasNext() ;)
            {
                Element document = (Element) j.next();
                String title = document.getChildText("title");
                String snippet = document.getChildText("snippet");
                clusteringContext.addSnippet(
                    new Snippet(document.getAttributeValue("id"), title, snippet)
                );
            }
    
            // Query
            clusteringContext.setQuery(doc.getRootElement().getChildText("query"));
    
            // Cluster !
            ClusteringResults clusteringResults;  
            try {
                clusteringResults = clusteringContext.cluster();
            }
            catch (Exception e) {
                logger.error("Error in clustering.", e);
                continue;
            }
            Cluster [] clusters = clusteringResults.getClusters();

            tlogger.logElapsedAndStart(logger, "clustering " + documentList.size() + " results. "
                + ", features: " + clusteringContext.getFeatures().length
                + ", clusters: " + clusters.length);

            if (logger.isEnabledFor(org.apache.log4j.Level.DEBUG)) {
                StringBuffer buf = new StringBuffer();                
                for (int j=0;j<clusters.length;j++) {
                    buf.append(Arrays.asList(clusters[j].getLabels()));
                    buf.append("\n\t" + clusters[j].getSnippets()[0].getId());
                    buf.append("\n\n");
                }
                logger.debug(buf);
            }
        }

    }
}
