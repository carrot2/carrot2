
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

package org.carrot2.filter.lingo.local;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.linguistic.*;
import org.carrot2.core.profiling.*;
import org.carrot2.filter.lingo.common.*;
import org.carrot2.filter.lingo.common.Cluster;
import org.carrot2.util.tokenizer.languages.*;


/**
 * An adapter for the 'old' Lingo to the new local interfaces
 * architecture. The old lingo is still in use, so it makes sense to 
 * add this support.
 * 
 * @author Dawid Weiss
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LingoLocalFilterComponent extends ProfiledLocalFilterComponentBase
    implements RawDocumentsConsumer, RawClustersProducer, RawDocumentsProducer, LocalFilterComponent
{
    /**
     * If this parameter is not-null (e.g. Boolean.TRUE), Lingo will leave in
     * the {@link RequestContext} an array of {@link Feature}s found in the
     * input snippets. Use the {@link #LINGO_EXTRACTED_FEATURES} key to obtain
     * the features from the context.
     */
    public static final String PARAMETER_LEAVE_FEATURES_IN_CONTEXT = "leave-features";

    /**
     * When the {@link #PARAMETER_LEAVE_FEATURES_IN_CONTEXT} request parameter
     * is not-null, Lingo will leave under this key in the
     * {@link RequestContext} an array of {@link Feature}s extracted while 
     * clustering.
     */
    public static final String LINGO_EXTRACTED_FEATURES = "lingo-extracted-features";
    
    /** Documents to be clustered */
    private ArrayList documents;

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object [] { RawDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object [] { RawDocumentsConsumer.class, RawClustersProducer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object [] { RawClustersConsumer.class }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;
    
    /** Raw documents consumer */
    private RawDocumentsConsumer rawDocumentsConsumer;
    
    /**
     *  Request params.
     */
    private Map requestParams;

    /**
     * An array of languages "recognized" by Lingo and used for preprocessing.
     */
	private Language[] languages;

	/**
	 * An array of default algorithm settings.
	 */
	private Map defaults;

    private Language tokenizerLanguage;
    
    /**
     * Creates a new Lingo filter with default parameters.
     */
    public LingoLocalFilterComponent()
    {
    	this(null, null);
    }
    
    public LingoLocalFilterComponent(Language [] languages, Map parameters)
    {
        this(languages, AllKnownLanguages.getLanguageForIsoCode("en"), parameters);
    }

    public LingoLocalFilterComponent(Language [] languages, Language tokenizerLanguage,
        Map parameters)
    {
        this.languages = languages;
        this.defaults = parameters;
        this.tokenizerLanguage = tokenizerLanguage;
    }
    
    /*
     * @see org.carrot2.core.LocalComponent#init(org.carrot2.core.LocalControllerContext)
     */
    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
    }

    /*
     * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * @see org.carrot2.core.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * @see org.carrot2.core.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * @see org.carrot2.core.LocalFilterComponent#setNext(org.carrot2.core.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawClustersConsumer = (RawClustersConsumer) next;
        if (next instanceof RawDocumentsConsumer) {
            rawDocumentsConsumer = (RawDocumentsConsumer) next;
        }
    }

    /*
     * @see org.carrot2.core.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();

        documents = null;
        rawClustersConsumer = null;
        rawDocumentsConsumer = null;
        requestParams = null;
    }

    /*
     * @see org.carrot2.core.LocalComponent#startProcessing(org.carrot2.core.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        this.requestParams = requestContext.getRequestParameters();
        documents = new ArrayList();        
    }

    /*
     * @see org.carrot2.core.clustering.TokenizedDocumentsConsumer#addDocument(org.carrot2.core.clustering.TokenizedDocument)
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        documents.add(new SnippetInterfaceAdapter(Integer.toString(documents.size()), doc));
        if (rawDocumentsConsumer != null) {
            rawDocumentsConsumer.addDocument(doc);
        }
    }

    /*
     * @see org.carrot2.core.LocalComponent#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        // Unfortunately, Lingo does not support incremental processing, so we
        // need to wait for all documents to come before we start processing
        startTimer();

        // Prepare data

        final Language [] languages = this.languages;
        
        // set the default parameters of the algorithm.
        // eh.. quick and dirty as always.
        Map current = new HashMap();
        if (this.defaults != null)
        {
        	current.putAll(this.defaults);
        }
        
        current.putAll(requestParams);
        final MultilingualClusteringContext clusteringContext = new MultilingualClusteringContext( current );
        clusteringContext.setLanguages(languages);
        clusteringContext.setTokenizerLanguage(tokenizerLanguage);

        if (documents.size() == 0) {
        	super.endProcessing();
        	return;
        }

        // Add documents to cluster.
        for (Iterator i = documents.iterator(); i.hasNext(); ) {
        	clusteringContext.addSnippet((SnippetInterfaceAdapter) i.next() );
        }

        // Query
        String query = (String) requestParams.get(LocalInputComponent.PARAM_QUERY);
        clusteringContext.setQuery(query);

        // Cluster them now.
        Cluster[] clusters = clusteringContext.cluster();
        
        // convert (lazily) back to the interfaces required by local architecture.
        for (int i = 0; i < clusters.length; i++) {
            this.rawClustersConsumer.addCluster(
            		new RawClusterInterfaceAdapter( clusters[i], documents ));
        }

        // Copy features to the context parameters
        requestParams.put(LINGO_EXTRACTED_FEATURES, current
            .get(LINGO_EXTRACTED_FEATURES));
        
        stopTimer();

        super.endProcessing();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getName()
     */
    public String getName()
    {
        return "Lingo (old)";
    }
}