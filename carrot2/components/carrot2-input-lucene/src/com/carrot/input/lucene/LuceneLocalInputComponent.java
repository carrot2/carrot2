/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.carrot.input.lucene;

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.document.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.profiling.*;

/**
 * Implements a local input component that reads data from Lucene index. Use
 * {@link com.carrot.input.lucene.LuceneLocalInputComponentFactory}to obtain
 * instances.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LuceneLocalInputComponent extends ProfiledLocalInputComponentBase
    implements RawDocumentsProducer
{
    /** The default number of requested results */
    public static final int DEFAULT_REQUESTED_RESULTS = 100;

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsProducer.class }));

    /** Lucene Searcher */
    private Searcher searcher;

    /** Lucene Analyzer */
    private Analyzer analyzer;

    /** Lucene fields to be searched */
    private String [] searchFields;

    /** Contet fields */
    private String titleField;
    private String summaryField;
    private String urlField;

    /** Current query. */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** Current request context */
    private RequestContext requestContext;

    /**
     * No direct instantiation
     */
    protected LuceneLocalInputComponent(Searcher searcher, Analyzer analyzer,
        String [] searchFields, String titleField, String summaryField,
        String urlField)
    {
        this.searcher = searcher;
        this.analyzer = analyzer;
        this.searchFields = searchFields;
        this.titleField = titleField;
        this.summaryField = summaryField;
        this.urlField = urlField;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        query = null;
        rawDocumentConsumer = null;
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
     * @see com.dawidweiss.carrot.core.local.LocalComponent#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        
        this.requestContext = requestContext;

        // See if the required attributes are present in the query
        // context:
        Map params = requestContext.getRequestParameters();

        try
        {
            // Produce results.
            pushResults(params);
        }
        catch (ParseException e)
        {
            throw new ProcessingException("Query parse exception", e);
        }
        catch (IOException e)
        {
            throw new ProcessingException("Query execution exception", e);
        }
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "Lucene Input";
    }

    /**
     * @throws ParseException
     * @throws IOException
     * @throws ProcessingException
     *  
     */
    private void pushResults(Map params) throws ParseException, IOException,
        ProcessingException
    {
        // Get parameters
        int requestedDocuments;
        try
        {
            requestedDocuments = Integer.parseInt((String) params
                .get(LocalInputComponent.PARAM_REQUESTED_RESULTS));
        }
        catch (Exception e)
        {
            requestedDocuments = DEFAULT_REQUESTED_RESULTS;
        }

        int startAt;
        try
        {
            startAt = Integer.parseInt((String) params
                .get(LocalInputComponent.PARAM_START_AT));
        }
        catch (Exception e)
        {
            startAt = 0;
        }

        startTimer();
        
        // Create a boolean query that combines all fields  
        BooleanQuery booleanQuery = new BooleanQuery();
        for (int i = 0; i < searchFields.length; i++)
        {
            Query queryComponent = QueryParser.parse(query, searchFields[i], analyzer);
            booleanQuery.add(queryComponent, false, false);
        }

        // Perform query
        Hits hits = searcher.search(booleanQuery);
        int endAt = Math.min(hits.length(), startAt + requestedDocuments);

        stopTimer();

        // Pass the actual document count
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
            new Integer(endAt - startAt));

        // Pass the query
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_QUERY, query);

        // Push results
        for (int i = startAt; i < endAt; i++)
        {
            Document doc = hits.doc(i);

            RawDocumentSnippet rawDocument = new RawDocumentSnippet(
                new Integer(hits.id(i)), doc.get(titleField), doc
                    .get(summaryField), doc.get(urlField), hits.score(i));
            rawDocumentConsumer.addDocument(rawDocument);
        }
    }
}