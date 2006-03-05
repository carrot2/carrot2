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
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * This component acts as an interceptor in the processing chain and saves
 * query, documents, clusters and other information to a file specified by the
 * {@link #PARAM_OUTPUT_FILE} request parameter. If the
 * {@link #PARAM_OUTPUT_FILE} parameter has not been specified, this component
 * will simply pass the clusters to the next component down the chain.
 * 
 * @author Stanislaw Osinski
 */
public class FileSaveInterceptorFilterComponent extends
    LocalFilterComponentBase implements RawClustersConsumer,
    RawClustersProducer
{
    /** Specifies the file to which the data should be saved */
    public static final String PARAM_OUTPUT_FILE = "output-file";

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            RawClustersProducer.class
        }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        {
            RawClustersConsumer.class, RawClustersProducer.class
        }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            RawClustersConsumer.class
        }));

    /** Reference to the current request context */
    private RequestContext requestContext;

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /** Temporal storage for clusters */
    private List rawClusters;

    /** The file we'll be saving to */
    private File outputFile;

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponentBase#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
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
     * @see com.dawidweiss.carrot.core.local.LocalComponentBase#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalFilterComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawClustersConsumer = (RawClustersConsumer) next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalFilterComponentBase#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        this.requestContext = requestContext;

        // Determine output file
        outputFile = (File) requestContext.getRequestParameters().get(
            PARAM_OUTPUT_FILE);

        if (outputFile != null && outputFile.isDirectory())
        {
            // We need a file here
            outputFile = null;
        }

        if (outputFile != null)
        {
            rawClusters = new ArrayList();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalFilterComponentBase#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        if (outputFile != null)
        {
            Element root = DocumentHelper.createElement("searchresult");

            // Add query
            root.add(createQueryElement(requestContext));

            // Add documents
            addDocuments(root, collectRawDocuments());

            // Add clusters
            addClusters(root, rawClusters);

            // Add additional information
            addAdditionalInformation(root, requestContext);

            // Save to the file
            Document document = DocumentHelper.createDocument(root);
            XMLWriter xmlWriter = null;
            try
            {
                xmlWriter = new XMLWriter(new FileOutputStream(outputFile),
                    new OutputFormat("  ", true));
                xmlWriter.write(document);
            }
            catch (Exception e)
            {
                throw new ProcessingException("Cannot write results: "
                    + e.getMessage());
            }
            finally
            {
                try
                {
                    xmlWriter.close();
                }
                catch (IOException e)
                {
                    throw new ProcessingException("Cannot write results: "
                        + e.getMessage());
                }
            }
        }

        super.endProcessing();
    }

    /**
     * Override this method to add some custom information to the xml file.
     * Implementation of this method in this class is empty.
     * 
     * @param root Root element of the XML file
     * @param requestContext current request context
     */
    protected void addAdditionalInformation(Element root,
        RequestContext requestContext)
    {
    }

    /**
     * @param root
     */
    private void addDocuments(Element root, List rawDocuments)
    {
        for (Iterator it = rawDocuments.iterator(); it.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) it.next();
            root.add(createDocumentElement(rawDocument));
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
            root.add(createClusterElement(rawCluster));
        }
    }

    /**
     * @param requestContext
     * @return
     */
    protected Element createQueryElement(RequestContext requestContext)
    {
        Element element = DocumentHelper.createElement("query");

        String query = (String) requestContext.getRequestParameters().get(
            LocalInputComponent.PARAM_QUERY);
        element.setText(query);

        return element;
    }

    /**
     * @param rawCluster
     * @return
     */
    protected Element createClusterElement(RawCluster rawCluster)
    {
        Element element = DocumentHelper.createElement("group");

        // Add cluster score
        if (rawCluster.getProperty(RawCluster.PROPERTY_SCORE) != null)
        {
            element.addAttribute("score", rawCluster.getProperty(
                RawCluster.PROPERTY_SCORE).toString());
        }

        // Add cluster label
        element.add(createClusterTitleElement(rawCluster));

        // Add document references
        List documents = rawCluster.getDocuments();
        for (Iterator it = documents.iterator(); it.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) it.next();
            element.addElement("document").addAttribute("refid",
                rawDocument.getId().toString());
        }

        // Add subclusters
        addClusters(element, rawCluster.getSubclusters());

        return element;
    }

    /**
     * @param rawCluster
     * @return
     */
    protected Element createClusterTitleElement(RawCluster rawCluster)
    {
        Element element = DocumentHelper.createElement("title");

        List phrases = rawCluster.getClusterDescription();
        for (Iterator it = phrases.iterator(); it.hasNext();)
        {
            String phrase = (String) it.next();
            element.addElement("phrase").setText(phrase);
        }

        return element;
    }

    /**
     * @param rawDocument
     * @return
     */
    protected Element createDocumentElement(RawDocument rawDocument)
    {
        Element element = DocumentHelper.createElement("document");

        if (rawDocument.getId() != null)
        {
            element.addAttribute("id", rawDocument.getId().toString());
        }

        if (rawDocument.getTitle() != null)
        {
            element.addElement("title").addText(rawDocument.getTitle());
        }

        if (rawDocument.getSnippet() != null)
        {
            element.addElement("snippet").addText(rawDocument.getSnippet());
        }

        if (rawDocument.getUrl() != null)
        {
            element.addElement("url").addText(rawDocument.getUrl());
        }

        return element;
    }

    /**
     * @return
     */
    private List collectRawDocuments()
    {
        Set documentSet = new HashSet();
        for (Iterator it = rawClusters.iterator(); it.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) it.next();
            collectRawDocuments(rawCluster, documentSet);
        }

        List orderedDocuments = new ArrayList(documentSet);

        Collections.sort(orderedDocuments, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                RawDocument docA = (RawDocument) o1;
                RawDocument docB = (RawDocument) o2;
                Integer seqA = (Integer) docA
                    .getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
                Integer seqB = (Integer) docB
                    .getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);

                if (seqA == null)
                {
                    return -1;
                }

                if (seqB == null)
                {
                    return 1;
                }

                return seqA.intValue() - seqB.intValue();
            }
        });

        return orderedDocuments;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalFilterComponentBase#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();

        requestContext = null;
        rawClustersConsumer = null;
        rawClusters = null;
        outputFile = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawClustersConsumer#addCluster(com.dawidweiss.carrot.core.local.clustering.RawCluster)
     */
    public void addCluster(RawCluster cluster) throws ProcessingException
    {
        if (outputFile != null)
        {
            rawClusters.add(cluster);
        }

        rawClustersConsumer.addCluster(cluster);
    }

}
