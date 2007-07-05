/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.lucene;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.ProfiledLocalInputComponentBase;

/**
 * @version $Revision: 1810 $
 */
public class LuceneDumpLocalInputComponent extends ProfiledLocalInputComponentBase
    implements RawDocumentsProducer
{
    /** The default number of requested results */
    public final static int DEFAULT_REQUESTED_RESULTS = 100;

    /**
     * A request-context parameter overriding the default search configuration. The value
     * of this parameter must be an instance of
     * {@link LuceneLocalInputComponentFactoryConfig}.
     */
    public final static String LUCENE_CONFIG = "org.carrot2.input.lucene.config";

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = toSet(RawDocumentsConsumer.class);

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = toSet(RawDocumentsProducer.class);

    /**
     * All information required to perform a search in Lucene.
     */
    private final LuceneLocalInputComponentConfig luceneSettings;

    /** Current query. */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** Current request context */
    private RequestContext requestContext;

    /**
     * Create an instance of the component with a set of predefined settings.
     */
    public LuceneDumpLocalInputComponent(LuceneLocalInputComponentConfig settings)
    {
        this.luceneSettings = settings;
    }

    /**
     * Create an empty instance of this component. You will need to pass Lucene
     * configuration at query time using {@link #LUCENE_CONFIG}.
     */
    public LuceneDumpLocalInputComponent()
    {
        this.luceneSettings = null;
    }

    /*
     * @see org.carrot2.core.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * @see org.carrot2.core.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        query = null;
        rawDocumentConsumer = null;
    }

    /*
     * @see org.carrot2.core.LocalInputComponent#setNext(org.carrot2.core.LocalComponent)
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
     * @see org.carrot2.core.LocalComponent#startProcessing(org.carrot2.core.RequestContext)
     */
    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        super.startProcessing(requestContext);

        this.requestContext = requestContext;

        // See if the required attributes are present in the query
        // context:
        Map params = requestContext.getRequestParameters();
        try
        {
            // Produce results.
            pushResults(params, luceneSettings);
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
     * @see org.carrot2.core.LocalComponent#getName()
     */
    public String getName()
    {
        return "Lucene Dump";
    }

    /**
     *
     */
    private void pushResults(Map params, LuceneLocalInputComponentConfig luceneSettings)
        throws ParseException, IOException, ProcessingException
    {
        // check if there is an override for lucene settings in the context.
        if (params.containsKey(LUCENE_CONFIG))
        {
            luceneSettings = (LuceneLocalInputComponentConfig) params.get(LUCENE_CONFIG);
        }

        if (luceneSettings == null)
        {
            throw new ProcessingException(
                "Lucene input component not configured. Need LuceneSettings.");
        }

        // Query is the number of documents to read
        int docsRequested = Math.min(Integer.parseInt(query), luceneSettings.indexReader
            .numDocs());

        // Pass the actual document count
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
            new Integer(docsRequested));

        // Pass the query
        requestContext.getRequestParameters().put(LocalInputComponent.PARAM_QUERY, query);

        int docsPushed = 0;
        for (int i = 0; i < luceneSettings.indexReader.maxDoc()
            && docsPushed < docsRequested; i++)
        {
            if (!luceneSettings.indexReader.isDeleted(i))
            {
                final Document doc = luceneSettings.indexReader.document(i);
                String summary;
                final String summaryField = doc
                    .get(luceneSettings.factoryConfig.summaryField);

                summary = summaryField;

                final RawDocumentSnippet rawDocument = new RawDocumentSnippet(
                    new Integer(i), doc.get(luceneSettings.factoryConfig.titleField),
                    summary, doc.get(luceneSettings.factoryConfig.urlField), 0.0f);
                rawDocumentConsumer.addDocument(rawDocument);

                docsPushed++;
            }
        }
    }
}