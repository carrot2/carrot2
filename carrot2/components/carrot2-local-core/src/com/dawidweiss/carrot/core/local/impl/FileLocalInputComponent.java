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
public class FileLocalInputComponent extends LocalInputComponentBase
{
    /** Data source path (directory or file) */
    public static final String PARAM_INPUT_DIR = "input-dir";

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsConsumer.class
        }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsProducer.class
        }));

    /** Current query, for information only */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** Current request context */
    private RequestContext requestContext;

    /** */
    private File inputDir;

    /** */
    private File defaultInputDir;

    public static class QueryResult
    {
        public String query;
        public List rawDocuments;
    }

    /**
     * 
     */
    public FileLocalInputComponent(File inputDir)
    {
        if (inputDir == null)
        {
            throw new IllegalArgumentException("inputDir must not be null");
        }

        if (!inputDir.isDirectory())
        {
            throw new IllegalArgumentException("inputDir must be a directory");
        }

        this.defaultInputDir = inputDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponentBase#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponentBase#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        if (next instanceof RawDocumentsConsumer)
        {
            rawDocumentConsumer = (RawDocumentsConsumer) next;
        }
        else
        {
            rawDocumentConsumer = null;
        }
    }

    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        // Get source path from the request context
        inputDir = (File) requestContext.getRequestParameters().get(
            PARAM_INPUT_DIR);
        if (inputDir == null)
        {
            inputDir = defaultInputDir;
        }

        if (inputDir == null)
        {
            throw new ProcessingException(
                "PARAM_INPUT_DIR parameter of type java.io.File must be set");
        }

        if (!inputDir.isDirectory())
        {
            throw new ProcessingException(
                "File provided in the PARAM_INPUT_DIR parameter must be a directory");
        }

        // Pass the query for the following components
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_QUERY, query);

        super.startProcessing(requestContext);

        // Store the current context
        this.requestContext = requestContext;

        File inputFile = new File(inputDir, query);

        if (!inputFile.isFile() || !inputFile.canRead())
        {
            throw new ProcessingException("Cannot read file: "
                + inputFile.getAbsolutePath());
        }

        try
        {
            int requestedResults;
            if (requestContext.getRequestParameters().containsKey(
                LocalInputComponent.PARAM_REQUESTED_RESULTS))
            {
                requestedResults = Integer
                    .parseInt(requestContext.getRequestParameters().get(
                        LocalInputComponent.PARAM_REQUESTED_RESULTS).toString());
            }
            else
            {
                requestedResults = -1;
            }
            QueryResult queryResult = loadQueryResult(inputFile,
                requestedResults);

            // Pass the actual document count
            requestContext.getRequestParameters().put(
                LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
                new Integer(queryResult.rawDocuments.size()));

            // Pass the query
            requestContext.getRequestParameters().put(
                LocalInputComponent.PARAM_QUERY, queryResult.query);

            for (Iterator iter = queryResult.rawDocuments.iterator(); iter
                .hasNext();)
            {
                RawDocument rawDocument = (RawDocument) iter.next();
                rawDocumentConsumer.addDocument(rawDocument);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("Problems opening source file: ", e);
        }
    }

    public static QueryResult loadQueryResult(File inputFile,
        int requestedResults) throws DocumentException
    {
        SAXReader reader = new SAXReader();
        Element root = reader.read(inputFile).getRootElement();
        QueryResult queryResult = extractQueryResult(root, requestedResults);
        return queryResult;
    }

    public static QueryResult extractQueryResult(Element root,
        int requestedResults)
    {
        List documents = root.elements("document");

        int matchingDocuments = documents.size();
        if (requestedResults > 0 && requestedResults < matchingDocuments)
        {
            matchingDocuments = requestedResults;
        }

        QueryResult queryResult = new QueryResult();
        queryResult.rawDocuments = new ArrayList(matchingDocuments);

        // Pass the query
        final Element queryElement = root.element("query");
        if (queryElement != null)
        {
            queryResult.query = queryElement.getText();
        }

        int id = 0;
        for (Iterator i = documents.iterator(); i.hasNext()
            && id < matchingDocuments; id++)
        {
            final Element docElem = (Element) i.next();

            final String url;
            if (docElem.element("url") != null)
            {
                url = docElem.elementText("url");
            }
            else
            {
                url = "nourl://document-id-" + id;
            }

            final String title;
            if (docElem.element("title") != null)
            {
                title = docElem.elementText("title");
            }
            else
            {
                title = null;
            }

            final String snippet;
            if (docElem.element("snippet") != null)
            {
                snippet = docElem.elementText("snippet");
            }
            else
            {
                snippet = null;
            }

            queryResult.rawDocuments.add(new RawDocumentSnippet(
                new Integer(id), title, snippet, url, 0));
        }

        return queryResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        query = null;
        inputDir = null;
        requestContext = null;
        rawDocumentConsumer = null;
    }
}
