/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
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
    public static final String PARAM_OUTPUT_DIR = "output-dir";

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
    private File outputDir;

    /** */
    private File defaultOutputDir;

    /** */
    private List rawClusters;

    public FileLocalOutputComponent()
    {
    }

    public FileLocalOutputComponent(File outputDir)
    {
        if (outputDir == null)
        {
            throw new IllegalArgumentException(
                "PARAM_OUTPUT_DIR parameter of type java.io.File must be set");
        }

        if (!outputDir.isDirectory())
        {
            throw new IllegalArgumentException(
                "File provided in the PARAM_OUTPUT_DIR parameter must be a directory");
        }

        this.defaultOutputDir = outputDir;
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
        // Determine file name
        String query = (String) requestContext.getRequestParameters().get(
            LocalInputComponent.PARAM_QUERY);
        File finalOutput = new File(outputDir, query);

        saveRawClusters(rawClusters, query, finalOutput, true);

        super.endProcessing();
    }

    /**
     * @throws ProcessingException
     */
    public static void saveRawClusters(List rawClusters, String query,
        File outputFile, boolean saveClusters) throws ProcessingException
    {
        // Prepare the XML
        Set documents = new HashSet();
        for (Iterator iter = rawClusters.iterator(); iter.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) iter.next();
            collectRawDocuments(rawCluster, documents);
        }

        Element root = DocumentHelper.createElement("searchresult");
        addQuery(root, query);
        addRawDocuments(root, documents);
        if (saveClusters)
        {
            addClusters(root, rawClusters);
        }        
        writeToFile(root, outputFile);
    }

    /**
     * @param root
     * @param finalOutput
     * @throws ProcessingException
     */
    private static void writeToFile(Element root, File finalOutput)
        throws ProcessingException
    {
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
    }

    /**
     * @param root
     */
    private static void addQuery(Element root, String query)
    {
        Element queryElement = root.addElement("query");
        if (query != null)
        {
            queryElement.addText(query.toString());
        }
    }

    /**
     * @param rawCluster
     * @param documents
     */
    private static void collectRawDocuments(RawCluster rawCluster, Set documents)
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
    private static void addRawDocuments(Element root, Set documents)
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
    private static void addClusters(Element root, List rawClusters)
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
    private static void addCluster(Element root, RawCluster rawCluster)
    {
        Element groupElement = root.addElement("group");
        if (rawCluster.getProperty(RawCluster.PROPERTY_SCORE) != null)
        {
            groupElement.addAttribute("score", rawCluster.getProperty(
                RawCluster.PROPERTY_SCORE).toString());
        }

        Element titleElement = groupElement.addElement("title");
        List titlePhrases = rawCluster.getClusterDescription();
        Double score = null;
        if (rawCluster.getProperty("label-score") != null)
        {
            score = (Double) rawCluster.getProperty("label-score");
        }

        for (Iterator it = titlePhrases.iterator(); it.hasNext();)
        {
            String phrase = (String) it.next();
            Element phraseElement = titleElement.addElement("phrase");
            phraseElement.addText(phrase);
            if (score != null)
            {
                phraseElement.addAttribute("score", score.toString());
            }
            break;
        }

        // Add synonyms
        List synonymLabels = (List) rawCluster.getProperty("label-synonyms");
        List synonymScores = (List) rawCluster
            .getProperty("label-synonym-scores");
        if (synonymLabels != null)
        {
            for (int i = 0; i < synonymLabels.size(); i++)
            {
                String label = (String) synonymLabels.get(i);
                Double synonymScore = (Double) synonymScores.get(i);

                Element phraseElement = titleElement.addElement("phrase");
                phraseElement.addText(label);
                phraseElement.addAttribute("score", synonymScore.toString());
            }
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
        outputDir = (File) requestContext.getRequestParameters().get(
            PARAM_OUTPUT_DIR);
        if (outputDir == null)
        {
            outputDir = defaultOutputDir;
        }

        if (outputDir == null)
        {
            throw new ProcessingException(
                "PARAM_OUTPUT_DIR parameter of type java.io.File must be set");
        }

        if (!outputDir.isDirectory())
        {
            throw new ProcessingException(
                "File provided in the PARAM_OUTPUT_DIR parameter must be a directory");
        }

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
        outputDir = null;
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
