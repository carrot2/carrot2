
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.impl;

import java.io.OutputStream;
import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * This component acts as an interceptor in the processing chain and writes an XML stream with the query, documents,
 * clusters and other information.
 * 
 * @see SaveFilterComponentBase
 * @see SaveFilterComponentBase#PARAM_OUTPUT_FILE
 * @see SaveFilterComponentBase#PARAM_OUTPUT_STREAM
 */
public class SaveXmlFilterComponent extends SaveFilterComponentBase
{
    /**
     * Save the XML with documents and/or clusters.
     */
    protected void endProcessing0(OutputStream outputStream, ArrayList rawDocuments, ArrayList rawClusters,
        boolean saveDocuments, boolean saveClusters) throws ProcessingException
    {
        final Element root = DocumentHelper.createElement("searchresult");

        // Add query
        root.add(createQueryElement(requestContext));

        // Add documents to the output XML
        if (saveDocuments)
        {
            addDocuments(root, rawDocuments);
        }

        if (saveClusters)
        {
            // Add clusters to the output XML
            addClusters(root, rawClusters, 0);
        }

        // Add any additional information.
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
            throw new ProcessingException("Cannot write results: " + e.getMessage(), e);
        }
    }

    /**
     * Override this method to add some custom information to the xml file. Implementation of this method in this class
     * is empty.
     * 
     * @param root Root element of the XML file
     * @param requestContext current request context
     */
    protected void addAdditionalInformation(Element root, RequestContext requestContext)
    {
    }

    /**
     * 
     */
    private void addDocuments(Element root, List rawDocuments)
    {
        for (Iterator it = rawDocuments.iterator(); it.hasNext();)
        {
            final RawDocument rawDocument = (RawDocument) it.next();
            root.add(createDocumentElement(rawDocument));
        }
    }

    /**
     * 
     */
    private int addClusters(Element root, List rawClusters, int id)
    {
        if (rawClusters == null)
        {
            return id;
        }

        for (Iterator it = rawClusters.iterator(); it.hasNext();)
        {
            final RawCluster rawCluster = (RawCluster) it.next();
            id = addClusterElement(root, rawCluster, id);
        }
        
        return id;
    }

    /**
     * Creates an element representing the query.
     * 
     * @param requestContext
     * @return an element representing the query.
     */
    protected Element createQueryElement(RequestContext requestContext)
    {
        final Element element = DocumentHelper.createElement("query");

        String query = (String) requestContext.getRequestParameters().get(LocalInputComponent.PARAM_QUERY);
        if (query == null)
        {
            query = "";
        }
        element.setText(query);

        return element;
    }

    protected int addClusterElement(Element root, RawCluster rawCluster, int id)
    {
        final Element element = DocumentHelper.createElement("group");
        element.addAttribute("id", Integer.toString(id));
        id++;

        // Add cluster score
        if (rawCluster.getProperty(RawCluster.PROPERTY_SCORE) != null)
        {
            element.addAttribute("score", rawCluster.getProperty(RawCluster.PROPERTY_SCORE).toString());
        }

        // Add cluster label
        element.add(createClusterTitleElement(rawCluster));

        // Add document references
        final List documents = rawCluster.getDocuments();
        for (Iterator it = documents.iterator(); it.hasNext();)
        {
            final RawDocument rawDocument = (RawDocument) it.next();
            element.addElement("document").addAttribute("refid", getDocumentId(rawDocument));
        }

        // Add to root
        root.add(element);
        
        // Add subclusters
        id = addClusters(element, rawCluster.getSubclusters(), id);

        return id;
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
     */
    private String getDocumentId(RawDocument rawDocument)
    {
        if (rawDocument.getId() != null)
        {
            return rawDocument.getId().toString();
        }
        else
        {
            Object id = rawDocument.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
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
}