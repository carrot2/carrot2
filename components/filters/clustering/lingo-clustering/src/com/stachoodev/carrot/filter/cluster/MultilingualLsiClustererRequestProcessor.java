

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



/**
 * Multilingual LSI clusterer  Stoplists for German, French, Dutch, Italian and Spanish obtained
 * from www.aspseek.org
 */
package com.stachoodev.carrot.filter.cluster;


import java.io.File;
import java.io.InputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.put.util.xml.JDOMHelper;

import com.dawidweiss.carrot.filter.stemming.DirectStemmer;
import com.stachoodev.carrot.filter.cluster.common.*;


/**
 * This filter clusters given documents using a multilingual version of an LSI-based clustering
 * algorithm.
 */
public class MultilingualLsiClustererRequestProcessor
    extends AbstractLsiClustererRequestProcessor
{
    /** Logger */
    protected static final Logger logger = Logger.getLogger(
            MultilingualLsiClustererRequestProcessor.class
        );

    /** Stemmers */
    private HashMap stemmers;

    /**
     * Filters Carrot2 XML data.
     *
     * @param carrotData A valid InputStream to search results data as specified in the Manual.
     *        This filter also accepts additional keywords in the input XML data.
     * @param request Http request which caused this processing (not used in this filter)
     * @param response Http response for this request
     * @param params A map of parameters sent before data stream (unused in this filter)
     */
    public void processFilterRequest(
        InputStream carrotData, HttpServletRequest request, HttpServletResponse response, Map params
    )
        throws Exception
    {
        try
        {
            // parse input data (must be UTF-8 encoded).
            Element root = parseXmlStream(carrotData, "UTF-8");

            // Prepare data
            MultilingualClusteringContext clusteringContext = new MultilingualClusteringContext(
                    new File(getServletConfig().getServletContext().getRealPath(""))
                );

            // Parameters
            clusteringContext.setParameters(params);

            // Stemmers            
            if (stemmers == null)
            {
                initializeStemmers(params);
            }

            clusteringContext.setStemmers(stemmers);

            // Snippets			
            List documentList = JDOMHelper.getElements("searchresult/document", root);

            if (documentList == null)
            {
                // save the output.
                serializeXmlStream(root, response.getOutputStream(), "UTF-8");

                return;
            }

            addSnippets(clusteringContext, documentList);

            // Query
            clusteringContext.setQuery(root.getChildText("query"));

            // Cluster !
            ClusteringResults clusteringResults = clusteringContext.cluster();
            Cluster [] clusters = clusteringResults.getClusters();

            // detect any group elements and remove them
            root.removeChildren("group");

            // Create the output XML
            for (int i = 0; i < clusters.length; i++)
            {
                addToElement(root, clusters[i]);
            }

            // save the output.
            serializeXmlStream(root, response.getOutputStream(), "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * @param stemmerClasses
     */
    private void initializeStemmers(Map parameters)
    {
        stemmers = new HashMap();

        for (Iterator keys = parameters.keySet().iterator(); keys.hasNext();)
        {
            String key = (String) keys.next();

            if (key.startsWith("stemmer."))
            {
                String stemmerClass = (String) ((List) parameters.get(key)).get(0);
                String stemmerName = key.substring(key.indexOf(".") + 1);

                try
                {
                    Class cl = this.getClass().getClassLoader().loadClass((String) stemmerClass);
                    DirectStemmer stemmer = (DirectStemmer) cl.newInstance();
                    stemmers.put(stemmerName, stemmer);
                }
                catch (ClassNotFoundException e)
                {
                    logger.error("Cannot instantiate stemmer: " + stemmerClass, e);
                }
                catch (InstantiationException e)
                {
                    logger.error("Cannot instantiate stemmer: " + stemmerClass, e);
                }
                catch (IllegalAccessException e)
                {
                    logger.error("Cannot instantiate stemmer: " + stemmerClass, e);
                }
            }
        }
    }
}
