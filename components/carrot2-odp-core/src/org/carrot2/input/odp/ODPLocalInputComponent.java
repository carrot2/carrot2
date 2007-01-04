
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

package org.carrot2.input.odp;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.*;
import org.carrot2.input.odp.*;
import org.carrot2.input.odp.mixer.*;

/**
 * An input component that serves snippets drawn from the index of the Open
 * Directory Project. In order for this component to work, the
 * {@link org.carrot2.input.odp.ODPIndex}must be properly initialized. The
 * original ODP <code>catid</code> is added to each document, see
 * {@link org.carrot2.core.clustering.RawDocumentsProducer#PROPERTY_CATID}.
 * 
 * <p>
 * 
 * Query format is the following: "mixerId: query", where 'mixerId' is the
 * identifier of some {@link org.carrot2.input.odp.mixer.TopicMixer},
 * 'query' is a query string accepted by the mixer. For possible mixerId values
 * and query format specifications refer to the implementations of the
 * {@link org.carrot2.input.odp.mixer.TopicMixer}interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ODPLocalInputComponent extends ProfiledLocalInputComponentBase
    implements RawDocumentsProducer, LocalComponent
{
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
     * @see org.carrot2.core.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * (non-Javadoc)
     * 
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
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#flushResources()
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
        // Pass the query for the following components
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_QUERY, query);

        // Propagate startProcessing
        super.startProcessing(requestContext);

        startTimer();

        // Get the requested topics
        List topics = getTopics();

        // Store topics in the profile
        if (profile != null)
        {
            profile.addProfileEntry("odp-topics", new ProfileEntry(
                "ODP Topics", null, Collections.unmodifiableList(topics)));
        }

        // Count the actual number of documents and convert to a list of
        // RawClusters
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

        // Add all topics to the consumer and create a list of original
        // RawClusters to be stored in the request context
        List rawClusters = new ArrayList();
        int id = 0;
        for (Iterator iter = topics.iterator(); iter.hasNext();)
        {
            Topic topic = (Topic) iter.next();
            RawClusterBase rawCluster = new RawClusterBase();
            List externalPages = topic.getExternalPages();

            for (Iterator iterator = externalPages.iterator(); iterator
                .hasNext();)
            {
                ExternalPage externalPage = (ExternalPage) iterator.next();

                RawDocumentSnippet rawDocumentSnippet = new RawDocumentSnippet(
                    Integer.toString(id++), externalPage.getTitle(),
                    externalPage.getDescription(), null, -1);
                rawDocumentSnippet.setProperty(PROPERTY_CATID, Integer
                    .toString(topic.getCatid()));
                rawDocumentConsumer.addDocument(rawDocumentSnippet);

                rawCluster.addDocument(rawDocumentSnippet);
                rawCluster.setProperty(PROPERTY_CATID, Integer.toString(topic
                    .getCatid()));
            }

            rawClusters.add(rawCluster);
        }

        // Pass the original documents as a structure of clusters
        requestContext.getRequestParameters().put(
            RawDocumentsProducer.PARAM_ORIGINAL_RAW_CLUSTERS, rawClusters);
    }

    /**
     * @return @throws ProcessingException
     */
    private List getTopics() throws ProcessingException
    {
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
        return topics;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalInputComponentBase#validate()
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
     * @see org.carrot2.core.LocalComponent#getName()
     */
    public String getName()
    {
        return "ODP Input";
    }
}