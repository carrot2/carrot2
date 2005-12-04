/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Stanislaw Osinski, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local.impl;

import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * @author Stanislaw Osinski
 */
public class FileLocalOutputComponent extends LocalOutputComponentBase
    implements RawClustersConsumer
{
    /** Data source path (directory or file) */
    public static final String PARAM_OUTPUT_FILE = "output-path";

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            RawClustersProducer.class
        }));

    /** Capabilities of this component */
    private final static Set CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        {
            RawClustersConsumer.class
        }));

    /** Current request context */
    private RequestContext requestContext;

    /** */
    private File outputFile;

    /** */
    private List rawClusters;

    /**
     * @param file
     */
    public FileLocalOutputComponent()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponentBase#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponentBase#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalOutputComponentBase#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        // Prepare the XML
        Set documents = new HashSet();
        for (Iterator iter = rawClusters.iterator(); iter.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) iter.next();
            collectRawDocuments(rawCluster, documents);
        }

        Element root = DocumentHelper.createElement("searchresult");
        addQuery(root);
        addDocuments(root, documents);
        addClusters(root, rawClusters);

        // Determine file name
        File finalOutput;
        if (outputFile.isDirectory())
        {
            File inputFile = (File) requestContext.getRequestParameters().get(
                FileLocalInputComponent.PARAM_INPUT_FILE);

            String fileName = null;
            if (inputFile != null)
            {
                fileName = inputFile.getName();
            }
            else
            {
                fileName = Integer.toString(new Random().nextInt()) + ".xml";
            }
            finalOutput = new File(outputFile, fileName);
        }
        else
        {
            finalOutput = outputFile;
        }

        // Write result to the file
        try
        {
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(
                finalOutput), new OutputFormat("  ", true));
            xmlWriter.write(root);
            xmlWriter.close();
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ProcessingException("Cannot write results: "
                + e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            throw new ProcessingException("Cannot write results: "
                + e.getMessage());
        }
        catch (IOException e)
        {
            throw new ProcessingException("Cannot write results: "
                + e.getMessage());
        }

        super.endProcessing();
    }

    /**
     * @param root
     */
    private void addQuery(Element root)
    {
        Element queryElement = root.addElement("query");
        final Object query = requestContext.getRequestParameters().get(
            LocalInputComponent.PARAM_QUERY);
        if (query != null)
        {
            queryElement.addText(query.toString());
        }
    }

    /**
     * @param rawCluster
     * @param documents
     */
    private void collectRawDocuments(RawCluster rawCluster, Set documents)
    {
        documents.addAll(rawCluster.getDocuments());
        List subclusters = rawCluster.getSubclusters();
        if (subclusters != null)
        {
            for (Iterator it = subclusters.iterator(); it.hasNext();)
            {
                RawCluster subcluster = (RawCluster) it.next();
                collectRawDocuments(subcluster, documents);
            }
        }
    }

    /**
     * @param root
     * @param documents
     */
    private void addDocuments(Element root, Set documents)
    {
        for (Iterator it = documents.iterator(); it.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) it.next();

            Element documentElement = root.addElement("document");

            documentElement.addAttribute("title", rawDocument.getId()
                .toString());

            if (rawDocument.getTitle() != null)
            {
                documentElement.addElement("title").addText(
                    rawDocument.getTitle());
            }

            if (rawDocument.getSnippet() != null)
            {
                documentElement.addElement("snippet").addText(
                    rawDocument.getSnippet());
            }

            if (rawDocument.getUrl() != null)
            {
                documentElement.addElement("url").addText(rawDocument.getUrl());
            }
        }
    }

    /**
     * @param root
     * @param rawClusters
     */
    private void addClusters(Element root, List rawClusters)
    {
        if (rawClusters == null)
        {
            return;
        }

        for (Iterator it = rawClusters.iterator(); it.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) it.next();
            addCluster(root, rawCluster);
        }
    }

    /**
     * @param root
     * @param rawCluster
     */
    private void addCluster(Element root, RawCluster rawCluster)
    {
        Element groupElement = root.addElement("group");
        if (rawCluster.getProperty(RawCluster.PROPERTY_SCORE) != null)
        {
            groupElement.addAttribute("score", rawCluster.getProperty(
                RawCluster.PROPERTY_SCORE).toString());
        }

        Element titleElement = groupElement.addElement("title");
        List titlePhrases = rawCluster.getClusterDescription();
        for (Iterator it = titlePhrases.iterator(); it.hasNext();)
        {
            String phrase = (String) it.next();
            titleElement.addElement("phrase").addText(phrase);
        }

        // Add documents
        List documents = rawCluster.getDocuments();
        for (Iterator it = documents.iterator(); it.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) it.next();
            groupElement.addElement("document").addAttribute("refid",
                rawDocument.getId().toString());
        }

        // Add subclusters
        addClusters(groupElement, rawCluster.getSubclusters());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalInputComponentBase#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        // Get source path from the request context
        if (!requestContext.getRequestParameters().containsKey(
            PARAM_OUTPUT_FILE))
        {
            throw new ProcessingException(
                "PARAM_OUTPUT_FILE parameter must be set");
        }
        outputFile = (File) requestContext.getRequestParameters().get(
            PARAM_OUTPUT_FILE);

        rawClusters = new ArrayList();

        super.startProcessing(requestContext);

        // Store the current context
        this.requestContext = requestContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        outputFile = null;
        requestContext = null;
        rawClusters = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalOutputComponent#getResult()
     */
    public Object getResult()
    {
        // We've written the result to a file
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawClustersConsumer#addCluster(com.dawidweiss.carrot.core.local.clustering.RawCluster)
     */
    public void addCluster(RawCluster cluster) throws ProcessingException
    {
        rawClusters.add(cluster);
    }
}
