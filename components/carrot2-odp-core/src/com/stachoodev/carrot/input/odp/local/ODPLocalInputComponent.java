/*
 * ODPLocalInputComponent.java
 * 
 * Created on 2004-06-28
 */
package com.stachoodev.carrot.input.odp.local;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.stachoodev.carrot.odp.*;
import com.stachoodev.carrot.odp.mixer.*;

/**
 * An input component that serves snippets drawn from the index of the Open
 * Directory Project. In order for this component to work, the
 * {@link com.stachoodev.carrot.odp.ODPIndex}must be properly initialized. The
 * original ODP <code>catid</code> is added to each document, see
 * {@link #PROPERTY_ODP_CATID}.
 * 
 * <p>
 * 
 * Query format is the following: "mixerId: query", where 'mixerId' is the
 * identifier of some {@link com.stachoodev.carrot.odp.mixer.TopicMixer},
 * 'query' is a query string accepted by the mixer. For possible mixerId values
 * and query format specifications refer to the implementations of the
 * {@link com.stachoodev.carrot.odp.mixer.TopicMixer}interface.
 * 
 * @author stachoo
 */
public class ODPLocalInputComponent extends ProfiledLocalInputComponentBase
    implements RawDocumentsProducer, LocalComponent
{
    /**
     * This property stores the id of the ODP topic (<code>catid</code>) to
     * which the document originally belonged.
     */
    public final static String PROPERTY_ODP_CATID = "catid";

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsProducer.class }));

    /** Current query */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

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
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        query = null;
        rawDocumentConsumer = null;
    }

    /*
     * (non-Javadoc)
     * 
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
        // Pass the query for the following components
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_QUERY, query);

        // Propagate startProcessing
        super.startProcessing(requestContext);

        startTimer();

        // Parse the query
        int colonPos = query.indexOf(':');
        if (colonPos == -1)
        {
            throw new ProcessingException("Malformed query, no ':' detected");
        }

        // Get catids
        String mixerName = query.substring(0, colonPos);
        String mixerQuery = query.substring(colonPos + 1);

        // Get topic mixer
        TopicMixer mixer = AllKnownTopicMixers.getTopicMixer(mixerName);
        if (mixer == null)
        {
            throw new ProcessingException("Unsupported topic mixer type: "
                + mixerName);
        }

        // Get topics
        List topics = mixer.mix(mixerQuery);

        // Count the actual number of documents
        int documentCount = 0;
        for (Iterator iter = topics.iterator(); iter.hasNext();)
        {
            Topic topic = (Topic) iter.next();
            documentCount += topic.getExternalPages().size();
        }

        // Pass the actual document count
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
            new Integer(documentCount));

        // We have to stop the timer before the call to the next component's
        // method. Otherwise, we would time the whole chain.
        stopTimer();

        // Add all topics to the consumer
        for (Iterator iter = topics.iterator(); iter.hasNext();)
        {
            Topic topic = (Topic) iter.next();
            List externalPages = topic.getExternalPages();

            for (Iterator iterator = externalPages.iterator(); iterator
                .hasNext();)
            {
                ExternalPage externalPage = (ExternalPage) iterator.next();
                RawDocumentSnippet rawDocumentSnippet = new RawDocumentSnippet(
                    externalPage.getTitle(), externalPage.getDescription());
                rawDocumentSnippet.setProperty(PROPERTY_ODP_CATID, topic
                    .getCatid());
                rawDocumentConsumer.addDocument(rawDocumentSnippet);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalInputComponentBase#validate()
     */
    protected void validate() throws ProcessingException
    {
        super.validate();

        if (rawDocumentConsumer == null)
        {
            throw new ProcessingException(
                "No successor component of type RawDocumentsProvider");
        }

        if (query == null)
        {
            throw new ProcessingException("No query provided");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "ODP Input";
    }
}