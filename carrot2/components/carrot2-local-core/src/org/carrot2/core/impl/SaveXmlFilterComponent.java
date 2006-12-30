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

package org.carrot2.core.impl;

import java.io.*;
import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * This component acts as an interceptor in the processing chain and saves
 * query, documents, clusters and other information to a file specified by the
 * {@link #PARAM_OUTPUT_FILE} request parameter. If the
 * {@link #PARAM_OUTPUT_FILE} parameter has not been specified, this component
 * will simply pass the clusters to the next component down the chain.
 * 
 * @author Stanislaw Osinski
 */
public class SaveXmlFilterComponent 
    extends LocalFilterComponentBase
    implements RawClustersConsumer, RawClustersProducer, RawDocumentsConsumer
{
    /** Specifies the file to which the data should be saved */
    public static final String PARAM_OUTPUT_FILE = "output-file";

    /** Specifies the output stream to which the data should be saved */
    public static final String PARAM_OUTPUT_STREAM = "output-stream";

    /**
     * Specifies whether clusters should also be saved. Value of this parameter
     * must be of type {@link Boolean}.
     */
    public static final String PARAM_SAVE_CLUSTERS = "save-clusters";

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = 
        toSet(RawClustersProducer.class);

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = 
        toSet(new Object [] {RawClustersConsumer.class, RawClustersProducer.class, RawDocumentsConsumer.class});

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = toSet(RawClustersConsumer.class);

    /** Reference to the current request context */
    private RequestContext requestContext;

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /** Temporal storage for clusters */
    private List rawClusters;

    /** Temporal storage for documents */
    private List rawDocuments;

    /** The stream we'll be saving to. */
    private OutputStream outputStream;

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponentBase#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponentBase#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponentBase#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalFilterComponent#setNext(org.carrot2.core.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawClustersConsumer = (RawClustersConsumer) next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalFilterComponentBase#startProcessing(org.carrot2.core.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        this.requestContext = requestContext;

        // Determine output file
        final OutputStream os = (OutputStream) requestContext.getRequestParameters().get(PARAM_OUTPUT_STREAM);
        final File outputFile = (File) requestContext.getRequestParameters().get(PARAM_OUTPUT_FILE);
        
        if (os != null && outputFile != null) {
            throw new ProcessingException("Stream or file is required (mutually exclusive).");
        }

        rawClusters = new ArrayList();
        rawDocuments = new ArrayList();
        if (outputFile != null) {
            if (outputFile.isDirectory()) {
                throw new ProcessingException("Output file must not be a directory.");
            }
            try {
                outputStream = new FileOutputStream(outputFile);
            } catch (FileNotFoundException e) {
                throw new ProcessingException(e);
            }
        } else if (os != null) {
            outputStream = os;
        } else {
            // do nothing.
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalFilterComponentBase#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        if (outputStream != null)
        {
            final Element root = DocumentHelper.createElement("searchresult");

            // Add query
            root.add(createQueryElement(requestContext));

            // Add documents
            if (this.rawDocuments.size() == 0) {
                this.rawDocuments = collectRawDocuments();                
            }
            addDocuments(root, this.rawDocuments);

            // Add clusters
            addClusters(root, rawClusters);

            // Add additional information
            addAdditionalInformation(root, requestContext);

            // Save to the file
            final Document document = DocumentHelper.createDocument(root);
            XMLWriter xmlWriter = null;
            try
            {
                xmlWriter = new XMLWriter(outputStream, new OutputFormat("  ", true));
                xmlWriter.write(document);
                xmlWriter.close();
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
                    outputStream.close();
                }
                catch (IOException e)
                {
                    // ignore.
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
    protected void addAdditionalInformation(Element root, RequestContext requestContext)
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

        Object saveClusters = requestContext.getRequestParameters().get(
            PARAM_SAVE_CLUSTERS);
        if (saveClusters == null || !((Boolean) saveClusters).booleanValue())
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
     * Creates an element representing the query.
     * 
     * @param requestContext
     * @return an element representing the query.
     */
    protected Element createQueryElement(RequestContext requestContext)
    {
        Element element = DocumentHelper.createElement("query");

        String query = (String) requestContext.getRequestParameters().get(
            LocalInputComponent.PARAM_QUERY);
        if (query == null) {
            query = "";
        }
        element.setText(query);

        return element;
    }

    /**
     * Creates an element representing a {@link RawCluster}.
     * 
     * @param rawCluster
     * @return an element representing the {@link RawCluster}.
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
                getDocumentId(rawDocument));
        }

        // Add subclusters
        addClusters(element, rawCluster.getSubclusters());

        return element;
    }

    /**
     * Creates an element representing a {@link RawCluster}'s title.
     * 
     * @param rawCluster
     * @return an element representing the {@link RawCluster}'s title.
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
     * Creates an element representing a {@link RawDocument}.
     * 
     * @param rawDocument
     * @return an element representing the {@link RawDocument}.
     */
    protected Element createDocumentElement(RawDocument rawDocument)
    {
        Element element = DocumentHelper.createElement("document");

        String id = getDocumentId(rawDocument);
        if (id != null)
        {
            element.addAttribute("id", id);
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
     * @param rawDocument
     * @return
     */
    private String getDocumentId(RawDocument rawDocument)
    {
        if (rawDocument.getId() != null)
        {
            return rawDocument.getId().toString();
        }
        else
        {
            Object id = rawDocument
                .getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
            if (id != null)
            {
                return id.toString();
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * 
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
     * @see org.carrot2.core.LocalFilterComponentBase#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();

        requestContext = null;
        rawClustersConsumer = null;
        rawClusters = null;
        outputStream = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawClustersConsumer#addCluster(org.carrot2.core.clustering.RawCluster)
     */
    public void addCluster(RawCluster cluster) throws ProcessingException
    {
        if (outputStream != null)
        {
            rawClusters.add(cluster);
        }

        rawClustersConsumer.addCluster(cluster);
    }

    public void addDocument(RawDocument doc) throws ProcessingException {
        if (outputStream != null) {
            rawDocuments.add(doc);
        }
    }
}
