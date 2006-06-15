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
 * Passes down the processing chain query results read from XML files in the
 * Carrot<sup>2</sup> format. This component expects that the
 * {@link LocalInputComponent#PARAM_QUERY} parameter contains the name of the
 * data file to be loaded. Tha name must be relative to the local filesystem
 * directory path provided in the constructor or in the {@link #PARAM_INPUT_DIR}
 * parameter.
 * 
 * @author Stanislaw Osinski
 */
public class FileLocalInputComponent extends LocalInputComponentBase
{
    /**
     * A path to the local filesystem directory to read the XML input files
     * from.
     */
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

    /** */
    private File inputDir;

    /** */
    private File defaultInputDir;

    /**
     * Represents a query result containing a list of {@link RawDocument}s and
     * the query that returned these documents.
     * 
     * @author Stanislaw Osinski
     */
    public static class QueryResult
    {
        /** The query */
        public String query;

        /** A list of {@link RawDocument}s returned for the query */
        public List rawDocuments;
    }

    public FileLocalInputComponent()
    {
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

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalInputComponentBase#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        File inputFile = getInputFile(requestContext);

        try
        {
            int requestedResults = getRequestedResultsCount(requestContext);

            // Load query results from the file
            SAXReader reader = new SAXReader();
            Element root = reader.read(inputFile).getRootElement();

            // Pass the actual document count
            passQueryResult(root, requestContext, requestedResults);

            // Pass additional information
            passAdditionalInformation(root, requestContext);
        }
        catch (Exception e)
        {
            throw new ProcessingException("Problems opening source file: ", e);
        }
    }

    /**
     * Returns the requested results count.
     * 
     * @param requestContext
     * @return the requested results count.
     */
    private int getRequestedResultsCount(RequestContext requestContext)
    {
        int requestedResults;
        if (requestContext.getRequestParameters().containsKey(
            LocalInputComponent.PARAM_REQUESTED_RESULTS))
        {
            requestedResults = Integer.parseInt(requestContext
                .getRequestParameters().get(
                    LocalInputComponent.PARAM_REQUESTED_RESULTS).toString());
        }
        else
        {
            requestedResults = -1;
        }
        return requestedResults;
    }

    /**
     * Invoked at the end of the {@link #startProcessing(RequestContext)} method
     * to allow subclasses to pass some additional information to the next
     * component in the chain. Implementation of this method in this class is
     * empty.
     * 
     * @param root
     * @param requestContext
     */
    protected void passAdditionalInformation(Element root,
        RequestContext requestContext)
    {
    }

    /**
     * Passes the query result to the next component in the chain.
     * 
     * @param root root element of the input XML
     * @param requestContext
     * @param requestedResults the number of requested results
     * @throws ProcessingException if an error occurs
     */
    protected void passQueryResult(Element root, RequestContext requestContext,
        int requestedResults) throws ProcessingException
    {
        // Extract documents
        QueryResult queryResult = extractQueryResult(root, requestedResults);

        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
            new Integer(queryResult.rawDocuments.size()));

        // Pass the query
        if (queryResult.query != null)
        {
            requestContext.getRequestParameters().put(
                LocalInputComponent.PARAM_QUERY, queryResult.query);
        }
        else
        {
            requestContext.getRequestParameters().put(
                LocalInputComponent.PARAM_QUERY, query);
        }

        for (Iterator iter = queryResult.rawDocuments.iterator(); iter
            .hasNext();)
        {
            RawDocument rawDocument = (RawDocument) iter.next();
            rawDocumentConsumer.addDocument(rawDocument);
        }
    }

    /**
     * Returns the input {@link File} to read from.
     * 
     * @param requestContext
     * @return the input {@link File} to read from
     * @throws ProcessingException in case of problems determining the input
     *             file
     */
    protected File getInputFile(RequestContext requestContext)
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

        File inputFile = new File(inputDir, query);

        if (!inputFile.isFile() || !inputFile.canRead())
        {
            throw new ProcessingException("Cannot read file: "
                + inputFile.getAbsolutePath());
        }
        return inputFile;
    }

    /**
     * Loads {@link QueryResult} from an XML file in the Carrot<sup>2</sup>
     * format.
     * 
     * @param inputFile input file to load from
     * @param requestedResults the number of results to load
     * @return loaded query result
     * @throws DocumentException if a parsing problem occurs
     */
    public static QueryResult loadQueryResult(File inputFile,
        int requestedResults) throws DocumentException
    {
        SAXReader reader = new SAXReader();
        Element root = reader.read(inputFile).getRootElement();
        QueryResult queryResult = extractQueryResult(root, requestedResults);
        return queryResult;
    }

    /**
     * Parses an XML element in the Carrot<sup>2</sup> format into a
     * {@link QueryResult}.
     * 
     * @param root XML element in the Carrot<sup>2</sup> format
     * @param requestedResults the number of results to parse
     * @return parsed query result
     */
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
        rawDocumentConsumer = null;
    }
}
