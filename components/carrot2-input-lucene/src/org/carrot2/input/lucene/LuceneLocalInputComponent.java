
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

package org.carrot2.input.lucene;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.ProfiledLocalInputComponentBase;

/**
 * Implements a local input component that reads data from Lucene index. Use
 * {@link org.carrot2.input.lucene.LuceneLocalInputComponentFactory} to obtain
 * instances of this component or provide a valid {@link LuceneLocalInputComponentFactoryConfig}
 * object.
 *
 * @author Stanislaw Osinski
 * @author Dawid Weiss
 * @author Sairaj Sunil
 *
 * @version $Revision$
 */
public class LuceneLocalInputComponent extends ProfiledLocalInputComponentBase
    implements RawDocumentsProducer
{
    /** The default number of requested results */
    public final static int DEFAULT_REQUESTED_RESULTS = 100;
    private final static String [] SOURCES = new String [] { "Lucene" };

    /**
     * A request-context parameter overriding the default search configuration.
     * The value of this parameter must be an instance of {@link LuceneLocalInputComponentFactoryConfig}.
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
     * Create an instance of the component with a set
     * of predefined settings.
     */
    public LuceneLocalInputComponent(LuceneLocalInputComponentConfig settings)
    {
        this.luceneSettings = settings;
    }

    /**
     * Create an empty instance of this component. You will need to pass
     * Lucene configuration at query time using {@link #LUCENE_CONFIG}.
     */
    public LuceneLocalInputComponent()
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
        return "Lucene Input";
    }

    /**
     *
     */
    private void pushResults(Map params, LuceneLocalInputComponentConfig luceneSettings) throws ParseException, IOException,
        ProcessingException
    {
        // assemble request parameters
        final int requestedDocuments = getIntFromRequestContext(
                requestContext, LocalInputComponent.PARAM_REQUESTED_RESULTS, DEFAULT_REQUESTED_RESULTS);
        final int startAt = getIntFromRequestContext(
                requestContext, LocalInputComponent.PARAM_START_AT, 0);

        // check if there is an override for lucene settings in the context.
        if (params.containsKey(LUCENE_CONFIG)) {
            luceneSettings = (LuceneLocalInputComponentConfig) params.get(LUCENE_CONFIG);
        }

        if (luceneSettings == null) {
            throw new ProcessingException("Lucene input component not configured. Need LuceneSettings.");
        }

        // Create a boolean query that combines all fields
        final Query booleanQuery = createQuery(luceneSettings);

        // Perform query
        final Hits hits = luceneSettings.searcher.search(booleanQuery);
        final int endAt = Math.min(hits.length(), startAt + requestedDocuments);

        // Pass the actual document count
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
            new Integer(endAt - startAt));

        // Pass the query
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_QUERY, query);

        // This improves document fetching when document count is > 100
        // The reason for this is 'specific' implementation of Lucene's
        // Hits class. The number 100 is hardcoded in Hits.
        if (endAt > 100)
        {
            hits.id(endAt - 1);
        }

        // This line is added to the original for the highlighting part. The
        // Highlighter is present in org.apache.lucene.search.highlight package
        Highlighter highlighter = null;
        if (luceneSettings.factoryConfig.summarizerConfig != null
            && luceneSettings.factoryConfig.summarizerConfig != LuceneLocalInputComponentFactoryConfig.NO_SUMMARIES)
        {
            highlighter = new Highlighter(luceneSettings.factoryConfig.summarizerConfig.formatter,
                new QueryScorer(booleanQuery));
        }

        // This is the new for loop written by me to handle the case of
        // highlighting, which was not present in the original version
        for (int i = startAt; i < endAt; i++)
        {
            final Document doc = hits.doc(i);
            String summary;
            final String summaryField = doc.get(luceneSettings.factoryConfig.summaryField);

            if (summaryField != null && highlighter != null)
            {
                String [] summaries = highlighter.getBestFragments(
                    luceneSettings.analyzer, luceneSettings.factoryConfig.summaryField,
                    summaryField, luceneSettings.factoryConfig.summarizerConfig.maxFragments);
                StringBuffer summaryBuffer = new StringBuffer();
                if (summaries.length > 0)
                {
                    summaryBuffer.append(summaries[0]);
                }
                for (int j = 1; j < summaries.length; j++)
                {
                    summaryBuffer.append(" ... ");
                    summaryBuffer.append(summaries[j]);
                }
                summary = summaryBuffer.toString();
            }
            else
            {
                summary = summaryField;
            }

            final RawDocumentSnippet rawDocument = new RawDocumentSnippet(
                new Integer(hits.id(i)), doc.get(luceneSettings.factoryConfig.titleField),
                summary, doc.get(luceneSettings.factoryConfig.urlField), hits.score(i));
            rawDocument.setProperty(RawDocument.PROPERTY_SOURCES, SOURCES);
            rawDocumentConsumer.addDocument(rawDocument);
        }
    }

    protected Query createQuery(LuceneLocalInputComponentConfig luceneSettings)
        throws ParseException
    {
        final BooleanQuery booleanQuery = new BooleanQuery();

        for (int i = 0; i < luceneSettings.factoryConfig.searchFields.length; i++)
        {
            final QueryParser queryParser =
                new QueryParser(luceneSettings.factoryConfig.searchFields[i],
                    luceneSettings.analyzer);
            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query queryComponent = queryParser.parse(query);
            booleanQuery.add(queryComponent, BooleanClause.Occur.SHOULD);
        }
        return booleanQuery;
    }
}