

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
public class LsiClusteringStrategyTest
    extends TestCase
{
 
    public LsiClusteringStrategyTest(String arg0)
    {
        super(arg0);
    }

    public void testClustering()
        throws Exception
    {
        org.apache.log4j.BasicConfigurator.configure();
        Logger logger = Logger.getLogger("tests.performance");

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(this.getClass().getClassLoader()
             .getResourceAsStream("data/data-mining.xml"));
            //   .getResourceAsStream("longq.xml"));

        List documentList = JDOMHelper.getElements("searchresult/document", doc.getRootElement());

        for (int i=50;i<documentList.size();i+=50) {
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
             //lingoOptions.put("feature.extraction.strategy",
             //    MultilingualFeatureExtractionStrategyWithCutoff.class.getName());
            lingoOptions.put("lsi.threshold.clusterAssignment", "0.150");
            lingoOptions.put("lsi.threshold.candidateCluster",  "0.775");

            MultilingualClusteringContext clusteringContext 
                = new MultilingualClusteringContext(new File("."), lingoOptions);
            
            int max = i;
            for (Iterator j = documentList.iterator(); j.hasNext() && max > 0 ; max--)
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
            ClusteringResults clusteringResults = clusteringContext.cluster();
            Cluster [] clusters = clusteringResults.getClusters();

            tlogger.logElapsedAndStart(logger, "clustering " + i + " results. "
                + ", features: " + clusteringContext.getFeatures().length
                + ", clusters: " + clusters.length);

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
